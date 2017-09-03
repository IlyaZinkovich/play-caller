import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.JsArray
import play.api.test.FakeRequest
import play.api.test.Helpers._

class FunctionalSpec extends PlaySpec with GuiceOneAppPerSuite {

  "PhoneProfileController" should {

    "return profile data for valid phone number" in {
      val response = route(app, FakeRequest(GET, "/search?countryCode=IN&phoneNumber=925436790")).get

      status(response) mustBe OK
      contentType(response) mustBe Some("application/json")
      (contentAsJson(response) \ "name").as[String] mustBe "Riya Riya Riya Riya"
    }

    "return not found for invalid phone number" in {
      val response = route(app, FakeRequest(GET, "/search?countryCode=IN&phoneNumber=someWrongNumber")).get

      status(response) mustBe OK
      (contentAsJson(response) \ "data").as[JsArray] mustBe JsArray.empty
    }
  }
}
