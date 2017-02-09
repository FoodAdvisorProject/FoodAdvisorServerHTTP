
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
          => throw DeserializationException("Article Expected.")
        }
    }
}

    // @TODO Implement the RootJSONFormat for each class in classes
    implicit object UserJSON extends RootJsonFormat[classes.User]{
      def write(obj: User): JsValue ={
      JsObject(
        "user_id"->JsNumber(obj.user_id),
        "login"->JsString(obj.login),
        "passw"->JsString(obj.passw),
        "email"->JsString(obj.email),
        "name"->JsString(obj.name),
        "second_name"->JsString(obj.second_name),
        "bool" -> JsBoolean(obj.bool),
        "enterprise_description"->JsString(obj.enterprise_description),
        "photo"-> JsString("")
      )
    }

      def read(v: JsValue): User = {
      v.asJsObject().getFields(
        "user_id",
        "login",
        "passw",
        "email",
        "name",
        "second_name",
        "bool",
        "enterprise,description",
        "photo") match {
          // If Some fields aren't initialized throws an exception
          case Seq(JsNumber(id),JsString(l),JsString(p),JsString(e),JsString(n),JsString(s_n),JsBoolean(b),JsString(desc),JsString(photo))
          => new User(id.toLong,l,p,e,n,s_n,b,desc,null)
          case _
          => throw DeserializationException("User Expected.")
        }
    }
}

    // @TODO Implement the RootJSONFormat for each class in classes
    implicit object TransactionJSON extends RootJsonFormat[classes.Transaction]{
      def write(obj: Transaction): JsValue ={
      JsObject(
        "id"->JsNumber(obj.id),
        "article_id"->JsNumber(obj.article_id),
        "buyer_id"->JsNumber(obj.buyer_id),
        "seller_id"->JsNumber(obj.seller_id),
        "longitude"->JsNumber(obj.longitude),
        "latitude"->JsNumber(obj.latitude)
      )
    }

      def read(v: JsValue): Transaction = {
      v.asJsObject().getFields(
        "id",
        "article_id",
        "buyer_id",
        "seller_id",
        "longitude",
        "latitude") match {
          // If Some fields aren't initialized throws an exception
          case Seq(JsNumber(id),JsNumber(a_id),JsNumber(b_id),JsNumber(s_id),JsNumber(lo),JsNumber(la))
          => new Transaction(id.toLong,a_id.toLong,b_id.toLong,s_id.toLong,lo.toFloat,la.toFloat)
          case _
          => throw DeserializationException("Transaction Expected.")
        }
    }

  }

}