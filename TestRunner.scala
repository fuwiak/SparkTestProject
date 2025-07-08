//> using scala "2.13.12"
//> using dep "org.apache.spark::spark-core:3.4.0"
//> using dep "org.apache.spark::spark-sql:3.4.0"

import org.apache.spark.sql.SparkSession
import org.apache.spark.SparkConf

object TestRunner {
  def main(args: Array[String]): Unit = {
    println("🧪 Starting Comprehensive Spark Test Suite")
    println("=" * 50)
    
    // Test 1: Basic Scala functionality
    testBasicScala()
    
    // Test 2: Spark initialization
    testSparkInitialization()
    
    println("=" * 50)
    println("🏁 Test Suite Complete")
  }
  
  def testBasicScala(): Unit = {
    println("\n📝 Test 1: Basic Scala Functionality")
    try {
      val data = List(1, 2, 3, 4, 5)
      val result = data.map(_ * 2).filter(_ > 5).sum
      println(s"✅ Scala collections test passed: $result")
    } catch {
      case e: Exception =>
        println(s"❌ Scala test failed: ${e.getMessage}")
    }
  }
  
  def testSparkInitialization(): Unit = {
    println("\n🚀 Test 2: Spark Initialization")
    
    var spark: SparkSession = null
    try {
      // Try to create Spark session with compatibility settings
      val conf = new SparkConf()
        .setAppName("TestRunner")
        .setMaster("local[1]")
        .set("spark.ui.enabled", "false")
        .set("spark.sql.warehouse.dir", "/tmp/spark-warehouse")
        .set("spark.hadoop.fs.defaultFS", "file:///")
        
      spark = SparkSession.builder()
        .config(conf)
        .getOrCreate()
        
      spark.sparkContext.setLogLevel("ERROR")
      
      // Simple test
      import spark.implicits._
      val testData = spark.sparkContext.parallelize(Seq(1, 2, 3, 4, 5))
      val count = testData.count()
      val sum = testData.sum()
      
      println(s"✅ Spark RDD test passed:")
      println(s"   - Count: $count")
      println(s"   - Sum: $sum")
      
      // DataFrame test
      val df = Seq(
        ("Alice", 25),
        ("Bob", 30),
        ("Charlie", 35)
      ).toDF("name", "age")
      
      val adultCount = df.filter($"age" >= 30).count()
      println(s"✅ Spark DataFrame test passed:")
      println(s"   - Adults count: $adultCount")
      
    } catch {
      case e: java.lang.UnsupportedOperationException if e.getMessage.contains("getSubject") =>
        println("❌ Java compatibility issue detected:")
        println("   This is a known issue with Java 24 and Spark 3.x")
        println("   📋 Solutions:")
        println("   1. Use Java 8, 11, or 17")
        println("   2. Or upgrade to Spark 4.x (when available)")
        println("   3. Or add JVM flags for compatibility")
        
      case e: Exception =>
        println(s"❌ Spark initialization failed: ${e.getMessage}")
        println("   This might be due to:")
        println("   - Java version compatibility")
        println("   - Missing dependencies")
        println("   - Environment setup issues")
        
    } finally {
      if (spark != null) {
        try {
          spark.stop()
          println("✅ Spark session closed successfully")
        } catch {
          case _: Exception => // Ignore cleanup errors
        }
      }
    }
  }
} 