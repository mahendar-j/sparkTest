import org.apache.log4j.Logger
import org.eclipse.jetty.server.{HttpConfiguration, HttpConnectionFactory, NetworkTrafficServerConnector, Server}
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener

object JettyMain extends App {

  val log: Logger = Logger.getLogger(this.getClass)
  val conf = new Configuration(args)

  /*object conf {
    var port = sys.env.get("PORT") map (_.toInt) getOrElse (conf.getConfInt("jetty-port", Some(8001)).get)
    val stopTimeout = sys.env.get("STOP_TIMEOUT") map (_.toInt) getOrElse (5000)
    val connectorIdleTimeout = sys.env.get("CONNECTOR_IDLE_TIMEOUT") map (_.toInt) getOrElse (90000)
    val webapp = sys.env.get("PUBLIC") getOrElse "webapp"
    val contextPath = sys.env.get("CONTEXT_PATH") getOrElse "/"
  }*/
  var port = sys.env.get("PORT") map (_.toInt) getOrElse (conf.getConfInt("jetty-port", Some(8001)).get)
  var stopTimeout = sys.env.get("STOP_TIMEOUT") map (_.toInt) getOrElse (conf.getConfInt("jetty-stopTimeout", Some(5000)).get)
  var connectorIdleTimeout = sys.env.get("CONNECTOR_IDLE_TIMEOUT") map (_.toInt) getOrElse (conf.getConfInt("jetty-connectorIdleTimeout", Some(90000)).get)
  var jettyWebapp = sys.env.get("PUBLIC") getOrElse conf.getConfString("jetty-webapp", Some("webapp")).get
  var contextPath = sys.env.get("CONTEXT_PATH") getOrElse conf.getConfString("jetty-contextPath", Some("/")).get

  val server: Server = new Server
  log.info(s"Starting standalone  Query Engine on port ${port}")

  server setStopTimeout stopTimeout
  //server setDumpAfterStart true
  server setStopAtShutdown true

  val httpConfig = new HttpConfiguration()
  httpConfig setSendDateHeader true
  httpConfig setSendServerVersion false

  val connector = new NetworkTrafficServerConnector(server, new HttpConnectionFactory(httpConfig))
  connector setPort port
  connector setSoLingerTime 0
  connector setIdleTimeout connectorIdleTimeout
  server addConnector connector

  //val webapp = jettyWebapp
  //  val webApp = new WebAppContext
  // webApp setContextPath contextPath
  //  webApp setResourceBase webapp
  // webApp setEventListeners Array(new ScalatraListener)
  val context = new WebAppContext()


  //def createMovieContext: ContextHandler = {
  // val c = new WebAppContext
  // c setInitParameter(ScalatraListener.LifeCycleKey,"ScalatraBootstrap")
  // c setInitParameter("org.scalatra.cors.enable", "false")
  //  c setContextPath "/"
  // c setResourceBase "/src/main/webapp"   // dummy setting is required...
  // c setEventListeners Array(new ScalatraListener)
  //  c
  //}
  context setContextPath "/"
  context.setInitParameter(ScalatraListener.LifeCycleKey, "ScalatraBootstrap")
  context.setResourceBase("src/main/webapp")
  context.addEventListener(new ScalatraListener)
  context addServlet(classOf[DefaultServlet], "/")
  server.setHandler(context)

  server.start()
  log.info(s"Standalone  Query Engine Started")
  server.join()


}
