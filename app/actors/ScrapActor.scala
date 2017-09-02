package actors

import akka.actor.{Actor, Props}
import akka.pattern.pipe
import clients.TrueCallerClient

import scala.concurrent.ExecutionContext.Implicits.global

object ScrapActor {

  def props(client: TrueCallerClient): Props = Props(new ScrapActor(client))

  case class Scrap(searchType: Int, countryCode: String, phoneNumber: String)

}

class ScrapActor(client: TrueCallerClient) extends Actor {

  import ScrapActor._

  def receive = {
    case Scrap(searchType: Int, countryCode: String, phoneNumber: String) =>
      client.search(searchType, countryCode, phoneNumber) pipeTo sender
  }
}