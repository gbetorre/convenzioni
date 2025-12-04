<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="it_IT"/>
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
              <h6>Modifica Convenzione</h6>
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
                    <legend>Modifica Convenzione</legend>
                    <input type="hidden" name="co-id" id="conv-id" value="${conv.id}" />
                    <!-- Titolo -->
                    <div class="form-group row">
                      <label for="conv-title" class="col-sm-4 col-form-label">
                        <strong>
                          Titolo Convenzione
                          <span class="text-danger"> *</span>
                        </strong>
                      </label>
                      <div class="col-sm-8">
                        <input type="text" name="co-titl" id="conv-title" value="${conv.titolo}" autocomplete="on" required class="form-control contact bg-warning">
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
                        <input type="text" name="co-prot" id="conv-prot" value="${conv.numRepertorio}" autocomplete="on" required class="form-control contact bg-warning">
                      </div>
                    </div>
                    <!-- Oggetto -->
                    <hr class="short">
                    <div class="form-group row">
                      <label for="conv-info" class="col-sm-4 col-form-label">
                        <strong>Oggetto</strong>
                      </label>
                      <div class="col-sm-8">
                        <textarea name="co-info" id="conv-info" class="contact"><c:out value="${conv.informativa}" /></textarea> 
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
                        <c:forEach var="scope" items="${pageScope.conv.finalita}">
                          <li>
                            <input type="checkbox" name="co-scop" value="${scope.id}" ${scope.informativa}>
                            <span><c:out value="${scope.nome}" /></span>
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
                        <textarea name="co-note" id="conv-note" class="contact"><c:out value="${conv.note}" /></textarea> 
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
                        <input type="text" name="co-dat1" id="conv-dat1" required class="contact bg-warning calendarData" value="<fmt:formatDate value="${conv.dataApprovazione}" pattern="dd/MM/yyyy" />">
                        <!-- input type="hidden" name="co-hid1" id="conv-dat1_sql" value="<fmt:formatDate value="${conv.dataApprovazione}" pattern="yyyy-MM-dd" />" -->
                      </div>
                    </div>
                    <hr class="separator">
                    <div class="form-group row">
                      <label for="conv-not1" class="col-sm-4 col-form-label">
                        <strong>Nota approvazione</strong>
                      </label>
                      <div class="col-sm-8">
                        <input type="text" name="co-not1" id="conv-not1" value="${conv.notaApprovazione}" autocomplete="on" class="form-control contact">
                      </div>
                    </div>
                    <!-- Data 2 -->
                    <hr class="separator">
                    <div class="form-group row">
                      <label for="conv-dat2" class="col-sm-4 col-form-label">
                        <strong>2<sup>a</sup> data approvazione</strong>
                      </label>
                      <div class="col-sm-8">
                        <input type="text" name="co-dat2" id="conv-dat2" class="contact calendarData" value="<fmt:formatDate value="${conv.dataApprovazione2}" pattern="dd/MM/yyyy" />">
                      </div>
                    </div>
                    <hr class="separator">
                    <div class="form-group row">
                      <label for="conv-not2" class="col-sm-4 col-form-label">
                        <strong>2<sup>a</sup> nota approvazione</strong>
                      </label>
                      <div class="col-sm-8">
                        <input type="text" name="co-not2" id="conv-not2" value="${conv.notaApprovazione2}" autocomplete="on" class="form-control contact">
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
                        <input type="text" name="co-dat3" id="conv-dat3" required class="contact bg-warning calendarData" value="<fmt:formatDate value="${conv.dataSottoscrizione}" pattern="dd/MM/yyyy" />">
                      </div>
                    </div>
                    <hr class="separator">
                    <div class="form-group row">
                      <label for="conv-not3" class="col-sm-4 col-form-label">
                        <strong>Nota sottoscrizione</strong>
                      </label>
                      <div class="col-sm-8">
                        <input type="text" name="co-not3" id="conv-not3" value="${conv.notaSottoscrizione}" autocomplete="on" class="form-control contact">
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
                        <input type="text" name="co-dat4" id="conv-dat4" required class="contact bg-warning calendarData" value="<fmt:formatDate value="${conv.dataScadenza}" pattern="dd/MM/yyyy" />">
                      </div>
                    </div>
                    <hr class="separator">
                    <div class="form-group row">
                      <label for="conv-not4" class="col-sm-4 col-form-label">
                        <strong>Nota scadenza</strong>
                      </label>
                      <div class="col-sm-8">
                        <input type="text" name="co-not4" id="conv-not4" value="${conv.notaScadenza}" autocomplete="on" class="form-control contact">
                      </div>
                    </div>
                    <!-- Carico bollo -->
                    <c:set var="all" value="" scope="page" />
                    <c:set var="fifty" value="" scope="page" />
                    <c:set var="zero" value="" scope="page" />
                    <c:set var="none" value="" scope="page" />
                    <c:choose>
                      <c:when test="${conv.caricoBollo eq 100.0}">
                        <c:set var="all" value="selected" scope="page" />
                      </c:when>
                      <c:when test="${conv.caricoBollo eq 50.0}">
                        <c:set var="fifty" value="selected" scope="page" />
                      </c:when>
                      <c:when test="${conv.caricoBollo eq 0.0}">
                        <c:set var="zero" value="selected" scope="page" />
                      </c:when>
                      <c:when test="${empty conv.caricoBollo}">
                        <c:set var="none" value="selected" scope="page" />
                      </c:when>
                    </c:choose>
                    <hr class="separator">
                    <div class="form-group row">
                      <label for="conv-boll" class="col-sm-4 col-form-label">
                        <strong>Ripartizione spese di bollo</strong>
                      </label>
                      <div class="col-sm-8">
                        <hr class="short">
                        <select name="co-boll" id="conv-boll" class=" contact">
                          <option value="100" ${all}>Imposte di bollo 100% a carico dell'ateneo</option>
                          <option value="50" ${fifty}>50% in carico all'ateneo e 50% in carico ai contraenti</option>
                          <option value="0" ${zero}>Imposte di bollo a carico dei contraenti</option>
                          <option value="" ${none}>-- Dato non presente --</option>
                        </select>
                      </div>
                    </div>
                    <!-- Bollo pagato -->
                    <c:set var="payed" value="" scope="page" />
                    <c:set var="toPay" value="" scope="page" />
                    <c:choose>
                      <c:when test="${conv.pagato}">
                        <c:set var="payed" value="selected" scope="page" />
                      </c:when>
                      <c:when test="${not conv.pagato}">
                        <c:set var="toPay" value="selected" scope="page" />
                      </c:when>
                      <c:otherwise>
                        <c:set var="undef" value="selected" scope="page" />
                      </c:otherwise>
                    </c:choose>
                    <hr class="separator">
                    <div class="form-group row">
                      <label for="conv-pago" class="col-sm-4 col-form-label">
                        <strong>Bollo pagato</strong>
                      </label>
                      <div class="col-sm-8">
                        <hr class="short">
                        <select name="co-pago" id="conv-pago" class=" contact">
                          <option value="1" ${payed} class="text-success">Bollo pagato</option>
                          <option value="0" ${toPay} class="text-danger">Bollo non pagato</option>
                          <option value="" ${undef}  class="bg-warning">-- Dato non presente --</option>
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
                          Aggiorna
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
