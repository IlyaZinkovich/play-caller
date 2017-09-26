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

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

@Singleton
class ProfileController @Inject()(cc: ControllerComponents,
                                  system: ActorSystem,
                                  trueCallerClient: TrueCallerClient,
                                  elasticSearchClient: ElasticSearchClient,
                                  cached: Cached) extends AbstractController(cc) {

  private val identityProvider =
    system.actorOf(IdentityProvider.props(trueCallerClient, elasticSearchClient), "identity-actor")

  private implicit val timeout: Timeout = 5.seconds

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

