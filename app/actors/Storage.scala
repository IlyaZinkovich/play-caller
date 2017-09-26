package actors

import akka.actor.{Actor, Props}
import clients.ElasticSearchClient
import play.api.libs.json.JsValue

object Storage {

  def props(client: ElasticSearchClient): Props = Props(new Storage(client))

  case class Store(data: JsValue)

}

class Storage(client: ElasticSearchClient) extends Actor {

  import Storage._

  def receive = {
    case Store(data: JsValue) => client.store(data)
  }
}