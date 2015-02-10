function onLoadConditionalDisplay(id, isDisplayed) {
    var element = document.getElementById(id);
    if (isDisplayed) {
        element.style.dislay = 'inherit';
    } else {
        element.style.display = 'none';
    }
}

/*
 * Display a field that fires when the link or button is clicked.
 *
 * ids - The ids of the links/buttons you want to track.
 */
function onEventChangeDisplay(event, idOfClickable, idOfDisplayable, styleDisplay) {
    var elementOfClickable = document.getElementById(idOfClickable);
    if (elementOfClickable) {
        if (elementOfClickable.addEventListener) {
            /* addEventListener is a W3 standard that is implemented in the majority of other browsers (FF, Webkit, Opera, IE9+) */
            elementOfClickable.addEventListener(event, function (e) {

                var elementOfDisplayable = document.getElementById(idOfDisplayable);
                if (elementOfDisplayable) {
                    console.log("onEventChangeDisplay addEventListener so on event " + event + " change: " + idOfDisplayable + " to display style: " + styleDisplay);
                    elementOfDisplayable.style.display = styleDisplay;
                } else {
                    console.error("element idOfDisplayable: " + idOfDisplayable + " not found on page");
                }
            });
        } else if (elementOfClickable.attachEvent) {
            /* attachEvent can only be used on older trident rendering engines ( IE5+ IE5-8*) */
            elementOfClickable.attachEvent('on' + event, function (e) {

                var elementOfDisplayable = document.getElementById(idOfDisplayable);
                if (elementOfDisplayable) {
                    console.log("onEventChangeDisplay attachEvent so on event " + 'on' + event + " change: " + idOfDisplayable + " to display style: " + styleDisplay);
                    elementOfDisplayable.style.display = styleDisplay;
                } else {
                    console.error("element idOfDisplayable: " + idOfDisplayable + " not found on page");
                }
            });
        } else {
            console.error("element does not support addEventListener or attachEvent");
            return false;
        }
    } else {
        console.error("elementOfClickable id: " + idOfClickable + " not found on page");
        return false;
    }
}