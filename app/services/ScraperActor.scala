package services

import akka.actor.{Actor, Props}
import akka.pattern.pipe
import play.api.libs.json._
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ScraperActor {
  def props(wsClient: WSClient):Props = Props(new ScraperActor(wsClient))

  case class Scrap(countryCode: String, phoneNumber: String)
}

class ScraperActor (ws: WSClient) extends Actor {

  import ScraperActor._

  def receive = {
    case Scrap(countryCode: String, phoneNumber: String) =>
      scrap(countryCode, phoneNumber) pipeTo sender
  }

  def scrap(countryCode: String, phoneNumber: String): Future[JsValue] = {
    ws.url(s"https://www.truecaller.com/api/search?type=4&countryCode=$countryCode&q=$phoneNumber")
      .withHttpHeaders(("Authorization", "Bearer Yo8r8i2waUqYoAdFKrJAjhuQq3j5j6e7"))
      .get.map { response => Json.parse(response.body) \ "data" get }
  }
}