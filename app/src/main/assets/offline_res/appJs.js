/**
 * Created by huangshaobin on 2017/3/28.
 */
function calliOS(Msg) {
    var message = {
        'method' : 'hello',
        'param1' : Msg,
    };
    window.webkit.messageHandlers.webViewApp.postMessage(message);
}
function callJS() {
    var message = {
        'method' : 'Call JS',
    };
    window.webkit.messageHandlers.webViewApp.postMessage(message);
}
function callJSMsg(Msg) {
    var message = {
        'method' : 'Call JS Msg',
        'param1' : Msg,
    };
    window.webkit.messageHandlers.webViewApp.postMessage(message);
}
function jsFunc() {
    alert('Hello World');
}
function iOSCallJSON() {
    alert('call no msg');
}
function iOSCallJS(Msg) {
    alert(Msg);
}