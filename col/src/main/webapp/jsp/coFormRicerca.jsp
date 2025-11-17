<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="types" value="${requestScope.tipi}" scope="page" />
<c:set var="scopes" value="${requestScope.finalita}" scope="page" />
<c:set var="cons" value="${requestScope.convenzioni}" scope="page" />
<c:set var="tipo" value="<em>Tutte</em>" scope="page" />
<c:if test="${not empty types[requestScope.params.res.type - 1]}">
  <c:set var="tipo" value="${types[requestScope.params.res.type - 1].nome}" scope="page" />
</c:if>
<c:set var="finalita" value="<em>Tutte</em>" scope="page" />
<c:if test="${not empty types[requestScope.params.res.scop - 1]}">
  <c:set var="finalita" value="${types[requestScope.params.res.scop - 1].nome}" scope="page" />
</c:if>
    <div class="main-banner">
      <div class="container">
        <div class="row">
          <div class="col-lg-12">
            <div class="top-text header-text">
              <h2>Cerca Convenzione</h2>
            </div>
          </div>
          <div class="col-lg-12">
            <form id="search-form" name="gs" method="post" role="search" action="${initParam.appName}/?q=co&op=res">
              <div class="row">
                <div class="col-lg-3 align-self-center">
                  <fieldset>
                    <select id="chooseCategory" name="co-tipo" class="form-select" aria-label="Tipo">
                      <option value="0" selected>Tutte le tipologie</option>
                      <c:forEach var="type" items="${pageScope.types}">
                      <option value="${type.id}"><c:out value="${type.nome}" /></option>
                      </c:forEach>
                    </select>
                  </fieldset>
                </div>
                <div class="col-lg-3 align-self-center">
                  <fieldset>
                    <input type="text" name="co-nome" class="searchText" placeholder="Chiave di ricerca" autocomplete="on" required>
                  </fieldset>
                </div>
                <div class="col-lg-3 align-self-center">
                  <fieldset>
                    <select id="chooseCategory" name="co-fine" class="form-select" aria-label="Finalita">
                      <option value="0" selected>Tutte le finalit&agrave;</option>
                      <c:forEach var="scope" items="${pageScope.scopes}">
                      <option value="${scope.id}"><c:out value="${scope.nome}" /></option>
                      </c:forEach>
                    </select>
                  </fieldset>
                </div>
                <div class="col-lg-3">                        
                  <fieldset>
                    <button class="main-button"><i class="fa fa-search"></i> Cerca</button>
                  </fieldset>
                </div>
              </div>
            </form>
          </div>
          <div class="col-lg-12">
            <div class="row text-white text-center">
              <ul class="categories">
                <li>
                  TIPOLOGIA:<br> 
                  <c:out value="${fn:toLowerCase(pageScope.tipo)}" escapeXml="false" />
                </li>
                <li>
                  FINALIT&Agrave;:<br> 
                  <c:out value="${fn:toLowerCase(pageScope.finalita)}" escapeXml="false" />
                </li>
                <li>
                  CHIAVE:<br> 
                  <c:out value="${requestScope.params.res.keys}" />
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
      <a href="#list" class="arrow-link" title="Vai ai risultati"></a>
    </div>
    <!-- Agreements List -->
    <div class="container my-4" id="list">
      <h2 class="panel-heading">Risultati della Ricerca</h2>
      <hr class="row">
      <div class="panel panel-primary">
        <ul class="list-group">
        <c:forEach var="conv" items="${pageScope.cons}">
          <li class="list-group-item">
            <a href="${initParam.appName}/?q=co&id=${conv.id}" class="btn-sm text-primary">
              <c:out value="${conv.titolo}" />
            </a> 
            <p class="snippet"><c:out value="${conv.informativa}" escapeXml="false" /></p> 
          </li>
        </c:forEach>
        <c:if test="${empty pageScope.cons}">
          <li class="list-group-item">
            Nessuna convenzione trovata per le chiavi di ricerca immesse!
          </li>
          <br>
        </c:if>
        </ul>
      </div>
    </div>

    <!-- jQuery first -->
    <script src="${initParam.urlDirFrameworks}jquery/jquery.min.js"></script>
    <!-- Paginathing JS -->
    <script src="${initParam.urlDirFrameworks}Paginathing/dist/paginathing.min.js" type="text/javascript"></script>
    <!-- Your script -->
    <script type="text/javascript">
      jQuery(document).ready(function ($) {
        $(".list-group").paginathing({
          // Items per page
          perPage: 10,
          // Extend default container class
          //containerClass: "panel-footer",
          // Previous button text
          prevText: '&lsaquo;',
          // Next button text
          nextText: '&rsaquo;',
          // "First button" text
          firstText:'&laquo;',
          // "Last button" text
          lastText: '&raquo;',
        });
      });
    </script>