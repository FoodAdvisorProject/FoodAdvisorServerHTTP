import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

import database.{DBDriver, DBFunctions}


import scala.io.StdIn

/**
  * Created by bp on 06/02/17.
  */

object WebServer {

  def main(args: Array[String]): Unit = {
    import JSONProtocol._

    println("Hello!")
    val database ="test_db"
    val user ="root"
    val passw="root"
    val user_table="UTENTE"
    val article_table="ARTICOLO"
    val transaction_table="TRANSAZIONE"

    val d=new DBDriver(database, user, passw)

    implicit val dbf = new DBFunctions(d, user_table, article_table, transaction_table)
    implicit val system: ActorSystem = ActorSystem()
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    implicit val executionContext = system.dispatcher

    val route: Route = RouteQueries.getRoute

    val bindingFuture = Http().bindAndHandle(route ,"localhost",8080)

    println("Everything is ok. Server is running. press Enter to exit")
    StdIn.readLine()
    bindingFuture.flatMap(_.unbind()).onComplete(_=>system.terminate())

  }
}
