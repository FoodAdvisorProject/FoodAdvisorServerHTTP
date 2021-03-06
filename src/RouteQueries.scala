import java.util

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, MediaTypes}
import akka.http.scaladsl.server.ContentNegotiator.Alternative.MediaType
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import classes.{Article, Photo, Travel}
import database.DBFunctions
import spray.json._
import scala.concurrent.duration._
import scalaj.http.Base64
/**
  * Created by bp on 08/02/17.
  */
object RouteQueries {
  val NOTFOUND ="NOTFOUND object not found"
  def getRoute(implicit dbf:DBFunctions ):Route = {
    import JSONProtocol._

    path ("getArticle"){
      get{
        parameter("article_id".as[Long]) {
          (art_id: Long) => {

            try {
              val article: Article = dbf.getArticle(art_id)
              val ret: String = if (article!=null) article.toJson.toString() else NOTFOUND
              complete(HttpEntity(ContentTypes.`application/json`, ret ))
            }
            catch {
              case e:Throwable => {
                e.printStackTrace()
                complete(HttpEntity("Error: " + e.getMessage ))
              }

            }

          }

        }

      }
    }~
    path ("getArticleImage"){
      get{
        parameter("article_id".as[Long]) {
          (art_id: Long) => {

            try {
              val article: Article = dbf.getArticle(art_id)
              println("debugging photo : \n"+article.photo.toBase64())
              val ret: Array[Byte] = Base64.decode(article.photo.toBase64().filter(_>=' '))
              complete(HttpEntity( ret ))
            }
            catch {
              case e:Throwable => {
                e.printStackTrace()
                complete(HttpEntity("Error: " + e.getMessage ))
              }

            }

          }

        }

      }
    }~
    path("getUserImage"){
      get{
       parameter("user_id".as[Long]){
         (usr_id)=>
           try{
             val user=dbf.getUser(usr_id)
             val ret: Array[Byte] = Base64.decode(user.photo.toBase64().filter(_>=' '))
             //complete(HttpEntity(MediaTypes.`image/png`,ret))
             complete(HttpEntity(ret))
           }catch{
             case e:Throwable =>
               complete(HttpEntity("Error: "+e.getMessage))
           }
       }
      }
    }~
    path("getUser"){
      get{
        parameter("user_id".as[Long]){
          (usr_id:Long)=>

            try {
              val user = dbf.getUser(usr_id)
              val ret = if (user!=null) user.toJson.toString() else NOTFOUND
              complete(HttpEntity(ContentTypes.`application/json`, ret ))
            }
            catch {
              case e:Throwable =>
                complete(HttpEntity("Error: "+e.getMessage))

            }


        }~
        parameter("email".as[String],"password".as[String]){
          (email:String,password:String)=>

            try {
              val user = dbf.getUser(dbf.getUserIdByEmail(email));
              val ret =
                if( user.passw == password) {
                 if (user != null) user.toJson.toString() else NOTFOUND
                }
                else{
                 "null"
                }
              complete(HttpEntity(ContentTypes.`application/json`, ret ))
            }
            catch {
              case e:Throwable =>
                complete(HttpEntity("Error: "+e.getMessage))

            }


        }
      }
    }~
    path("getUserIdByEmail"){
      get{
        parameter("email"){
          (email)=>
            try{
              val user_id = dbf.getUserIdByEmail(email)
              complete(HttpEntity(user_id.toString))

            }
            catch{
              case e:Throwable =>
                complete(HttpEntity("Error "+e.getMessage))
            }
        }
      }
    }~
    path("getTransaction"){
      get{
        parameter("tran_id".as[Long]){
          (tran_id:Long)=>{

            try {
              val tran = dbf.getTransaction(tran_id)
              val ret = if (tran!=null) tran.toJson.toString() else NOTFOUND
              complete(HttpEntity(ContentTypes.`application/json`, ret ))
            }
            catch {
              case e:Throwable =>
                complete(HttpEntity("Error: "+e.getMessage))

            }

          }
        }~
        parameter("art_id".as[Long],"buyer_id".as[Long]) {
          (art_id: Long, usr_id: Long) => {

            try {
              val tran = dbf.getTransaction(art_id, usr_id)
              val ret = if (tran != null) tran.toJson.toString() else NOTFOUND
              complete(HttpEntity(ContentTypes.`application/json`, ret))
            }
            catch {
              case e: Throwable =>
                complete(HttpEntity("Error: " + e.getMessage))

            }


          }
        }
      }
    }~
      path("getArticleTravel"){
        get {
          parameter("tran_id".as[Long]) {

            (tran_id: Long) =>
              try {
                val trav: Travel = dbf.getArticleTravel(tran_id)
                val ret = if (trav != null && trav.getTransactionList.size()>0 ) trav.toJson.toString() else NOTFOUND
                complete(HttpEntity(ContentTypes.`application/json`, ret))
              }
              catch {
                case e: Throwable =>
                  complete(HttpEntity("Error: " + e.getMessage))

              }
          }
        }
    }~
      path("getArticleLife"){
        get {
          parameter("article_id".as[Long],"seller_id".as[Long]) {

            (article_id: Long , seller_id: Long) =>
              try {
                val art_life: util.List[Travel] = dbf.getArticleLife(article_id,seller_id)

                val ret = if (art_life != null && art_life.size() >0 ) art_life.toJson.toString() else NOTFOUND
                complete(HttpEntity(ContentTypes.`application/json`, ret))
              }
              catch {
                case e: Throwable =>
                  e.printStackTrace();
                  complete(HttpEntity("Error: " + e.getMessage))
              }
          }
        }
    }~
    path("getUserArticles"){
      parameter("user_id".as[Long]){
        (user_id:Long)=>
          try{
            val usr_articles: util.List[Article] = dbf.getUserArticles(user_id)
            val ret = if (usr_articles!=null && usr_articles.size() >0 ) usr_articles.toJson.toString() else NOTFOUND
            complete(HttpEntity(ContentTypes.`application/json`,ret))
          }
          catch {
            case e: Throwable =>
              e.printStackTrace()
              complete(HttpEntity("Error!: " + e.getMessage+" "+e.getCause))


          }
      }
    }~
    path("addUser") {
      toStrictEntity(10.seconds) {
        post {
          formFields("login_name".as[String],
            "login_passw".as[String],
            "email".as[String],
            "name".as[String],
            "second_name".as[String],
            "is_enterprise".as[Int],
            "enterprise_description".as[String],
            "photo".as[String]) {

            (login, passw, email, name, sec_name, is_enterpr, enterpr_desc, photo) =>
              try {

                dbf.addUser(login, passw, email, name, sec_name, is_enterpr, enterpr_desc, new Photo(photo))
                val ret = dbf.getUser(dbf.getUserIdByEmail(email));
                complete(HttpEntity(ret.user_id.toString()));
              } catch {
                case e: Throwable => complete("Error: " + e.getMessage)

              }

          }
        }
      }
    }~
    path("addArticle"){
      toStrictEntity(10.seconds) {
        post {
          formField("name".as[String],
            "creator_id".as[Long],
            "description".as[String],
            "longitude".as[Float],
            "latitude".as[Float],
            "photo") {

            (name, cr_id, description, long, lat, photo) =>
              try {
                val ret: Long = dbf.addArticle(name, cr_id, description, long, lat, new Photo(photo))
                complete(HttpEntity(ret.toString()))
              } catch {
                case e: Throwable => complete("Error: " + e.getMessage)
              }

          }
        }
      }
    }~
    path("addTransaction"){
      post{
        formFields("article_id".as[Long],
          "buyer_id".as[Long],
          "seller_id".as[Long],
          "longitude".as[Float],
          "latitude".as[Float]){

          (art_id,buyer_id,seller_id,long,lat)=>
            try{
              dbf.addTransaction(art_id,buyer_id,seller_id,long,lat)
              val tr = dbf.getTransaction(art_id,buyer_id )
              complete(HttpEntity(tr.id.toString()))
            }catch{
              case e:Throwable => complete("Error: "+e.getMessage)
            }
        }
      }
    }


  }
}
