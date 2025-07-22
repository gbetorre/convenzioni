<%@ page contentType="text/html;" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet" href="<c:out value="${initParam.urlDirectoryStili}" />bootstrap/plugin/registrationform.css" type="text/css" />
<c:if test="${error}">
  <div class="alert alert-danger alert-dismissible fade show" role="alert">
    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
      <span aria-hidden="true">&times;</span>
    </button>
    <strong>ATTENZIONE: </strong>${msg}
  </div>
</c:if>
<c:catch var="exception">

    <div class="row justify-content-center">
      <div class="col-3 text-center">
        <img class="logo" src="${initParam.urlDirectoryImmagini}/logo2.png" />
      </div>
    </div>
    <br />
    <div class="form-body">
      <div class="row">
        <div class="form-holder">
          <div class="form-content">
            <div class="form-items">
              <h3>Login</h3>
              <p>Inserisci username e password.</p>
              <form class="requires-validation" action="${initParam.appName}/auth" method="post" novalidate>
                <div class="col-md-12">
                  <input id="usr" class="form-control" type="text" name="usr" placeholder="Username" required>
                  <div class="valid-feedback">Username field is valid!</div>
                  <div class="invalid-feedback">Username field cannot be blank!</div>
                </div>

                <div class="col-md-12">
                  <input class="form-control" type="password" name="pwd" placeholder="Password" id="pwd" required>
                  <div class="valid-feedback">Password field is valid!</div>
                  <div class="invalid-feedback">Password field cannot be blank!</div>
                </div>
                 

                            <div class="form-button mt-3 centerlayout">
                                <button id="submit" type="submit" class="btn btn-primary">Login</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <%--@ include file="about.jspf"--%>

</c:catch>
  <p style="color:red;"><c:out value="${exception}" /></p>
