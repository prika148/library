<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Welcome to Spring Web MVC project</title>
          <script>
                function addAuthor()
                {
                    for(var i = 1; i < 100; i++)
                    {
                        if(document.getElementById("Author" + i) === null)
                        {
                            var np = document.createElement("p");
                            var ni = document.createElement("input");
                            ni.id = "Author" + i;
                            ni.type = "text";
                            ni.placeholder = "Автор";
                            np.appendChild(ni);
                            document.getElementById("bookForm").appendChild(np);
                            break;
                        }
                    }
                }
                function foo() 
                {
                    var newBook = new Object();
                    newBook.title = document.getElementById("bookTitle").value;
                    newBook.year = document.getElementById("bookYear").value;
                    newBook.authors = [];
                    
                    for(var i = 1; i < 100; i++)
                    {
                        if(document.getElementById("Author" + i) === null)
                            break;
                        else
                        {
                            newBook.authors.push(document.getElementById("Author" + i).value);
                        }
                    }
                    var jsonString = JSON.stringify(newBook);
                    var xhr = new XMLHttpRequest();
                    var body = 'json=' + encodeURIComponent(jsonString);

                    xhr.open("POST", '/library/add_book.htm', true);
                    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

                    xhr.onreadystatechange = function() 
                        {
                            if (this.readyState !== 4) return;

                            alert( this.responseText );
                        }
                    xhr.send(body);
                }
           </script>
    </head>

    <body>
        <form action="" id="bookForm">
            <p><input type="button" value="Отправить" onclick="foo()"></p>
            <p><input type="button" value="Добавить автора" onclick="addAuthor()"></p>
            <p>Введите название книги, год издания и авторов</p>
            <p><input type="text" placeholder="Название книги" id="bookTitle"/></p>
            <p><input type="text" placeholder="Год издания" id="bookYear"/></p>
            <p><input type="text" placeholder="Автор" id="Author1"/></p>
        </form>
    </body>
</html>
