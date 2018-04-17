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
        CONSTRAINT "FK_book_id" FOREIGN KEY ("book_id") REFERENCES library.books ("id"),
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
    static SimpleDataBaseWorker instance;
    
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
            getAllBooks = connection.prepareStatement(
                    "select tb.id, title, year, name from library.books as tb "
                            + "inner join library.book_to_author as tl on (tb.id = tl.book_id) "
                            + "inner join library.authors as ta on (ta.id = tl.author_id)");
            linkInsert = connection.prepareStatement(
                    "insert into library.book_to_author(book_id, author_id)"
                            + " values(?, ?)");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SimpleDataBaseWorker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(SimpleDataBaseWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static SimpleDataBaseWorker getWorker()
    {
        if(instance == null)
            instance = new SimpleDataBaseWorker();
        return instance;
    }
    
    public Collection<Book> getAllBooks()
    {
        Map<Integer, Book> result = new HashMap<>();
        
        try 
        {
            System.out.println("1");
            ResultSet selectResults = getAllBooks.executeQuery();
            System.out.println("2");
            while(selectResults.next())
            {
            System.out.println("3");
                int bookId = selectResults.getInt(1);
                if(!result.containsKey(bookId))
                {
            System.out.println("4");
                    String bookTitle = selectResults.getString(2);
                    int bookYear = selectResults.getInt(3);
                    result.put(bookId, new Book(bookTitle, bookYear));
                }
            System.out.println("5");
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
