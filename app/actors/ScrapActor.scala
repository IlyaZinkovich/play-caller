package actors

import akka.actor.{Actor, Props}
import akka.pattern.pipe
import clients.TrueCallerClient

object ScrapActor {

  def props(client: TrueCallerClient): Props = Props(new ScrapActor(client))

  case class Scrap(searchType: Int, countryCode: String, phoneNumber: String)

}

class ScrapActor(client: TrueCallerClient) extends Actor {

  import ScrapActor._

  private implicit val ec = context.dispatcher

  def receive = {
    case Scrap(searchType: Int, countryCode: String, phoneNumber: String) =>
      client.search(searchType, countryCode, phoneNumber) pipeTo sender
  }
}