package models

import test._

import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._

import play.api.libs.json._
import play.api.libs.json.Json._

import scala.concurrent.{Await,ExecutionContext}

object WidgetSpec extends Specification {

  def create(names:String *):List[Int] = {

    names.foldLeft(List[Int]()) {
      (list,name) =>

      val id:Int = waitFor {
        Widget.create(name) map {
          case Some(id:Long) => id.toInt
          case _ => failure("Widget should have been created")
        }
      }

      list :+ id
    }
  }


  "Widget.getById" should {

    "return some Widget if such a Widget exists" in new App {

      val created = create("asdf")

      created.length mustEqual 1

      waitFor {
        Widget.getById(created.head) map {
          case Some(widget:Widget) => {
            widget.name mustEqual "asdf"
            success
          }
          case _ => failure("This widget should exist")
        }
      }
    }
    
    "return nothing if no such Widget exists" in new App {

      val result = Widget.getById(-1) map {
        case Some(widget:Widget) => failure("This widget should not exist")
        case _ => success
      }

      waitFor { result }
    }
  }


  "Widget.create" should {

    "return some newly created Widget" in new App {

      create("asdf")
    }
  }
}