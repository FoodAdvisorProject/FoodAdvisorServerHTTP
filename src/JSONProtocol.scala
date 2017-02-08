
import spray.json._
import classes._
/**
  * Created by bp on 06/02/17.
  */
object JSONProtocol extends DefaultJsonProtocol {

  // for each class T in classes we must implement a RootJsonFormat[T] object
  // this object must contain two methods (read,write) that will
  // (de)serialize the object into JSON format

  // the code is almost self explanatory

  // @TODO add photo implementation
  implicit object ArticleJSON extends RootJsonFormat[classes.Article]{

    // Creates a JSON Object that contains relevant fields of the Article class
    def write(a:classes.Article): JsValue ={
      JsObject(
        "article_id"->JsNumber(a.article_id),
        "name"->JsString(a.name),
        "creator_id"->JsNumber(a.creator_id),
        "description"->JsString(a.description),
        "photo"-> JsString("")
      )
    }

    // takes a JsValue, that must be instance of JsObject, and parse it
    // this function returns an Article object initialized with supplied data
    def read(v:JsValue): Article ={
      v.asJsObject().getFields("article_id",
        "name",
        "creator_id",
        "description",
        "photo") match {
          // If Some fields aren't initialized throws an exception
          case Seq(JsNumber(id),JsString(n),JsNumber(c_id),JsString(desc),JsString(p))
          => new Article(id.toLong,n,c_id.toLong,desc,null)
          case _
          => throw new DeserializationException("Article Expected.")
        }
    }

    // @TODO Implement the RootJSONFormat for each class in classes
    implicit object UserJSON extends RootJsonFormat[classes.User]{
      def write(obj: User): JsValue = ???

      def read(json: JsValue): User = ???
    }

  }

}