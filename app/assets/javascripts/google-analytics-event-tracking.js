/*
* Add an event listener to a html element.
*
* inputId - the id of the html field you want to track.
*
* event - A String that specifies the name of the event e.g. 'click', 'change', etc.
* Note: Do not use the "on" prefix. For example, use "click" instead of "onclick".
* For a list of all HTML DOM events, look at our complete HTML DOM Event Object Reference http://www.w3schools.com/jsref/dom_obj_event.asp.
*/
function track(inputId, event) {
    var element = document.getElementById(inputId);
    if (element) {
        if (element.addEventListener) {
            /* addEventListener is a W3 standard that is implemented in the majority of other browsers (FF, Webkit, Opera, IE9+) */
            element.addEventListener(event, function (e) {
                console.log("trackClick addEventListener send event: " + location.href + ", id: " + inputId + ", event " + event);
                ga('send', 'event', location.href, inputId, event);
            });
        } else if (element.attachEvent) {
            /* attachEvent can only be used on older trident rendering engines ( IE5+ IE5-8*) */
            element.attachEvent('on' + event, function (e) {
                /*console.log("trackClick attachEvent send event: " + location.href + ", id: " + inputId + ", event " + 'on' + event);*/
                ga('send', 'event', location.href, inputId, event);
            });
        } else {
            console.error("element does not support addEventListener or attachEvent");
            return false;
        }
    } else {
        console.error("element id: " + inputId + " not found on page");
        return false;
    }
}

/*
* Add a listener that fires when the link or button is clicked.
*
* ids - The ids of the links/buttons you want to track.
*/
function trackClick(ids) {
    for (var i = 0; i < ids.length; i++) {
        console.log("attach 'click' listener to: " + ids[i]);
        track(ids[i], 'click');
    }
}

/*
* Add a listener that fires when the user leaves the text field (e.g by tabbing out of it) and the text is different to
* what was there before.
*
* ids - The ids of the text fields you want to track.
*/
function trackChange(ids) {
    for (var i = 0; i < ids.length; i++) {
        console.log("attach 'change' listener to: " + ids[i]);
        track(ids[i], 'change');
    }
}