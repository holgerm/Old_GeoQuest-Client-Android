CSS: github.css 

# Aufbau

* Consists of HTML-Code and Template-Codes
* Template-Codes can have parameters to individualize them
* should contain <base href="YOUR_SERVER_URI">
* does not use a CSS file from Qeevee
* JQuery DataTables can be included in your template page, so all table contents can be flexible
* you can define the CSS classes we use in your CSS files to change how the GeoQuest content looks

# Template Codes

possible parameters and use-cases on individual pages:

* [`%%_GEOQUEST_NAV_LI_%%`](https://github.com/qeevee/GQ_Specification/wiki/GEOQUEST_NAV_LI) (navigation)
* [`%%_GEOQUEST_CONTENT_CODE_START_%%`](https://github.com/qeevee/GQ_Specification/wiki/GEOQUEST_CONTENT_CODE_START) (content of subpage)
* [`%%_GEOQUEST_CONTENT_TITLE_%%`](https://github.com/qeevee/GQ_Specification/wiki/GEOQUEST_CONTENT_TITLE) (title of subpage)
* [`%%_GEOQUEST_USER_INFO_%%`](https://github.com/qeevee/GQ_Specification/wiki/GEOQUEST_USER_INFO) (user information)
* [`%%_GEOQUEST_PORTAL_NAME_%%`](https://github.com/qeevee/GQ_Specification/wiki/GEOQUEST_PORTAL_NAME) (portal name)
* [`%%_GEOQUEST_SERVER_VERSION_%%`](https://github.com/qeevee/GQ_Specification/wiki/GEOQUEST_SERVER_VERSION) (server version number)
* [`%%_GEOQUEST_DEFAULT_COLOR_%%`](https://github.com/qeevee/GQ_Specification/wiki/GEOQUEST_DEFAULT_COLOR) (color from layout preferences)
* [`%%_GEOQUEST_YOUR_LOGO_URL_%%`](https://github.com/qeevee/GQ_Specification/wiki/GEOQUEST_YOUR_LOGO_URL) (branding image from layout preferences)
* [`%%_GEOQUEST_HEADER_FUNCTIONS_%%`](https://github.com/qeevee/GQ_Specification/wiki/GEOQUEST_HEADER_FUNCTIONS) (<head> functions)
* [`%%_GEOQUEST_SERVER_URL_%%`](https://github.com/qeevee/GQ_Specification/wiki/GEOQUEST_SERVER_URL) (URI of Qeevee servers)
* [`%%_GEOQUEST_SCRIPT_DIV_%%`](https://github.com/qeevee/GQ_Specification/wiki/GEOQUEST_SCRIPT_DIV) (DIV to copy)
* [`%%_GEOQUEST_CONTENT_CODE_END_%%`](https://github.com/qeevee/GQ_Specification/wiki/GEOQUEST_CONTENT_CODE_END) (end of page)

# Verschlüsselung

* should be SSL-protected
* should be password-protected
* decryption currently only supported by html forms on your server
* you need to insert the needed login information in the portal encryption settings
* if you have multiple forms on your site and the login form isn't the first in the code, you need to insert the DOM-IDs of all form objects

# CSS Klassen

* based on Bootstrap 2.3
* which CSS Classes are used by the Qeevee contents