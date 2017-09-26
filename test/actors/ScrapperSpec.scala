package actors

import actors.Scrapper.Scrap
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout
import clients.TrueCallerClient
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class ScrapperSpec extends TestKit(ActorSystem("testSystem")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll with MockFactory {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  private implicit val timeout: Timeout = 5.seconds


  "A Scrap actor" must {

    "send back response from TrueCaller client" in {
      val trueCallerClient = stub[TrueCallerClient]
      val searchType = 4
      val countryCode = "IN"
      val phoneNumber = "925436790"
      val json = Json.parse("{\"name\":\"Riya\"}")

      (trueCallerClient.search _).when(searchType, countryCode, phoneNumber).returns(Future(Option(json)))

      val scrapActor = system.actorOf(Scrapper.props(trueCallerClient))
      val future = scrapActor ? Scrap(searchType, countryCode, phoneNumber)

      val result = Await.result(future, 5.seconds).asInstanceOf[Option[JsValue]]
      result should be(Some(json))
    }

  }
}
