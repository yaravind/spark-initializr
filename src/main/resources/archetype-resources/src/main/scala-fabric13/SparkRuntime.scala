package ${package}

import org.apache.spark.sql.SparkSession

object SparkRuntime {
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
    println("Spark version:\t\t\t\t\t " + spark.version)
    println("Delta version:\t\t\t\t\t " + io.delta.VERSION)
    println("Scala version:\t\t\t\t\t " + util.Properties.versionNumberString)
    println("Java version:\t\t\t\t\t " + System.getProperty("java.version"))
    println("JRE name:\t\t\t\t\t\t " + sys.props.get("java.runtime.name").getOrElse("**Not Available**"))
    println("JRE version:\t\t\t\t\t " + sys.props.get("java.runtime.version").getOrElse("**Not Available**"))
    println("java.vendor:\t\t\t\t\t " + System.getProperty("java.vendor"))
    println("os.name:\t\t\t\t\t\t " + sys.props.get("os.name").getOrElse("**Not Available**"))
    println("Metastore version:\t\t\t " + getSparkConfProp("spark.sql.hive.metastore.version"))
    println("Catalog/Metastore type:\t\t " + getSparkConfProp("spark.sql.catalogImplementation"))
    // println("Spark warehouse location: "+ getSparkConfProp("spark.sql.warehouse.dir"))
    println("user.timezone:\t\t\t\t\t " + sys.props.get("user.timezone").getOrElse("**Not Available**"))
    println("spark.sql.session.timeZone:\t " + getSparkConfProp("spark.sql.session.timeZone"))
  }

  private def getSparkConfProp(key: String): String = {
    val spark = SparkSession.getActiveSession.getOrElse(SparkSession.active)
    Option(spark.conf.getOption(key)).flatten.getOrElse("**Not Available**")
  }
}
