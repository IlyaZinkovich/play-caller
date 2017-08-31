package source

import akka.actor.{Actor, Props}
import akka.pattern.pipe
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat
import domain.{Phone, Profile}
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
      collection.flatMap(_.find(byE164(countryCode, phoneNumber)).one[JsValue].map(toProfile)) pipeTo sender
    case PersistProfile(profileJson: JsValue) =>
      val jsObject = changeIdPropertyToMongoId(profileJson.as[JsArray].value(0).as[JsObject])
      collection.flatMap(_.update(jsObject, jsObject, upsert = true)).map(_ => toProfile(Option(jsObject))) pipeTo sender
    case FindAllProfiles =>
      collection.flatMap(c => c.find(Json.obj()).one[JsArray]) pipeTo sender
  }

  private def byE164(countryCode: String, phoneNumber: String) = {
    val util = PhoneNumberUtil.getInstance()
    val e164 = util.format(util.parse(phoneNumber, countryCode), PhoneNumberFormat.E164)
    Json.obj("phones.e164Format" -> e164)
  }

  def changeIdPropertyToMongoId(jsObject: JsObject): JsObject = {
    jsObject \ "id" match {
      case JsDefined(value) => jsObject ++ Json.obj("_id" -> value) - "id"
      case _: JsUndefined => jsObject
    }
  }

  def toProfile(jsObject: Option[JsValue]): Option[Profile] = {
    jsObject match {
      case Some(json) =>
        Some(Profile(
          (json \ "_id").as[String],
          (json \ "name").asOpt[String],
          (json \ "phones").asOpt[List[Phone]]
        ))
      case None => None
    }
  }
}

