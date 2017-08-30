package controllers

import javax.inject._

import play.api.mvc.{Action, AnyContent}

//import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.ProfileService
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class PhoneProfileController @Inject()(cc: ControllerComponents,
                                       profileService: ProfileService) extends AbstractController(cc) {

//  private implicit val profileWrites = Json.writes[Profile]
//  private implicit val profileReads = Json.reads[Profile]

  def search(countryCode: String, phoneNumber: String): Action[AnyContent] = Action.async {
    profileService.findByPhone(countryCode, phoneNumber).map {
      case Some(result) => Ok(result)
      case None => NotFound
    }
  }
}

