package repository

import javax.inject.Singleton

import play.api.libs.json._
import reactivemongo.play.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ProfilesRepository extends ProfilesCollection {

  def findProfile(countryCode: String, phoneNumber: String): Future[Option[JsObject]] = {
    collection.flatMap(_.find(Json.obj("phones.nationalFormat" -> phoneNumber)).one[JsObject])
  }

  def findAllProfiles(): Future[Option[JsArray]] = {
    collection.flatMap(c => c.find(Json.obj()).one[JsArray])
  }

  def persistProfile(profileJson: JsValue): Future[Option[JsObject]] = {
    val jsObject = changeIdPropertyToMongoId(profileJson.as[JsArray].value(0).as[JsObject])
    collection.flatMap(_.update(jsObject, jsObject, upsert = true)).map(_ => Option(jsObject))
  }

  def changeIdPropertyToMongoId(jsObject: JsObject): JsObject = {
    jsObject \ "id" match {
      case JsDefined(value) => jsObject ++ Json.obj("_id" -> value) - "id"
      case _: JsUndefined => jsObject
    }
  }
}
