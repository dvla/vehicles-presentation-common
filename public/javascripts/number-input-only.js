//prevent non-numeric characters from being entered into the field (decimal points excepted)
//See http://demosthenes.info/blog/748/The-HTML5-number-Input
function isNumber(event) {
    if (event) {
        var charCode = (event.which) ? event.which : event.keyCode;
        if (charCode != 190 && charCode > 31 &&
            (charCode < 48 || charCode > 57) &&
            (charCode < 96 || charCode > 105) &&
            (charCode < 37 || charCode > 40) &&
            charCode != 110 && charCode != 8 && charCode != 46 )
            return false;
        }
    return true;
}