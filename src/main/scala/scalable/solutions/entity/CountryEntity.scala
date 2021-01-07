package scalable.solutions.entity

import doobie.ConnectionIO
import doobie.implicits._

case class Country(code: String, name: String, population: Long)

class CountryEntity {
  def find(n: String): ConnectionIO[Option[Country]] =
    sql"SELECT code, name, population FROM country WHERE name = $n".query[Country].option

  def all(): ConnectionIO[List[Country]] =
    sql"SELECT code, name, population FROM country".query[Country].to[List]

  def insert(country: Country): ConnectionIO[Int] =
    sql"INSERT INTO country (code, name, population) VALUES (${country.code}, ${country.name}, ${country.population})".update.run

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

object CountryEntity {
  def apply() = new CountryEntity()
}