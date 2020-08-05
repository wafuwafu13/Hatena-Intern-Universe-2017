package internbookmark.repository

import internbookmark.model.{Bookmark, Entry, User}
import org.joda.time.LocalDateTime
import slick.jdbc.GetResult
import com.github.takezoe.slick.blocking.BlockingMySQLDriver.blockingApi._
import com.github.tototoshi.slick.MySQLJodaSupport._

object Bookmarks {
  private case class BookmarkRow(id: Long,
                                 userId: Long,
                                 entryId: Long,
                                 comment: String,
                                 createdAt: LocalDateTime,
                                 updatedAt: LocalDateTime) {
    def toBookmark(user: User, entry: Entry): Bookmark =
      Bookmark(id, user, entry, comment, createdAt, updatedAt)
  }

  private implicit val getBookmarkRowResult = GetResult(r => BookmarkRow(r.<<, r.<<, r.<<, r.<<, r.<<, r.<<))

  private implicit def context2session(implicit ctx: Context) = ctx.session

  def createOrUpdate(user: User, entry: Entry, comment: String)(implicit ctx: Context): Unit =
    findByEntry(user, entry) match {
      case Some(bookmarkRow) =>
        val updatedAt = new LocalDateTime()
        sqlu"""
         UPDATE bookmark
           SET
             comment = $comment,
             updated_at = $updatedAt
           WHERE
             id = ${bookmarkRow.id}
       """.execute
        ()

      case None =>
        val id = Identifier.generate
        val bookmark: Bookmark = Bookmark(id, user, entry, comment, new LocalDateTime(), new LocalDateTime())
        sqlu"""
         INSERT INTO bookmark
           (id, user_id, entry_id, comment, created_at, updated_at)
           VALUES
           (
             ${bookmark.id},
             ${bookmark.user.id},
             ${bookmark.entry.id},
             ${bookmark.comment},
             ${bookmark.createdAt},
             ${bookmark.updatedAt}
           )
       """.execute
        ()
    }

  def delete(bookmark: Bookmark)(implicit ctx: Context): Unit = {
    sqlu"""DELETE FROM bookmark WHERE id = ${bookmark.id} """.execute
  }

  def findByEntry(user: User, entry: Entry)(implicit ctx: Context): Option[Bookmark] = {
    sql"""
      SELECT * FROM bookmark
        WHERE user_id = ${user.id} AND entry_id = ${entry.id} LIMIT 1
    """.as[BookmarkRow].list.headOption.map(_.toBookmark(user, entry))
  }

  def find(bookmarkId: Long)(implicit ctx: Context): Option[Bookmark] =
    for {
      bookmarkRow <- sql"""
      SELECT * FROM bookmark
        WHERE id = $bookmarkId LIMIT 1
    """.as[BookmarkRow].list.headOption
      entry <- Entries.find(bookmarkRow.entryId)
      user <- Users.find(bookmarkRow.userId)
    } yield bookmarkRow.toBookmark(user, entry)

  private def loadBookmarks(user: User, bookmarkRows: Seq[BookmarkRow])(implicit ctx: Context): Seq[Bookmark] = {
    val entries = Entries.searchByIds(bookmarkRows.map(_.entryId))
    val entryById = entries.map(e => e.id -> e).toMap
    bookmarkRows.flatMap { row =>
      entryById.get(row.entryId).map(row.toBookmark(user, _))
    }
  }

  def listAll(user: User)(implicit ctx: Context): Seq[Bookmark] = {
    val rows = sql"""
        SELECT * FROM bookmark
          WHERE user_id = ${user.id} ORDER BY updated_at DESC
      """.as[BookmarkRow].list
    loadBookmarks(user, rows)
  }

  def listPaged(user: User, page: Int, limit: Int)(implicit ctx: Context): Seq[Bookmark] = {
    require(page > 0)
    require(limit > 0)

    val offset = (page - 1) * limit

    val rows = sql"""
      SELECT * FROM bookmark
        WHERE user_id = ${user.id} ORDER BY updated_at DESC LIMIT $offset,$limit
    """.as[BookmarkRow].list
    loadBookmarks(user, rows)
  }
}
