package com.quantexa.assessments.customerAddresses

import com.quantexa.assessments.accounts.AccountAssessment.{AccountData, CustomerAccountOutput, customerAccountOutputDS}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

/***
  * A problem we have at Quantexa is where an address is populated with one string of text. In order to use this information
  * in the Quantexa product, this field must be "parsed".
  *
  * The purpose of parsing is to extract information from an entry - for example, to extract a forename and surname from
  * a 'full_name' field. We will normally place the extracted fields in a case class which will sit as an entry in the
  * wider case class; parsing will populate this entry.
  *
  * The parser on an address might yield the following "before" and "after":
  *
  * +-----------------------------------------+-------+--------------------+-------+--------+
  * |Address                                  |number |road                |city   |country |
  * +-----------------------------------------+-------+--------------------+-------+--------+
  * |109 Borough High Street, London, England |null   |null                |null   |null    |
  * +-----------------------------------------+-------+--------------------+-------+--------+
  *
  * +-----------------------------------------+-------+--------------------+-------+--------+
  * |Address                                  |number |road                |city   |country |
  * +-----------------------------------------+-------+--------------------+-------+--------+
  * |109 Borough High Street, London, England |109    |Borough High Street |London |England |
  * +-----------------------------------------+-------+--------------------+-------+--------+
  *
  *
  * You have been given addressData. This has been read into a DataFrame for you and then converted into a
  * Dataset of the given raw case class.
  *
  * You have been provided with a basic address parser which must be applied to the CustomerDocument model.
  *

  * Example Answer Format:
  *
  * val customerDocument: Dataset[CustomerDocument] = ???
  * customerDocument.show(1000,truncate = false)
  *
  * +----------+-----------+-------+--------------------------------------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------+
  * |customerId|forename   |surname|accounts                                                            |address                                                                                                                               |
  * +----------+-----------+-------+--------------------------------------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------+
  * |IND0001   |Christopher|Black  |[]                                                                  |[[ADR360,IND0001,762, East 14th Street, New York, United States of America,762, East 14th Street, New York, United States of America]]|
  * |IND0002   |Madeleine  |Kerr   |[[IND0002,ACC0155,323], [IND0002,ACC0262,60]]                       |[[ADR139,IND0002,675, Khao San Road, Bangkok, Thailand,675, Khao San Road, Bangkok, Thailand]]                                        |
  * |IND0003   |Sarah      |Skinner|[[IND0003,ACC0235,631], [IND0003,ACC0486,400], [IND0003,ACC0540,53]]|[[ADR318,IND0003,973, Blue Jays Way, Toronto, Canada,973, Blue Jays Way, Toronto, Canada]]                                            |
  * | ...
  */

object CustomerAddress extends App {

  //Create a spark context, using a local master so Spark runs on the local machine
  val spark = SparkSession.builder().master("local[*]").appName("CustomerAddress").getOrCreate()

  //importing spark implicits allows functions such as dataframe.as[T]

  import spark.implicits._

  //Set logger level to Warn
  Logger.getRootLogger.setLevel(Level.WARN)

  case class AddressRawData(
                             addressId: String,
                             customerId: String,
                             address: String
                           )

  case class AddressData(
                          addressId: String,
                          customerId: String,
                          address: String,
                          number: Option[Int],
                          road: Option[String],
                          city: Option[String],
                          country: Option[String]
                        )

  //Expected Output Format
  case class CustomerDocument(
                               customerId: String,
                               forename: String,
                               surname: String,
                               //Accounts for this customer
                               accounts: Seq[AccountData],
                               //Addresses for this customer
                               address: Seq[AddressData]
                             )


  def addressParser(unparsedAddress: Seq[AddressData]): Seq[AddressData] = {
    unparsedAddress.map(address => {
      val split = address.address.split(", ")

      address.copy(
        number = Some(split(0).toInt),
        road = Some(split(1)),
        city = Some(split(2)),
        country = Some(split(3))
      )
    }
    )
  }


  val addressDF: DataFrame = spark.read.option("header", "true").csv("src/main/resources/address_data.csv")

//  val customerAccountDS = spark.read.parquet("src/main/resources/customerAccountOutputDS.parquet").as[CustomerAccountOutput]

  //END GIVEN CODE

  val addressRawDS: Dataset[AddressRawData] = addressDF.as[AddressRawData]

  // Map raw addresses to AddressData and parse
  val parsedAddresses: Dataset[AddressData] = addressRawDS
    .map(raw => AddressData(raw.addressId, raw.customerId, raw.address, None, None, None, None))
    .map(address => addressParser(Seq(address)).head)

  // Group parsed addresses by customer
  val addressesByCustomer: Dataset[(String, Seq[AddressData])] =
    parsedAddresses
      .groupByKey(_.customerId)
      .mapGroups { case (cid, addr) => cid -> addr.toSeq }

  val customerAccountDS =
    spark.read.parquet("src/main/resources/customerAccountOutputDS.parquet").as[CustomerAccountOutput]

  // Join customers to their addresses
  val customerDocument: Dataset[CustomerDocument] =
    customerAccountDS
      .joinWith(addressesByCustomer, customerAccountDS("customerId") === addressesByCustomer("_1"), "left_outer")
      .map { case (customer, optAddr) =>
        val addresses = Option(optAddr).map(_._2).getOrElse(Seq.empty[AddressData])
        CustomerDocument(customer.customerId, customer.forename, customer.surname, customer.accounts, addresses)
      }

  customerDocument.show(1000, truncate = false)
  customerDocument.write.mode("overwrite").parquet("src/main/resources/customerDocument.parquet")

}