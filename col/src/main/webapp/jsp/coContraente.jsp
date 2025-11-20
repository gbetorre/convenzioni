<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="cont" value="${requestScope.contraente}" scope="page" />
    <div class="page-heading contractor">
      <div class="container">
        <div class="row">
          <div class="col-lg-12">
            <div class="top-text header-text">
              <h2>Scheda Contraente</h2>
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
                                    <h5><strong>Denominazione</strong></h5>
                                    <hr class="short">
                                    <p><c:out value="${cont.nome}" /></p>
                                  </div>
                                  <div class="col-lg-3">
                                    <div class="text-icon">
                                      <h5>
                                        <img src="${initParam.urlDirImages}listing-icon-heading.png" alt=""> 
                                        <strong>Tipologia</strong>
                                      </h5>
                                      <hr class="short">
                                    </div>
                                    <span class="list-item">
                                      <c:out value="${cont.note}" />
                                    </span>
                                  </div>
                                </div>
                              </div>
                              <hr class="separator">
                              <div class="col-lg-12">
                                <div class="row">
                                  <h5><strong>Convenzioni</strong></h5>
                                  <dl>
                                    <dt></dt>
                                  <c:forEach var="conv" items="${cont.convenzioni}" varStatus="status">
                                    <dd>
                                      <a href="${initParam.appName}/?q=co&id=${conv.id}" class="btn-sm text-primary" title="${conv.note}">
                                        <c:out value="${conv.titolo}" />
                                      </a>
                                    </dd>
                                  </c:forEach>
                                  <c:if test="${empty cont.convenzioni}">
                                    <span class="list-item">
                                      <cite>Nessuna convenzione gi&agrave; assegnata</cite>
                                    </span>
                                  </c:if>
                                  </dl>
                                </div>
                              </div>
                              <hr class="separator">
                              <div class="col-lg-12">
                                <div class="row">
                                  <h5><strong>Codice Fiscale</strong></h5>
                                  <hr class="short">
                                  <p><c:out value="${cont.codiceFiscale}" /></p>
                                </div>
                              </div>
                              <hr class="separator">
                              <div class="col-lg-12">
                                <div class="row">
                                  <div class="col-lg-12">
                                    <h5><strong>Partita IVA</strong></h5>
                                    <hr class="short">
                                    <p><c:out value="${cont.partitaIva}" /></p>
                                  </div>
                                </div>
                              </div>
                              <hr class="separator">
                              <div class="col-lg-12">
                                <div class="row">
                                  <h5><strong>Contatto</strong></h5>
                                  <hr class="short">
                                  <p>
                                    <a href="mailto:${cont.email}">
                                      <c:out value="${cont.email}" />
                                    </a>
                                  </p>
                                </div>
                              </div>
                              <c:if test="${not empty cont.informativa}">
                              <hr class="separator">
                                <div class="row">
                                  <div class="col-lg-12">
                                    <h5><strong>Note</strong></h5>
                                    <hr class="short">
                                    <p><c:out value="${cont.informativa}" /></p>
                                  </div>
                                </div>
                              </c:if>
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
