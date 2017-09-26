package controllers

import javax.inject._

import actors.IdentityProvider
import actors.IdentityProvider.IdentifyByPhone
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import clients.{ElasticSearchClient, TrueCallerClient}
import play.api.cache.Cached
import play.api.libs.json.{JsArray, JsValue, Json}
import play.api.mvc._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

@Singleton
class ProfileController @Inject()(cc: ControllerComponents,
                                  system: ActorSystem,
                                  trueCallerClient: TrueCallerClient,
                                  elasticSearchClient: ElasticSearchClient,
                                  cached: Cached)
                                 (implicit ec: ExecutionContext) extends AbstractController(cc) {

  private implicit val timeout: Timeout = 5.seconds

  private val identityProvider =
    system.actorOf(IdentityProvider.props(trueCallerClient, elasticSearchClient), "identity-actor")

  def search(searchType: Int, countryCode: String, phoneNumber: String) = cached(s"$countryCode/$phoneNumber") {
    Action.async {
      (identityProvider ? IdentifyByPhone(searchType, countryCode, phoneNumber))
        .mapTo[Option[JsValue]]
        .map {
          case Some(scrappedData) => Ok(Json.toJson(scrappedData))
          case None => Ok(Json.obj("data" -> JsArray.empty))
        }
    }
  }
}

