window.setupRTVAlarm = function(hour, minutes, days, onSuccessCallback, onFailureCallback) {
  cordova.exec(
    onSuccessCallback,
    onFailureCallback,
    "AlarmPlugin",
    "setup",
    [hour, minutes, days]
  );
};

window.cancelRTVAlarm = function(args, onSuccessCallback, onFailureCallback) {
  cordova.exec(
    onSuccessCallback,
    onFailureCallback,
    "AlarmPlugin",
    "cancel",
    [args]
  );
};