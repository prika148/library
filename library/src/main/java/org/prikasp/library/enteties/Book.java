/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.prikasp.library.enteties;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Bool entity.
 * This class seems to be useless, but may be helpful for further development
 *
 * @author libre
 */
public class Book 
{
    private final String title;
    private final int year;
    private final List<Author> authors;

    /**
     * Creates Book with name, year of publishing and list of Authors
     * @param title Title of the book
     * @param year  Year of pusblishing
     * @param authors   List of Authors. Order is important
     */
    public Book(String title, int year, List<Author> authors) 
    {
        this.title = title;
        this.year = year;
        this.authors = authors;
    }

    /**
     * Creates Book with name, year of publishing and list of Authors
     * @param title Title of the book
     * @param year  Year of pusblishing
     * @param authors   List of Authors. Order is important
     */
    public Book(String title, int year) 
    {
        this.title = title;
        this.year = year;
        this.authors = new LinkedList<>();
    }

    public String getTitle() 
    {
        return title;
    }

    public int getYear() 
    {
        return year;
    }

    public List<Author> getAuthors() 
    {
        return authors;
    }

    public List<String> getAuthorsStr() 
    {
        return authors.stream().map((Author elem)->{ return elem.getName();})
                .collect(Collectors.toList());
    }
}
