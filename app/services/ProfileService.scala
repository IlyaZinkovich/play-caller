package services

import javax.inject.{Inject, Singleton}

import play.api.libs.json.JsObject
import repository.ProfilesRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ProfileService @Inject()(scraper: TrueCallerScraper,
                               profilesRepository: ProfilesRepository) {

  def findByPhone(countryCode: String, phoneNumber: String): Future[Option[JsObject]] = {
    profilesRepository.findProfile(countryCode, phoneNumber).flatMap {
      case Some(result) => Future(Option(result))
      case None => scrapAndGet(countryCode, phoneNumber)
    }
  }

  private def scrapAndGet(countryCode: String, phoneNumber: String): Future[Option[JsObject]] = {
    for {
      foundProfile <- scraper.scrap(countryCode, phoneNumber)
      persistedProfile <- profilesRepository.persistProfile(foundProfile)
    } yield persistedProfile
  }
}
