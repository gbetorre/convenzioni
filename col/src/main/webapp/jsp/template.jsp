<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="head" value="${requestScope.header}" scope="page" />
<c:set var="foot" value="${requestScope.footer}" scope="page" />
<c:set var="bgbody" value="" scope="page" />
<c:if test="${empty sessionScope.usr}">
  <c:set var="bgbody" value="masthead" scope="page" />
</c:if>
<!DOCTYPE html>
<html lang="it">
  <head>
    <title><c:out value="${requestScope.tP}" escapeXml="false" /></title>
    <meta charset="utf-8" />
    <meta name="language" content="Italian" />
    <meta name="keywords" content="pa,software pa,analisi del rischio,gestione del rischio corruttivo,mappatura dei processi organizzativi">
    <meta name="description" content="${requestScope.advice}" />
    <meta name="viewport" content="width=device-width, user-scalable=no,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0">
    <meta name="creator" content="Giovanroberto Torre" />
    <meta name="author" content="Giovanroberto Torre, giovanroberto.torre@univr.eu" />
    <link rel="author" href="giovanroberto.torre@gmail.com" />
    <meta http-equiv='cache-control' content='no-cache'>
    <!-- Include jQuery from CDN or from local hosted copy -->
    <script src="https://code.jquery.com/jquery-3.3.1.js"></script>
    <script>
      window.jQuery || document.write('<script src="${initParam.urlDirectoryScrypt}/jquery/jquery-3.3.1.js"><\/script>');
    </script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    <!-- Include jQuery validate Form elements from CDN or from local hosted copy -->
    <script src="<c:out value="${initParam.urlDirectoryScript}" />jquery/plugin/jquery.validate-1.17.0.js"></script>
    <script src="<c:out value="${initParam.urlDirectoryScript}" />jquery/plugin/additional-methods-1.17.0.js"></script>
    <script src="<c:out value="${initParam.urlDirectoryScript}" />jquery/plugin/jquery-ui.js"></script>
    <script src="<c:out value="${initParam.urlDirectoryScript}" />jquery/plugin/jquery.textarea_autosize.js"></script>
    <!-- <script src="<c:out value="${initParam.urlDirectoryScript}" />bootstrap/bootstrap.js"></script>
    <script src="<c:out value="${initParam.urlDirectoryScript}" />bootstrap/plugin/bootstrap.bundle.js"></script> -->
    <script src="<c:out value="${initParam.urlDirectoryScript}" />jquery/plugin/jquery-modal.js"></script>
    <script src="<c:out value="${initParam.urlDirectoryScript}" />bootstrap/plugin/grayscale.js"></script>
    <script src="<c:out value="${initParam.urlDirectoryScript}" />cookie/cookieconsent.min.js"></script>
    <!-- <script src="<c:out value="${initParam.urlDirectoryScript}" />jquery/plugin/jquery-datatables.js"></script> -->
    <script src="https://cdn.datatables.net/1.12.1/js/jquery.dataTables.min.js"></script>
    <script src="<c:out value="${initParam.urlDirectoryScript}" />jquery/plugin/jquery-datatables-bootstrap4.js"></script>
    <script src="<c:out value="${initParam.urlDirectoryScript}" />rol.js"></script>
    <script src="<c:out value="${initParam.urlDirectoryScript}" />popup.js"></script>
    <!-- Begin Cookie Consent plugin by Silktide - http://silktide.com/cookieconsent -->
    <script>
      window.addEventListener("load", function(){
      window.cookieconsent.initialise({
        "palette": {
          "popup": {
            "background": "#000"
          },
          "button": {
            "background": "#f1d600"
          }
        },
        "theme": "classic",
        "position": "bottom-right",
        "content": {
          "message": "Questo sito o gli strumenti terzi da questo utilizzati si avvalgono di cookie necessari al funzionamento ed utili alle finalit&agrave; illustrate nella <a class=\"cc-link\" href=\"https://www.univr.it/it/privacy\" target=\"_blank\">cookie policy</a>. Inoltre nel contesto di alcune funzionalit&agrave; vengono memorizzati gli autori e le date di alcune modifiche. Se vuoi saperne di pi&uacute; o negare il consenso a tutti o ad alcuni cookie, consulta la <a class=\"cc-link\" href=\"https://www.univr.it/it/privacy\" target=\"_blank\">cookie policy</a>. Cliccando su \'Acconsento\' dai l\'autorizzazione all\'utilizzo dei cookie e delle profilazioni.",
          "dismiss": "Acconsento",
          "link": "Informazioni",
          "href": "https://www.univr.it/it/privacy"
        }
      })});
    </script>
    <!-- End Cookie Consent plugin -->
    <%--base href="${requestScope.baseHref}" /--%>
    <!-- Latest compiled and minified CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-rbsA2VBKQhggwzxH7pPCaAqO46MgnOM80zW1RWuH61DGLwZJEdK2Kadq2F9CUG65" crossorigin="anonymous">
    <!-- Latest compiled CSS, just in case -->
    <link rel="stylesheet" href="<c:out value="${initParam.urlDirectoryStili}" />bootstrap/bootstrap.css" type="text/css" />
    <link rel="stylesheet" href="<c:out value="${initParam.urlDirectoryStili}" />jquery/jquery-ui.css" type="text/css" />
    <link rel="stylesheet" href="<c:out value="${initParam.urlDirectoryStili}" />bootstrap/plugin/grayscale.css" type="text/css" />
    <link rel="stylesheet" href="<c:out value="${initParam.urlDirectoryStili}" />bootstrap/plugin/sbadmin.css" type="text/css" />
    <link rel="stylesheet" href="<c:out value="${initParam.urlDirectoryStili}" />jquery/jquery-modal.css" type="text/css" />
    <link rel="stylesheet" href="<c:out value="${initParam.urlDirectoryStili}" />style.css" type="text/css" />
    <link rel="stylesheet" href="<c:out value="${initParam.urlDirectoryStili}" />menu.css" type="text/css" />
    <link rel="stylesheet" href="<c:out value="${initParam.urlDirectoryStili}" />cookie/cookieconsent.min.css" type="text/css" />
    <%-- <link rel="stylesheet" href="<c:out value="${initParam.urlDirectoryStili}" />jquery/jquery-datatables.css" type="text/css" /> --%>
    <link rel="stylesheet" href="https://cdn.datatables.net/1.12.1/css/jquery.dataTables.min.css" />
    <link rel="stylesheet" href="<c:out value="${initParam.urlDirectoryStili}" />jquery/jquery-bootstrap-datatables.css" type="text/css" />
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v6.1.1/css/all.css" integrity="sha384-/frq1SRXYH/bSyou/HUp/hib7RVN1TawQYja658FEOodR/FQBKVqT9Ol+Oz3Olq5" crossorigin="anonymous">
    <!-- google web font css -->
    <link href="http://fonts.googleapis.com/css?family=Montserrat:400,700" rel="stylesheet" type="text/css">
  </head>
  <body class="${pageScope.bgbody}">
  <c:catch var="exception">
    <!-- Header -->
    <div id="idHeader">
      <%--@ include file="header.jspf"--%>
    </div>
    <%-- Menu orizzontale -->
    <div id="divMenu"> -->
      <%@ include file="menu.jspf"%>
    </div> 
     --%>
    <div id="divBreadcrumbs">
      <%--@ include file="breadcrumbs.jspf"--%>
    </div>
    <!-- Corpo pagina -->
    <div class="page">
      <a href="#top" id="goTop">
        <span>
          <img src="${initParam.urlDirectoryImmagini}ico-up.png" class="imgTop">
        </span>
      </a>
      <jsp:include page="${fileJsp}" />
    </div>
    <br /><br />
    <%-- Footer -->
    <footer>
      <%@ include file="footer.jspf" %>
    </footer>
    --%>
  </c:catch>
  <c:out value="${exception}" />
    <script src="<c:out value="${initParam.urlDirectoryScript}" />rol.js"></script>
    <script src="<c:out value="${initParam.urlDirectoryScript}" />menu.js"></script>
  </body>
</html>
