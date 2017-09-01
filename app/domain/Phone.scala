package domain

import play.api.libs.functional.syntax._
import play.api.libs.functional.syntax.unlift
import play.api.libs.json.{JsPath, Reads, Writes}

case class Phone(e164Format: String, nationalFormat: String, countryCode: String, carrier: String,
                 spamScore: Int, spamType: String)

object Phone {
  implicit val phoneWrites: Writes[Phone] = (
    (JsPath \ "e164format").write[String] and
      (JsPath \ "nationalFormat").write[String] and
      (JsPath \ "countryCode").write[String] and
      (JsPath \ "carrier").write[String] and
      (JsPath \ "spamScore").write[Int] and
      (JsPath \ "spamType").write[String]
    ) (unlift(Phone.unapply))

  implicit val phoneReads: Reads[Phone] = (
    (JsPath \ "e164format").read[String] and
      (JsPath \ "nationalFormat").read[String] and
      (JsPath \ "countryCode").read[String] and
      (JsPath \ "carrier").read[String] and
      (JsPath \ "spamScore").read[Int] and
      (JsPath \ "spamType").read[String]
    ) (Phone.apply _)
}
