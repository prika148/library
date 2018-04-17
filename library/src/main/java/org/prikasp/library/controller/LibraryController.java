/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.prikasp.library.controller;

import javax.servlet.http.HttpServletRequest;
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
    
    @RequestMapping(value = "/add_book.htm")
    public String addBook(HttpServletRequest request,  ModelMap model)
    {
        System.out.println(request.getParameter("json"));
        model.addAttribute("text", "Успешно добавлено");
        return "response";
    }
}
