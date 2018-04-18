/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.prikasp.library.json;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.LinkedList;
import java.util.List;
import org.prikasp.library.enteties.Author;
import org.prikasp.library.enteties.Book;

/**
 * Class for JSON parsing by GSON library.
 * TODO: Should rewrite this class more secure.
 * 
 * @author libre
 */
public class GsonProccessor 
{
    static Gson proccessor = new Gson();
    
    /**
     * Create book by json. There are two cases: books with id, and without
     * @param jsonBook json to be parsed
     * @return Book created
     */
    public static Book parseBook(String jsonBook)
    {
        JsonObject jsonObject = new JsonParser().parse(jsonBook).getAsJsonObject();
        String title = jsonObject.get("title").getAsString();
        int year = jsonObject.get("year").getAsInt();
        
        //If there is id, we do not need authors' list
        if(jsonObject.has("id"))
        {
            int id = jsonObject.get("id").getAsInt();
            return new Book(title, year, id);
        }
        
        List<Author> authors = new LinkedList<>();
        JsonArray arr = jsonObject.get("authors").getAsJsonArray();
        for (JsonElement jsonElement : arr) 
        {
            authors.add(new Author(jsonElement.getAsString()));
        }
        return new Book(title, year, authors);
    }
}
