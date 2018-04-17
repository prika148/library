/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.prikasp.library.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

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
}
