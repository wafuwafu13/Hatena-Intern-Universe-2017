package internbookmark.repository

import com.github.takezoe.slick.blocking.BlockingMySQLDriver.blockingApi._

object Schema {
  private implicit def context2session(implicit ctx: Context) = ctx.session

  def create(implicit ctx: Context): Unit = {
    val schema = scala.io.Source.fromFile("./db/schema.sql").mkString.trim
    schema.split(";").foreach { statement =>
      val query = sqlu"""#$statement"""
      println(query.statements.head)
      query.execute
    }
  }
}
