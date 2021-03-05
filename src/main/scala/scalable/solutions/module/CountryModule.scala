package scalable.solutions.module

import cats.effect.IO
import doobie.ConnectionIO
import doobie.implicits._
import doobie.util.fragment.Fragment

trait CountryModule {
  val country: IO[CountryModule.Service.type] = IO.pure(CountryModule.Service)
}

object CountryModule extends CountryModule {

  case class Country(code: String, name: String, population: Long)

  trait Service[F[_]] {
    def find(n: String): F[Option[Country]]

    def all(): F[List[Country]]

    def insert(country: Country): F[Int]

    def update(country: Country): F[Int]

    def delete(code: String): F[Int]

    def drop(): F[Int]

    def create(): F[Int]
  }

  object Service extends CountryModule.Service[ConnectionIO] {
    val columns: Seq[String] = Seq(
      "code",
      "name",
      "population"
    )

    val columnsC: String = columns.mkString(", ")
    val columnsF: Fragment = Fragment.const(columnsC)

    def find(n: String): ConnectionIO[Option[Country]] =
      (fr"SELECT " ++ columnsF ++ fr" FROM country WHERE name = $n")
        .query[Country]
        .option

    def all(): ConnectionIO[List[Country]] =
      (fr"SELECT " ++ columnsF ++ fr" FROM country").query[Country].to[List]

    def insert(country: Country): ConnectionIO[Int] =
      (fr"INSERT INTO country (" ++ columnsF ++ fr") VALUES (${country.code}, ${country.name}, ${country.population})").update.run

    def update(country: Country): ConnectionIO[Int] =
      sql"UPDATE country SET name = ${country.name}, population = ${country.population} WHERE code = ${country.code}".update.run

    def delete(code: String): ConnectionIO[Int] =
      sql"DELETE FROM country WHERE code = $code".update.run

    def drop(): ConnectionIO[Int] = sql"DROP TABLE IF EXISTS country".update.run

    def create(): ConnectionIO[Int] =
      sql"""CREATE TABLE country (
        code       VARCHAR PRIMARY KEY,
        name       VARCHAR NOT NULL UNIQUE,
        population BIGINT)""".update.run
  }

}
