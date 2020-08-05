package internbookmark.helper

import org.scalatest.{BeforeAndAfterAll, FunSpecLike}
import internbookmark.repository.Context

trait SetupDB extends BeforeAndAfterAll { self: FunSpecLike =>
  implicit lazy val ctx = Context.createContext
  override def beforeAll(): Unit = {
    Context.setup("db.default")
    super.beforeAll()
  }
  override def afterAll(): Unit = {
    Context.destroy()
    super.afterAll()
  }
}
