package clients

import javax.inject.{Inject, Singleton}

import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class ElasticSearchClient @Inject() (wsClient: WSClient) {

  def store(data: JsValue) {
    val id = (data \ "id").get.as[String].filterNot("+/=".toSet)
    wsClient.url(s"http://localhost:9200/profiles/profile/$id")
      .post(Json.stringify(data))
      .foreach(response => {
        Logger.debug(response.statusText)
        Logger.debug(response.body)
      })
  }
}
