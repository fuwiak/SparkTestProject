//> using scala "2.13.12"
//> using dep "org.apache.spark::spark-core:3.5.0"
//> using dep "org.apache.spark::spark-sql:3.5.0"

package com.quantexa.assessments.example

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession

object Example {
  def main(args: Array[String]): Unit = {
    //Set logger level to Warn
    Logger.getRootLogger.setLevel(Level.WARN)

    //Create a spark context, using a local master so Spark runs on the local machine
    val spark = SparkSession.builder()
      .master("local[*]")
      .appName("ScoringModel")
      .config("spark.sql.adaptive.enabled", "true")
      .config("spark.sql.adaptive.coalescePartitions.enabled", "true")
      .getOrCreate()

    //importing spark implicits allows functions such as dataframe.as[T]
    import spark.implicits._

    case class CustomerData(
                             customerId: String,
                             forename: String,
                             surname: String
                           )
                           
    case class FullName(
                         firstName: String,
                         surname: String
                       )

    case class CustomerModel(
                              customerId: String,
                              forename: String,
                              surname: String,
                              fullname: FullName
                            )

    println("Loading customer data...")
    val customerData = spark.read
      .option("header", "true")
      .csv("src/main/resources/customer_data.csv")
      .as[CustomerData]

    val customerModel = customerData
      .map(customer =>
        CustomerModel(
          customerId = customer.customerId,
          forename = customer.forename,
          surname = customer.surname,
          fullname = FullName(
            firstName = customer.forename,
            surname = customer.surname)
        )
      )

    println("Processing result:")
    customerModel.show(20, truncate = false)

    println("Saving result...")
    customerModel.write
      .mode("overwrite")
      .parquet("src/main/resources/customerModel.parquet")
      
    println("Done!")
    spark.stop()
  }
} 