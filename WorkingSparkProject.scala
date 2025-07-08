//> using scala "2.13.12"
//> using dep "org.apache.spark::spark-core:3.4.0"
//> using dep "org.apache.spark::spark-sql:3.4.0"

import org.apache.spark.sql.{SparkSession, DataFrame, Dataset}
import org.apache.spark.sql.functions._

object WorkingSparkProject {
  // Define data schemas
  case class CustomerData(
    customerId: String,
    forename: String,
    surname: String
  )

  case class AccountData(
    customerId: String,
    accountId: String,
    balance: Long
  )

  case class CustomerAccount(
    customerId: String,
    forename: String,
    surname: String,
    accounts: Seq[AccountData]
  )

  def main(args: Array[String]): Unit = {
    println("🚀 Starting Quantexa Spark Project!")
    
    // Create SparkSession
    val spark = SparkSession.builder()
      .appName("Quantexa Assessment")
      .master("local[*]")
      .config("spark.sql.adaptive.enabled", "true")
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")
    import spark.implicits._

    try {

      println("📊 Processing customer and account data...")

      // Create test data (as in resources)
      val customerData = spark.createDataset(Seq(
        CustomerData("IND00001", "Charlize", "Theron"),
        CustomerData("IND00002", "Keanu", "Reeves"),
        CustomerData("IND00003", "Laurence", "Fishburne"),
        CustomerData("IND00004", "Carrie-Anne", "Moss"),
        CustomerData("IND00005", "Hugo", "Weaving")
      ))

      val accountData = spark.createDataset(Seq(
        AccountData("IND00001", "ACC0001", 1500),
        AccountData("IND00001", "ACC0002", 15600),
        AccountData("IND00002", "ACC0003", 1900),
        AccountData("IND00002", "ACC0004", 1700),
        AccountData("IND00003", "ACC0005", 1100),
        AccountData("IND00004", "ACC0006", 6800),
        AccountData("IND00004", "ACC0007", 1300),
        AccountData("IND00005", "ACC0008", 9200)
      ))

      println("👥 Top 20 rows of customer data:")
      customerData.show()

      println("💰 Top 20 rows of account data:")
      accountData.show()

      // Join data as in AccountAssessment
      val joinedData = customerData
        .join(accountData, Seq("customerId"), "left")
        .groupBy($"customerId", $"forename", $"surname")
        .agg(collect_list(
          struct($"customerId", $"accountId", $"balance")
        ).as("accounts"))

      println("🔗 Joined data (customers with their accounts):")
      joinedData.show(truncate = false)

      // Convert to CustomerAccount
      val customerAccounts: Dataset[CustomerAccount] = joinedData.as[CustomerAccount]

      println("📈 Analysis:")
      val analysis = customerAccounts.map { ca =>
        val totalBalance = ca.accounts.map(_.balance).sum
        val accountCount = ca.accounts.length
        (ca.customerId, ca.forename, ca.surname, accountCount, totalBalance)
      }.toDF("customerId", "forename", "surname", "accountCount", "totalBalance")

      analysis.orderBy($"totalBalance".desc).show()

      println("💡 Statistics:")
      analysis.agg(
        sum("totalBalance").as("total_balance"),
        avg("totalBalance").as("average_balance"),
        max("accountCount").as("max_accounts")
      ).show()

      println("✅ Processing completed successfully!")

    } catch {
      case e: Exception =>
        println(s"❌ Error: ${e.getMessage}")
        e.printStackTrace()
    } finally {
      spark.stop()
    }
  }
} 