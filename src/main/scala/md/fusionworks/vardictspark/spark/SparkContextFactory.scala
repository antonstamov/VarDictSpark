package md.fusionworks.vardictspark.spark

import md.fusionworks.vardictspark.config.ConfigLoader
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}


object SparkContextFactory {
  private var sparkContext: Option[SparkContext] = None
  private lazy val sparkSqlContext = new SQLContext(getSparkContext())

  def getSparkContext(master: Option[String] = None): SparkContext = {
    sparkContext match {
      case Some(context) => context
      case None =>
        val sparkConf = new SparkConf().setAppName("JBrowse-ADAM")
        .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")

        master match {
          case Some(url) => sparkConf.setMaster(url)
          case None =>
            val jbConf = ConfigLoader.getJBrowseConf
            if (jbConf.hasPath("spark.masterUrl")) {
              val masterUrl = jbConf.getString("spark.masterUrl")
              sparkConf.setMaster(masterUrl)
            }
        }

        sparkContext = Some(new SparkContext(sparkConf))
        sparkContext.get
    }
  }

  def startSparkContext(): Unit = {
    println("Starting SparkContext...")
    getSparkContext()
  }

  def getSparkSqlContext = sparkSqlContext

  def stopSparkContext() = getSparkContext().stop()
}
