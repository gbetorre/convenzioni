(function (window, document, $) { // in private anonymous namespace, $ maps to jQuery

    var identificator = 'cookieConsent';
	var currentPreferences = null;
	var showDialog = false;

    // general function to set cookie value
    function setCookie(cname, cookieValue, expires) {
		document.cookie = cname + '=' + encodeURIComponent(cookieValue) + expires + '; path=/';
	}
	
    // general function to get cookie value
	function getCookie(cname) {
	    var name = cname + "=";
	    var decodedCookie = decodeURIComponent(document.cookie);
	    var ca = decodedCookie.split(';');
	    
	    for (var i = 0; i < ca.length; i++) {
	    	var c = ca[i];
	        while (c.charAt(0) == ' ') {
	            c = c.substring(1);
	        }
	        if (c.indexOf(name) == 0) {
	            return c.substring(name.length, c.length);
	        }
	    }
	    return "";
	}

    function hideCookieDialog() {
		$('.cookieDialog').css('display','none');
		$('.cookieOverlay').hide();
	}

    function showCookieDialog() {
		$('.cookieDialog').css('display','table');
		$('.cookieOverlay').show();
	}

    // creates timestamp and save cookie preferences to cookies
    // runs GTM consent update 
    // triggers custom GTM event 'consentFormSubmit'
    // hides dialog
    function saveCookiePreferences(preferences) {
        var now = new Date();
		var cookieValue = preferences + '&consentTimestamp=' + now.toGMTString();
		var expires = getExpiresValues();

        setCookie(identificator, cookieValue, expires);
        runScripts();
        triggerGTMEvent('consentFormSubmit');
        hideCookieDialog();
    }

	function getExpiresValues() {
		var expirationDate = new Date();
        expirationDate.setFullYear(expirationDate.getFullYear() + 1)
		return '; expires=' + expirationDate.toGMTString();
	}

    // run GTM consent update based on current consent settings
    function runScripts() {
		if (currentPreferences != null) {
	    	if (currentPreferences["marketing"] == 1) {
				gtag('consent', 'update', {
							'ad_storage': 'granted', 
							'analytics_storage': 'granted',
							'personalization_storage': 'granted'});
			} else if (currentPreferences["marketing"] == 0) {
				gtag('consent', 'update', {
							'ad_storage': 'denied', 
							'analytics_storage': 'denied',
							'personalization_storage': 'denied'});
                }
		}
	}

    // push GTM event to data layer
    function triggerGTMEvent(eventName) {
        window.dataLayer.push({
			'event' : eventName
		});
    }

    function initCookieDialog() {
        // hide on overlay or close click
		$('.cookieDialog, .cookieOverlay, .cookieClose').click(function(e) {
			hideCookieDialog();
			e.preventDefault();
		});
		// dont do anything if clicking on dialog content
		$('.cookieBox').click(function(e) {
			e.stopPropagation();
		});

        // show cookie dialog when clicking on element with class showCookieDialog
        // so user can change his saved preferences
        // preloads form with current consent
        $('.showCookieDialog').click(function(e) {
            loadData();
			showCookieDialog();
			e.preventDefault();
		});
		
        // accept all - sets all cookies to be allowed
		// YOU CAN ADD MORE COOKIE TYPES HERE IF NECESSARY
		$('#coookieDialog_acceptAll_button').click(function(e) {
			currentPreferences = {'necessary': 1, 'marketing': 1};
			var preferences = 'preferences=' + JSON.stringify(currentPreferences);

			saveCookiePreferences(preferences);
			e.preventDefault();
		});
		
        // save preferences - saves cookie consent based on submitted form
		// YOU CAN ADD MORE COOKIE TYPES HERE IF NECESSARY
		$('#coookieDialog_save_button').click(function(e) {
			var marketingConsent = $('#marketingCookiesConsent_input').is(":checked");
			currentPreferences = {'necessary': 1, 'marketing': marketingConsent ? 1 : 0}
			var preferences = 'preferences=' + JSON.stringify(currentPreferences);

			saveCookiePreferences(preferences);
			e.preventDefault();
		});
	}

    // load saved cookie consent and preset form
    function loadData() {
		var params = getCookie(identificator).split("&");
		// go though cookie value params and find preferences
		for (var i = 0; i < params.length; i++) {
	    	var param = params[i];
	        var name_value = param.split("=");
	        
	        if (name_value[0] == "preferences") {
	            currentPreferences = JSON.parse(name_value[1]);
	        }
	    }
	    
		setCookieConsentForm()
	    
	}

	// set checkbox values in form based on user preferences
	// YOU CAN ADD MORE COOKIE TYPES HERE IF NECESSARY
	function setCookieConsentForm() {
		// set marketing consent to false as default if there are no cookie preferences
		if (currentPreferences == null) {
			showDialog = true;
            $('#marketingCookiesConsent_input').prop('checked', true);
		// otherwise set marketing cookie consent checkbox value based on the user preferences
		} else {
            if (currentPreferences["marketing"] == 1) {
				$('#marketingCookiesConsent_input').prop('checked', true);
			} else if (currentPreferences["marketing"] == 0) {
				$('#marketingCookiesConsent_input').prop('checked', false);
			}
        }
	}

    function init() {
        loadData();
		initCookieDialog();
		
		if(showDialog) {
			showCookieDialog();
		} else {
			runScripts();
		}
	}
	
	init();

})(window, window.document, jQuery); // end of private namespace