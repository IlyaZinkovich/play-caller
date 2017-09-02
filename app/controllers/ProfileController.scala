package controllers

import javax.inject._

import actors.ScrapActor.Scrap
import actors.StorageActor.Store
import actors.{ScrapActor, StorageActor}
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import clients.{ElasticSearchClient, TrueCallerClient}
import play.api.cache.Cached
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

@Singleton
class ProfileController @Inject()(cc: ControllerComponents,
                                  system: ActorSystem,
                                  trueCallerClient: TrueCallerClient,
                                  elasticSearchClient: ElasticSearchClient,
                                  cached: Cached) extends AbstractController(cc) {

  private val scrapActor = system.actorOf(ScrapActor.props(trueCallerClient), "scrap-actor")
  private val storageActor = system.actorOf(StorageActor.props(elasticSearchClient), "storage-actor")
  private implicit val timeout: Timeout = 5.seconds

  def search(searchType: Int, countryCode: String, phoneNumber: String) = cached(s"$countryCode/$phoneNumber") {
    Action.async {
      scrap(searchType, countryCode, phoneNumber).map(store).map {
        case Some(scrappedData) => Ok(Json.toJson(scrappedData))
        case None => NotFound
      }
    }
  }

  private def scrap(searchType: Int, countryCode: String, phoneNumber: String): Future[Option[JsValue]] = {
    (scrapActor ? Scrap(searchType, countryCode, phoneNumber)).mapTo[Option[JsValue]]
  }

  private def store(value: Option[JsValue]): Option[JsValue] = {
    value match {
      case Some(data) => storageActor ! Store(data)
      case _ =>
    }
    value
  }
}

