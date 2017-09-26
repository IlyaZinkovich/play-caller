package clients

import javax.inject.{Inject, Singleton}

import play.api.libs.json.{JsArray, JsValue, Json}
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TrueCallerClient @Inject() (wsClient: WSClient)
                                 (implicit ec: ExecutionContext) {

  val authorizationToken = "NHrai9x2N5VH09g_qf6p-KrdR3jprwgV"

  def search(searchType: Int, countryCode: String, phoneNumber: String): Future[Option[JsValue]] = {
    wsClient.url(s"https://www.truecaller.com/api/search?type=$searchType&countryCode=$countryCode&q=$phoneNumber")
      .withHttpHeaders(("Authorization", s"Bearer $authorizationToken"))
      .get
      .map(extractResponseData)
  }

  private def extractResponseData(response: WSResponse) = {
    (Json.parse(response.body) \ "data").toOption match {
      case Some(result) => result.as[JsArray].value.lift(0)
      case None => None
    }
  }
}
