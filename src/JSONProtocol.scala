
import spray.json._
import classes._
import database.DBFunctions
import org.iq80.leveldb.DBFactory

import scala.collection.JavaConversions._
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
    def write(a:classes.Article): JsObject ={
      val ret = JsObject(
        "article_id"->JsNumber(a.article_id),
        "name"->JsString(a.name),
        "creator_id"->JsNumber(a.creator_id),
        "description"->JsString(a.description)
        //"photo"-> JsString(if (a.photo!=null && a.photo.toBase64() !=null ) a.photo.toBase64 else "")
      )
      ret
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
          => new Article(id.toLong,n,c_id.toLong,desc,new Photo(p))
          case _
          => throw DeserializationException("Article Expected.")
        }
    }
}

    // @TODO Implement the RootJSONFormat for each class in classes
    implicit object UserJSON extends RootJsonFormat[classes.User]{
      def write(user: User): JsObject ={
        if(user == null) {
          JsObject("user_id" -> JsNumber(0))
        }
        else {
          JsObject(
            "user_id" -> JsNumber(user.user_id),
            "login" -> JsString(user.login),
            //"passw" -> JsString(user.passw),
            "email" -> JsString(user.email),
            "name" -> JsString(user.name),
            "second_name" -> JsString(user.second_name),
            "is_enterprise" -> JsBoolean(user.bool),
            "enterprise_description" -> JsString(user.enterprise_description)
            //"photo" -> JsString(if (user.photo != null && user.photo.toBase64() != null) user.photo.toBase64() else "")
          )
        }
    }

      def read(v: JsValue): User = {
      v.asJsObject().getFields(
        "user_id",
        "login",
        "passw",
        "email",
        "name",
        "second_name",
        "is_enterprise",
        "enterprise_description",
        "photo") match {
          // If Some fields aren't initialized throws an exception
          case Seq(JsNumber(id),JsString(l),JsString(p),JsString(e),JsString(n),JsString(s_n),JsBoolean(b),JsString(desc),JsString(photo))
          => new User(id.toLong,l,p,e,n,s_n,b,desc,new Photo(photo))
          case _
          => throw DeserializationException("User Expected.")
        }
    }
}

    // @TODO Implement the RootJSONFormat for each class in classes
    implicit object TransactionJSON extends RootJsonFormat[classes.Transaction]{
      def write(obj: Transaction): JsValue ={
        if (obj.isInstanceOf[RichTransaction]) {
           val t_obj:RichTransaction = obj.asInstanceOf[classes.RichTransaction]
          JsObject(
              "id" -> JsNumber(obj.id),
              "article_id" -> JsNumber(obj.article_id),
              "buyer_id" -> JsNumber(obj.buyer_id),
              "seller_id" -> JsNumber(obj.seller_id),
              "longitude" -> JsNumber(obj.longitude),
              "latitude" -> JsNumber(obj.latitude),
              "buyer" -> t_obj.buyer.toJson,
              "seller" -> t_obj.seller.toJson
            )
          }
        else {
          JsObject(
            "id" -> JsNumber(obj.id),
            "article_id" -> JsNumber(obj.article_id),
            "buyer_id" -> JsNumber(obj.buyer_id),
            "seller_id" -> JsNumber(obj.seller_id),
            "longitude" -> JsNumber(obj.longitude),
            "latitude" -> JsNumber(obj.latitude)
          )
        }
    }

      def read(transaction: JsValue): Transaction = {
      transaction.asJsObject().getFields(
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

     implicit object TravelJSON extends RootJsonFormat[classes.Travel]{

       def write(travel: Travel): JsValue ={
         val temp1: List[Transaction] = travel.getTransactionList.toList
         val  temp2:List[JsValue] = for( t <- temp1) yield t.toJson
         JsArray(temp2.toVector)

       }

       def read(v: JsValue): Travel =
         throw new Exception("the transaction read breaks the logic. " +
             "it isn't implemented")

     }
  implicit object ArticleListJSON extends RootJsonFormat[java.util.List[Article]]{

    def write(article_list: java.util.List[Article]): JsValue ={
      val  temp:List[JsValue] = for( art <- article_list.toList) yield art.toJson
      JsArray(temp.toVector)

    }

    def read(v: JsValue): java.util.List[Article] =
      throw new Exception("article list is not intended to be readed.")

  }
}