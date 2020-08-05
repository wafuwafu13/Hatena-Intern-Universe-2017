mysqladmin -uroot create intern_diary
mysqladmin -uroot create intern_diary_test

cat /app/db/schema.sql | mysql -uroot intern_diary
cat /app/db/schema.sql | mysql -uroot intern_diary_test
