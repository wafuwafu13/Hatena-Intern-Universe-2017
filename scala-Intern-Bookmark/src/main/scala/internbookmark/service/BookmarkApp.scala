package internbookmark.service

import internbookmark.model.{Bookmark, User}
import internbookmark.repository
import internbookmark.repository.Entries

class BookmarkAppImpl(val currentUserName: String) extends BookmarkApp {
  override def titleExtractor = TitleExtractorImpl
}

trait BookmarkApp {
  def currentUser(implicit ctx: Context): User = {
    repository.Users.findOrCreateByName(currentUserName)
  }

  val currentUserName: String
  def titleExtractor: TitleExtractor

  val entriesRepository: repository.Entries = repository.Entries

  def find(bookmarkId: Long)(implicit ctx: Context): Option[Bookmark] = {
    repository.Bookmarks.find(bookmarkId)
  }

  def add(url: String, comment: String)(implicit ctx: Context): Either[Error, Bookmark] = {
    val entry = entriesRepository.findOrCreateByUrl(url, titleExtractor.extract(url))
    val user = currentUser
    repository.Bookmarks.createOrUpdate(user, entry, comment)
    repository.Bookmarks.findByEntry(user, entry).toRight(BookmarkNotFoundError)
  }

  def edit(bookmarkId: Long, comment: String)(implicit ctx: Context): Either[Error, Bookmark] = {
    for {
      bookmark <- repository.Bookmarks
        .find(bookmarkId)
        .toRight(BookmarkNotFoundError)
        .right
      _ <- Right(
        repository.Bookmarks
          .createOrUpdate(currentUser, bookmark.entry, comment)
      ).right
      editedBookmark <- repository.Bookmarks
        .find(bookmarkId)
        .toRight(BookmarkNotFoundError)
        .right
    } yield editedBookmark
  }

  def list()(implicit ctx: Context): List[Bookmark] =
    repository.Bookmarks.listAll(currentUser).toList

  def listPaged(page: Int, limit: Int)(implicit ctx: Context): List[Bookmark] = {
    require(page > 0)
    require(limit > 0)
    repository.Bookmarks.listPaged(currentUser, page, limit).toList
  }

  def delete(bookmarkId: Long)(implicit ctx: Context): Either[Error, Unit] =
    ctx.withTransaction {
      val a = for {
        bookmark <- repository.Bookmarks
          .find(bookmarkId)
          .toRight(BookmarkNotFoundError)
          .right
      } yield repository.Bookmarks.delete(bookmark)
      a
    }

  def deleteByUrl(url: String)(implicit ctx: Context): Either[Error, Unit] =
    ctx.withTransaction {
      for {
        entry <- repository.Entries
          .findByUrl(url)
          .toRight(EntryNotFoundError)
          .right
        bookmark <- repository.Bookmarks
          .findByEntry(currentUser, entry)
          .toRight(BookmarkNotFoundError)
          .right
      } yield repository.Bookmarks.delete(bookmark)
    }

  def createTables()(implicit ctx: Context): Unit = repository.Schema.create
}
