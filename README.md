# library

CRUD приложение библиотеки.

Использован фреймворк Spring MVC 4.
В качестве DAO используется наивная обертка над Postgresql JDBC.

Перед запуском задайте sql окружение:
'''
create shema if not exists library;
Create table if not exists library.books (id serial primary key, title text, year integer);
Create table if not exists library.authors (id serial primary key, name text);
Create table if not exists library.book_to_author (book_id INTEGER NOT NULL, author_id INTEGER NOT NULL,
        CONSTRAINT "FK_book_id" FOREIGN KEY ("book_id") REFERENCES library.books ("id") ON DELETE CASCADE,
        CONSTRAINT "FK_author_id" FOREIGN KEY ("author_id") REFERENCES library.authors ("id") );
CREATE UNIQUE INDEX if not exists "UI_book_to_author"  ON library.book_to_author USING btree ("book_id", "author_id");
CREATE UNIQUE INDEX if not exists "UI_books"  ON library.books USING btree (title, year);
CREATE UNIQUE INDEX if not exists "UI_authors"  ON library.authors USING btree (name);
'''

TODO:
- Список авторов (делается аналогично списку книг) и детали автора
- Добавить возможность изменить список авторов для данной ниги
- Полнотекстовый поиск по названиям книг и именам авторов
- Документирование кода
- логирование ошибок
