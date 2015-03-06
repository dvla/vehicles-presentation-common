$(function() {
    $('.optional-field').hide();

    $('.expandable-optional .option-visible').on('click', function() {
        $(this).closest('.expandable-optional').find('.optional-field').show();
    });
    $('.expandable-optional .option-invisible').on('click', function() {
        $(this).closest('.expandable-optional').find('.optional-field').hide();
    });

    $('.expandable-optional .option-visible:checked').click();
})
