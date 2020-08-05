package internbookmark.web

import jp.ne.hatena.intern.scalatra.HatenaOAuthSupport

import scala.util.control.Exception
import internbookmark.service.{BookmarkApp, BookmarkAppImpl}
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import org.scalatra._
import org.slf4j.{Logger, LoggerFactory}

case class ViewContext(userName: String, csrfKey: String, csrfToken: String)

class BookmarkWeb
    extends ScalatraServlet
    with BookmarkAPIWeb
    with HatenaOAuthSupport
    with AppContextSupport
    with CsrfTokenSupport {
  val logger: Logger = LoggerFactory.getLogger("bookmarkweb")

  override def handle(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    logger.info("method:{}\turi:{}", Seq(request.requestMethod, request.getRequestURI): _*)
    super.handle(request, response)
  }

  def viewContext(implicit request: HttpServletRequest): ViewContext =
    ViewContext(userName = currentUserName(), csrfKey = csrfKey, csrfToken = csrfToken)

  before() {
    if (isAuthenticated) {
      // nop
    } else {
      halt(Found("/auth/login"))
    }
  }

  def currentUserName()(implicit request: HttpServletRequest): String = {
    scentry.user.id
  }

  def createApp()(implicit request: HttpServletRequest): BookmarkApp =
    new BookmarkAppImpl(currentUserName())

  get("/") {
    Found("/bookmarks")
  }

  get("/bookmarks") {
    val app = createApp()
    internbookmark.html.list(viewContext, app.list())
  }

  get("/bookmarks/add") {
    internbookmark.html.add(viewContext)
  }

  post("/bookmarks/add") {
    val app = createApp()
    (for {
      url <- params.get("url").toRight(BadRequest()).right
      bookmark <- app
        .add(url, params.getOrElse("comment", ""))
        .left
        .map(_ => InternalServerError())
        .right
    } yield bookmark) match {
      case Right(_)          => Found(s"/bookmarks")
      case Left(errorResult) => errorResult
    }
  }

  get("/bookmarks/:id/edit") {
    val app = createApp()
    (for {
      rawId <- params.get("id").toRight(BadRequest()).right
      id <- Exception
        .catching(classOf[NumberFormatException])
        .either(rawId.toLong)
        .left
        .map(_ => BadRequest())
        .right
      bookmark <- app.find(id).toRight(NotFound()).right
    } yield bookmark) match {
      case Right(bookmark)   => internbookmark.html.edit(viewContext, bookmark)
      case Left(errorResult) => errorResult
    }
  }

  post("/bookmarks/:id/edit") {
    val app = createApp()
    (for {
      rawId <- params.get("id").toRight(BadRequest()).right
      id <- Exception
        .catching(classOf[NumberFormatException])
        .either(rawId.toLong)
        .left
        .map(_ => BadRequest())
        .right
      _ <- Right(logger.debug(id.toString)).right
      bookmark <- app
        .edit(id, params.getOrElse("comment", ""))
        .left
        .map(_ => NotFound())
        .right
    } yield bookmark) match {
      case Right(bookmark)   => Found(s"/bookmarks/${bookmark.id}/edit")
      case Left(errorResult) => errorResult
    }
  }

  get("/bookmarks/:id/delete") {
    val app = createApp()
    (for {
      rawId <- params.get("id").toRight(BadRequest()).right
      id <- Exception
        .catching(classOf[NumberFormatException])
        .either(rawId.toLong)
        .left
        .map(_ => BadRequest())
        .right
      bookmark <- app.find(id).toRight(NotFound()).right
    } yield bookmark) match {
      case Right(bookmark) =>
        internbookmark.html.delete(viewContext, bookmark)
      case Left(errorResult) => errorResult
    }
  }

  post("/bookmarks/:id/delete") {
    val app = createApp()
    (for {
      rawId <- params.get("id").toRight[ActionResult](BadRequest()).right
      id <- Exception
        .catching(classOf[NumberFormatException])
        .either(rawId.toLong)
        .left
        .map(_ => BadRequest())
        .right
      _ <- app.delete(id).left.map(_ => NotFound()).right
    } yield ()) match {
      case Right(()) =>
        Found(s"/bookmarks")
      case Left(errorResult) => errorResult
    }
  }
}
