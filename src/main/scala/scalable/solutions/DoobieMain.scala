package scalable.solutions

import cats.effect.{ContextShift, IO}
import cats.implicits._
import com.typesafe.config.ConfigFactory
import doobie._
import doobie.implicits._
import scalable.solutions.entity.{Country, CountryEntity}

import scala.concurrent.ExecutionContext

object DoobieMain extends App {

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  val config = ConfigFactory.load()
  val cfg = config.getConfig("db")

  val xa = Transactor.fromDriverManager[IO](
    driver = cfg.getString("driver"),
    url = cfg.getString("url"),
    user = cfg.getString("username"),
    pass = cfg.getString("password")
  )

  val country = CountryEntity()

  val result = (country.drop(),
    country.create(),
    country.insert(Country("ro", "Romania", 19410000)),
    country.insert(Country("de", "Germany", 83020000)),
    country.insert(Country("uk", "Great Britain", 66650000)),
    country.insert(Country("fr", "France", 66990000))
    ).mapN(_ + _ + _ + _ + _ + _)
    .transact(xa)
    .unsafeRunSync

  println(s"1. Create table country and insert records\n\tinserted => $result")

  println("2. Retrieve and print all records: ")
  country.all()
    .transact(xa)
    .unsafeRunSync
    .take(5)
    .foreach(r => println(s"\t=> $r"))

  println("3. Lookup for name = 'France'")
  print("\t=> ")
  println(country.find("France")
    .transact(xa)
    .unsafeRunSync)

  println("4. Delete record for code = ro")
  print("\tdeleted => ")
  println(country.delete("ro")
    .transact(xa)
    .unsafeRunSync)
}
