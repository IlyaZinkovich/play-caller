package repository

import reactivemongo.api.{DefaultDB, MongoConnection, MongoDriver}
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ProfilesCollection {

  val driver = new MongoDriver
  val connection: MongoConnection = driver.connection(List("localhost"))
  val database: Future[DefaultDB] = connection.database("experimental")
  val collection: Future[JSONCollection] = database.map(_.collection[JSONCollection]("profiles"))
}
