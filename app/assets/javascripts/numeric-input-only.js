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

function isFleetNumber(event) {
    var backspace = 8;
    var tab = 9;
    var end = 35;
    var home = 36;
    var leftArrow = 37;
    var downArrow = 40;
    var deleteCode = 46;
    var zero = 48;
    var nine = 57;
    var numpad0 = 96;
    var numpad9 = 105;
    var hyphen = 173;
    if (event) {
        var charCode = (event.which) ? event.which : event.keyCode;
        console.log(charCode)
        if (charCode != hyphen && charCode != tab && charCode != end &&
            charCode != home && charCode != backspace && charCode != deleteCode &&
            (charCode < zero || charCode > nine) &&
            (charCode < numpad0 || charCode > numpad9) &&
            (charCode < leftArrow || charCode > downArrow)
           )
           return false;
        }
    return true;
}
