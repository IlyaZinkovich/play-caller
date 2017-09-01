package data

import akka.actor.{Actor, Props}
import akka.pattern.pipe
import play.api.libs.json._
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ScrapActor {

  def props(wsClient: WSClient): Props = Props(new ScrapActor(wsClient))

  case class Scrap(countryCode: String, phoneNumber: String)

}

class ScrapActor(ws: WSClient) extends Actor {

  import ScrapActor._

  def receive = {
    case Scrap(countryCode: String, phoneNumber: String) =>
      scrap(countryCode, phoneNumber) pipeTo sender
  }

  def scrap(countryCode: String, phoneNumber: String): Future[Option[JsValue]] = {
    ws.url(s"https://www.truecaller.com/api/search?type=4&countryCode=$countryCode&q=$phoneNumber")
      .withHttpHeaders(("Authorization", "Bearer Yo8r8i2waUqYoAdFKrJAjhuQq3j5j6e7"))
      .get
      .map { response =>
        (Json.parse(response.body) \ "data").toOption match {
          case Some(result) => Option(result.as[JsArray].value(0))
          case None => None
        }
      }
  }
}