package models

import play.api.libs.json._
import models.Date._
import anorm._
import anorm.SqlParser._
import scala.concurrent.Future

case class Place(
  id        : Long,
  name      : String,
  longitude : Double,
  latitude  : Double,
  created   : DateTime = now
)

object Place extends ((
  Long,
  String,
  Double,
  Double,
  DateTime
) => Place) {

  implicit val jsonFormat = Json.format[Place]

  val places =
    long("id") ~
    str("name") ~
    get[Double]("name") ~
    get[Double]("name") ~
    date("created") map {
      case     id~name~created =>
        Place(id,name,asDateTime(created))
    }

  def getById(id:Long) = Future {
    DB.withConnection { implicit connection =>
      SQL(
        """
          SELECT
            id,
            name,
            created
          FROM place
          WHERE id = {id};
        """
      ).on(
        'id -> id
      ).as(places.singleOpt)
    }
  }

  def findByName(name:String) = Future {
    DB.withConnection { implicit connection =>
      SQL(
        """
          SELECT
            id,
            name,
            created
          FROM place
          WHERE name LIKE '%' || {name} || '%';
        """
      ).on(
        'name -> name
      ).as(places *)
    }
  }

  def countAll() = Future {
    DB.withConnection { implicit connection =>
      val result = SQL(
        """
          SELECT COUNT(1) count
          FROM place w;
        """
      ).apply()

      try {
        Some(result.head[Long]("count"))
      } catch {
        case e:Exception => None
      }
    }
  }

  def update(id:Int,name:String) = Future {
    DB.withConnection { implicit connection =>
      SQL(
        """
          UPDATE place
          SET name = {name}
          WHERE id = {id};
        """
      ).on(
        'id -> id,
        'name -> name
      ).executeUpdate()
    }
  }
  

  def delete(id:Int) = Future {
    DB.withConnection { implicit connection =>
      SQL(
        """
          DELETE
          FROM place
          WHERE id = {id};
        """
      ).on(
        'id -> id
      ).executeUpdate()
    }
  }

  def deleteAll() = Future {
    DB.withConnection { implicit connection =>
      SQL(
        """
          DELETE
          FROM place;
        """
      ).executeUpdate()
    }
  }

  def create(name:String,longitude:Double,latitude:Double) = Future {

    val created = now

    DB.withConnection { implicit connection =>
      SQL(
        """
          INSERT INTO place (
            name,
            longitude,
            latitude,
            created
          ) VALUES (
            {name},
            {longitude},
            {latitude},
            {created}
          );
        """
      ).on(
        'name      -> name,
        'longitude -> longitude,
        'latitude  -> latitude,
        'created   -> asTimestamp(created)
      ).executeInsert()
    }
  }

}
