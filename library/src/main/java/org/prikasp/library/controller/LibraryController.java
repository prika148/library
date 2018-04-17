/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.prikasp.library.controller;

import javax.servlet.http.HttpServletRequest;
import org.prikasp.library.database.SimpleDataBaseWorker;
import org.prikasp.library.enteties.Book;
import org.prikasp.library.json.GsonProccessor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

/**
 *
 * @author libre
 */
@Controller
public class LibraryController 
{
    @RequestMapping(value = "/index.htm")
    public String index()
    {
        return "index";
    }
        
    @RequestMapping(value = "/books.htm")
    public String books()
    {
        return "books";
    }
    
    @RequestMapping(value = "/add_book.htm")
    public String addBook(HttpServletRequest request,  ModelMap model)
    {
        String clientInput = request.getParameter("json");
        String clientOut = null;
        
        Book parsed = null;
        try
        {
            parsed = GsonProccessor.parseBook(clientInput);
            if(SimpleDataBaseWorker.getWorker().saveBook(parsed))
                clientOut = "Успешно добавлено!";
        }
        catch (NumberFormatException ex)
        {
            clientOut = "Неверный формат года издания. Используйте только цифры";
        }
        if(clientOut == null)
            clientOut = "При добалении произошла ошибка";
        model.addAttribute("text", clientOut);
        return "response";
    }
    
    @RequestMapping(value = "/book_details.htm")
    public String details(HttpServletRequest request,  ModelMap model)
    {
        int bookId = -1;
        try{
            bookId = Integer.parseInt(request.getParameter("id"));
        }
        catch(Exception e)
        {
            return "index";
        }
        String clientOut = null;
        
        Book needed = SimpleDataBaseWorker.getWorker().getBook(bookId);
        if(needed == null)
            return "index";
        model.addAttribute("title", needed.getTitle());
        model.addAttribute("year", String.valueOf(needed.getYear()));
        model.addAttribute("id", String.valueOf(needed.getId()));
        model.addAttribute("authors", needed.getAuthorsStr());
        return "book_details";
    }
    
    @RequestMapping(value = "/drop.htm")
    public String drop(HttpServletRequest request)
    {
        int bookId = -1;
        try{
            bookId = Integer.parseInt(request.getParameter("id"));
        }
        catch(Exception e)
        {
            return "books";
        }
        String clientOut = null;
        
        SimpleDataBaseWorker.getWorker().removeBook(bookId);
        return "books";
    }
    
    @RequestMapping(value = "/update_book.htm")
    public String updateBook(HttpServletRequest request,  ModelMap model)
    {
        String clientInput = request.getParameter("json");
        String clientOut = null;
        
        Book parsed = null;
        try
        {
            parsed = GsonProccessor.parseBook(clientInput);
            if(SimpleDataBaseWorker.getWorker().updateBook(parsed))
                clientOut = "";
        }
        catch (NumberFormatException ex)
        {
            clientOut = "Неверный формат года издания. Используйте только цифры";
        }
        if(clientOut == null)
            clientOut = "При изменении произошла ошибка";
        model.addAttribute("text", clientOut);
        return "response";
    }
}
