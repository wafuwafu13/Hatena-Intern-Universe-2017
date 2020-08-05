package interndiary.cli

import scala.sys.process

object DiaryCli {
  def main(args: Array[String]): Unit = {
    val exitStatus = run(args)
    sys.exit(exitStatus)
  }

  def run(args: Array[String]): Int = {
    // TODO: implement
    args.toList match {
      case "init_db" :: _ =>
        initDB()

      case _ =>
        help()
    }
  }

  def help(): Int = {
    process.stderr.println("""
        | usage:
        |   run add url [comment]
        |   run list
        |   run delete url
      """.stripMargin)
    1
  }

  def initDB(): Int = {
    import interndiary.repository._
    Context.setup("db.default")

    implicit val ctx = Context.createContext()
    try {
      Schema.create(ctx)
      0
    } catch {
      case _: Throwable => 1
    } finally {
      Context.destroy()
    }
  }
}
