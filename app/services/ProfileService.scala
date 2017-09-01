package services

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import data.ScrapActor
import data.ScrapActor.Scrap
import play.api.libs.json.JsValue
import play.api.libs.ws.WSClient

import scala.concurrent.Future
import scala.concurrent.duration._

@Singleton
class ProfileService @Inject()(system: ActorSystem,
                               wsClient: WSClient) {

  private val scrapActor = system.actorOf(ScrapActor.props(wsClient), "scrap-actor")
  private implicit val timeout: Timeout = 5.seconds

  def findByPhone(countryCode: String, phoneNumber: String): Future[Option[JsValue]] = {
    (scrapActor ? Scrap(countryCode, phoneNumber)).mapTo[Option[JsValue]]
  }
}