package models

import play.api.libs.json._
import models.Date._
import anorm._
import anorm.SqlParser._
import scala.concurrent.Future

case class Widget(
  id      : Long,
  name    : String,
  created : DateTime = now
)

object Widget extends ((
  Long,
  String,
  DateTime
) => Widget) {

  implicit val jsonFormat = Json.format[Widget]

  val widgets =
    long("id") ~
    str("name") ~
    date("created") map {
      case     id~name~created =>
        Widget(id,name,asDateTime(created))
    }

  def getById(id:Long) = Future {
    DB.withConnection { implicit connection =>
      SQL(
        """
          SELECT
            id,
            name,
            created
          FROM widget
          WHERE id = {id};
        """
      ).on(
        'id -> id
      ).as(widgets.singleOpt)
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
          FROM widget
          WHERE name LIKE '%' || {name} || '%';
        """
      ).on(
        'name -> name
      ).as(widgets *)
    }
  }

  def countAll() = Future {
    DB.withConnection { implicit connection =>
      val result = SQL(
        """
          SELECT COUNT(1) count
          FROM widget w;
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
          UPDATE widget
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
          FROM widget
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
          FROM widget;
        """
      ).executeUpdate()
    }
  }

  def create(name:String) = Future {

    val created = now

    DB.withConnection { implicit connection =>
      SQL(
        """
          INSERT INTO widget (
            name,
            created
          ) VALUES (
            {name},
            {created}
          );
        """
      ).on(
        'name    -> name,
        'created -> asTimestamp(created)
      ).executeInsert()
    }
  }

}
