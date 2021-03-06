package actors

import actors.Storage.Store
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout
import clients.ElasticSearchClient
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import play.api.Configuration
import play.api.libs.json.Json
import play.api.test.WsTestClient

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class StorageSpec extends TestKit(ActorSystem("testSystem")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll with MockFactory {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  private implicit val timeout: Timeout = 5.seconds

  "A Storage actor" should {

    "store data in ElasticSearch" in {
      WsTestClient.withClient { client =>
        val configuration = Configuration(("elasticsearch.baseUrl", "http://localhost:9200"))
        val elasticSearchClient = new ElasticSearchClient(client, configuration)
        val json = Json.parse("{\"id\":\"id\",\"name\":\"Riya\"}")

        val storageActor = system.actorOf(Storage.props(elasticSearchClient))
        storageActor ? Store(json)
      }
    }
  }
}
