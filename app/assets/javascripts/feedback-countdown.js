
// Feedback form character countdown

function updateCountdown() {
    // 500 is the max message length
    var remaining = 500 - $('#feedback_field textarea').val().length;
    $('.character-countdown').text(remaining + ' characters remaining.');
}
$(document).ready(function($) {
    updateCountdown();
    $('#feedback_field textarea').change(updateCountdown);
    $('#feedback_field textarea').keyup(updateCountdown);
});