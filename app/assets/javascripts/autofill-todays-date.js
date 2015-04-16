function AutoFillTodaysDate(day, month, year, checkboxId, dayId, monthId, yearId) {
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
}