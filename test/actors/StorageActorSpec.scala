package actors

import actors.StorageActor.Store
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout
import clients.ElasticSearchClient
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import play.api.libs.json.Json

import scala.concurrent.duration._

class StorageActorSpec extends TestKit(ActorSystem("testSystem")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll with MockFactory {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  private implicit val timeout: Timeout = 5.seconds


  "A Storage actor" must {

    "store data in ElasticSearch" in {
      val elasticSearchClient = mock[ElasticSearchClient]
      val json = Json.parse("{\"name\":\"Riya\"}")

      (elasticSearchClient.store _).expects(json)

      val storageActor = system.actorOf(StorageActor.props(elasticSearchClient))
      storageActor ! Store(json)
    }

  }
}
