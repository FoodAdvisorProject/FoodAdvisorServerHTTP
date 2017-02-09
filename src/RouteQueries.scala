import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import classes.{Article, Photo}
import database.DBFunctions
import spray.json._
/**
  * Created by bp on 08/02/17.
  */
object RouteQueries {
  val NOTFOUND ="Error: object not found"
  def getRoute(implicit dbf:DBFunctions ):Route = {
    import JSONProtocol._

    path ("getArticle"){
      get{
        parameter("art_id".as[Long]) {
          (art_id: Long) => {

            try {
              val article: Article = dbf.getArticle(art_id)
              val ret = if (article!=null) article.toJson.toString() else NOTFOUND
              complete(HttpEntity(ContentTypes.`application/json`, ret ))
            }
            catch {
              case e:Throwable =>
                complete(HttpEntity("Error: "+e.getMessage))

            }

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
        parameter("art_id".as[Long],"user_id".as[Long]) {
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
        parameter("art_id".as[Long], "user_id".as[Long]) {
          (art_id: Long, buyer_id: Long) =>
            try {
              val trav = dbf.getArticleTravel(art_id,buyer_id)
              val ret = if (trav != null) trav.toJson.toString() else NOTFOUND
              complete(HttpEntity(ContentTypes.`application/json`, ret))
            }
            catch {
              case e: Throwable =>
                complete(HttpEntity("Error: " + e.getMessage))

            }
        }~
        parameter("tran_id".as[Long]) {
          (tran_id:Long) =>
            try {
              val trav = dbf.getArticleTravel(tran_id)

              val ret = if (trav != null) trav.toJson.toString() else NOTFOUND
              complete(HttpEntity(ContentTypes.`application/json`, ret))
            }
            catch {
              case e: Throwable =>
                complete(HttpEntity("Error: " + e.getMessage))

            }
        }
      }
    }~
    path("addUser"){
      post{

        parameter("login_name".as[String],
          "passw_login".as[String],
          "email".as[String],
          "name".as[String],
          "second_name".as[String],
          "is_enterprise".as[Int],
          "enterprise_description".as[String],
          "photo".as[String]){

          (login,passw,email,name,sec_name,is_enterpr,enterpr_desc,photo)=>
            try{

              dbf.addUser(login,passw,email,name,sec_name,is_enterpr,enterpr_desc, new Photo(photo))
              complete(HttpEntity("OK"))
            }catch{
              case e:Throwable => complete("Error: "+e.getMessage)

            }

        }
      }
    }~
    path("addArticle"){
      post{
        parameter("name".as[String],"creator_id".as[Long],"description".as[String],"longitude".as[Float],"latitude".as[Float],"photo"){
          (name,cr_id,description,long,lat,photo)=>

            try {
              dbf.addArticle(name,cr_id,description,long,lat,new Photo(photo))
              complete(HttpEntity("OK"))
            }catch{
              case e:Throwable => complete("Error: "+e.getMessage)
            }

        }
      }
    }~
    path("addTransaction"){
      post{
        parameter("article_id".as[Long],"buyer_id".as[Long],"seller_id".as[Long],"longitude".as[Float],"latitude".as[Float]){
          (art_id,buyer_id,seller_id,long,lat)=>
            try{
              dbf.addTransaction(art_id,buyer_id,seller_id,long,lat)
              complete(HttpEntity("OK"))
            }catch{
              case e:Throwable => complete("Error: "+e.getMessage)
            }
        }
      }
    }


  }
}
