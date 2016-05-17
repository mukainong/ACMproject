/* GenerateNames.scala */import org.apache.spark.SparkContext;import org.apache.spark.SparkConf;import scala.util.Random
object GenerateNames {    val outputDir = "/Users/mukainong/Documents/sparkApp/output/part"
    def main(args: Array[String]) {        val conf = new SparkConf()            .setMaster("local[3]")            .setAppName("GenerateNames")
        val sc = new SparkContext(conf)
        for (partition <- 0 to 3) {            val data = Seq.fill(100)(Random.alphanumeric.take(5).mkString);            sc.parallelize(data, 1).saveAsTextFile(outputDir + "_" + partition)	} 
    }}