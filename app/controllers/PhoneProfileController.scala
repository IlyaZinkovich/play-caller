package controllers

import javax.inject._

import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.ProfileService

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class PhoneProfileController @Inject()(cc: ControllerComponents,
                                       profileService: ProfileService) extends AbstractController(cc) {

  def search(countryCode: String, phoneNumber: String): Action[AnyContent] = Action.async {
    profileService.findByPhone(countryCode, phoneNumber).map {
      case Some(result) => Ok(Json.toJson(result))
      case None => NotFound
    }
  }
}

