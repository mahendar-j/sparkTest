import com.typesafe.config.{Config, ConfigFactory, ConfigResolveOptions}
import org.apache.log4j.Logger

import java.io.File
import scala.collection.JavaConverters._

class Configuration extends  Serializable {
  @transient var conf:Config = null
  @transient var log:Logger = null
  /**
   * initialize a blank object
   */
  def initialize = {
    log = Logger.getLogger(this.getClass())

  }
  def loadConfig(path:String, allowUnresolved:Boolean) :Config = {
    val file=new File(path)
    if(file.exists) {
      log.debug(s"Loading configuration from ${path} allowing unresolved ${allowUnresolved}.")
      ConfigFactory.load(ConfigFactory.parseFile(file), ConfigResolveOptions.defaults().setAllowUnresolved(allowUnresolved))
    } else {
      log.warn(s"Configuration file ${path} does not exist!")
      ConfigFactory.empty()
    }
  }
  /** initialize an object from arguments */
  def initializeWithArgs(arguments:Seq[String]): Unit = {

    conf = arguments
      .sliding(2,2)
      .zipWithIndex
      .map {
        case (Seq("--config", path), num) =>
          loadConfig(path, num < arguments.length/2-1)
        case (Seq(name, value), _) if name.startsWith("--") =>
          val m = Map(name.substring(2) -> value.replace("\n","\\n"))
          ConfigFactory.parseMap(m.asJava)
        case e =>
          throw new UnsupportedOperationException(s"error parsing argument/value pairs close to $e")
      }
      .foldLeft(ConfigFactory.load()) { (prevConf, nextConf) =>
        nextConf.withFallback(prevConf)
      }

    if(log.isTraceEnabled) {
      log.trace(s"Finally derived at configuration ${conf}")
    }
  }
  def this(arguments: Seq[String]) = {
    this()
    initialize
    initializeWithArgs(arguments)
  }

  /** easier for java clients */
  def this(arguments: Array[String]) = {
    this(arguments.toSeq)
  }
  def getConfString(key:String, defVal : Option[String]) : Option[String] = {
    try {
      return Some(conf.getString(key))
    } catch {
      case _:Throwable => return defVal
    }
  }



  /** get a mulit-string-based config entry */
  def getConfSeq(key: String, defVal: Option[Seq[String]]) : Option[Seq[String]] = {
    try {
      return Some(conf.getString(key).split(','))
    } catch {
      case _:Throwable => return defVal
    }
  }



  /** get int-based config entry */
  def getConfInt(key: String, defVal: Option[Int]) : Option[Int] = {
    try {
      return Some(conf.getInt(key))
    } catch {
      case _:Throwable => return defVal
    }
  }



  /** get long-based config entry */
  def getConfLong(key: String, defVal: Option[Long]) : Option[Long] = {
    try {
      return Some(conf.getLong(key))
    } catch {
      case _:Throwable => return defVal
    }
  }



  /** get int-based config entry */
  def getConfBoolean(key: String, defVal: Option[Boolean]) : Option[Boolean] = {
    try {
      return Some(conf.getBoolean(key))
    } catch {
      case _:Throwable => return defVal
    }
  }



  /** get double-based config entry */
  def getConfDouble(key: String, defVal: Option[Double]) : Option[Double] = {
    try {
      return Some(conf.getDouble(key))
    } catch {
      case _:Throwable => return defVal
    }
  }


}
