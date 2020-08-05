package jp.ne.hatena.intern.scalatra
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import org.slf4j.{Logger, LoggerFactory}

import scalaj.http.{Http, Token}
import scalaj.http.HttpConstants.urlEncode
import org.scalatra._
import org.scalatra.auth._

case class HatenaOAuthUserName(id: String)

class HatenaOAuth extends ScalatraServlet with HatenaOAuthSupport {
  get("/login") {
    if (isAuthenticated) {
      // nop
    } else {
      scentry.authenticate()
    }
    Found("/")
  }
}

trait HatenaOAuthSupport extends ScentrySupport[HatenaOAuthUserName] {
  self: ScalatraBase =>

  override protected def fromSession: PartialFunction[String, HatenaOAuthUserName] = {
    case id: String => HatenaOAuthUserName(id)
  }

  override protected def toSession: PartialFunction[HatenaOAuthUserName, String] = {
    case HatenaOAuthUserName(id) => id
  }

  override protected def scentryConfig: ScentryConfiguration =
    new ScentryConfig {}.asInstanceOf[ScentryConfiguration]

  override protected def configureScentry(): Unit = {
    scentry.unauthenticated {
      scentry.strategies("HatenaOAuth").unauthenticated()
    }
  }

  override protected def registerAuthStrategies(): Unit = {
    scentry.register("HatenaOAuth", app => new HatenaOAuthStrategy(app))
  }
}

class HatenaOAuthStrategy(protected val app: ScalatraBase) extends ScentryStrategy[HatenaOAuthUserName] {
  val consumerKey: String = Option(System.getenv("HATENA_OAUTH_CONSUMER_KEY"))
    .getOrElse(throw new IllegalStateException("No consumerkey is set"))
  val consumerSecret: String =
    Option(System.getenv("HATENA_OAUTH_CONSUMER_SECRET"))
      .getOrElse(throw new IllegalStateException("No consumersecrete is set"))

  val oauthConsumer = Token(consumerKey, consumerSecret)

  val logger: Logger = LoggerFactory.getLogger("hatenaoauth")

  private[this] val REQUEST_TOKEN_SESSION_KEY = "hatena_request_token"

  override def authenticate()(implicit request: HttpServletRequest,
                              response: HttpServletResponse): Option[HatenaOAuthUserName] = {
    app.params.get("oauth_verifier") map { verifier =>
      val requestToken =
        app.session.getAttribute(REQUEST_TOKEN_SESSION_KEY).asInstanceOf[Token]
      app.session.removeAttribute(REQUEST_TOKEN_SESSION_KEY)

      val accessToken = Http("https://www.hatena.com/oauth/token").postForm
        .oauth(oauthConsumer, requestToken, verifier)
        .asToken
        .body
      logger.debug("accessToken: {}", accessToken)

      // http://developer.hatena.ne.jp/ja/documents/nano/apis/oauth#my
      val meJson = Http("http://n.hatena.com/applications/my.json")
        .oauth(oauthConsumer, accessToken)
        .asString
        .body

      import org.json4s
      import org.json4s.jackson.JsonMethods._
      implicit val format = json4s.DefaultFormats

      val json = parse(meJson)
      (json \ "url_name").extractOpt[String].map { name =>
        HatenaOAuthUserName(name)
      } getOrElse {
        throw new IllegalStateException("Requesting to API is failed.")
      }
    }
  }

  override def unauthenticated()(implicit request: HttpServletRequest, response: HttpServletResponse) {
    val requestToken = Http("https://www.hatena.com/oauth/initiate")
      .postForm(Seq("oauth_callback" -> app.fullUrl("/login"), "scope" -> "read_public"))
      .oauth(oauthConsumer)
      .asToken
      .body
    app.session.setAttribute(REQUEST_TOKEN_SESSION_KEY, requestToken)
    app.redirect("https://www.hatena.com/oauth/authorize?oauth_token=" + urlEncode(requestToken.key, "utf-8"))
  }
}
