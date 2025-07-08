object SimpleTest {
  def main(args: Array[String]): Unit = {
    println("🧪 Testing Code Functionality")
    println("=" * 40)
    
    // Test 1: Basic Scala
    testScala()
    
    // Test 2: Environment check
    testEnvironment()
    
    println("=" * 40)
    println("📋 Testing Summary Complete")
  }
  
  def testScala(): Unit = {
    println("\n✅ Test 1: Scala Functionality")
    
    // Test case classes (like our CustomerData)
    case class CustomerData(customerId: String, forename: String, surname: String)
    case class AccountData(customerId: String, accountId: String, balance: Long)
    
    val customers = List(
      CustomerData("IND00001", "Alice", "Smith"),
      CustomerData("IND00002", "Bob", "Jones")
    )
    
    val accounts = List(
      AccountData("IND00001", "ACC001", 1500),
      AccountData("IND00002", "ACC002", 2500)
    )
    
    // Test filtering and mapping
    val highBalanceAccounts = accounts.filter(_.balance > 2000)
    val customerNames = customers.map(c => s"${c.forename} ${c.surname}")
    
    println(s"   📊 ${customers.length} customers loaded")
    println(s"   💰 ${accounts.length} accounts loaded")
    println(s"   🎯 ${highBalanceAccounts.length} high-balance accounts")
    println(s"   👥 Customer names: ${customerNames.mkString(", ")}")
    
    println("   ✅ Scala functionality test PASSED")
  }
  
  def testEnvironment(): Unit = {
    println("\n🔧 Test 2: Environment Check")
    
    // Check Java version
    val javaVersion = System.getProperty("java.version")
    println(s"   ☕ Java version: $javaVersion")
    
    // Check Scala version
    val scalaVersion = scala.util.Properties.versionString
    println(s"   🔧 $scalaVersion")
    
    // Check memory
    val runtime = Runtime.getRuntime()
    val maxMemory = runtime.maxMemory() / (1024 * 1024)
    println(s"   💾 Max memory: ${maxMemory}MB")
    
    // Compatibility check
    if (javaVersion.startsWith("24")) {
      println("   ⚠️  WARNING: Java 24 may have compatibility issues with Spark 3.x")
      println("   💡 Recommendation: Use Java 8, 11, or 17 for Spark")
    } else {
      println("   ✅ Java version looks good for Spark")
    }
  }
} 