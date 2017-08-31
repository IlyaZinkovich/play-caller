package services

import akka.actor.{Actor, Props}
import play.api.libs.json._
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}

object ScraperActor {
  def props(wsClient: WSClient):Props = Props(new ScraperActor(wsClient))

  case class Scrap(countryCode: String, phoneNumber: String)
}

import scala.concurrent.duration._

class ScraperActor (ws: WSClient) extends Actor {

  import ScraperActor._

  private val timeout = 5.seconds

  def receive = {
    case Scrap(countryCode: String, phoneNumber: String) =>
      sender() ! Await.result(scrap(countryCode, phoneNumber), timeout)
  }

  def scrap(countryCode: String, phoneNumber: String): Future[JsValue] = {
    ws.url(s"https://www.truecaller.com/api/search?type=4&countryCode=$countryCode&q=$phoneNumber")
      .withHttpHeaders(("Authorization", "Bearer Yo8r8i2waUqYoAdFKrJAjhuQq3j5j6e7"))
      .get.map { response => Json.parse(response.body) \ "data" get }
  }
}