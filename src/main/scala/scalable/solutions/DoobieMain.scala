package scalable.solutions

import cats.effect.{ContextShift, IO}
import cats.implicits._
import com.typesafe.config.ConfigFactory
import doobie._
import doobie.implicits._
import scalable.solutions.entity.{City, CityEntity, Country, CountryEntity}

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
  val city = CityEntity()

  val result = (country.drop(),
    country.create(),
    country.insert(Country("ro", "Romania", 19410000)),
    country.insert(Country("de", "Germany", 83020000)),
    country.insert(Country("uk", "Great Britain", 66650000)),
    country.insert(Country("fr", "France", 66990000))
    ).mapN(_ + _ + _ + _ + _ + _)
    .transact(xa)
    .unsafeRunSync

  val result1 = (city.drop(),
    city.create(),
    city.insert(City(1, "ro", "Sibiu", 140000)),
    city.insert(City(2, "ro", "Cluj", 300000)),
    city.insert(City(3, "ro", "Timisoara", 200000)),
    city.insert(City(4, "de", "Munchen", 3200000)),
    city.insert(City(5, "fr", "Paris", 3200000)),
    city.insert(City(6, "uk", "London", 3200000))
    ).mapN(_ + _ + _ + _ + _ + _ + _ + _)
    .transact(xa)
    .unsafeRunSync

  println(s"1. Create tables country and city and insert records" +
    s"\n\tcountry => $result\n\tcity => $result1")

  println("2. Retrieve and print all country records: ")
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

  println("4. Delete records for code = uk")
  print("\tcity deleted => ")
  println(city.delete(6)
    .transact(xa)
    .unsafeRunSync)
  print("\tcountry deleted => ")
  println(country.delete("uk")
    .transact(xa)
    .unsafeRunSync)

  println("5. Retrieve and print all city records: ")
  city.all()
    .transact(xa)
    .unsafeRunSync
    .take(5)
    .foreach(r => println(s"\t=> $r"))
}
