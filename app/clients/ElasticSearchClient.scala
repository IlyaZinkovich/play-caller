package clients

import javax.inject.{Inject, Singleton}

import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.{Configuration, Logger}

import scala.concurrent.ExecutionContext

@Singleton
class ElasticSearchClient @Inject()(wsClient: WSClient, configuration: Configuration)
                                   (implicit ec: ExecutionContext) {

  private lazy val baseUrl = configuration.get[String]("elasticsearch.baseUrl")

  def store(data: JsValue) {
    val id = (data \ "id").get.as[String].filterNot("+/=".toSet)
    wsClient.url(s"$baseUrl/profiles/profile/$id")
      .post(Json.stringify(data))
      .foreach(response => {
        Logger.debug(response.statusText)
        Logger.debug(response.body)
      })
  }
}
