package actors

import akka.actor.{Actor, Props}
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext.Implicits.global

object StorageActor {

  def props(wsClient: WSClient): Props = Props(new StorageActor(wsClient))

  case class Store(data: JsValue)

}

class StorageActor(ws: WSClient) extends Actor {

  import StorageActor._

  def receive = {
    case Store(data: JsValue) => store(data)
  }

  def store(data: JsValue) {
    val id = (data \ "id").get.as[String].filterNot("+/=".toSet)
    ws.url(s"http://localhost:9200/profiles/profile/$id")
      .post(Json.stringify(data))
      .foreach(response => {
        Logger.debug(response.statusText)
        Logger.debug(response.body)
      })
  }
}