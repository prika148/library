/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.prikasp.library.enteties;

/**
 * Author entity.
 * This class seems to be useless, but may be helpful for further development
 *
 * @author libre
 */
public class Author 
{
    String name;

    /**
     * Creates author with speciied name
     * @param name name of the Author
     */
    public Author(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
}
