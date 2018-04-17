<%-- 
    Document   : books
    Created on : 17.04.2018, 21:41:59
    Author     : libre
--%>

<%@page import="org.prikasp.library.enteties.Book"%>
<%@page import="org.prikasp.library.database.SimpleDataBaseWorker"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Список книг</title>
    </head>
    <body>
        <h1>Все книги:</h1>
        <table border="2">
            <tr>
                <td>Название</td>
                <td>Год Издания</td>
                <td>Список авторов</td>
            </tr>
            <% 
                for (Book book : SimpleDataBaseWorker.getWorker().getAllBooks()) 
                {
            %>
                <td><%out.print(book.getTitle());%></td>
                <td><%out.print(book.getYear());%></td>
                <td><%out.print(String.join("; ", book.getAuthorsStr()));%></td>
            <%
                }
            %>
        </table>
        <br>
        <a href="/library/index.htm">Добавить книгу</a>
    </body>
</html>
