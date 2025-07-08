//> using scala "2.13.12"
//> using dep "org.apache.spark::spark-core:3.4.0"
//> using dep "org.apache.spark::spark-sql:3.4.0"

import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}

object SimpleSparkDemo {
  def main(args: Array[String]): Unit = {
    println("🚀 Welcome to Apache Spark!")
    
    // Create simple SparkContext
    val conf = new SparkConf()
      .setAppName("SimpleSparkDemo")
      .setMaster("local[*]")
      .set("spark.sql.adaptive.enabled", "true")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    
    val sc = new SparkContext(conf)
    sc.setLogLevel("WARN")
    
    try {
      println("📊 Demonstration of basic Spark operations:")
      
      // 1. Create simple RDD
      val numbers = sc.parallelize(1 to 100)
      println(s"Created RDD with numbers from 1 to 100")
      
      // 2. Simple operations
      val evenNumbers = numbers.filter(_ % 2 == 0)
      val squares = evenNumbers.map(x => x * x)
      val sum = squares.sum()
      
      println(s"Count of even numbers: ${evenNumbers.count()}")
      println(s"Sum of squares of even numbers: ${sum}")
      
      // 3. Working with text
      val words = sc.parallelize(Seq(
        "Apache Spark works excellently",
        "Scala is a beautiful language", 
        "Spark works in cluster",
        "Data processing with Spark"
      ))
      
      val wordCount = words
        .flatMap(_.split(" "))
        .map(word => (word.toLowerCase, 1))
        .reduceByKey(_ + _)
        .collect()
      
      println("\n📈 Word count:")
      wordCount.sortBy(-_._2).take(10).foreach { case (word, count) =>
        println(s"  '$word': $count times")
      }
      
      println("\n✅ Demonstration completed successfully!")
      println("🎯 Scala and Spark are successfully configured and working!")
      
    } finally {
      sc.stop()
    }
  }
} 