package data

import reactivemongo.api.{DefaultDB, MongoConnection, MongoDriver}
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ProfilesCollection {

  private val driver = new MongoDriver
  private val connection: MongoConnection = driver.connection(List("localhost"))
  private val database: Future[DefaultDB] = connection.database("experimental")
  val collection: Future[JSONCollection] = database.map(_.collection[JSONCollection]("profiles"))
}
