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
            <form id="search-form" name="gs" method="post" role="search" action="#">
              <div class="row">
                <div class="col-lg-3 align-self-center">
                  <fieldset>
                    <select name="area" class="form-select" aria-label="Area" id="chooseCategory" onchange="this.form.click()">
                      <option selected>Tutte le tipologie</option>
                      <c:forEach var="type" items="${pageScope.types}">
                      <option value="${type.id}"><c:out value="${type.nome}" /></option>
                      </c:forEach>
                    </select>
                  </fieldset>
                </div>
                <div class="col-lg-3 align-self-center">
                  <fieldset>
                    <input type="text" name="address" class="searchText" placeholder="Chiave di ricerca" autocomplete="on" required>
                  </fieldset>
                </div>
                <div class="col-lg-3 align-self-center">
                  <fieldset>
                    <select name="price" class="form-select" aria-label="Default select example" id="chooseCategory" onchange="this.form.click()">
                      <option selected>Tutte le finalit&agrave;</option>
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
      <a href="#list" class="arrow-link" title="Go to list section"></a>
    </div>
    <!-- Agreements List -->
    <div class="container my-4" id="list">
      <h2>Elenco Convenzioni</h2>
      <table id="apartmentTable" class="table table-striped table-bordered datatable">
        <thead>
          <tr>
            <th width="10%">Tipologia</th>
            <th width="*">Titolo</th>
            <th width="5%">Data approvazione</th>
            <th width="10%">Nota approvazione</th>
            <th width="5%">Data sottoscrizione</th>
            <th width="10%">Nota sottoscrizione</th>
            <th width="10%">Data scadenza</th>
            <th width="10%">Nota scadenza</th>
            <th width="10%">Num. repertorio</th>
            <th width="10%">Azioni</th>
          </tr>
        </thead>
        <tbody>
        <c:forEach var="conv" items="${pageScope.cons}">
          <tr>
            <td><c:out value="${conv.tipo}" /></td>
            <td><c:out value="${conv.titolo}" /></td>
            <td><fmt:formatDate value="${conv.dataApprovazione}" pattern="dd MMMMM yyyy" /></td>
            <td><c:out value="${conv.notaApprovazione}" /></td>
            <td><fmt:formatDate value="${conv.dataSottoscrizione}" pattern="dd MMMMM yyyy" /></td>
            <td><c:out value="${conv.notaSottoscrizione}" /></td>
            <td><fmt:formatDate value="${conv.dataScadenza}" pattern="dd MMMMM yyyy" /></td>
            <td><c:out value="${conv.notaScadenza}" /></td>
            <td><c:out value="${conv.numRepertorio}" /></td>
            <td>
              <ul class="list-inline">
                <li class="list-inline-item me-0">
                  <a href="${initParam.appName}/?q=co&id=${conv.id}" class="btn btn-sm btn-success" title="Vedi dettagli Convenzione">
                    <i class="fa fa-eye"></i>
                  </a>
                </li>
                <li class="list-inline-item me-0">
                  <a href="${initParam.appName}/?q=co&op=ins&obj=cont&data=rel&id=${conv.id}" class="btn btn-sm btn-warning" title="Assegna Contraenti">
                    <i class="fa fa-users" aria-hidden="true"></i> 
                  </a>
                </li>
                <li class="list-inline-item me-0">
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
    <!-- DataTables JS -->
    <script src="${initParam.urlDirFrameworks}DataTables/js/datatables.min.js" type="text/javascript"></script>
    <script>
      $(document).ready(function () {
        $('#apartmentTable').DataTable({
          "pageLength":   10,
          "lengthChange": true,
          "ordering":     true,
          "searching":    true,
          "info":         true,
          "autoWidth":    false,
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
        