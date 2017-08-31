package domain

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads, Writes}

case class Profile(id: String, name: Option[String], phones: Option[List[Phone]])

object Profile {

  implicit val profileWrites: Writes[Profile] = (
    (JsPath \ "id").write[String] and
      (JsPath \ "name").writeNullable[String] and
      (JsPath \ "phones").writeNullable[List[Phone]]
    ) (unlift(Profile.unapply))

  implicit val profileReads: Reads[Profile] = (
    (JsPath \ "id").read[String] and
      (JsPath \ "name").readNullable[String] and
      (JsPath \ "phones").readNullable[List[Phone]]
    ) (Profile.apply _)
}
