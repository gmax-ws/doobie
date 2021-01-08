package scalable.solutions.entity

import doobie.ConnectionIO
import doobie.implicits._

case class City(id: Long, country: String, name: String, population: Long)

class CityEntity {
  def find(name: String): ConnectionIO[Option[City]] =
    sql"SELECT id, country, name, population FROM city WHERE name = $name".query[City].option

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

object CityEntity {
  def apply() = new CityEntity()
}