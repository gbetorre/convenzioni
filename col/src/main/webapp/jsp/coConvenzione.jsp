<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="it_IT"/>
<c:set var="conv" value="${requestScope.convenzione}" scope="page" />
    <div class="page-heading">
      <div class="container">
        <div class="row">
          <div class="col-lg-12">
            <div class="top-text header-text">
              <h2>Dettagli Convenzione</h2>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="category-post" style="margin-bottom:20px;">
      <div class="container">
        <div class="row">
          <div class="col-lg-12">
            <div class="naccs">
              <div class="grid">
                <div class="row">
                  <hr class="separator">
                  <div class="col-lg-12">
                    <ul class="nacc">
                      <li class="active">
                        <div>
                          <div class="thumb">
                            <div class="row">
                              <div class="col-lg-12">
                                <div class="row">
                                  <div class="col-lg-9">
                                    <h5><strong>Titolo</strong></h5>
                                    <hr class="short">
                                    <p><c:out value="${conv.titolo}" /></p>
                                  </div>
                                  <div class="col-lg-3">
                                    <div class="text-icon">
                                      <h5>
                                        <img src="${initParam.urlDirImages}listing-icon-heading.png" alt=""> 
                                        <strong>Repertorio</strong>
                                      </h5>
                                      <hr class="short">
                                    </div>
                                    <span class="list-item">
                                      <c:out value="${conv.numRepertorio}" />
                                    </span>
                                  </div>
                                </div>
                              </div>
                              <hr class="separator">
                              <div class="col-lg-12">
                                <div class="row">
                                  <h5><strong>Contraenti</strong></h5>
                                  <dl>
                                    <dt></dt>
                                  <c:forEach var="contraente" items="${conv.contraenti}" varStatus="status">
                                    <dd><c:out value="${contraente.nome}" /></dd>
                                  </c:forEach>
                                  <c:if test="${empty conv.contraenti}">
                                    <span class="list-item">
                                      <cite>Nessun contraente gi&agrave; assegnato</cite>
                                    </span>
                                  </c:if>
                                  </dl>
                                </div>
                              </div>
                              <hr class="separator">
                              <div class="col-lg-12">
                                <div class="row">
                                  <h5><strong>Tipologia</strong></h5>
                                  <hr class="short">
                                  <p><c:out value="${conv.tipo}" /></p>
                                </div>
                              </div>
                              <hr class="separator">
                              <div class="col-lg-12">
                                <div class="general-info">
                                  <div class="row">
                                    <div class="col-lg-12">
                                      <h5><strong>Oggetto</strong></h5>
                                      <hr class="short">
                                      <p><c:out value="${conv.informativa}" /></p>
                                    </div>
                                  </div>
                                  <div class="row">
                                    <div class="col-lg-12">
                                      <h5><strong>Data approvazione</strong></h5>
                                      <hr class="short">
                                      <fmt:formatDate value="${conv.dataApprovazione}" pattern="dd MMMMM yyyy" />
                                      <br>
                                      <span class="list-item">
                                        <c:out value="${conv.notaApprovazione}" />
                                      </span>
                                    </div>
                                  </div>
                                <c:if test="${not empty conv.dataApprovazione2}">
                                  <hr class="separator">
                                  <div class="row">
                                    <div class="col-lg-12">
                                      <h5><strong>Data seconda approvazione</strong></h5>
                                      <hr class="short">
                                      <fmt:formatDate value="${conv.dataApprovazione2}" pattern="dd MMMMM yyyy" />
                                      <br>
                                      <span class="list-item">
                                        <c:out value="${conv.notaApprovazione2}" />
                                      </span>
                                    </div>
                                  </div>
                                </c:if>
                                  <hr class="separator">
                                  <div class="row">
                                    <div class="col-lg-12">
                                      <h5><strong>Data sottoscrizione</strong></h5>
                                      <hr class="short">
                                      <fmt:formatDate value="${conv.dataSottoscrizione}" pattern="dd MMMMM yyyy" />
                                      <br>
                                      <span class="list-item">
                                        <c:out value="${conv.notaSottoscrizione}" />
                                      </span>
                                    </div>
                                  </div>
                                  <hr class="separator">
                                  <div class="row">
                                    <div class="col-lg-12">
                                      <h5><strong>Data scadenza</strong></h5>
                                      <hr class="short">
                                      <fmt:formatDate value="${conv.dataScadenza}" pattern="dd MMMMM yyyy" />
                                      <br>
                                      <span class="list-item">
                                        <c:out value="${conv.notaScadenza}" />
                                      </span>
                                    </div>
                                  </div>
                                <c:if test="${not empty conv.caricoBollo}">
                                  <hr class="separator">
                                  <div class="row">
                                    <div class="col-lg-12">
                                      <h5><strong>Ripartizione spese di bollo</strong></h5>
                                      <hr class="short">
                                      <p>
                                    <c:choose>
                                      <c:when test="${conv.caricoBollo eq 100.0}">
                                        Imposte di bollo 100% a carico all'ateneo
                                      </c:when>
                                      <c:when test="${conv.caricoBollo eq 50.0}">
                                        50% in carico all'ateneo e 50% in carico ai contraenti
                                      </c:when>
                                      <c:when test="${conv.caricoBollo eq 0.0}">
                                        Imposte di bollo a carico dei contraenti
                                      </c:when>
                                      <c:otherwise>
                                        <c:out value="${conv.caricoBollo}" />% a carico dell'ateneo
                                      </c:otherwise>
                                    </c:choose>  
                                      </p>
                                    </div>
                                  </div>
                                </c:if>
                                <c:if test="${not empty conv.pagato}">
                                  <hr class="separator">
                                  <div class="row">
                                    <div class="col-lg-12">
                                      <h5><strong>Bollo pagato</strong></h5>
                                      <hr class="short">
                                      <p><c:out value="${conv.pagato}" /></p>
                                    </div>
                                  </div>
                                </c:if>
                                <c:if test="${not empty conv.note}">
                                  <hr class="separator">
                                  <div class="row">
                                    <div class="col-lg-12">
                                      <h5><strong>Note</strong></h5>
                                      <hr class="short">
                                      <p><c:out value="${conv.note}" /></p>
                                    </div>
                                  </div>
                                </c:if>
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                      </li>                 
                    </ul>
                  </div>
                  <hr class="separator">
                  <div>
                    <div class="col-lg-12">
                      <div class="col text-center">
                        <div class="main-white-button">
                          <a href="${initParam.appName}/?q=co&op=ins&obj=cont&data=rel&id=${conv.id}" class="bg-info text-white btn btn-sm btn-warning">
                            <i class="fa fa-plus bg-warning text-grey"></i> Assegna Contraenti
                          </a>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>          
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <script src="${initParam.urlDirFrameworks}jquery/jquery.min.js"></script>
