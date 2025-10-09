<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="it_IT"/>
<c:set var="conv" value="${requestScope.convenzione}" scope="page" />
    <div class="page-heading">
      <div class="container">
        <div class="row">
          <div class="col-lg-8">
            <div class="top-text header-text">
              <h6><c:out value="${conv.titolo}" /></h6>
              <h2>Dettagli convenzione</h2>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="category-post">
      <div class="container">
        <div class="row">
          <div class="col-lg-12">
            <div class="naccs">
              <div class="grid">
                <div class="row">
                  <div class="col-lg-12">
                    <div class="menu">
                      <div class="first-thumb active">
                        <div class="thumb">
                          <h4>CONTRAENTI</h4>    
                        </div>
                      </div>
                    <c:forEach var="contraente" items="${conv.contraenti}" varStatus="status">
                      <div>
                        <div class="thumb">                 
                          <span class="icon">
                            <img src="${initParam.urlDirImages}search-icon-02.png" alt="">
                          </span>
                          <h4 class="small-text"><c:out value="${contraente.nome}" /></h4>
                        </div>
                      </div>
                    </c:forEach>
                    <c:if test="${empty conv.contraenti}">
                      <div>                 
                        <h4>Nessun contraente<br> gi&agrave; assegnato</h4>
                      </div>
                    </c:if>
                    </div>
                  </div>
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
                                    <h4>Tipo convenzione</h4>
                                    <p><c:out value="${conv.tipo}" /></p>
                                  </div>
                                  <div class="col-lg-3">
                                    <div class="text-icon">
                                      <h4>
                                        <img src="${initParam.urlDirImages}listing-icon-heading.png" alt=""> 
                                        N. Repertorio
                                      </h4>
                                    </div>
                                    <span class="list-item">
                                      <c:out value="${conv.numRepertorio}" />
                                    </span>
                                  </div>
                                </div>
                              </div>
                              <hr class="separator">
                              <div class="col-lg-12">
                                <div class="general-info">
                                  <div class="row">
                                    <div class="col-lg-12">
                                      <h4>Descrizione</h4>
                                      <p><c:out value="${conv.informativa}" /></p>
                                    </div>
                                  </div>
                                  <div class="row">
                                    <div class="col-lg-12">
                                      <h4>Data approvazione</h4>
                                      <p><fmt:formatDate value="${conv.dataApprovazione}" pattern="dd MMMMM yyyy" /></p>
                                      <span class="list-item"><c:out value="${conv.notaApprovazione}" /></span>
                                    </div>
                                  </div>
                                <c:if test="${not empty conv.dataApprovazione2}">
                                  <div class="row">
                                    <div class="col-lg-12">
                                      <h4>Data seconda approvazione</h4>
                                      <p><fmt:formatDate value="${conv.dataApprovazione2}" pattern="dd MMMMM yyyy" /></p>
                                      <span class="list-item"><c:out value="${conv.notaApprovazione2}" /></span>
                                    </div>
                                  </div>
                                </c:if>
                                  <div class="row">
                                    <div class="col-lg-12">
                                      <h4>Data sottoscrizione</h4>
                                      <p><fmt:formatDate value="${conv.dataSottoscrizione}" pattern="dd MMMMM yyyy" /></p>
                                      <span class="list-item"><c:out value="${conv.notaSottoscrizione}" /></span>
                                    </div>
                                  </div>
                                  <div class="row">
                                    <div class="col-lg-12">
                                      <h4>Data scadenza</h4>
                                      <p><fmt:formatDate value="${conv.dataScadenza}" pattern="dd MMMMM yyyy" /></p>
                                      <span class="list-item"><c:out value="${conv.notaScadenza}" /></span>
                                    </div>
                                  </div>
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                      </li>                 
                      <li>
                        <div>
                          <div class="thumb">
                            <div class="row">
                              <div class="col-lg-12">
                                <div class="top-content">
                                  <div class="row">
                                    <div class="col-lg-12 ">
                                      <div class="main-white-button d-flex justify-content-center">
                                        <a href="${initParam.appName}/?q=co&op=ins&obj=cont&data=rel&id=${conv.id}">
                                          <i class="fa fa-plus"></i>Aggiungi Contraenti
                                        </a>
                                      </div>
                                    </div>
                                  </div>
                                </div>      
                              </div>
                            </div>
                          </div>
                        </div>
                      </li>
                    </ul>
                  </div>          
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <script src="${initParam.urlDirFrameworks}jquery/jquery.min.js"></script>
