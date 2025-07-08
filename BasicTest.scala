object BasicTest {
  def main(args: Array[String]): Unit = {
    println("🔧 Testing basic Scala functionality...")
    
    // Test 1: Basic operations
    val numbers = (1 to 10).toList
    val evenNumbers = numbers.filter(_ % 2 == 0)
    val sum = evenNumbers.sum
    
    println(s"✅ Numbers 1-10: $numbers")
    println(s"✅ Even numbers: $evenNumbers")
    println(s"✅ Sum of even numbers: $sum")
    
    // Test 2: Case classes
    case class Person(name: String, age: Int)
    val people = List(
      Person("Alice", 25),
      Person("Bob", 30),
      Person("Charlie", 35)
    )
    
    val adults = people.filter(_.age >= 30)
    println(s"✅ Adults (30+): ${adults.map(_.name)}")
    
    // Test 3: Collections operations
    val wordCounts = List("scala", "test", "scala", "code")
      .groupBy(identity)
      .mapValues(_.length)
    
    println(s"✅ Word counts: $wordCounts")
    
    println("🎉 Basic Scala test completed successfully!")
  }
} 