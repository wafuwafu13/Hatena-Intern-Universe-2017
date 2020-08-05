mysqladmin -uroot create intern_bookmark
mysqladmin -uroot create intern_bookmark_test

cat /app/db/schema.sql | mysql -uroot intern_bookmark
cat /app/db/schema.sql | mysql -uroot intern_bookmark_test
