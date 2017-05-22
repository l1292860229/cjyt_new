/*
 *	ExMobi4.x+ JS
 *	Version	: 1.0.0
 *	Author	: fiberlezp
 *	Email	:
 *	Weibo	:
 *	Copyright 2015 (c)
 */

$weixin = {};
(function() {
	var _ready_callback = [];
	var _error_callback = [];

	$weixin.ready = function(callback) {
		if (typeof callback == 'function') {
			_ready_callback.push(callback);
		}
	};

	$weixin.error = function(callback) {
		if (typeof callback == 'function') {
			_error_callback.push(callback);
		}
	};

	wx.ready(function() {
		$weixin.isWeixinFunction = true;
		for(var key in _ready_callback){
			if(typeof _ready_callback[key] == 'function'){
				_ready_callback[key]();
			}
		}
	});

	wx.error(function(res) {
		$weixin.isWeixinFunction = false;
		for(var key in _error_callback){
			if(typeof _ready_callback[key] == 'function'){
				_ready_callback[key](res);
			}
		}
	});

	$weixin.isWeixinFunction = false;

	$weixin.configUrl = null;

	$weixin.uploadUrl = null;

	$weixin.init = function(opt) {

		$weixin.configUrl = opt.configUrl;
		$weixin.uploadUrl = opt.uploadUrl;

		document.write("<script src='" + $weixin.configUrl + "'></" + "script>");
	};

	_unsupportAPI = [
		'checkJsApi',
		'onMenuShareTimeline',
		'onMenuShareAppMessage',
		'onMenuShareQQ',
		'onMenuShareWeibo',
		'hideMenuItems',
		'showMenuItems',
		'hideAllNonBaseMenuItem',
		'showAllNonBaseMenuItem',
		'translateVoice',
		'onVoiceRecordEnd',
		'onVoicePlayEnd',
		'downloadVoice',
		'downloadImage',
		'getNetworkType',
		'openLocation',
		'hideOptionMenu',
		'showOptionMenu',
		'closeWindow',
		'chooseWXPay',
		'openProductSpecificView',
		'addCard',
		'chooseCard',
		'openCard'
	];

	for (var key in _unsupportAPI) {
		$weixin[_unsupportAPI[key]] = (function(key) {
			return function(opt) {
				if ($weixin.isWeixinFunction) {
					wx[_unsupportAPI[key]] && wx[_unsupportAPI[key]](opt);
				} else {
					alert("微信JS尚未初始化完全或当前环境并非微信浏览器");
				}
			};
		})(key);
	}


})();

(function() {

	var _imageObj = null;

	var _fileChoice = null;

	var _imageChoice = null;

	var _ajax = null;

	var _imageLocalIds = {};

	$weixin.chooseImage = function(opt) { //{success: function (res) {var localIds = res.localIds; // 返回选定照片的本地ID列表，localId可以作为img标签的src属性显示图片}}

		if ($weixin.isWeixinFunction) {
			wx.chooseImage(opt);
			return true;
		}

		if (!_imageChoice) {
			_imageChoice = new ImageChoice();
		}

		_imageChoice.pwidth = 600;

		_imageChoice.nums = 1;

		_imageChoice.onCallback = onImageChoice;

		_imageChoice.path = "res:/";

		_imageChoice.start();

		function onImageChoice(selectPaths) {

			var _localIds = new Array();

			for (var key in selectPaths) {

				var randomId = null;

				do {
					randomId = Math.floor(Math.random() * 1000);
				} while (_imageLocalIds[randomId]);

				var path = selectPaths[key];

				_imageLocalIds[randomId] = path;

				_localIds.push(randomId);
			}

			opt.success({
				localIds: _localIds
			});
		}

	};

	$weixin.previewImage = function(opt) { //{current: '', // 当前显示的图片链接	urls: [] // 需要预览的图片链接列表}
		if ($weixin.isWeixinFunction) {
			wx.previewImage(opt);
			return true;
		}

		var jsonData = {
			"config": {
				"type": "news",

				"bgColor": "#000000",

				"cache": 1,

				"index": 2,
			}

		};

		var imgData = new Array();

		for (var key in opt.urls) {
			imgData.push({
				"src": opt.urls[key]
			});
		}

		jsonData.data = imgData;

		ClientUtil.openImageList(jsonData);
	};

	$weixin.uploadImage = function(opt) { //{localId: '', // 需要上传的图片的本地ID，由chooseImage接口获得	isShowProgressTips: 1, // 默认为1，显示进度提示	success: function (res) {var serverId = res.serverId; // 返回图片的服务器端ID	}}
		if ($weixin.isWeixinFunction) {
			wx.uploadImage(opt);
			return true;
		}

		if (!opt.localId || !_imageLocalIds[opt.localId]) {
			return false;
		}

		var arrayData = new Array();

		var fileObject = {
			type: 1,

			name: "file",

			value: _imageLocalIds[opt.localId]
		};

		var typeObject = {
			type: 0,

			name: "filetype",

			value: "image"
		};

		arrayData.push(fileObject);
		arrayData.push(typeObject);

		$weixin.getRspProcess = function(data) {
			var dataObject = eval("(" + data.responseText + ")");

			if (!data || !dataObject.media_id) {
				opt.success();
				return false;
			}
			opt.success({
				serverId: dataObject.media_id
			});
		};

		$weixin.getRspFailFunction = function(data) {
			alert(data.status);
		};

		var dataOpt = {
			url: $weixin.uploadUrl,

			method: "POST",

			data: arrayData,

			isBlock: true,

			successFunction: '$weixin.getRspProcess',

			failFunction: '$weixin.getRspFailFunction'
		};

		var ajax = new DirectFormSubmit(dataOpt);

		ajax.send();
	};

})();

(function() {
	var _recordObj = null;

	var _audioPlayer = null;

	var _recordLocalIds = {};

	$weixin.startRecord = function() {
		if ($weixin.isWeixinFunction) {
			wx.startRecord();
			return true;
		}

		if (!_recordObj) {
			_recordObj = MediaUtil.getRecord();
		}

		alert(_recordObj);

		recordObj.path = 'res:/';
		/*设置生成文件路径*/

		recordObj.maxtime = 12000;
		/*设置最大支持录音时间*/

		recordObj.startRecord();
		/*开始启动录音*/

	};

	$weixin.stopRecord = function(opt) { //{success: function (res) {var localId = res.localId;}}
		if ($weixin.isWeixinFunction) {
			wx.stopRecord(opt);
			return true;
		}

		alert(_recordObj.isRecord());

		if (!_recordObj || !_recordObj.isRecord()) { /*是否正在录音*/
			return;
		}

		recordObj.stopRecord();
		/*关闭录音*/

		var randomId = null;

		do {
			randomId = Math.floor(Math.random() * 1000);
		} while (_recordLocalIds[randomId]);

		_recordLocalIds[randomId] = {
			localId: randomId,
			path: recordObj.value,
			filename: recordObj.objName,
			recordtime: recordObj.recordtime
		};

		opt.success({
			localId: _recordLocalIds[randomId]
		});

	};

	$weixin.playVoice = function(opt) { //{localId: '' // 需要播放的音频的本地ID，由stopRecord接口获得}
		if ($weixin.isWeixinFunction) {
			wx.playVoice(opt);
			return true;
		}

		if (!_audioPlayer) {
			_audioPlayer = MediaUtil.getAudioPlayer();
		}

		if (!_recordLocalIds[randomId]) {
			alert("voice doesn't exist");
			return false;
		}

		_audioPlayer.start(_recordLocalIds[opt.localId].path, false);
		/*播放本地音频*/

	};

	$weixin.pauseVoice = function(opt) { //{localId: '' // 需要暂停的音频的本地ID，由stopRecord接口获得}
		if ($weixin.isWeixinFunction) {
			wx.pauseVoice(opt);
			return true;
		}

		_audioPlayer.pause();
	};

	$weixin.stopVoice = function(opt) { //{localId: '' // 需要停止的音频的本地ID，由stopRecord接口获得}
		if ($weixin.isWeixinFunction) {
			wx.stopVoice(opt);
			return true;
		}

		_audioPlayer.stop();
	};

	$weixin.uploadVoice = function(opt) { //{localId: '', // 需要上传的音频的本地ID，由stopRecord接口获得	isShowProgressTips: 1, // 默认为1，显示进度提示	success: function (res) {var serverId = res.serverId; // 返回音频的服务器端ID}}
		if ($weixin.isWeixinFunction) {
			wx.uploadVoice(opt);
			return true;
		}

		if (!opt.localId || !_recordLocalIds[opt.localId]) {
			return false;
		}

		var arrayData = new Array();

		var fileObject = {
			type: 1,

			name: "file",

			value: _recordLocalIds[opt.localId]
		};

		var typeObject = {
			type: 0,

			name: "filetype",

			value: "voice"
		};

		arrayData.push(fileObject);
		arrayData.push(typeObject);

		$weixin.getVoiceRspProcess = function(data) {
			var dataObject = eval("(" + data.responseText + ")");

			if (!data || !dataObject.media_id) {
				opt.success();
				return false;
			}
			opt.success({
				serverId: dataObject.media_id
			});
		};

		$weixin.getVoiceRspFailFunction = function(data) {
			alert(data.status);
		};

		var dataOpt = {
			url: $weixin.uploadUrl,

			method: "POST",

			data: arrayData,

			isBlock: true,

			successFunction: '$weixin.getVoiceRspProcess',

			failFunction: '$weixin.getVoiceRspFailFunction'
		};

		var ajax = new DirectFormSubmit(dataOpt);

		ajax.send();
	};

})();

(function() {
	var _position = null;

	$weixin.getLocation = function(opt) { //{success: function (res) {var latitude = res.latitude; // 纬度，浮点数，范围为90 ~ -90	var longitude = res.longitude; // 经度，浮点数，范围为180 ~ -180。	var speed = res.speed; // 速度，以米/每秒计	var accuracy = res.accuracy; // 位置精度}}
		if ($weixin.isWeixinFunction) {
			wx.getLocation(opt);
			return true;
		}

		if (!_position) {
			_position = new Gps();
		}

		position.onCallback = function() { /*设置定位回调函数*/
			if (!_position.isSuccess()) { /*返回定位是否成功*/
				alert(_position.objName + "定位失败");
				_position.stopPosition();
				return false;
			}

			opt.success({
				latitude: position.latitude,
				longitude: position.longitude,
				locationtime: position.locationtime,
				accuracy: position.accuracy,
				altitude: position.altitude,
				speed: position.speed
			});
		};

		position.setTimeout(2000);
		/*设置定位超时间*/

		position.startPosition();
		/*开始定位*/
	};

})();

(function() {
	var _decode = null;

	$weixin.scanQRCode = function(opt) { //opt={needResult: 0, // 默认为0，扫描结果由微信处理，1则直接返回扫描结果，scanType: ["qrCode","barCode"], // 可以指定扫二维码还是一维码，默认二者都有success: function (res) {var result = res.resultStr; // 当needResult 为 1 时，扫码返回的结果}}
		if ($weixin.isWeixinFunction) {
			wx.scanQRCode(opt);
			return true;
		}

		if (!_decode) {
			_decode = new Decode();
		}

		function decodeCallback() {
			if (!decode.isSuccess()) { /*返回解码是否成功*/
				opt.success();
				return;
			}
			opt.success({
				resultStr: _decode.result
			});
		}


		_decode.onCallback = decodeCallback;
		/*设置解码结束回调函数*/

		_decode.startDecode();
		/*开始解码*/

	};
})();