<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- c:set var="head" value="${requestScope.header}" scope="page" />
<c:set var="foot" value="${requestScope.footer}" scope="page" /--%>
<!DOCTYPE html>
<html lang="it">
  <head>
    <meta charset="utf-8">
    <meta name="language" content="Italian">
    <meta name="keywords" content="pa,software pa,open source, convenzioni,gestione convenzioni e partecipate pubbliche,mappatura delle scadenze legate agli incarichi di referente">
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="creator" content="Giovanroberto Torre">
    <meta name="author" content="Giovanroberto Torre, giovanroberto.torre@univr.it">
    <link rel="author" href="gianroberto.torre@gmail.com">
    <meta http-equiv="cache-control" content="no-cache">
    <link rel="preconnect" href="https://fonts.gstatic.com">
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@100;200;300;400;500;600;700;800;900&display=swap" rel="stylesheet">
    <title><c:out value="${requestScope.tP}" escapeXml="false" /></title>
    <!-- Bootstrap core CSS -->
    <link href="${initParam.urlDirFrameworks}bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <!-- Additional CSS Files -->
    <link rel="stylesheet" href="${initParam.urlDirStyles}fontawesome.css">
    <link rel="stylesheet" href="${initParam.urlDirStyles}templatemo-plot-listing.css">
    <link rel="stylesheet" href="${initParam.urlDirStyles}animated.css">
    <link rel="stylesheet" href="${initParam.urlDirStyles}owl.css">
  <body class="${pageScope.bgbody}">
  <c:catch var="exception">
    <!-- Header -->
    <c:if test="${not empty sessionScope.usr}">
    <div id="idHeader">
      <%@ include file="header.jspf" %>
    </div>
    </c:if>
    <%-- Menu orizzontale -->
    <div id="divMenu"> -->
      <%@ include file="menu.jspf"%>
    </div> 
     --%>
    <!-- Corpo pagina -->
    <div class="page"><%--
      <a href="#top" id="goTop">
        <span>
          <img src="${initParam.urlDirectoryImmagini}ico-up.png" class="imgTop">
        </span>
      </a> --%>
      <jsp:include page="${fileJsp}" />
    </div>
    <br><br>
    <%-- Footer -->
    <footer>
      <%@ include file="footer.jspf" %>
    </footer>
    --%>
  </c:catch>
  <c:out value="${exception}" />
    <!-- Scripts -->
<%--     <script src="${initParam.urlDirFrameworks}jquery/jquery.min.js"></script> --%>
    <script src="${initParam.urlDirFrameworks}bootstrap/js/bootstrap.bundle.min.js"></script>
    <script src="${initParam.urlDirScripts}owl-carousel.js"></script>
    <script src="${initParam.urlDirScripts}animation.js"></script>
    <script src="${initParam.urlDirScripts}imagesloaded.js"></script>
    <script src="${initParam.urlDirScripts}custom.js"></script>
  </body>
</html>
