<%@ page contentType="text/html;" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page isErrorPage="true" %>
<%@ page session="false" %>
<c:set var="isErrorPage" value="true" scope="request" />
<c:set var="msg" value="${requestScope.message}" scope="page" />
<!DOCTYPE html>
<html>
  <head>
    <title>Pagina di errore dell'applicazione COL</title>
    <meta charset="utf-8" />
    <meta name="language" content="Italian" />
    <meta name="creator" content="Giovanroberto Torre, giovanroberto.torre@univr.it" />
    <meta name="author" content="Giovanroberto Torre, giovanroberto.torre@univr.it" />
    <meta http-equiv='cache-control' content='no-cache'>
    <link rel="stylesheet" href="${initParam.urlDirStyles}error.css" />
  </head>
  <body>
    <nav>
      <div class="menu">
        <p class="website_name"><a href="/col">COL</a></p>
        <div class="menu_links">
          <a href="" class="link">about</a>
          <a href="https://at.univr.it" class="link">projects</a>
          <a href="" class="link">contacts</a>
        </div>
        <div class="menu_icon">
          <span class="icon"></span>
        </div>
      </div>
    </nav>
    <section class="wrapper">
      <div class="container">
        <div id="scene" class="scene" data-hover-only="false">
          <div class="circle" data-depth="1.2"></div>
          <div class="one" data-depth="0.9">
            <div class="content">
              <span class="piece"></span>
              <span class="piece"></span>
              <span class="piece"></span>
            </div>
          </div>
          <div class="two" data-depth="0.60">
            <div class="content">
              <span class="piece"></span>
              <span class="piece"></span>
              <span class="piece"></span>
            </div>
          </div>
          <div class="three" data-depth="0.40">
            <div class="content">
              <span class="piece"></span>
              <span class="piece"></span>
              <span class="piece"></span>
            </div>
          </div>
          <p class="p404" data-depth="0.50">ERRORE</p>
          <p class="p404" data-depth="0.10">ERRORE</p>
        </div>
        <div class="text">
          <article>
            <p><c:out value="${fn:substring(pageScope.msg, 35, 150)}" /></p>
            <!-- ${msg} -->
            <button type="button" onclick="window.location.href='/col'">home</button>
          </article>
        </div>
      </div>
    </section>
    <script src="vendor/jquery/jquery-3.7.1.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/parallax/3.1.0/parallax.min.js"></script>
    <script>
      // Parallax Code
      var scene = document.getElementById('scene');
      var parallax = new Parallax(scene);
    </script>
  </body>
</html>
