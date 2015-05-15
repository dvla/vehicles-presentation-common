define([], function() {
   return function(testFx, onReady, timeOutMillis, assert) {
      var defaultMaxTimeout = 3000
      var maxtimeOutMillis = timeOutMillis ? timeOutMillis : defaultMaxTimeout,
          start = new Date().getTime(),
          condition = false,
          interval = setInterval(function() {
              if ( (new Date().getTime() - start < maxtimeOutMillis) && !condition ) {
                  // If not time-out yet and condition not yet fulfilled
                  condition = (typeof(testFx) === "string" ? eval(testFx) : testFx()); //< defensive code
              } else {
                  if(!condition) {
                      // If condition still not fulfilled (timeout but condition is 'false')
                      console.log("'waitFor()' timeout");
                      assert.ok(false, "Condition not met after timeout " + timeOutMillis);
                      clearInterval(interval);
                  } else {
                      // Condition fulfilled (timeout and/or condition is 'true')
                      console.log("'waitFor()' finished in " + (new Date().getTime() - start) + "ms.");
                      typeof(onReady) === "string" ? eval(onReady) : onReady(); //< Do what it's supposed to do once the condition is fulfilled
                      clearInterval(interval); //< Stop this interval
                  }
              }
          }, 50); //< repeat check every 50ms
   };
});