package repository

import akka.actor.{Actor, Props}
import akka.pattern.pipe
import play.api.libs.json._
import reactivemongo.play.json._

import scala.concurrent.ExecutionContext.Implicits.global

object ProfileActor {
  def props = Props[ProfileActor]

  case class FindProfile(countryCode: String, phoneNumber: String)

  case class PersistProfile(profileJson: JsValue)

  case class FindAllProfiles()

}

class ProfileActor extends Actor with ProfilesCollection {

  import ProfileActor._

  def receive = {
    case FindProfile(countryCode: String, phoneNumber: String) =>
      collection.flatMap(_.find(Json.obj("phones.nationalFormat" -> phoneNumber)).one[JsObject]) pipeTo sender
    case PersistProfile(profileJson: JsValue) =>
      val jsObject = changeIdPropertyToMongoId(profileJson.as[JsArray].value(0).as[JsObject])
      collection.flatMap(_.update(jsObject, jsObject, upsert = true)).map(_ => Option(jsObject)) pipeTo sender
    case FindAllProfiles =>
      collection.flatMap(c => c.find(Json.obj()).one[JsArray]) pipeTo sender
  }

  def changeIdPropertyToMongoId(jsObject: JsObject): JsObject = {
    jsObject \ "id" match {
      case JsDefined(value) => jsObject ++ Json.obj("_id" -> value) - "id"
      case _: JsUndefined => jsObject
    }
  }
}