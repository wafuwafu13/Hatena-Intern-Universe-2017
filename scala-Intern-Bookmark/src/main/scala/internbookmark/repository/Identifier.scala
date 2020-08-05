package internbookmark.repository

import com.github.takezoe.slick.blocking.BlockingMySQLDriver.blockingApi._

object Identifier {
  def generate(implicit session: Session): Long = {
    sql"SELECT UUID_SHORT() as ID".as[Long].first
  }
}
