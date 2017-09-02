package actors

import akka.actor.{Actor, Props}
import clients.ElasticSearchClient
import play.api.libs.json.JsValue

object StorageActor {

  def props(client: ElasticSearchClient): Props = Props(new StorageActor(client))

  case class Store(data: JsValue)

}

class StorageActor(client: ElasticSearchClient) extends Actor {

  import StorageActor._

  def receive = {
    case Store(data: JsValue) => client.store(data)
  }

}