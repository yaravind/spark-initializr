package ${package}

import org.apache.logging.log4j.LogManager
import org.apache.spark.sql.SparkSession

object SparkRuntime {
  private val logger = LogManager.getLogger(getClass)

  def session(appName: String): SparkSession = {
    val spark = SparkSession
      .builder()
      .appName(appName)
      .master("local[*]")
      .config("spark.sql.extensions", "io.delta.sql.DeltaSparkSessionExtension")
      .config("spark.sql.catalog.spark_catalog", "org.apache.spark.sql.delta.catalog.DeltaCatalog")
      .getOrCreate()
    spark
  }

  def printVersions(): Unit = {
    val spark = SparkSession.getActiveSession.getOrElse(SparkSession.active)
    logger.debug("Spark version:\t\t\t\t\t " + spark.version)
    logger.debug("Delta version:\t\t\t\t\t " + io.delta.VERSION)
    logger.debug("Scala version:\t\t\t\t\t " + util.Properties.versionNumberString)
    logger.debug("Java version:\t\t\t\t\t " + System.getProperty("java.version"))
    logger.debug("JRE name:\t\t\t\t\t\t " + sys.props.get("java.runtime.name").getOrElse("**Not Available**"))
    logger.debug("JRE version:\t\t\t\t\t " + sys.props.get("java.runtime.version").getOrElse("**Not Available**"))
    logger.debug("java.vendor:\t\t\t\t\t " + System.getProperty("java.vendor"))
    logger.debug("os.name:\t\t\t\t\t\t " + sys.props.get("os.name").getOrElse("**Not Available**"))
    logger.debug("Metastore version:\t\t\t " + getSparkConfProp("spark.sql.hive.metastore.version"))
    logger.debug("Catalog/Metastore type:\t\t " + getSparkConfProp("spark.sql.catalogImplementation"))
    // logger.debug("Spark warehouse location: " + getSparkConfProp("spark.sql.warehouse.dir"))
    logger.debug("user.timezone:\t\t\t\t\t " + sys.props.get("user.timezone").getOrElse("**Not Available**"))
    logger.debug("spark.sql.session.timeZone:\t " + getSparkConfProp("spark.sql.session.timeZone"))
  }

  private def getSparkConfProp(key: String): String = {
    val spark = SparkSession.getActiveSession.getOrElse(SparkSession.active)
    Option(spark.conf.getOption(key)).flatten.getOrElse("**Not Available**")
  }
}
