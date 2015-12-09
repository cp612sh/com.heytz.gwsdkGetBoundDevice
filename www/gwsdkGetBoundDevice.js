/**
 * Created by chaipeixi on 8/26/15.
 */
var exec = require('cordova/exec');

exports.getBoundDevicesWithUid = function (appid, token, specialProductKeys, uid, success, error) {
    exec(success, error, "gwsdkGetBoundDevice", "start", [appid, token, specialProductKeys, uid]);
};
exports.dealloc = function (win, fail) {
    exec(win, fail, "gwsdkGetBoundDevice", "start", []);
};
