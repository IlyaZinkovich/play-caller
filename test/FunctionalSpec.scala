import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._

class FunctionalSpec extends PlaySpec with GuiceOneAppPerSuite {

  "Routes" should {

    "send 200 on a good request" in {
      route(app, FakeRequest(GET, "/")).map(status(_)) mustBe Some(OK)
    }

  }

  "HomeController" should {

    "render the index page" in {
      val home = route(app, FakeRequest(GET, "/")).get

      status(home) mustBe Status.OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include("Your new application is ready.")
    }

  }

  "PhoneProfileController" should {

    "return profile data for valid phone number" in {
      val response = route(app, FakeRequest(GET, "/search?countryCode=IN&phoneNumber=925436790")).get

      status(response) mustBe OK
      contentType(response) mustBe Some("application/json")
      (contentAsJson(response) \ "name").as[String] mustBe "Riya Riya Riya Riya"
    }

  }
}
