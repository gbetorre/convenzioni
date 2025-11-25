<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="conts" value="${requestScope.contraenti}" scope="page" />
    <link rel="stylesheet" href="${initParam.urlDirFrameworks}DataTables/css/datatables.min.css" type="text/css" />
    <link rel="stylesheet" href="${initParam.urlDirFrameworks}DataTables/plug-ins/searchHighlight/dataTables.searchHighlight.css" type="text/css" />
    <div class="page-heading contractors">
      <div class="container">
        <div class="row">
          <div class="col-lg-12">
            <div class="top-text header-text">
              <h2>Registro Contraenti</h2>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- Contractors List -->
    <div class="container my-4" id="list">
      <h2>Elenco Contraenti</h2>
      <table id="contractorsTable" class="table table-striped table-bordered datatable">
        <thead>
          <tr>
            <th width="6%">Tipologia</th>
            <th width="19%">Nome</th>
            <th width="*">Convenzioni</th>
            <th width="5%" class="dt-head-left">Codice Fiscale</th>
            <th width="5%" class="dt-head-left">Partita IVA</th>
            <th width="18%" class="dt-head-left">Contatto</th>
            <th width="12%">Note</th>
          </tr>
        </thead>
        <tbody>
        <c:forEach var="cont" items="${pageScope.conts}">
          <tr>
            <td><c:out value="${cont.note}" /></td>
            <td>
              <a href="${initParam.appName}/?q=co&op=sel&obj=cont&id=${cont.id}" class="btn-sm text-success" title="N. convenzioni associate: ${cont.convenzioni.size()}">
                <c:out value="${cont.nome}" />
              </a>
            </td>
            <td>
            <c:forEach var="conv" items="${cont.convenzioni}">
              <i class="fa fa-caret-right" aria-hidden="true" me-1></i>
              <a href="${initParam.appName}/?q=co&op=sel&id=${conv.id}" class="btn-sm text-primary">
                <c:out value="${conv.titolo}" />
              </a><br>
            </c:forEach>
            </td>
            <td><c:out value="${cont.codiceFiscale}" /></td>
            <td><c:out value="${cont.partitaIva}" /></td>
            <td>
              <a href="mailto:${cont.email}">
                <c:out value="${cont.email}" />
              </a>
            </td>
            <td><c:out value="${cont.informativa}" /></td>
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
        $('#contractorsTable').DataTable({
          "pageLength":   25,
          "lengthChange": true,
          "ordering":     true,
          "searching":    true,
          "info":         true,
          "autoWidth":    false,
          "searchHighlight": true,
          "mark":         true,
          "order": [[ 1, 'asc' ]], // columnIndex is the zero-based index of the column one want to sort by
          "language": {
            "search": "_INPUT_",
            "searchPlaceholder": "Filtra contraenti...",
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
