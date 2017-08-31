package services

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import play.api.libs.json.{JsObject, JsValue}
import play.api.libs.ws.WSClient
import repository.ProfileActor
import repository.ProfileActor.{FindProfile, PersistProfile}
import services.ScraperActor.Scrap

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

@Singleton
class ProfileService @Inject()(system: ActorSystem,
                               wsClient: WSClient) {

  private val profileActor = system.actorOf(ProfileActor.props, "profile-actor")
  private val scraperActor = system.actorOf(ScraperActor.props(wsClient), "scraper-actor")
  private implicit val timeout: Timeout = 5.seconds

  def findByPhone(countryCode: String, phoneNumber: String): Future[Option[JsObject]] = {
    (profileActor ? FindProfile(countryCode, phoneNumber)).mapTo[Option[JsObject]].flatMap {
      case Some(result) => Future(Option(result))
      case None => scrapAndGet(countryCode, phoneNumber)
    }
  }

  private def scrapAndGet(countryCode: String, phoneNumber: String): Future[Option[JsObject]] = {
    for {
      foundProfile <- (scraperActor ? Scrap(countryCode, phoneNumber)).mapTo[JsValue]
      persistedProfile <- (profileActor ? PersistProfile(foundProfile)).mapTo[Option[JsObject]]
    } yield persistedProfile
  }
}
