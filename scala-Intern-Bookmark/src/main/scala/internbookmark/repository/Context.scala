package internbookmark.repository

import com.github.takezoe.slick.blocking.BlockingMySQLDriver.blockingApi._
import slick.jdbc.JdbcBackend

object Context {
  private var db: JdbcBackend.DatabaseDef = _

  def setup(configName: String) {
    db = JdbcBackend.Database
      .forConfig(configName)
      .asInstanceOf[JdbcBackend.DatabaseDef]
  }

  def createContext(): Context =
    new Context(session = db.createSession())

  def releaseContext(ctx: Context): Unit =
    ctx.session.close()

  def destroy() {
    if (db != null) db.close()
  }
}

case class Context(session: JdbcBackend#SessionDef) {
  def withTransaction[T](f: => T): T = session.withTransaction(f)
}
