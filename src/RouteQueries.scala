import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import database.DBFunctions
import spray.json._
/**
  * Created by bp on 08/02/17.
  */
object RouteQueries {
  def getRoute(implicit dbf:DBFunctions ):Route = {
    import JSONProtocol._
    path ("getArticle"){
      get{
        parameter("article_id".as[Long]) {
          (art_id: Long) => {
            val artjson: JsValue = dbf.getArticle(art_id).toJson
            complete(HttpEntity(ContentTypes.`application/json`, artjson.toString()))
          }
        }

      }
    }~
    path("getUser"){
      get{
        parameter("user_id".as[Long]){
          (usr_id:Long)=>{
            complete("Still not implemented.")
          }
        }
      }
    }


  }
}
