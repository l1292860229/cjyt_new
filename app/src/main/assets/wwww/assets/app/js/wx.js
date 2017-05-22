/*
 * 注意：
 * 1. 所有的JS接口只能在公众号绑定的域名下调用，公众号开发者需要先登录微信公众平台进入“公众号设置”的“功能设置”里填写“JS接口安全域名”。
 * 2. 如果发现在 Android 不能分享自定义内容，请到官网下载最新的包覆盖安装，Android 自定义分享接口需升级至 6.0.2.58 版本及以上。
 * 3. 完整 JS-SDK 文档地址：http://mp.weixin.qq.com/wiki/7/aaa137b55fb2e0456bf8dd9148dd613f.html
 *
 * 如有问题请通过以下渠道反馈：
 * 邮箱地址：weixin-open@qq.com
 * 邮件主题：【微信JS-SDK反馈】具体问题
 * 邮件内容说明：用简明的语言描述问题所在，并交代清楚遇到该问题的场景，可附上截屏图片，微信团队会尽快处理你的反馈。
 */

// 1 判断当前版本是否支持指定 JS 接口，支持批量判断


$('.menu_li').on(A.options.clickEvent, function(e) {
	var selector = $(e.currentTarget).data('href');
	var scroll = A.Scroll('#weixin_demo_article');
	scroll.scrollToElement(selector, 500);
	A.Controller.aside();
	scroll.refresh();
});

$weixin.ready(function() {
	$('#checkJsApi').on(A.options.clickEvent, function() {
		$weixin.checkJsApi({
			jsApiList: ['checkJsApi', 'onMenuShareTimeline', 'onMenuShareAppMessage', 'onMenuShareQQ', 'onMenuShareWeibo', 'hideMenuItems', 'showMenuItems', 'hideAllNonBaseMenuItem', 'showAllNonBaseMenuItem', 'translateVoice', 'startRecord', 'stopRecord', 'onVoiceRecordEnd', 'playVoice', 'pauseVoice', 'stopVoice', 'uploadVoice', 'downloadVoice', 'chooseImage', 'previewImage', 'uploadImage', 'downloadImage', 'getNetworkType', 'openLocation', 'getLocation', 'hideOptionMenu', 'showOptionMenu', 'closeWindow', 'scanQRCode', 'chooseWXPay', 'openProductSpecificView', 'addCard', 'chooseCard', 'openCard'],
			success: function(res) {
				alert(JSON.stringify(res));
			}
		});
	});

	// 2. 分享接口
	// 2.1 监听“分享给朋友”，按钮点击、自定义分享内容及分享结果接口
	$('#onMenuShareAppMessage').on(A.options.clickEvent, function() {
		$weixin.onMenuShareAppMessage({
			title: 'ExMobi+Agile Lite微信开发',
			desc: '原生UI组件UIXML引擎、HTML5引擎、原生插件引擎三大引擎并驾齐驱，跨平台多分辨率支持。',
			link: 'http://www.exmobi.cn/product.html',
			imgUrl: 'http://www.exmobi.cn/images/exmobi5.png',
			trigger: function(res) {
				// 不要尝试在trigger中使用ajax异步请求修改本次分享的内容，因为客户端分享操作是一个同步操作，这时候使用ajax的回包会还没有返回
				alert('用户点击发送给朋友');
			},
			success: function(res) {
				alert('已分享');
			},
			cancel: function(res) {
				alert('已取消');
			},
			fail: function(res) {
				alert(JSON.stringify(res));
			}
		});
		alert('已注册获取“发送给朋友”状态事件');
	});

	// 2.2 监听“分享到朋友圈”按钮点击、自定义分享内容及分享结果接口
	$('#onMenuShareTimeline').on(A.options.clickEvent, function() {
		$weixin.onMenuShareTimeline({
			title: 'ExMobi+Agile Lite微信开发',
			link: 'http://www.exmobi.cn/product.html',
			imgUrl: 'http://www.exmobi.cn/images/exmobi5.png',
			trigger: function(res) {
				// 不要尝试在trigger中使用ajax异步请求修改本次分享的内容，因为客户端分享操作是一个同步操作，这时候使用ajax的回包会还没有返回
				alert('用户点击分享到朋友圈');
			},
			success: function(res) {
				alert('已分享');
			},
			cancel: function(res) {
				alert('已取消');
			},
			fail: function(res) {
				alert(JSON.stringify(res));
			}
		});
		alert('已注册获取“分享到朋友圈”状态事件');
	});

	// 2.3 监听“分享到QQ”按钮点击、自定义分享内容及分享结果接口
	$('#onMenuShareQQ').on(A.options.clickEvent, function() {
		$weixin.onMenuShareQQ({
			title: 'ExMobi+Agile Lite微信开发',
			desc: '原生UI组件UIXML引擎、HTML5引擎、原生插件引擎三大引擎并驾齐驱，跨平台多分辨率支持。',
			link: 'http://www.exmobi.cn/product.html',
			imgUrl: 'http://www.exmobi.cn/images/exmobi5.png',
			trigger: function(res) {
				alert('用户点击分享到QQ');
			},
			complete: function(res) {
				alert(JSON.stringify(res));
			},
			success: function(res) {
				alert('已分享');
			},
			cancel: function(res) {
				alert('已取消');
			},
			fail: function(res) {
				alert(JSON.stringify(res));
			}
		});
		alert('已注册获取“分享到 QQ”状态事件');
	});

	// 2.4 监听“分享到微博”按钮点击、自定义分享内容及分享结果接口
	$('#onMenuShareWeibo').on(A.options.clickEvent, function() {
		$weixin.onMenuShareWeibo({
			title: 'ExMobi+Agile Lite微信开发',
			desc: '原生UI组件UIXML引擎、HTML5引擎、原生插件引擎三大引擎并驾齐驱，跨平台多分辨率支持。',
			link: 'http://www.exmobi.cn/product.html',
			imgUrl: 'http://www.exmobi.cn/images/exmobi5.png',
			trigger: function(res) {
				alert('用户点击分享到微博');
			},
			complete: function(res) {
				alert(JSON.stringify(res));
			},
			success: function(res) {
				alert('已分享');
			},
			cancel: function(res) {
				alert('已取消');
			},
			fail: function(res) {
				alert(JSON.stringify(res));
			}
		});
		alert('已注册获取“分享到微博”状态事件');
	});

	// 3 智能接口
	var voice = {
		localId: '',
		serverId: ''
	};
	// 3.1 识别音频并返回识别结果
	$('#translateVoice').on(A.options.clickEvent, function() {
		if (voice.localId == '') {
			alert('请先使用 startRecord 接口录制一段声音');
			return;
		}
		$weixin.translateVoice({
			localId: voice.localId,
			complete: function(res) {
				if (res.hasOwnProperty('translateResult')) {
					alert('识别结果：' + res.translateResult);
				} else {
					alert('无法识别');
				}
			}
		});
	});

	// 4 音频接口
	// 4.2 开始录音
	$('#startRecord').on(A.options.clickEvent, function() {
		$weixin.startRecord({
			cancel: function() {
				alert('用户拒绝授权录音');
			}
		});
	});

	// 4.3 停止录音
	$('#stopRecord').on(A.options.clickEvent, function() {
		$weixin.stopRecord({
			success: function(res) {
				voice.localId = res.localId;
			},
			fail: function(res) {
				alert(JSON.stringify(res));
			}
		});
	});


	// 4.5 播放音频
	$('#playVoice').on(A.options.clickEvent, function() {
		if (voice.localId == '') {
			alert('请先使用 startRecord 接口录制一段声音');
			return;
		}
		$weixin.playVoice({
			localId: voice.localId
		});
	});

	// 4.6 暂停播放音频
	$('#pauseVoice').on(A.options.clickEvent, function() {
		$weixin.pauseVoice({
			localId: voice.localId
		});
	});

	// 4.7 停止播放音频
	$('#stopVoice').on(A.options.clickEvent, function() {
		$weixin.stopVoice({
			localId: voice.localId
		});
	});



	// 4.8 上传语音
	$('#uploadVoice').on(A.options.clickEvent, function() {
		if (voice.localId == '') {
			alert('请先使用 startRecord 接口录制一段声音');
			return;
		}
		$weixin.uploadVoice({
			localId: voice.localId,
			success: function(res) {
				alert('上传语音成功，serverId 为' + res.serverId);
				voice.serverId = res.serverId;
			}
		});
	});

	// 4.9 下载语音
	$('#downloadVoice').on(A.options.clickEvent, function() {
		if (voice.serverId == '') {
			alert('请先使用 uploadVoice 上传声音');
			return;
		}
		$weixin.downloadVoice({
			serverId: voice.serverId,
			success: function(res) {
				alert('下载语音成功，localId 为' + res.localId);
				voice.localId = res.localId;
			}
		});
	});

	// 5 图片接口
	// 5.1 拍照、本地选图
	var images = {
		localId: [],
		serverId: []
	};
	$('#chooseImage').on(A.options.clickEvent, function() {
		$weixin.chooseImage({
			success: function(res) {
				images.localId = res.localIds;
				alert('已选择 ' + res.localIds.length + ' 张图片');
			}
		});
	});

	// 5.2 图片预览
	$('#previewImage').on(A.options.clickEvent, function() {
		if (images.serverId.length == 0) {
			alert('请先使用 uploadImage 接口上传图片');
			return;
		}
		var serveridImageUrl = [];
		for (var key in images.serverId) {
			serveridImageUrl.push($config.weixinImageURI + '?serverid=' + images.serverId[key]);
		}
		$weixin.previewImage({
			current: $config.weixinImageURI + '?serverid=' + images.serverId[0],
			urls: serveridImageUrl
		});
	});

	// 5.3 上传图片
	$('#uploadImage').on(A.options.clickEvent, function() {
		if (images.localId.length == 0) {
			alert('请先使用 chooseImage 接口选择图片');
			return;
		}
		var i = 0,
			length = images.localId.length;
		images.serverId = [];

		function upload() {
			$weixin.uploadImage({
				localId: images.localId[i],
				success: function(res) {
					i++;
					alert('已上传：' + i + '/' + length);
					images.serverId.push(res.serverId);
					if (i < length) {
						upload();
					}
				},
				fail: function(res) {
					alert(JSON.stringify(res));
				}
			});
		}

		upload();
	});

	// 5.4 下载图片
	$('#downloadImage').on(A.options.clickEvent, function() {
		if (images.serverId.length === 0) {
			alert('请先使用 uploadImage 上传图片');
			return;
		}
		var i = 0,
			length = images.serverId.length;
		images.localId = [];

		function download() {
			$weixin.downloadImage({
				serverId: images.serverId[i],
				isShowProgressTips: 1,
				success: function(res) {
					i++;
					alert('已下载：' + i + '/' + length);
					images.localId.push(res.localId);
					if (i < length) {
						download();
					}
				}
			});
		}

		download();
	});

	// 6 设备信息接口
	// 6.1 获取当前网络状态
	$('#getNetworkType').on(A.options.clickEvent, function() {
		$weixin.getNetworkType({
			success: function(res) {
				alert(res.networkType);
			},
			fail: function(res) {
				alert(JSON.stringify(res));
			}
		});
	});

	// 7 地理位置接口
	// 7.1 查看地理位置
	$('#openLocation').on(A.options.clickEvent, function() {
		$weixin.openLocation({
			latitude: 31.990775,
			longitude: 118.737617,
			name: '南京烽火',
			address: '南京市云龙山路88号',
			scale: 14,
			infoUrl: 'http://www.exmobi.cn/'
		});
	});

	// 7.2 获取当前地理位置
	$('#getLocation').on(A.options.clickEvent, function() {
		$weixin.getLocation({
			success: function(res) {
				alert(JSON.stringify(res));
			},
			cancel: function(res) {
				alert('用户拒绝授权获取地理位置');
			}
		});
	});

	// 8 界面操作接口
	// 8.1 隐藏右上角菜单
	$('#hideOptionMenu').on(A.options.clickEvent, function() {
		$weixin.hideOptionMenu();
	});

	// 8.2 显示右上角菜单
	$('#showOptionMenu').on(A.options.clickEvent, function() {
		$weixin.showOptionMenu();
	});

	// 8.3 批量隐藏菜单项
	$('#hideMenuItems').on(A.options.clickEvent, function() {
		$weixin.hideMenuItems({
			menuList: ['menuItem:readMode', // 阅读模式
				'menuItem:share:timeline', // 分享到朋友圈
				'menuItem:copyUrl' // 复制链接
			],
			success: function(res) {
				alert('已隐藏“阅读模式”，“分享到朋友圈”，“复制链接”等按钮');
			},
			fail: function(res) {
				alert(JSON.stringify(res));
			}
		});
	});

	// 8.4 批量显示菜单项
	$('#showMenuItems').on(A.options.clickEvent, function() {
		$weixin.showMenuItems({
			menuList: ['menuItem:readMode', // 阅读模式
				'menuItem:share:timeline', // 分享到朋友圈
				'menuItem:copyUrl' // 复制链接
			],
			success: function(res) {
				alert('已显示“阅读模式”，“分享到朋友圈”，“复制链接”等按钮');
			},
			fail: function(res) {
				alert(JSON.stringify(res));
			}
		});
	});

	// 8.5 隐藏所有非基本菜单项
	$('#hideAllNonBaseMenuItem').on(A.options.clickEvent, function() {
		$weixin.hideAllNonBaseMenuItem({
			success: function() {
				alert('已隐藏所有非基本菜单项');
			}
		});
	});

	// 8.6 显示所有被隐藏的非基本菜单项
	$('#showAllNonBaseMenuItem').on(A.options.clickEvent, function() {
		$weixin.showAllNonBaseMenuItem({
			success: function() {
				alert('已显示所有非基本菜单项');
			}
		});
	});

	// 8.7 关闭当前窗口
	$('#closeWindow').on(A.options.clickEvent, function() {
		$weixin.closeWindow();
	});

	// 9 微信原生接口
	// 9.1.1 扫描二维码并返回结果
	$('#scanQRCode0').on(A.options.clickEvent, function() {
		$weixin.scanQRCode();
	});
	// 9.1.2 扫描二维码并返回结果
	$('#scanQRCode1').on(A.options.clickEvent, function() {
		$weixin.scanQRCode({
			needResult: 1,
			desc: 'scanQRCode desc',
			success: function(res) {
				alert(JSON.stringify(res));
			}
		});
	});

	// 10 微信支付接口
	// 10.1 发起一个支付请求
	$('#chooseWXPay').on(A.options.clickEvent, function() {
		window.open('https://open.weixin.qq.com/connect/oauth2/authorize?appid=' + $weixin.appId + '&redirect_uri=' + encodeURIComponent($config.weixinPayURI) + '&response_type=code&scope=snsapi_base&state=123456#wechat_redirect');
	});

	// 11.3  跳转微信商品页
	$('#openProductSpecificView').on(A.options.clickEvent, function() {
		$weixin.openProductSpecificView({
			productId: 'pDF3iY_m2M7EQ5EKKKWd95kAxfNw'
		});
	});

	// 12 微信卡券接口
	// 12.1 添加卡券
	$('#addCard').on(A.options.clickEvent, function() {
		$weixin.addCard({
			cardList: [{
				cardId: 'pDF3iY9tv9zCGCj4jTXFOo1DxHdo',
				cardExt: '{"code": "", "openid": "", "timestamp": "1418301401", "signature":"f54dae85e7807cc9525ccc127b4796e021f05b33"}'
			}, {
				cardId: 'pDF3iY9tv9zCGCj4jTXFOo1DxHdo',
				cardExt: '{"code": "", "openid": "", "timestamp": "1418301401", "signature":"f54dae85e7807cc9525ccc127b4796e021f05b33"}'
			}],
			success: function(res) {
				alert('已添加卡券：' + JSON.stringify(res.cardList));
			}
		});
	});

	// 12.2 选择卡券
	$('#chooseCard').on(A.options.clickEvent, function() {
		$weixin.chooseCard({
			cardSign: '97e9c5e58aab3bdf6fd6150e599d7e5806e5cb91',
			timestamp: 1417504553,
			nonceStr: 'k0hGdSXKZEj3Min5',
			success: function(res) {
				alert('已选择卡券：' + JSON.stringify(res.cardList));
			}
		});
	});

	// 12.3 查看卡券
	$('#openCard').on(A.options.clickEvent, function() {
		alert('您没有该公众号的卡券无法打开卡券。');
		$weixin.openCard({
			cardList: []
		});
	});



	if ($weixin.isWeixinFunction) {

		var shareData = {
			title: 'ExMobi+Agile Lite微信开发',
			desc: '烽火ExMobi,三大引擎完美融合,跨平台多分辨率支持。',
			link: 'http://www.exmobi.cn/product.html',
			imgUrl: 'http://www.exmobi.cn/images/exmobi5.png'
		};
		$weixin.onMenuShareAppMessage(shareData);
		$weixin.onMenuShareTimeline(shareData);


		// 4.4 监听录音自动停止
		$weixin.onVoiceRecordEnd({
			complete: function(res) {
				voice.localId = res.localId;
				alert('录音时间已超过一分钟');
			}
		});

		// 4.8 监听录音播放停止
		$weixin.onVoicePlayEnd({
			complete: function(res) {
				alert('录音（' + res.localId + '）播放结束');
			}
		});
	}
});
/*
$weixin.error(function(res) {
	alert(res.errMsg);
});
*/