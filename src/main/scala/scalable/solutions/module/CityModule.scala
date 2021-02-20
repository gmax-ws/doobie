package scalable.solutions.module

import doobie.ConnectionIO
import doobie.implicits._

trait CityModule {
  val city: CityModule.Service.type = CityModule.Service
}

object CityModule extends CityModule {

  case class City(id: Long, country: String, name: String, population: Long)

  trait Service[F[_]] {
    def find(name: String): F[Option[City]]

    def all(): F[List[City]]

    def insert(city: City): F[Int]

    def update(city: City): F[Int]

    def delete(id: Long): F[Int]

    def drop(): F[Int]

    def create(): F[Int]

    def fk(): F[Int]
  }

  object Service extends CityModule.Service[ConnectionIO] {
    def find(name: String): ConnectionIO[Option[City]] =
      sql"SELECT id, country, name, population FROM city WHERE name = $name"
        .query[City]
        .option

    def all(): ConnectionIO[List[City]] =
      sql"SELECT id, country, name, population FROM city".query[City].to[List]

    def insert(city: City): ConnectionIO[Int] =
      sql"INSERT INTO city (id, country, name, population) VALUES (${city.id}, ${city.country}, ${city.name}, ${city.population})".update.run

    def update(city: City): ConnectionIO[Int] =
      sql"UPDATE city SET name = ${city.name}, population = ${city.population} WHERE id = ${city.id}".update.run

    def delete(id: Long): ConnectionIO[Int] =
      sql"DELETE FROM city WHERE id = $id".update.run

    def drop(): ConnectionIO[Int] = sql"DROP TABLE IF EXISTS city".update.run

    def create(): ConnectionIO[Int] =
      sql"""CREATE TABLE city (
        id         INT PRIMARY KEY,
        country    VARCHAR NOT NULL,
        name       VARCHAR NOT NULL,
        population BIGINT,
        CONSTRAINT fk_city_country FOREIGN KEY (country) REFERENCES country(code))""".update.run

    def fk(): ConnectionIO[Int] =
      sql"ALTER TABLE city ADD FOREIGN KEY (country) REFERENCES country(code)".update.run
  }
}
