require(["jquery"], function($) {
    function areCookiesEnabled(){
        var cookieEnabled = (navigator.cookieEnabled) ? true : false;

        if (typeof navigator.cookieEnabled == "undefined" && !cookieEnabled)
        {
            document.cookie="testcookie";
            cookieEnabled = (document.cookie.indexOf("testcookie") != -1) ? true : false;
        }
        return (cookieEnabled);
    }

    function opt(v){
        if (typeof v == 'undefined') return [];
        else return[v];
    }
});
