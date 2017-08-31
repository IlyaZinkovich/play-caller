package repository

import akka.actor.{Actor, Props}
import play.api.libs.json._
import reactivemongo.play.json._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

object ProfileActor {
  def props = Props[ProfileActor]

  case class FindProfile(countryCode: String, phoneNumber: String)

  case class PersistProfile(profileJson: JsValue)

  case class FindAllProfiles()

}

import scala.concurrent.duration._

class ProfileActor extends Actor with ProfilesCollection {

  import ProfileActor._

  private val timeout = 5.seconds

  def receive = {
    case FindProfile(countryCode: String, phoneNumber: String) =>
      sender() ! Await.result(collection.flatMap(_.find(Json.obj("phones.nationalFormat" -> phoneNumber)).one[JsObject]), timeout)
    case PersistProfile(profileJson: JsValue) =>
      val jsObject = changeIdPropertyToMongoId(profileJson.as[JsArray].value(0).as[JsObject])
      sender ! Await.result(collection.flatMap(_.update(jsObject, jsObject, upsert = true)).map(_ => Option(jsObject)), timeout)
    case FindAllProfiles =>
      sender ! Await.result(collection.flatMap(c => c.find(Json.obj()).one[JsArray]), timeout)
  }

  def changeIdPropertyToMongoId(jsObject: JsObject): JsObject = {
    jsObject \ "id" match {
      case JsDefined(value) => jsObject ++ Json.obj("_id" -> value) - "id"
      case _: JsUndefined => jsObject
    }
  }
}