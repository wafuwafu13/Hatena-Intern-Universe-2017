package internbookmark.repository

import internbookmark.model.Entry
import org.joda.time.LocalDateTime
import slick.jdbc.GetResult
// tarao.slickjdbc.interpolation.SQLInterpolation とバッティングさせないため actionBasedSQLInterpolation は隠す
import com.github.takezoe.slick.blocking.BlockingMySQLDriver.blockingApi.{actionBasedSQLInterpolation => _, _}
import com.github.tarao.slickjdbc.interpolation.SQLInterpolation._
import com.github.tarao.slickjdbc.interpolation.ListParameter._
import com.github.tototoshi.slick.MySQLJodaSupport._
import com.github.tarao.slickjdbc.util.NonEmpty

trait Entries {
  private type EntryRow = Entry
  private val EntryRow = Entry
  private implicit val getUserRowResult = GetResult(r => EntryRow(r.<<, r.<<, r.<<, r.<<, r.<<))

  private implicit def context2session(implicit ctx: Context) = ctx.session

  def find(entryId: Long)(implicit ctx: Context): Option[Entry] =
    sql"SELECT * FROM entry WHERE id = $entryId LIMIT 1"
      .as[EntryRow]
      .list
      .headOption

  def findByUrl(url: String)(implicit ctx: Context): Option[Entry] =
    sql"SELECT * FROM entry WHERE url = $url LIMIT 1"
      .as[EntryRow]
      .list
      .headOption

  def findOrCreateByUrl(url: String, title: => Option[String])(implicit ctx: Context): Entry =
    findByUrl(url).getOrElse(create(url, title))

  def create(url: String, title: Option[String])(implicit ctx: Context): Entry = {
    val id = Identifier.generate
    val entry = Entry(id, url, title.getOrElse(""), new LocalDateTime(), new LocalDateTime())
    sqlu"""
      INSERT INTO entry
        (id, url, title, created_at, updated_at)
        VALUES
        (
          ${entry.id},
          ${entry.url},
          ${entry.title},
          ${entry.createdAt},
          ${entry.updatedAt}
        )
    """.execute
    entry
  }

  def searchByIds(ids: Seq[Long])(implicit ctx: Context): Seq[Entry] = {
    NonEmpty.fromTraversable(ids).fold(Seq(): Seq[EntryRow]) { nel =>
      sql"SELECT * FROM entry WHERE id IN $nel".as[EntryRow].list
    }
  }
}

object Entries extends Entries
