/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.prikasp.library.enteties;

import java.util.List;


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
    private final List<String> authors;

    /**
     * Creates Book with name, year of publishing and list of Authors
     * @param title Title of the book
     * @param year  Year of pusblishing
     * @param authors   List of Authors. Order is important
     */
    public Book(String title, int year, List<String> authors) 
    {
        this.title = title;
        this.year = year;
        this.authors = authors;
    }

    public String getTitle() 
    {
        return title;
    }

    public int getYear() 
    {
        return year;
    }

    public List<String> getAuthors() 
    {
        return authors;
    }
}
