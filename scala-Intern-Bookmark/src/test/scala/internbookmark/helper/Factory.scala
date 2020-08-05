package internbookmark.helper

import org.joda.time.LocalDateTime
import internbookmark.model._
import internbookmark.repository
import internbookmark.repository.Context

import scala.util.Random

// tarao.slickjdbc.interpolation.SQLInterpolation とバッティングさせないため actionBasedSQLInterpolation は隠す
import com.github.takezoe.slick.blocking.BlockingMySQLDriver.blockingApi.{actionBasedSQLInterpolation => _, _}
import com.github.tarao.slickjdbc.interpolation.SQLInterpolation._
import com.github.tarao.slickjdbc.interpolation.CompoundParameter._
import com.github.tototoshi.slick.MySQLJodaSupport._

object Factory {
  private implicit def context2session(implicit ctx: Context) = ctx.session

  def createUser(name: String = Random.nextInt().toString)(implicit ctx: Context): User =
    repository.Users.createByName(name)

  def createEntry(url: String = "http://example.com?" + Random.nextInt().toString,
                  title: String = Random.nextInt().toString)(implicit ctx: Context): Entry =
    repository.Entries.create(url, Some(title))

  def createBookmark(optionalUser: Option[User] = None,
                     optionalEntry: Option[Entry] = None,
                     comment: String = "",
                     updatedAt: LocalDateTime = new LocalDateTime())(implicit ctx: Context): Bookmark = {
    val user = optionalUser.getOrElse(createUser())
    val entry = optionalEntry.getOrElse(createEntry())
    repository.Bookmarks.createOrUpdate(user, entry, comment)
    val bookmark = repository.Bookmarks.findByEntry(user, entry).get
    sqlu"""UPDATE bookmark SET updated_at = $updatedAt where id = ${bookmark.id}""".execute
    repository.Bookmarks.findByEntry(user, entry).get
  }
}
