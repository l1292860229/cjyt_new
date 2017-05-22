//启动agile
var $config = {
	weixinPayURI: location.protocol + "//" + location.host + '/process/service/weixinlite/weixinpay?showwxpaytitle=1',
	weixinImageURI: location.protocol + "//" + location.host + '/process/service/weixinlite/weixinimage'
};
A.launch({
	readyEvent: 'ready', //触发ready的事件，在ExMobi中为plusready
	backEvent: 'backmonitor',
	crossDomainHandler: function(opts) {
		$util.server(opts);
	}
});