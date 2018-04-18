/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.prikasp.library.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.prikasp.library.enteties.Author;
import org.prikasp.library.enteties.Book;

/**
 * Naiv implementation of DAO
 * 
 * Sturtup queries:
Create table library.books (id serial primary key, title text, year integer);
Create table library.authors (id serial primary key, name text);
Create table library.book_to_author (book_id INTEGER NOT NULL, author_id INTEGER NOT NULL,
        CONSTRAINT "FK_book_id" FOREIGN KEY ("book_id") REFERENCES library.books ("id") ON DELETE CASCADE,
        CONSTRAINT "FK_author_id" FOREIGN KEY ("author_id") REFERENCES library.authors ("id") );
CREATE UNIQUE INDEX "UI_book_to_author"  ON library.book_to_author USING btree ("book_id", "author_id");
CREATE UNIQUE INDEX "UI_books"  ON library.books USING btree (title, year);
CREATE UNIQUE INDEX "UI_authors"  ON library.authors USING btree (name);
 *
 * Need we add hash of authors to books table? May be two different book with 
 * same title, same year but different authors? Or two authors with same names 
 * but different books? Or both thins together? just ignore.
 * 
 * @author libre
 */
public class SimpleDataBaseWorker 
{
    Connection connection;
    PreparedStatement begin;
    PreparedStatement commit;
    PreparedStatement rollback;
    PreparedStatement bookLookup;
    PreparedStatement bookInsert;
    PreparedStatement authorLookup;
    PreparedStatement authorInsert;
    PreparedStatement linkInsert;
    PreparedStatement getAllBooks;
    PreparedStatement getBook;
    PreparedStatement removeBook;
    PreparedStatement updateBook;
    static SimpleDataBaseWorker instance;
    
    /**
     * This constructor should stay the only constructor in class.
     * 
     * Connect to database and Create all prepared statements.
     */
    private SimpleDataBaseWorker()
    {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://host:port/database","username", "password");
            begin = connection.prepareStatement("begin");
            commit = connection.prepareStatement("commit");
            rollback = connection.prepareStatement("rollback");
            bookLookup = connection.prepareStatement(
                    "select id from library.books where title = ? and year = ?");
            bookInsert = connection.prepareStatement(
                    "insert into library.books(title, year) values(?, ?) returning id");
            authorLookup = connection.prepareStatement(
                    "select id from library.authors where name = ?");
            authorInsert = connection.prepareStatement(
                    "insert into library.authors(name) values(?) returning id");
            removeBook = connection.prepareStatement(
                    "delete from library.books where id = ?");
            updateBook = connection.prepareStatement(
                    "update library.books set title = ?, year = ? where id = ?");
            getAllBooks = connection.prepareStatement(
                    "select tb.id, title, year, name from library.books as tb "
                            + "inner join library.book_to_author as tl on (tb.id = tl.book_id) "
                            + "inner join library.authors as ta on (ta.id = tl.author_id)");
            getBook = connection.prepareStatement(
                    "select title, year, name from library.books as tb "
                            + "inner join library.book_to_author as tl on (tb.id = tl.book_id) "
                            + "inner join library.authors as ta on (ta.id = tl.author_id)"
                            + "where tb.id = ?");
            linkInsert = connection.prepareStatement(
                    "insert into library.book_to_author(book_id, author_id)"
                            + " values(?, ?)");
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(SimpleDataBaseWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Get worker object. 
     * If it is the first call, new SimpleDataBaseWorker would be created
     * and stored for firther usage.
     * @return worker instance
     */
    public static synchronized SimpleDataBaseWorker getWorker()
    {
        if(instance == null)
            instance = new SimpleDataBaseWorker();
        return instance;
    }
    
    /**
     * Get iterable collection of books with their authors.
     * @return Collection of all books in the database
     */
    public Collection<Book> getAllBooks()
    {
        Map<Integer, Book> result = new HashMap<>();
        
        try 
        {
            ResultSet selectResults = getAllBooks.executeQuery();
            while(selectResults.next())
            {
                int bookId = selectResults.getInt(1);
                if(!result.containsKey(bookId))
                {
                    String bookTitle = selectResults.getString(2);
                    int bookYear = selectResults.getInt(3);
                    result.put(bookId, new Book(bookTitle, bookYear, bookId));
                }
                String authorName = selectResults.getString(4);
                result.get(bookId).getAuthors().add(new Author(authorName));
            }
        } 
        catch (SQLException ex) 
        {
            //TODO: log error
        }
        return result.values();
    }
    
    /**
     * Get certain book by its Id (or null in case of failture)
     * @param bookId id of book to be returned
     * @return Book with specified id or null if such not exists
     */
    public Book getBook(int bookId)
    {
        Book result = null;
        
        try 
        {
            getBook.setInt(1, bookId);
            ResultSet selectResults = getBook.executeQuery();
            while(selectResults.next())
            {
                if(result == null)
                {
                    String bookTitle = selectResults.getString(1);
                    int bookYear = selectResults.getInt(2);
                    result = new Book(bookTitle, bookYear, bookId);
                }
                String authorName = selectResults.getString(3);
                result.getAuthors().add(new Author(authorName));
            }
        } 
        catch (SQLException ex) 
        {
            //TODO: log error
        }
        return result;
    }
    
    /**
     * Delete book from database by its id.
     * 
     * Note! this method return true if query wass callen, even if such book was not exist.
     * @param bookId id of book to delete
     * @return true if query was callen, false in case of errors
     */
    public boolean removeBook(int bookId)
    {
        if(!beginTransaction())
            return false;
        if(bookId == -1)
        {
            rollbackTransaction();
            return false;
        }
        if(!deleteBook(bookId))
        {
            rollbackTransaction();
            return false;
        }
            
        return commitTransaction();
    }
    
    /**
     * Updates row in the table with id = book.getId()
     * @param book book with id and new values of fields Title and Year
     * @return true in case of success
     */
    public boolean updateBook(Book book)
    {
        if(!beginTransaction())
            return false;
        if(!updateBookImpl(book))
        {
            rollbackTransaction();
            return false;
        }
            
        return commitTransaction();
    }
    
    /**
     * Saves book to database
     * @param book book to save
     * @return true in case of success
     */
    public boolean saveBook(Book book)
    {
        if(!beginTransaction())
            return false;
        int bookId = checkIfBookExists(book);
        if(bookId == -1)
            bookId = addNewBook(book);
        if(bookId == -1)
        {
            rollbackTransaction();
            return false;
        }
        for (Author author : book.getAuthors()) 
        {
            int authorId = checkIfAuthorExists(author);
            if(authorId == -1)
                authorId = addNewAuthor(author);
            if(authorId == -1)
            {
                rollbackTransaction();
                return false;
            }
            if(!connectBookAndAuthor(bookId, authorId))
            {
                rollbackTransaction();
                return false;
            }
        }
        return commitTransaction();
    }
    
    private boolean beginTransaction()
    {
        try 
        {
            begin.execute();
        } 
        catch (SQLException ex) 
        {
            return false;
        }
        return true;
    }
    
    private boolean commitTransaction()
    {
        try 
        {
            commit.execute();
        } 
        catch (SQLException ex) 
        {
            return false;
        }
        return true;
    }
    
    private boolean rollbackTransaction()
    {
        try 
        {
            rollback.execute();
        } 
        catch (SQLException ex) 
        {
            return false;
        }
        return true;
    }
    
    private int addNewBook(Book book)
    {
        try 
        {
            bookInsert.setString(1, book.getTitle());
            bookInsert.setInt(2, book.getYear());
            ResultSet insertResults = bookInsert.executeQuery();
            if(insertResults.next())
                return insertResults.getInt(1);
            else
                return -1;
        } 
        catch (SQLException ex) 
        {
            return -1;
        }
    }
    
    private boolean deleteBook(int bookId)
    {
        try 
        {
            removeBook.setInt(1, bookId);
            removeBook.execute();
            return true;
        } 
        catch (SQLException ex) 
        {
            return false;
        }
    }
    
    private boolean updateBookImpl(Book book)
    {
        try 
        {
            updateBook.setString(1, book.getTitle());
            updateBook.setInt(2, book.getYear());
            updateBook.setInt(3, book.getId());
            updateBook.execute();
            return true;
        } 
        catch (SQLException ex) 
        {
            return false;
        }
    }
    
    private int checkIfBookExists(Book book)
    {
        try 
        {
            bookLookup.setString(1, book.getTitle());
            bookLookup.setInt(2, book.getYear());
            ResultSet lookupResults = bookLookup.executeQuery();
            if(lookupResults.next())
                return lookupResults.getInt(1);
            else
                return -1;
        } 
        catch (SQLException ex) 
        {
            return -1;
        }
    }
    
    private int addNewAuthor(Author author)
    {
        try 
        {
            authorInsert.setString(1, author.getName());
            ResultSet insertResults = authorInsert.executeQuery();
            if(insertResults.next())
                return insertResults.getInt(1);
            else
                return -1;
        } 
        catch (SQLException ex) 
        {
            return -1;
        }
    }
    
    private int checkIfAuthorExists(Author author)
    {
        try 
        {
            authorLookup.setString(1, author.getName());
            ResultSet lookupResults = authorLookup.executeQuery();
            if(lookupResults.next())
                return lookupResults.getInt(1);
            else
                return -1;
        } 
        catch (SQLException ex) 
        {
            return -1;
        }
    }
    
    private boolean connectBookAndAuthor(int bookId, int authorId)
    {
        try 
        {
            linkInsert.setInt(1, bookId);
            linkInsert.setInt(2, authorId);
            linkInsert.execute();
            return true;
        } 
        catch (SQLException ex) 
        {
            return false;
        }
    }
}
