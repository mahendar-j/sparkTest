package backend.swagger.server.api
import org.scalatra.ScalatraServlet
import org.scalatra.swagger.Swagger
case class User(id: Int, name: String)

class MainServlet(implicit val swagger: Swagger) extends ScalatraServlet {
  var users: List[User] = List(User(1, "John"), User(2, "Todd"), User(3, "Chris"))
  get("/users") {
  users
  }

}
