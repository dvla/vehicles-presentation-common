require(["jquery"], function($) {
    $(':submit').on('click', function(e) {
        var runTimes;

        if ( $(this).hasClass("disabled") ) {
            return false;
        }

        $(this).html('Loading').addClass('loading-action disabled');
        runTimes = 0;
        setInterval(function() {
            if ( runTimes < 3 ){
                $(':submit').append('.');
                runTimes++;
            } else {
                runTimes = 0;
                $(':submit').html('Loading');
            }
        }, 1000);
    });
});
