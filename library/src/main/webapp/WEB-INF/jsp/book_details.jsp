<%-- 
    Document   : update_book
    Created on : 17.04.2018, 21:42:45
    Author     : libre
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>${title}</title>
          <script>
                function updateBook() 
                {
                    var aBook = new Object();
                    aBook.title = document.getElementById("bookTitle").value;
                    aBook.year = document.getElementById("bookYear").value;
                    aBook.id = ${id};
                    
                    var jsonString = JSON.stringify(aBook);
                    var xhr = new XMLHttpRequest();
                    var body = 'json=' + encodeURIComponent(jsonString);

                    xhr.open("POST", '/library/update_book.htm', true);
                    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

                    xhr.onreadystatechange = function() 
                        {
                            if (this.readyState !== 4) return;
                            if(this.responseText.length > 3)
                                alert( this.responseText );
                            else
                                location.reload();
                        }
                    xhr.send(body);
                }
           </script>
    </head>
    <body>
        <h1>Книга ${title}, издана в ${year} году.</h1>
        <br>
        <h2>Авторы: ${authors}</h2>
        <br>
        <form action="" id="editForm">
            <p><input type="button" value="Отправить" onclick="updateBook()"></p>
            <p>Введите название книги, год издания и авторов</p>
            <p><input type="text" placeholder="Название книги" id="bookTitle" value="${title}"/></p>
            <p><input type="text" placeholder="Год издания" id="bookYear" value="${year}"/></p>
        </form>
        <br>
        <a href="/library/drop.htm?id=${id}">Удалить книгу</a>
    </body>
</html>
