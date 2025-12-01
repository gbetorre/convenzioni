<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="scopes" value="${requestScope.finalita}" scope="page" />
<c:set var="conv" value="${requestScope.convenzione}" scope="page" />
    <style>
    .col-form-label {
      cursor: pointer; /* changes mouse pointer to hand */
      position: relative;
      display: flex;
      align-items: center;
      padding-right: 20px; /* space for the arrow */
      white-space: nowrap;
    }
    
    .col-form-label::after {
      content: '';
      position: absolute;
      top: 50%;
      left: calc(100% + 5px); /* start right after label text */
      right: 20px; /* end just before the arrow */
      height: 2px;
      background-color: #007bff;
      transform: translateY(-50%);
      z-index: 1;
      transition: background-color 0.3s ease;
    }
    
    .col-form-label::before {
      content: '';
      position: absolute;
      top: 50%;
      right: 0;
      width: 0;
      height: 0;
      border-top: 6px solid transparent;
      border-bottom: 6px solid transparent;
      border-left: 10px solid #007bff;
      transform: translateY(-50%);
      z-index: 2;
      transition: border-left-color 0.3s ease;
    }
    
    .col-form-label:hover::before {
      border-left-color: #ff4500; /* change arrow color on hover, for example, to orange */
      filter: drop-shadow(1px 1px 1px rgba(0,0,0,0.3)); /* optional shadow for emphasis */
      transition: border-left-color 0.3s ease;
    }
    
    .col-form-label:hover::after {
      background-color: #ff4500; /* same highlight color */
      transition: background-color 0.3s ease;
    }
    </style>
    <div class="page-heading">
      <div class="container">
        <div class="row">
          <div class="col-lg-8">
            <div class="top-text header-text">
              <h6>Modifica Convenzione</h6>
              <h2><c:out value="${conv.titolo}" /></h2>
            </div>
          </div>
        </div>
      </div>
    </div>
  

    <div class="container">
      <div class="row">
        <div class="col-lg-12">
          <div class="inner-content">
            <div class="row">
              <div class="col-lg-12 align-self-center">
                <form name="upd-conv" id="contact" action="" method="post" class="contact">
                  <fieldset>
                    <legend><!-- Dati identificativi della convenzione --></legend>
                    <div class="form-group row">
                      <label for="conv-title" class="col-sm-4 col-form-label">
                        <strong>Titolo Convenzione</strong>
                      </label>
                      <div class="col-sm-8">
                        <input type="text" name="co-titl" id="conv-title" value="${conv.titolo}" autocomplete="on" required class="form-control contact">
                      </div>
                    </div>
                    <hr class="short">
                    <div class="form-group row">
                      <label for="conv-prot" class="col-sm-4 col-form-label">
                        <strong>Repertorio</strong>
                      </label>
                      <div class="col-sm-8">
                        <input type="text" name="co-prot" id="conv-prot" value="${conv.numRepertorio}" autocomplete="on" required class="form-control contact">
                      </div>
                    </div>
                    <hr class="short">
                    <div class="form-group row">
                      <label for="conv-info" class="col-sm-4 col-form-label">
                        <strong>Oggetto</strong>
                      </label>
                      <div class="col-sm-8">
                        <textarea name="co-info" id="conv-info" required class="form-control contact"><c:out value="${conv.informativa}" /></textarea> 
                      </div>
                    </div>
                    <hr class="short">
                    <div class="form-group row">
                      <label for="conv-scop" class="col-sm-4 col-form-label">
                        <strong>Finalit&agrave;</strong>
                      </label>
                      <div class="col-sm-8">&nbsp;
                        <ul name="co-fine" id="conv-scop" class="contact">
                        <c:forEach var="scope" items="${pageScope.scopes}">
                          <li>
                            <input type="checkbox" name="option1" value="cars" ${scope.informativa}>
                            <span><c:out value="${scope.nome}" /></span>
                          </li>
                        </c:forEach>
                         
                          <li><input type="checkbox" name="option2" value="aparmtents" checked><span>Apartments</span></li>
                          <li><input type="checkbox" name="option3" value="shopping"><span>Shopping</span></li>
                          <li><input type="checkbox" name="option4" value="food"><span>Food &amp; Life</span></li>
                          <li><input type="checkbox" name="option5" value="traveling"><span>Traveling</span></li>
                        </ul>
                      </div>
                    </div>
                    
                    <div class="form-group row">
                      <button type="submit" id="form-submit" class="main-button contact"><i class="fa fa-paper-plane"></i> Aggiorna</button>
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
