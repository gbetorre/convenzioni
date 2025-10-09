<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="conv" value="${requestScope.convenzione}" scope="page" />
<c:set var="contractors" value="${requestScope.contraenti}" scope="page" />
    <style>
      select {
        height: 400px;
        width: 100%;
      }
      .btn-move {
        display: block;
        width: 60px;
        margin: 10px auto;
        font-size: 1.5rem;
        text-align: center;
        user-select: none;
      }
    </style>
    <div class="main-banner padding-small">
      <div class="container">
        <h6 class="text-white text-center">
          &nbsp;<c:out value="${conv.titolo}" />
        </h6>
        <div>
          <ul class="categories list-group list-group-horizontal d-flex justify-content-center text-center">
          <c:forEach var="contraente" items="${conv.contraenti}">
            <li>
              <a href="category.html">
                <span class="icon">
                  <img src="${initParam.urlDirImages}search-icon-02.png" alt="Home">
                </span> 
                <span class="smaller-text">
                  <c:out value="${contraente.nome}" />
                </span>
              </a>
            </li>
          </c:forEach>
          <c:if test="${empty conv.contraenti}">
            <li>
              <a href="category.html">
                <span class="icon">
                  <img src="${initParam.urlDirImages}search-icon-01.png" alt="Home">
                </span> 
                <span class="smaller-text">
                  Nessun contraente gi&agrave; assegnato
                </span>
              </a>
            </li>
          </c:if>
          </ul>
        </div>
        <form accept-charset="ISO-8859-1" id="contractor-rel" action="" method="post">
          <div class="row align-items-center contact">
            <div class="col-lg-12">
              <div class="top-text header-text">
                <h2>Assegna Contraenti</h2>
              </div>
            </div>
            <div class="col-5">
              <select id="leftSelect" multiple class="form-select dataTable" aria-label="Left multiple select">
              <c:forEach var="contractor" items="${contractors}">
                <option value="${contractor.id}" title="${contractor.nome} [tipo: ${contractor.note}]"><c:out value="${contractor.nome}" /></option>
              </c:forEach>
              </select>
            </div>
            <div class="col-2 text-center">
              <button id="btnRight" type="button" class="btn btn-warning btn-move" disabled aria-label="Move selected to right">&gt;</button>
              <hr class="short">
              <button id="btnLeft" type="button" class="btn btn-warning btn-move" disabled aria-label="Move selected to left">&lt;</button>
            </div>
            <div class="col-5">
              <select id="rightSelect" name="co-cont" multiple class="form-select dataTable" aria-label="Right multiple select"></select>
            </div>
          </div>
          <div class="row mt-3 contact">
            <div class="col text-center">
            <fieldset>
              <button type="submit" id="submitBtn" class="main-button"><i class="fa  fa-bookmark-o" aria-hidden="true"></i> Salva</button>
              </fieldset>
            </div>
          </div>
        </form>
      </div>
    </div>
    <script src="${initParam.urlDirFrameworks}jquery/jquery.min.js"></script>
    <script>
      const leftSelect = document.getElementById("leftSelect");
      const rightSelect = document.getElementById("rightSelect");
      const btnRight = document.getElementById("btnRight");
      const btnLeft = document.getElementById("btnLeft");
      const form = document.getElementById("contractor-rel");
  
      function updateButtons() {
        btnRight.disabled = leftSelect.selectedOptions.length === 0;
        btnLeft.disabled = rightSelect.selectedOptions.length === 0;
      }
  
      function moveSelected(source, target) {
        const options = Array.from(source.selectedOptions);
        options.forEach(opt => {
          target.appendChild(opt);
          opt.selected = false;
        });
        updateButtons();
      }
  
      leftSelect.addEventListener("change", updateButtons);
      rightSelect.addEventListener("change", updateButtons);
  
      btnRight.addEventListener("click", () => {
        moveSelected(leftSelect, rightSelect);
      });
  
      btnLeft.addEventListener("click", () => {
        moveSelected(rightSelect, leftSelect);
      });
  
      form.addEventListener("submit", e => {
        e.preventDefault();
        for (const opt of rightSelect.options) {
          opt.selected = true;
        }
        //alert("Form submitted with selected options on right!");
        // Send form here if needed, e.g. form.submit();
        form.submit();
      });
  
      updateButtons();
    </script>
  
