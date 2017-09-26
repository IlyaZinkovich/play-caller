package actors

import akka.actor.{Actor, Props}
import akka.pattern.pipe
import clients.TrueCallerClient

object Scrapper {

  def props(client: TrueCallerClient): Props = Props(new Scrapper(client))

  case class Scrap(searchType: Int, countryCode: String, phoneNumber: String)

}

class Scrapper(client: TrueCallerClient) extends Actor {

  import Scrapper._

  private implicit val ec = context.dispatcher

  def receive = {
    case Scrap(searchType: Int, countryCode: String, phoneNumber: String) =>
      client.search(searchType, countryCode, phoneNumber) pipeTo sender
  }
}