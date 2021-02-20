package scalable.solutions

import cats.effect.{Async, ContextShift}
import com.typesafe.config.Config
import doobie.Transactor
import doobie.util.transactor.Transactor.Aux

object Db {

  def apply[F[_]](config: Config)(implicit cs: ContextShift[F], as: Async[F]): Aux[F, Unit] = {
    val cfg = config.getConfig("db")

    Transactor.fromDriverManager[F](
      driver = cfg.getString("driver"),
      url = cfg.getString("url"),
      user = cfg.getString("username"),
      pass = cfg.getString("password")
    )
  }
}
