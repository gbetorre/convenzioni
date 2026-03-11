<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="it_IT"/>
<c:set var="types" value="${requestScope.tipi}" scope="page" />
<c:set var="scopes" value="${requestScope.finalita}" scope="page" />
<c:set var="conv" value="${requestScope.convenzione}" scope="page" />
    <!-- Bootstrap Datepicker CSS -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.10.0/css/bootstrap-datepicker.min.css" rel="stylesheet">
    <!-- Banner -->
    <div class="page-heading convention-note">
      <div class="container">
        <div class="row">
          <div class="col-lg-12">
            <div class="top-text header-text">
              <h6><c:out value="${requestScope.tP}" /></h6>
              <h3><a href="${initParam.appName}/?q=co&id=${conv.id}" class="text-white"><c:out value="${conv.titolo}" /></a></h3>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- Page -->
    <div class="container">
      <div class="row">
        <div class="col-lg-12">
          <div class="inner-content">
            <div class="row">
              <div class="col-lg-12 align-self-center">
                <form accept-charset="ISO-8859-1" name="upd-conv" id="contact" action="" method="post" class="contact">
                  <fieldset>
                    <legend><c:out value="${requestScope.tP}" /></legend>
                    <!-- Tipologia -->
                    <hr class="separator">
                    <div class="form-group row">
                      <label for="conv-tipo" class="col-sm-4 col-form-label">
                        <strong>
                          Tipo Convenzione
                          <span class="text-danger"> *</span>
                        </strong>
                      </label>
                      <div class="col-sm-8">
                        <hr class="short">
                        <select name="co-tipo" id="conv-tipo" required class="form-control contact bg-warning dropdown" title="Clicca per scegliere una tipologia">
                          <option value="">-- Scegli una tipologia --</option>
                        <c:forEach var="type" items="${pageScope.types}">
                          <option value="${type.id}"><c:out value="${type.nome}" /></option>
                        </c:forEach>
                        </select>
                      </div>
                    </div>
                    <!-- Titolo -->
                    <hr class="separator">
                    <div class="form-group row">
                      <label for="conv-title" class="col-sm-4 col-form-label">
                        <strong>
                          Titolo Convenzione
                          <span class="text-danger"> *</span>
                        </strong>
                      </label>
                      <div class="col-sm-8">
                        <input type="text" name="co-titl" id="conv-title" placeholder="Inserisci Titolo (obbligatorio)" autocomplete="on" required class="form-control contact bg-warning">
                      </div>
                    </div>
                    <!-- Repertorio -->
                    <hr class="separator">
                    <div class="form-group row">
                      <label for="conv-prot" class="col-sm-4 col-form-label">
                        <strong>
                          Repertorio
                          <span class="text-danger"> *</span>
                        </strong>
                      </label>
                      <div class="col-sm-8">
                        <input type="text" name="co-prot" id="conv-prot" placeholder="Inserisci Repertorio (obbligatorio)" autocomplete="on" required class="form-control contact bg-warning">
                      </div>
                    </div>
                    <!-- Oggetto -->
                    <hr class="short">
                    <div class="form-group row">
                      <label for="conv-info" class="col-sm-4 col-form-label">
                        <strong>Oggetto</strong>
                      </label>
                      <div class="col-sm-8">
                        <textarea name="co-info" id="conv-info" placeholder="Oggetto della convenzione" class="form-control contact"></textarea> 
                      </div>
                    </div>
                    <!-- Finalità -->
                    <hr class="short">
                    <div class="form-group row">
                      <label for="conv-scop" class="col-sm-4 col-form-label">
                        <strong>Finalit&agrave;</strong>
                      </label>
                      <div class="col-sm-8">&nbsp;
                        <ul id="conv-scop" class="contact">
                        <c:forEach var="scope" items="${scopes}">
                          <li>
                            <input type="checkbox" id="scop${scope.id}" name="co-scop" value="${scope.id}">
                            <label for="scop${scope.id}"><span><c:out value="${scope.nome}" /></span></label>
                          </li>
                        </c:forEach>
                        </ul>
                      </div>
                    </div>
                    <!-- Note -->
                    <hr class="short">
                    <div class="form-group row">
                      <label for="conv-note" class="col-sm-4 col-form-label">
                        <strong>Note</strong>
                      </label>
                      <div class="col-sm-8">
                        <textarea name="co-note" id="conv-note" placeholder="Note sulla convenzione" class="form-control contact"><c:out value="${conv.note}" /></textarea> 
                      </div>
                    </div>
                    <!-- Data 1 -->
                    <hr class="separator">
                    <div class="form-group row">
                      <label for="conv-dat1" class="col-sm-4 col-form-label">
                        <strong>
                          Data approvazione
                          <span class="text-danger">*</span>
                        </strong>
                      </label>
                      <div class="col-sm-8">
                        <input type="text" name="co-dat1" id="conv-dat1" required class="contact bg-warning calendarData">
                      </div>
                    </div>
                    <hr class="separator">
                    <div class="form-group row">
                      <label for="conv-not1" class="col-sm-4 col-form-label">
                        <strong>Nota approvazione</strong>
                      </label>
                      <div class="col-sm-8">
                        <input type="text" name="co-not1" id="conv-not1" autocomplete="on" class="form-control contact">
                      </div>
                    </div>
                    <!-- Data 2 -->
                    <hr class="separator">
                    <div class="form-group row">
                      <label for="conv-dat2" class="col-sm-4 col-form-label">
                        <strong>2<sup>a</sup> data approvazione</strong>
                      </label>
                      <div class="col-sm-8">
                        <input type="text" name="co-dat2" id="conv-dat2" class="contact calendarData">
                      </div>
                    </div>
                    <hr class="separator">
                    <div class="form-group row">
                      <label for="conv-not2" class="col-sm-4 col-form-label">
                        <strong>2<sup>a</sup> nota approvazione</strong>
                      </label>
                      <div class="col-sm-8">
                        <input type="text" name="co-not2" id="conv-not2" autocomplete="on" class="form-control contact">
                      </div>
                    </div>
                    <!-- Data 3 -->
                    <hr class="separator">
                    <div class="form-group row">
                      <label for="conv-dat3" class="col-sm-4 col-form-label">
                        <strong>
                          Data sottoscrizione
                          <span class="text-danger">*</span>
                        </strong>
                      </label>
                      <div class="col-sm-8">
                        <input type="text" name="co-dat3" id="conv-dat3" required class="contact bg-warning calendarData">
                      </div>
                    </div>
                    <hr class="separator">
                    <div class="form-group row">
                      <label for="conv-not3" class="col-sm-4 col-form-label">
                        <strong>Nota sottoscrizione</strong>
                      </label>
                      <div class="col-sm-8">
                        <input type="text" name="co-not3" id="conv-not3" autocomplete="on" class="form-control contact">
                      </div>
                    </div>
                    <!-- Data 4 -->
                    <hr class="separator">
                    <div class="form-group row">
                      <label for="conv-dat4" class="col-sm-4 col-form-label">
                        <strong>
                          Data scadenza
                          <span class="text-danger">*</span>
                        </strong>
                      </label>
                      <div class="col-sm-8">
                        <input type="text" name="co-dat4" id="conv-dat4" required class="contact bg-warning calendarData">
                      </div>
                    </div>
                    <hr class="separator">
                    <div class="form-group row">
                      <label for="conv-not4" class="col-sm-4 col-form-label">
                        <strong>Nota scadenza</strong>
                      </label>
                      <div class="col-sm-8">
                        <input type="text" name="co-not4" id="conv-not4" autocomplete="on" class="form-control contact">
                      </div>
                    </div>
                    <!-- Carico bollo -->
                    <hr class="separator">
                    <div class="form-group row">
                      <label for="conv-boll" class="col-sm-4 col-form-label">
                        <strong>Ripartizione spese di bollo</strong>
                      </label>
                      <div class="col-sm-8">
                        <hr class="short">
                        <select name="co-boll" id="conv-boll" class=" contact">
                          <option value="">-- Dato non presente --</option>
                          <option value="100">Imposte di bollo 100% a carico dell'ateneo</option>
                          <option value="50">50% in carico all'ateneo e 50% in carico ai contraenti</option>
                          <option value="0">Imposte di bollo interamente a carico dei contraenti</option>
                        </select>
                      </div>
                    </div>
                    <!-- Bollo pagato -->
                    <hr class="separator">
                    <div class="form-group row">
                      <label for="conv-pago" class="col-sm-4 col-form-label">
                        <strong>Bollo pagato</strong>
                      </label>
                      <div class="col-sm-8">
                        <hr class="short">
                        <select name="co-pago" id="conv-pago" class=" contact">
                          <option value=""  class="bg-warning">-- Dato non presente --</option>
                          <option value="1" class="text-success">Bollo pagato</option>
                          <option value="0" class="text-danger">Bollo non pagato</option>
                        </select>
                      </div>
                    </div>
                    <!-- Submit -->
                    <hr class="separator">
                    <div class="form-group row">
                     <label for="form-submit" class="col-sm-4 col-form-label">
                        <strong>Salva</strong>
                      </label>
                      <div class="col-sm-2">
                        <button type="submit" name="upd-conv-form-submit" id="openModalBtn" class="bg-info text-white btn btn-sm btn-primary form-control contact">
                          <i class="fa fa-save"></i> &nbsp;
                          Inserisci
                        </button>
                      </div>
                    </div>
                  </fieldset>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <script src="${initParam.urlDirFrameworks}jquery/jquery.min.js"></script>
    <!-- Bootstrap Datepicker JS -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.10.0/js/bootstrap-datepicker.min.js"></script>
    <!-- Italian localization (example) -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.10.0/locales/bootstrap-datepicker.it.min.js"></script>
    <script>
    $(document).ready(function(){
        $('input[type="text"].calendarData').datepicker({
          language: 'it',             // Italian (matches bootstrap-datepicker.it.min.js)
          format: 'dd/mm/yyyy',       // Italian date format
          autoclose: true,            // Close calendar after selection
          todayHighlight: true,       // Highlight today's date
          weekStart: 1                // Monday = 1 (Bootstrap Datepicker specific)
        });
    });
    </script>
