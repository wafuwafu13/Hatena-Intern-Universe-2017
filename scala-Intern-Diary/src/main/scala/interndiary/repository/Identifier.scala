package interndiary.repository

import com.github.takezoe.slick.blocking.BlockingMySQLDriver.blockingApi._

object Identifier {
  implicit private def context2session(implicit ctx: Context) = ctx.session

  def generate(implicit ctx: Context): Long =
    sql"SELECT UUID_SHORT() as ID".as[Long].list.head
}
