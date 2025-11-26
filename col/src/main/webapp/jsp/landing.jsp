<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<fmt:setLocale value="it_IT"/>
<c:set var="types" value="${requestScope.tipi}" scope="page" />
<c:set var="scopes" value="${requestScope.finalita}" scope="page" />
<c:set var="cons" value="${requestScope.convenzioni}" scope="page" />
<c:set var="grp" value="${fn:toUpperCase(usr.gruppi.get(0).nome)}" />
    <link rel="stylesheet" href="${initParam.urlDirFrameworks}DataTables/css/datatables.min.css" type="text/css" />
    <link rel="stylesheet" href="${initParam.urlDirFrameworks}DataTables/plug-ins/searchHighlight/dataTables.searchHighlight.css" type="text/css" />
    <div class="main-banner">
      <div class="container">
        <div class="row">
          <div class="col-lg-12">
            <div class="top-text header-text">
              <h6>
                <span class="badge badge-pill badge-light">
                  <c:out value="${cons.size()}" />
                </span> 
                &nbsp;Convenzioni Attive
              </h6>
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
          <hr class="separator">
          <hr class="row">
          <div class="col-lg-12">
            <div class="row text-white text-center">
              <h4>oppure filtra qui sotto tra le convenzioni attive</h4>
            </div>
          </div>
        </div>
      </div>
      <a href="#list" class="arrow-link" title="Vai all'elenco"></a>
    </div>
    <!-- Agreements List -->
    <div class="container my-4" id="list">
      <h2>Elenco Convenzioni</h2>
      <table id="apartmentTable" class="table table-striped table-bordered datatable">
        <thead>
          <tr>
            <th width="6%">Tipologia</th>
            <th width="19%">Contraenti</th>
            <th width="*">Titolo</th>
            <th width="5%" class="dt-head-left">Data approvazione</th>
            <th width="5%" class="dt-head-left">Data sottoscrizione</th>
            <th width="9%" class="dt-head-left">Data scadenza&nbsp;</th>
            <th width="12%">Num. repertorio</th>
            <th width="10%">Azioni</th>
          </tr>
        </thead>
        <tbody>
        <c:forEach var="conv" items="${pageScope.cons}">
          <tr>
            <td><c:out value="${conv.tipo}" /></td>
            <td>
            <c:forEach var="cont" items="${conv.contraenti}">
              <i class="fa fa-caret-right" aria-hidden="true" me-1></i>
              <a href="${initParam.appName}/?q=co&op=sel&obj=cont&id=${cont.id}" class="btn-sm text-success">
                <c:out value="${cont.nome}" />
              </a><br>
            </c:forEach>
            </td>
            <td>
              <a href="${initParam.appName}/?q=co&id=${conv.id}" class="btn-sm text-primary" title="${conv.note}">
                <c:out value="${conv.titolo}" />
              </a>
            <c:choose>
              <c:when test="${conv.caricoBollo eq 100.0}">
                <span class="text-icon" title="Imposte di bollo 100% a carico dell'ateneo">
                  <i class="fa fa-circle teal-icon"></i>
                </span>
              </c:when>
              <c:when test="${conv.caricoBollo eq 50.0}">
                <span class="text-icon" title="50% in carico all'ateneo e 50% in carico ai contraenti">
                  <i class="fa fa-adjust teal-icon"></i>
                </span>
              </c:when>
              <c:when test="${conv.caricoBollo eq 0.0}">
                <span class="text-icon" title="Imposte di bollo a carico dei contraenti">
                 <i class="fa fa-circle-o teal-icon"></i> 
                </span>
              </c:when>
            </c:choose>
            <c:choose>
              <c:when test="${conv.pagato}">
                <span class="text-icon" title="Bollo pagato">
                  <i class="fa fa-check-circle text-success"></i> 
                </span>
              </c:when>
              <c:when test="${not empty conv.pagato and not conv.pagato}">
                <span class="text-icon" title="Bollo da pagare">
                  <i class="fa fa-times-circle text-danger"></i>
                </span>
              </c:when>
            </c:choose>
            </td>
            <td data-order="<fmt:formatDate value="${conv.dataApprovazione}" pattern="yyyy-MM-dd" />">
              <fmt:formatDate value="${conv.dataApprovazione}" pattern="dd MMMMM yyyy" />
            </td>
            <td data-order="<fmt:formatDate value="${conv.dataSottoscrizione}" pattern="yyyy-MM-dd" />">
              <fmt:formatDate value="${conv.dataSottoscrizione}" pattern="dd MMMMM yyyy" />
            </td>
            <td data-order="<fmt:formatDate value="${conv.dataScadenza}" pattern="yyyy-MM-dd" />">
              <fmt:formatDate value="${conv.dataScadenza}" pattern="dd MMMMM yyyy" />
            </td>
            <td><c:out value="${conv.numRepertorio}" /></td>
            <td>
              <ul class="list-inline d-flex justify-content-center">
                <li class="list-inline-item me-1">
                  <a href="${initParam.appName}/?q=co&id=${conv.id}" class="btn btn-sm btn-success" title="Vedi dettagli Convenzione">
                    <i class="fa fa-eye"></i>
                  </a>
                </li>
                <li class="list-inline-item me-1">
                  <a href="${initParam.appName}/?q=co&op=ins&obj=cont&data=rel&id=${conv.id}" class="btn btn-sm btn-warning" title="Assegna Contraenti a Convenzione">
                    <i class="fa fa-users" aria-hidden="true"></i> 
                  </a>
                </li>
                <li class="list-inline-item me-1">
                  <a href="#" class="btn btn-sm btn-primary" title="Modifica Convenzione">
                    <i class="fa fa-pencil"></i>
                  </a>
                </li>
              </ul>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </div>
    <!-- jQuery first -->
    <script src="${initParam.urlDirFrameworks}jquery/jquery.min.js"></script>
    <!-- DataTables JS -->
    <script src="${initParam.urlDirFrameworks}DataTables/js/datatables.min.js" type="text/javascript"></script>
    <script src="${initParam.urlDirFrameworks}DataTables/plug-ins/searchHighlight/dataTables.searchHighlight.min.js" type="text/javascript"></script>
    <script src="${initParam.urlDirFrameworks}DataTables/plug-ins/jquery.highlight/jquery.highlight.js" type="text/javascript"></script>
    <!--  <script src="https://bartaz.github.io/sandbox.js/jquery.highlight.js"></script> -->
    <script>
      $(document).ready(function () {
        // Set datatable for a layer of mine
        $('#apartmentTable').DataTable({
          "pageLength":   10,
          "lengthChange": true,
          "ordering":     true,
          "searching":    true,
          "info":         true,
          "autoWidth":    false,
          "searchHighlight": true,
          "mark":         true,
          "order": [[ 2, 'asc' ]], // columnIndex is the zero-based index of the column one want to sort by
          "language": {
            "search": "_INPUT_",
            "searchPlaceholder": "Filtra convenzione...",
            "lengthMenu": "Mostra _MENU_ risultati per pagina",    // Label for the dropdown to select page length
            "info": "Mostrati _START_ a _END_ di _TOTAL_",  // Info about currently shown entries
            "infoEmpty": "Mostrati 0 a 0 di 0 risultati trovati",      // When empty
            "infoFiltered": "(filtrati da _MAX_ di risultati totali)", // Filter info
            "zeroRecords": "Nessun risultato trovato",     // When no results found
            "emptyTable": "Nessun dato disponibile per il gruppo '${pageScope.grp}' cui ${usr.nome} ${usr.cognome} appartiene"  // Table empty of data
          }
        });
      });
    </script>
        