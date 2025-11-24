    /* Basic script code for the cookie consent banner */

    (function() {
      // Utility to get cookie by name
      function getCookie(name) {
        const match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
        return match ? decodeURIComponent(match[2]) : null;
      }
  
      // Utility to set cookie
      function setCookie(name, value, days) {
        const expires = new Date(Date.now() + days*864e5).toUTCString();
        document.cookie = name + '=' + encodeURIComponent(value) + '; expires=' + expires + '; path=/';
      }
  
      const consentName = 'cookieConsent';
  
      function showBanner() {
        document.getElementById('cookieConsent').style.display = 'block';
      }
      function hideBanner() {
        document.getElementById('cookieConsent').style.display = 'none';
      }
  
      // Check if consent already given
      if (getCookie(consentName) !== 'accepted') {
        showBanner();
      }
  
      document.getElementById('acceptCookies').addEventListener('click', function() {
        setCookie(consentName, 'accepted', 365); // 1 year expiration
        hideBanner();
  
        // Custom callback can go here, e.g. enabling analytics scripts
        console.log('User accepted cookies');
      });
    })();
