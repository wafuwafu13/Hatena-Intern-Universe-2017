package internbookmark.service

import scalaj.http.Http

object TitleExtractorImpl extends TitleExtractor {
  protected def fetch(url: String): Option[String] = {
    val response = Http(url).asString
    if (response.isSuccess) {
      Some(response.body)
    } else {
      None
    }
  }
}
