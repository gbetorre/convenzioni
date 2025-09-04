<%@ page contentType="text/html;" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${error}">
  <div class="alert alert-danger alert-dismissible fade show" role="alert">
    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
      <span aria-hidden="true">&times;</span>
    </button>
    <strong>ATTENZIONE: </strong>${msg}
  </div>
</c:if>
<c:catch var="exception">
    <link rel="stylesheet" href="${initParam.urlDirFrameworks}neumorphism/style.css" type="text/css" />
    <div class="login-container">
      <div class="login-card">
        <div class="login-header">
          <div class="neu-icon">
            <div class="icon-inner">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                  <circle cx="12" cy="7" r="4"/>
              </svg>
            </div>
          </div>
            <h2>Convenzioni On Line</h2>
            <p>Accedi per continuare</p>
        </div>
            
            <div class="success-message" id="successMessage">
                <div class="success-icon neu-icon">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
                        <polyline points="20 6 9 17 4 12"/>
                    </svg>
                </div>
                <h3>Success!</h3>
                <p>Redirecting to your dashboard...</p>
            </div>
            
            <form class="login-form" id="loginForm" action="${initParam.appName}/auth" method="post" novalidate>
                <div class="form-group">
                    <div class="input-group neu-input">
                        <input type="text" id="email" name="usr" required autocomplete="email" placeholder=" ">
                        <label for="email">Username</label>
                        <div class="input-icon">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
                                <polyline points="22,6 12,13 2,6"/>
                            </svg>
                        </div>
                    </div>
                    <span class="error-message" id="emailError"></span>
                </div>

                <div class="form-group">
                    <div class="input-group neu-input password-group">
                        <input type="password" id="password" name="pwd" required autocomplete="current-password" placeholder=" ">
                        <label for="password">Password</label>
                        <div class="input-icon">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                                <path d="M7 11V7a5 5 0 0110 0v4"/>
                            </svg>
                        </div>
                        <button type="button" class="password-toggle neu-toggle" id="passwordToggle" aria-label="Toggle password visibility">
                            <svg class="eye-open" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                                <circle cx="12" cy="12" r="3"/>
                            </svg>
                            <svg class="eye-closed" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/>
                                <line x1="1" y1="1" x2="23" y2="23"/>
                            </svg>
                        </button>
                    </div>
                    <span class="error-message" id="passwordError"></span>
                </div>
                <%--
                <div class="form-options">
                    <div class="remember-wrapper">
                        <input type="checkbox" id="remember" name="remember">
                        <label for="remember" class="checkbox-label">
                            <div class="neu-checkbox">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
                                    <polyline points="20 6 9 17 4 12"/>
                                </svg>
                            </div>
                            Remember me
                        </label>
                    </div>
                    <a href="#" class="forgot-link">Forgot password?</a>
                </div>
                --%>
                <button type="submit" class="neu-button login-btn">
                    <span class="btn-text">Login</span>
                    <div class="btn-loader">
                        <div class="neu-spinner"></div>
                    </div>
                </button>
            </form>

            <div class="divider">
                <div class="divider-line"></div>
                <span>COL [Convenzioni On Line]</span>
                <div class="divider-line"></div>
            </div>

            <div class="social-login">
                <button type="button" class="social-btn neu-large">
                    Versione: 0.11 
                </button>
                <button type="button" class="social-btn neu-large">
                    Data rilascio: 05/09/2025
                </button>
                <button type="button" class="social-btn neu-social">
                   &copy; 2025
                </button>
            </div>
<%--
            <div class="signup-link">
                <p>Don't have an account? <a href="#">Sign up</a></p>
            </div>
 --%>
        </div>
    </div>
<%--
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
    <script src="${initParam.urlDirFrameworks}neumorphism/form-utils.js" type="text/javascript"></script>
    <script src="${initParam.urlDirFrameworks}neumorphism/script.js" type="text/javascript"></script>
</c:catch>
  <p style="color:red;"><c:out value="${exception}" /></p>
