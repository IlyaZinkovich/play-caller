package services

import javax.inject.{Inject, Singleton}

import play.api.libs.json._
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class TrueCallerScraper @Inject()(ws: WSClient) {

  def scrap(countryCode: String, phoneNumber: String): Future[JsValue] = {
    ws.url(s"https://www.truecaller.com/api/search?type=4&countryCode=$countryCode&q=$phoneNumber")
      .withHttpHeaders(("Authorization", "Bearer Yo8r8i2waUqYoAdFKrJAjhuQq3j5j6e7"))
      .get.map { response => Json.parse(response.body) \ "data" get }
  }
}