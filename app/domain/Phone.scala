package domain

import play.api.libs.functional.syntax._
import play.api.libs.functional.syntax.unlift
import play.api.libs.json.{JsPath, Reads, Writes}

case class Phone(e164Format: Option[String], nationalFormat: String, countryCode: String, carrier: String,
                 spamScore: Option[Int], spamType: Option[String])

object Phone {
  implicit val phoneWrites: Writes[Phone] = (
    (JsPath \ "e164format").writeNullable[String] and
      (JsPath \ "nationalFormat").write[String] and
      (JsPath \ "countryCode").write[String] and
      (JsPath \ "carrier").write[String] and
      (JsPath \ "spamScore").writeNullable[Int] and
      (JsPath \ "spamType").writeNullable[String]
    ) (unlift(Phone.unapply))

  implicit val phoneReads: Reads[Phone] = (
    (JsPath \ "e164format").readNullable[String] and
      (JsPath \ "nationalFormat").read[String] and
      (JsPath \ "countryCode").read[String] and
      (JsPath \ "carrier").read[String] and
      (JsPath \ "spamScore").readNullable[Int] and
      (JsPath \ "spamType").readNullable[String]
    ) (Phone.apply _)
}
