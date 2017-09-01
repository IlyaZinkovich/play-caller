package controllers

import javax.inject._

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import actors.ScrapActor
import actors.ScrapActor.Scrap
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

@Singleton
class ProfileController @Inject()(cc: ControllerComponents,
                                  system: ActorSystem,
                                  wsClient: WSClient) extends AbstractController(cc) {

  private val scrapActor = system.actorOf(ScrapActor.props(wsClient), "scrap-actor")
  private implicit val timeout: Timeout = 5.seconds

  def search(searchType: Int, countryCode: String, phoneNumber: String): Action[AnyContent] = Action.async {

    (scrapActor ? Scrap(countryCode, phoneNumber)).mapTo[Option[JsValue]].map {
      case Some(result) => Ok(Json.toJson(result))
      case None => NotFound
    }
  }
}

