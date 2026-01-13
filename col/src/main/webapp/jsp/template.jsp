<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="it">
  <head>
    <meta charset="utf-8">
    <meta name="language" content="Italian">
    <meta name="keywords" content="pa,software pa,open source,convenzioni,gestione convenzioni e partecipate pubbliche,mappatura delle scadenze legate agli incarichi di referente">
    <meta name="description" content="Applicazione web per la gestione e il monitoraggio delle convenzioni stipulate dalla pubblica amministrazione">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="creator" content="Giovanroberto Torre">
    <meta name="author" content="Giovanroberto Torre, giovanroberto.torre@univr.it">
    <link rel="author" href="gianroberto.torre@gmail.com">
    <meta http-equiv="cache-control" content="no-cache">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@100;200;300;400;500;600;700;800;900&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Bitter:ital,wght@0,100..900;1,100..900&family=Great+Vibes&family=Merriweather:ital,opsz,wght@0,18..144,300..900;1,18..144,300..900&display=swap" rel="stylesheet">
    <title><c:out value="${requestScope.tP}" escapeXml="false" /></title>
    <!-- Bootstrap core CSS -->
    <link href="${initParam.urlDirFrameworks}bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <!-- Additional CSS Files -->
    <link rel="stylesheet" href="${initParam.urlDirStyles}fontawesome.css">
    <link rel="stylesheet" href="${initParam.urlDirStyles}templatemo-plot-listing.css">
    <link rel="stylesheet" href="${initParam.urlDirStyles}animated.css">
    <link rel="stylesheet" href="${initParam.urlDirStyles}owl.css">
    <!--  Customized and taylored CSS for col  -->
    <link rel="stylesheet" href="${initParam.urlDirStyles}col.css">
  <body class="${pageScope.bgbody}">
  <c:catch var="exception"><!-- Cookie consent -->
    <%@ include file="cookieConsent.jspf" %>
    <!-- Header -->
    <c:if test="${not empty sessionScope.usr}">
    <div id="idHeader">
      <%@ include file="header.jspf" %>
    </div>
    </c:if>
    <!-- Corpo pagina -->
    <div class="page">
      <jsp:include page="${fileJsp}" />
    </div>
    <!-- Footer -->
    <c:if test="${not empty sessionScope.usr}">
      <hr class="separator"> 
      <hr class="row">
      <%@ include file="footer.jspf" %>
    </c:if>
    <script src="${initParam.urlDirFrameworks}bootstrap/js/bootstrap.bundle.min.js"></script>
    <script src="${initParam.urlDirScripts}owl-carousel.js"></script>
    <script src="${initParam.urlDirScripts}animation.js"></script>
    <script src="${initParam.urlDirScripts}imagesloaded.js"></script>
    <script src="${initParam.urlDirScripts}custom.js"></script>
  </c:catch>
  <c:out value="${exception}" />
  </body>
</html>
