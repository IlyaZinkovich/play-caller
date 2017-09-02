package clients

import javax.inject.{Inject, Singleton}

import play.api.libs.json.{JsArray, JsValue, Json}
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class TrueCallerClient @Inject() (wsClient: WSClient) {

  val authorizationToken = "Yo8r8i2waUqYoAdFKrJAjhuQq3j5j6e7"

  def search(searchType: Int, countryCode: String, phoneNumber: String): Future[Option[JsValue]] = {
    wsClient.url(s"https://www.truecaller.com/api/search?type=$searchType&countryCode=$countryCode&q=$phoneNumber")
      .withHttpHeaders(("Authorization", s"Bearer $authorizationToken"))
      .get
      .map(extractResponseData)
  }

  private def extractResponseData(response: WSResponse) = {
    (Json.parse(response.body) \ "data").toOption match {
      case Some(result) => Option(result.as[JsArray].value(0))
      case None => None
    }
  }
}
