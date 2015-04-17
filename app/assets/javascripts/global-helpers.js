require(["jquery"], function($) {

    // All the functions in this file are added as properties of the window object thus putting them in the global scope
    window.areCookiesEnabled = function() {
        var cookieEnabled = (navigator.cookieEnabled) ? true : false;

        if (typeof navigator.cookieEnabled == "undefined" && !cookieEnabled)
        {
            document.cookie="testcookie";
            cookieEnabled = (document.cookie.indexOf("testcookie") != -1) ? true : false;
        }
        return (cookieEnabled);
    };

    window.opt = function(v) {
        if (typeof v == 'undefined') return [];
        else return[v];
    };

    // TODO: this one does no date padding so we should see where it is used and replace it with the one below
    window.autoFillTodaysDate = function(day, month, year, checkboxId, dayId, monthId, yearId) {
        var dod_elm = document.getElementById(checkboxId);
        var dod_day = document.getElementById(dayId);
        var dod_month = document.getElementById(monthId);
        var dod_year = document.getElementById(yearId);

        if (dod_elm.checked === false) {
            dod_day.value = '';
            dod_month.value = '';
            dod_year.value = '';
        } else {
            dod_day.value = day;
            dod_month.value = month;
            dod_year.value = year;
        }
    };

    // TODO: this is used by inputDayMonthYearTextBox so should remain
    window.AutoFillTodaysDate = function (day, month, year, checkboxId, dayId, monthId, yearId) {
        var dod_elm = document.getElementById(checkboxId);
        var dod_day = document.getElementById(dayId);
        var dod_month = document.getElementById(monthId);
        var dod_year = document.getElementById(yearId);

        var padded_day = ('0' + day).substr(('0' + day).length - 2, 2);
        var padded_month = ('0' + month).substr(('0' + month).length - 2, 2);

        if (dod_elm.checked === false) {
            dod_day.value = '';
            dod_month.value = '';
            dod_year.value = '';
        } else {
            dod_day.value = padded_day;
            dod_month.value = padded_month;
            dod_year.value = year;
        }
    };
});
