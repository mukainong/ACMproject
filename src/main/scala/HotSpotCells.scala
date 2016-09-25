import scala.io.Source
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf


object csvSplit {
    def main(args: Array[String]): Unit = {
        // set spark context
        val conf = new SparkConf().setAppName("wordcount").setMaster("local[*]")
        val sc = new SparkContext(conf)

        // Read the CSV file
        // val csv = sc.textFile("/Users/mukainong/Documents/sparkApp/sample.csv")
	val csv = sc.textFile("/Users/mukainong/Documents/sparkApp/yellow_tripdata_2015-01.csv")
	
	val header = csv.first()

	val rows = csv.filter{x => x != header}

        // split / clean data        
	val rawData = rows.map(line => (transform(List(line.split(",")(2), line.split(",")(9), line.split(",")(10))), 
						List(line.split(",")(2), line.split(",")(9), line.split(",")(10))))

	val validPairs = rawData.filter{case (x, y) => x != null}

	val group = validPairs.groupByKey()

	val numOfValues = group.map{case (x, y) => (x, y.size) }
	val duplicateRDD = group.map{case (x, y) => (x, y.size) }.collectAsMap()

	val avg :Float = duplicateRDD.foldLeft(0)(_+_._2).toFloat/(550*400*52)
	val S :Double = Math.sqrt(duplicateRDD.values.foldLeft(0.0)(_ + Math.pow(_, 2)).toFloat/(550*400*52) - Math.pow(avg,2).toFloat)

	val neighbors = numOfValues.map{case (List(x1,x2,x3), y) => (List(x1,x2,x3),
		numNeighbors(x1, x2, x3, 550, 400, 52),
	duplicateRDD.filter{case(List(x4,x5,x6), y) => ((x4>=(x1-1)&&x4<=(x1+1))&&(x5>=(x2-1)&&x5<=(x2+1))&&(x6>=(x3-1)&&x6<=(x3+1)))}.foldLeft(0)(_+_._2)
								     ) }	
	
	val result = neighbors .map{case (List(x1, x2, x3), y, z) => (List(x1, x2, x3), 
		(z - avg*y)/(S*Math.sqrt((550*400*52*y - y*y)/(550*400*52 - 1)))
				   )}

	result.take(100)foreach(println)
    }

    def transform( l : List[String] ) : List[Int] = {
	
	var timeDate = l(0).split(" ")(0)
	var timeMonth = timeDate.split("-")(1)
	var timeDay = timeDate.split("-")(2).toInt

	val daysTillLastMonth = timeMonth match {
	    case "01"  => 0
  	    case "02"  => 31
            case "03"  => 59
  	    case "04"  => 90
  	    case "05"  => 120
  	    case "06"  => 151
  	    case "07"  => 181
  	    case "08"  => 212
  	    case "09"  => 243
  	    case "10"  => 273
  	    case "11"  => 304
  	    case "12"  => 334
  	    case _  => 0  // the default, catch-all
	}
	
	val timeStep = (timeDay + daysTillLastMonth)/7

	var longitude = ((l(1).toDouble + 74.250)/0.001).toInt
	
	// longitude.toInt

	var latitude = ((l(2).toDouble - 40.500)/0.001).toInt
	
	// latitude.toInt

	if (timeStep < 0 || longitude < 0 || latitude < 0 || longitude > 550 || latitude > 400) {
	    return null
	}		

	return List(timeStep + 0, longitude + 0, latitude + 0)
    }

    def numNeighbors ( x:Int, y:Int , z:Int, a:Int, b:Int , c:Int) : Int = {
	if ((x == 0 ||x == a)&&(y == 0 ||y == b)&&(z == 0 ||z == c)) {
	    return 8
	} else if (((x == 0 ||x == a)&&(y == 0 ||y == b))||((x == 0 ||x == a)&&(z == 0 ||z == c))||((y == 0 ||y == b)&&(y == 0 ||y == b))) {
	    return 12
	} else if ((x == 0 ||x == a)||(y == 0 ||y == b)||(z == 0 ||z == c)) {
	    return 18
	}
	return 27
    }

    //def computeGi () : Double = {

    //}
}