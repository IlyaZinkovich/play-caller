package actors

import actors.ScrapActor.Scrap
import akka.actor.{Actor, Props}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import clients.{ElasticSearchClient, TrueCallerClient}
import play.api.libs.json.JsValue

import scala.concurrent.Future
import scala.concurrent.duration._

object IdentityProvider {

  def props(trueCallerClient: TrueCallerClient, elasticSearchClient: ElasticSearchClient) =
    Props(new IdentityProvider(trueCallerClient, elasticSearchClient))

  case class IdentifyByPhone(searchType: Int, countryCode: String, phoneNumber: String)

}

class IdentityProvider(trueCallerClient: TrueCallerClient,
                       elasticSearchClient: ElasticSearchClient) extends Actor {

  import IdentityProvider._

  private implicit val ec = context.dispatcher
  private implicit val timeout: Timeout = 5.seconds

  private val scrapper = context.actorOf(ScrapActor.props(trueCallerClient), "scrap-actor")
  private val storage = context.actorOf(StorageActor.props(elasticSearchClient), "storage-actor")

  def receive = {
    case IdentifyByPhone(searchType, countryCode, phoneNumber) =>
      val scrappedData = scrap(searchType, countryCode, phoneNumber)
      scrappedData pipeTo sender
      scrappedData pipeTo storage
  }

  private def scrap(searchType: Int, countryCode: String, phoneNumber: String): Future[Option[JsValue]] = {
    (scrapper ? Scrap(searchType, countryCode, phoneNumber)).mapTo[Option[JsValue]]
  }
}
