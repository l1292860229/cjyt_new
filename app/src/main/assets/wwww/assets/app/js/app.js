//启动agile
var $config = {
	exmobiSevice : '${$native.getAppSetting().domain}/process/service/${ClientUtil.getAppId()}',
	baseUrl : 'http://shop.wei20.cn',
	user : {username:''},
	siteName : '小鱼塘 beta1'
};
(function(){
	A.event.add({
		beforeunload : function(){
			$(document).on('beforeunload', function(){
				A.Controller.close();
			});
		}
	});
	
	A.Controller.add({
		close : {
			selector : '[data-toggle="close"]',
			handler : function(hash){
				try{
					ExMobiWindow;
					$native.close();
				}catch(e){
					history.go(-1);
				}
				
			}
		},
		html : {
			selector : '[data-toggle="html"]',
			handler : function(hash, el){
				try{
					ExMobiWindow;
					var $el = $(el);
					var isBlank = $el.attr('target')=='_self'?false:true;
					var transition = $el.data('transition')||'';
					$native.openWebview(hash, isBlank, transition);
				}catch(e){
					location.href = A.util.parseURL(hash).getURL();
				}
				
			}
		}
	});
	
	A.launch({
		//showPageLoading : true,
		readyEvent : 'ready',//触发ready的事件，在ExMobi中为plusready
		backEvent : 'backmonitor'
	});
})();

$(document).on(A.options.clickEvent, '#category_aside a',function(){
	A.Controller.aside();
});
$(document).on('articleshow', '#index_article', function(){
		var params =  A.Component.params('#my_friends');
		//if(params.id==id) return;
		id = params.id;
		name = params.name;
		page = 1;
		$('#my_friends header .title').html(name);
		A.template('#my_chat_tmpl').renderReplace({list:{}});
		doRefresh();
		sh=clearInterval(sh);
	});
var doRefresh = function(){
		A.template('#my_chat_tmpl').renderAfter('http://kf.wei20.cn/kf/client/a_mychat_list.jsp?id='+$user.ypid+'&sid='+$user.ka6_id+'&page='+page, function(h){
			if(h.length==0){
				A.showToast('未查询到数据');
				return;
			}
			page++;
		});
	};
$('#index_article').on('refreshInit', function(){
	var city = "";
	var doRefresh = function(flag){
		A.util.ajax({
			url : 'a_index.shtml?city='+city,
			type : 'get',
			dataType : 'json',
			success : function(data){
				if(flag){
					var slider = data.slider;
					var sliderArr = [];
					for(var k in slider){
						sliderArr.push('<div class="slide"><img style="height:154px;" data-source="'+$config.baseUrl+'/'+slider[k].img+'" class="full-width" data-toggle="section" href="'+slider[k].type+'_section.html?id='+k+'"/></div>');
					}
					$('#sliderContent').html(sliderArr.join(''));
					A.Slider('#slide', {
						dots : 'center'
					});
					A.template('#categoryAsideTmpl').renderReplace(data);
				}
				
				$config.cityList = data.city;
				
				A.template('#jiuTmpl').renderReplace(data);	
				if(data.username){
					$config.user.username = data.username;
					$('#index_section h1.title').html('欢迎您,'+data.username);
				}else{
					$config.user.username = '';
					$('#index_section h1.title').html($config.siteName);
				}
			}
		});
	};
	
	doRefresh(true);
	
	var refresh = A.Refresh(this);
	
	refresh.on('pulldown', function(){
		doRefresh();
	});
	
	$(document).on('articleshow', '#city_article', function(){
		
		A.template('#cityTmpl').renderReplace($config.cityList, function(){
			var scroll = A.Scroll('#city_article');
			scroll.refresh();
		});
		
		$(document).on(A.options.clickEvent, '[data-param-pinyin]', function(){
			city = $(this).data('param-pinyin');
			$('#currCity').html($(this).html());
			doRefresh();
			A.Controller.back();
		});
	});
	
});

(function(){
	
	$('#find_article').on('articleshow', function(){
		A.util.ajax({
			url : 'user.shtml',
			type : 'get',
			dataType : 'json',
			success : function(data){
				if(data.username) $('#index_section h1.title').html('欢迎您,'+data.username);
				if(typeof lastUsername=='undefined'||lastUsername!=data.username){
					lastUsername = data.username;
					A.template('#findTmpl').renderReplace(data);
				}
			}
		});
	});
	
})();

(function(){
	
	$('#star_article').on('refreshInit', function(){
		var page = 1;
		var refresh = A.Refresh(this);
		refresh.on('pullup', function(){
			doRefresh();
		});
		var doRefresh = function(){
			A.template('#starTmpl').renderReplace('search.shtml?type=best&page='+page, function(){
				refresh.refresh();
				page++;
			});
		};
		doRefresh();
	});
	
})();

(function(){
	$('#search_article').on('refreshInit', function(){
		var page = 1;
		var refresh = A.Refresh('#search_article');
		refresh.on('pullup', function(){
			if(page==1) return;
			doRefresh();
		});
		var doRefresh = function(){
			A.template('#searchTmpl')[page==1?"renderReplace":"renderAfter"]('search?type=keyword&page='+page+'&keyword='+$('#keyword').val(), function(h){
				if(h.length==0){
					A.showToast('未查询到数据');
					return;
				}
				page++;
				refresh.refresh();
			});
		};
		
		$('#search').on(A.options.clickEvent, function(){
			if(!$('#keyword').val().trim()){
				A.showToast('请输入商品名称');
				return;
			}
			page = 1;
			doRefresh();
		});
	});
})();
//好友列表
		
(function(){
	var id,name,page;
	var doRefresh = function(){
		A.template('#my_friends_tmpl').renderAfter('http://'+$user.domain+'/gouwu/wish3d/a_myfriends.shtml?id='+$user.ypid+'&ka6_id='+$user.ka6_id+'&o='+id+'&page='+page, function(h){
			if(h.length==0){
				A.showToast('暂无好友信息');
				return;
			}
			page++;
		});
	};
	$(document).on('sectionshow', '#my_friends', function(){
		var params =  A.Component.params('#my_friends');
		//if(params.id==id) return;
		id = params.id;
		name = params.name;
		page = 1;
		$('#my_friends header .mytitle').html(name);
		A.template('#my_friends_tmpl').renderReplace({list:{}});
		doRefresh();
	});
	
	$(document).on('refreshInit', '#my_friends_article', function(){
		var refresh = A.Refresh(this);
		refresh.on('pullup', function(){
			doRefresh();
		});
	});

})();
			
(function(){
	var id,name,page;
	var doRefresh = function(){
		A.template('#categoryTmpl').renderAfter('cata.shtml?id='+id+'&page='+page, function(h){
			if(h.length==0){
				A.showToast('未查询到数据');
				return;
			}
			page++;
		});
	};
	$(document).on('sectionshow', '#category_section', function(){
		var params =  A.Component.params('#category_section');
		//if(params.id==id) return;
		id = params.id;
		name = params.name;
		page = 1;
		$('#category_section header .title').html(name);
		A.template('#categoryTmpl').renderReplace({list:{}});
		doRefresh();
	});
	
	$(document).on('refreshInit', '#category_article', function(){
		var refresh = A.Refresh(this);
		refresh.on('pullup', function(){
			doRefresh();
		});
	});

})();


//个人资料
(function(){

	$(document).on('sectionload', '#friends_info', function(){
		var params =  A.Component.params(this);
		var id = params.id;
		var uid = params.uid;
		A.util.ajax({
			url : 'http://'+$user.domain+'/gouwu/wish3d/a_friends_info.shtml?kid='+uid+'&id='+$user.ypid+'&k6id='+$user.ka6_id+'&uid='+id,
			type : 'get',
			dataType : 'json',
			success : function(data){
				//var slider = data.slider;
				//var sliderArr = [];
				//for(var i=0;i<slider.length;i++){
					//sliderArr.push('<div class="slide"><img style="height:300px;" data-source="'+slider[i]+'" class="full-width"/></div>');
				//}
				//$('#goodsSliderContent').html(sliderArr.join(''));
				//A.Slider('#goodsSlider', {
					//dots : 'center'
				//});
				A.template('#goodsTmpl').renderReplace(data.detail);	
				//sh=setInterval(function(){alert("1")},5000);
			}
		});
		
		$('#addCarts').on(A.options.clickEvent, function(){
			if(!$config.user.username){
				A.Controller.modal('#login_modal');
			}else{
				A.util.ajax({
					url : 'add2cart?id='+id,
					type : 'get',
					dataType : 'json',
					success : function(data){
						A.showToast(data.result=='success'?'添加成功':'添加失败');
					}
				});
			}
		});
		
		$('#favariteBtn').on(A.options.clickEvent, function(){
			A.util.ajax({
				url : 'add2favorite?id='+id,
				type : 'get',
				dataType : 'json',
				success : function(data){
					A.showToast(data.msg);
				}
			});
		});
	});
	
	

})();
//chat_list
			
(function(){
	var id,name,page;
	
	var doRefresh = function(){
		A.template('#chat_tmpl').renderBefore('http://kf.wei20.cn/kf/client/chat_list.jsp?Cid='+Cid+'&Aid='+$user.ka6_id+'&page='+page, function(h){
			if(h.length==0){
				A.showToast('暂无聊天信息');
				return;
			}
			page++;
		});
	};
	var doRefreshmy = function(){
		A.template('#chat_tmpl').renderBefore('http://kf.wei20.cn/conid_update.jsp?Cid='+Cid+'&Aid='+$user.ka6_id+'&myheader='+$_kfheader+'&kfheader='+$_kfheader+'&myid='+$user.ka6_id+'&kfid='+$user.ka6_id+'&page='+page, function(h){
																																																									    
			if(h.length==0){
				A.showToast('未查询到数据s');
				return;
			}
			page++;
		});
	};
	$(document).on('sectionshow', '#my_chat_list', function(){
		var params =  A.Component.params('#chat2');
		//if(params.id==id) return;
		id = params.id;
		name = params.name;
		page = 1;
		
		//$('#my_chat_list header .title').html(name);
		//A.template('#chat_tmpl').renderReplace({list:{}});
		//doRefresh();
	});
	$(document).on('sectionload', '#chat2', function(){
		var params =  A.Component.params(this);
		var uid = params.uid;
		var kid = params.kid;
		var sh;
sh=setInterval(function(){GetMessage()},3000);
		A.util.ajax({
			url : 'http://'+$user.domain+'/gouwu/wish3d/a_friends_info.shtml?id='+$user.ypid+'&uid='+uid+'&kid='+kid,
			type : 'get',
			dataType : 'json',
			success : function(data){
				$('#chat2 header .title').html(data.detail.username);
				
	   //我的头像
	   _kfheader=data.detail.userface;
	  // CustomerHeadUrl=data.detail.userface;
	  Cid=data.detail.ka6_id;
	   document.title = data.detail.username;
	   //将头像ID更新至对话服务器
	 // doRefreshmy();
	  doRefresh();
	
	  // var scroller = A.Scroll('#my_chat_list');
			////scroller.scrollBottom();
				A.showToast('请使用文明用语，下载APP可语音聊天，体验更强大功能');
				
			}
		});
		});
	$(document).on('refreshInit', '#my_chat_list', function(){
		var refresh = A.Refresh(this);
		refresh.on('pulldown', function(){
			//doRefresh();
		});
	});

})();
			//chat_list end
//商品
(function(){

	$(document).on('sectionload', '#goods_section', function(){
		var params =  A.Component.params(this);
		var id = params.id;
		A.util.ajax({
			url : 'goods?id='+id,
			type : 'get',
			dataType : 'json',
			success : function(data){
				var slider = data.slider;
				var sliderArr = [];
				for(var i=0;i<slider.length;i++){
					sliderArr.push('<div class="slide"><img style="height:300px;" data-source="'+$config.baseUrl+'/'+slider[i]+'" class="full-width"/></div>');
				}
				$('#goodsSliderContent').html(sliderArr.join(''));
				A.Slider('#goodsSlider', {
					dots : 'center'
				});
				A.template('#goodsTmpl').renderReplace(data.detail);	
			}
		});
		
		$('#addCarts').on(A.options.clickEvent, function(){
			if(!$config.user.username){
				A.Controller.modal('#login_modal');
			}else{
				A.util.ajax({
					url : 'add2cart?id='+id,
					type : 'get',
					dataType : 'json',
					success : function(data){
						A.showToast(data.result=='success'?'添加成功':'添加失败');
					}
				});
			}
		});
		
		$('#favariteBtn').on(A.options.clickEvent, function(){
			A.util.ajax({
				url : 'add2favorite?id='+id,
				type : 'get',
				dataType : 'json',
				success : function(data){
					A.showToast(data.msg);
				}
			});
		});
	});
	
	

})();

			
(function(){
	var id;
	var doRefresh = function(){
		A.template('#topicTmpl').renderReplace('topic?id='+id, function(h, t, o){
			$('#topic_section h1.title').html(o.title);
		});
	};
	$(document).on('sectionshow', '#topic_section', function(){
		var params =  A.Component.params(this);
		if(params.id==id) return;
		id = params.id;
		doRefresh();
	});

	$(document).on('refreshInit', '#topic_article', function(){
		var refresh = A.Refresh(this);
		refresh.on('pullup', function(){
			doRefresh();
		});
	});

})();
			
(function(){
	var lastUsername;
	$('#person_article').on('articleshow', function(){
	//$('#myname').html('欢迎您,');
		A.util.ajax({
			url : 'http://'+$user.domain+'/gouwu/wish3d/a_friends_info.shtml?id='+$user.ypid+'&kid='+$user.ka6_id,
			type : 'get',
			dataType : 'json',
			success : function(data){
				if(data.username) $('#index_section h1.title').html('欢迎您,'+data.username);
				if(typeof lastUsername=='undefined'||lastUsername!=data.username){
					lastUsername = data.username;
					A.template('#my_Tmpl').renderReplace(data.detail);
				}
			}
		});
	});
	
	$(document).on(A.options.clickEvent, '#myinfo .grid-cell', function(){
		if(!$config.user.username){
			A.Controller.modal('#login_modal');
		}else{
			A.Controller.section('#'+this.id+'_section');
		}
	});
	

	$(document).on(A.options.clickEvent, '#btn_login', function(){
		var username = $('#username').val().trim();
		var password = $('#password').val().trim();
		if(!username||!password){
			A.showToast('请输入完整信息');
			return;
		}
		
		A.util.ajax({
			url : 'login',
			type : 'post',
			data : 'username='+username+'&password='+password,
			dataType : 'json',
			success : function(data){
				if(data.result=='success'){
					$config.user.username = username;
					$('#index_section h1.title').html('欢迎您,'+data.username);
					A.Controller.modal('#login_modal');
				}else{
					A.showToast('登录失败');
				}
			}
		});
		
	});

	$(document).on(A.options.clickEvent, '#logut', function(){
		A.util.ajax({
			url : 'logout',
			type : 'get',
			dataType : 'json',
			success : function(data){
				if(data.result=='success'){
					$config.user.username = '';
					$('#index_section h1.title').html($config.siteName);
					A.template('#my_Tmpl').renderReplace({});
				}
			}
		});
	});

})();


(function(){
	
	var getCarts = function(){
		A.util.ajax({
			url : 'carts',
			type : 'get',
			dataType : 'json',
			success : function(data){
				if(data.list.length==0) A.showToast('购物车里没有宝贝');
				A.template('#findTmpl').renderReplace(data);
			}
		});
	};
	
	$('#carts_article').on('articleshow', function(){
		getCarts();
	}).on('refreshInit', function(){
		var refresh = A.Refresh(this);
		refresh.on('pulldown', function(){
			getCarts();
		});
	});
	
	var doUpdate = function(el){
		try{
			var val = Number(el.value);
			if(val<0){
				A.showToast('数据错误');
				return;
			}
			var cartsTotal = 0;
			$('.carts-item').each(function(){
				var price = $(this).data('price');
				var num = $(this).find('input').val();
				cartsTotal += Number(price)*Number(num);
			});
			$('#cartsTotal').html(cartsTotal);
			var $el = $(el).closest('.carts-item');
			A.util.ajax({
				url : 'cartsUpdate?id='+$el.data('id')+'&num='+val,
				type : 'post',
				dataType : 'json',
				success : function(data){
					if(val==0) $el.remove();
				}
			});
		}catch(e){
			A.showToast('数据错误');
		}
	};
	
	$(document).on('change', '.carts-item input', function(){
		doUpdate(this);
	});
	
	$(document).on(A.options.clickEvent, '.carts-item .carts-minus', function(){
		var $input = $(this).siblings('input');
		var val = Number($input.val());
		$input.val(--val);
		doUpdate($input[0]);
	});
	
	$(document).on(A.options.clickEvent, '.carts-item .carts-plus', function(){
		var $input = $(this).siblings('input');
		var val = Number($input.val());
		$input.val(++val);
		doUpdate($input[0]);
	});
	
})();


(function(){
	
	var getAddress = function(){
		A.template("#addressTmpl").renderReplace('getAddress', function(){
			var scroller = A.Scroll('#address_article');
			scroller.refresh();
		});
	};
	
	$(document).on('articleload', '#address_article', function(){
		if($('.carts-item').length==0){
			A.Controller.back();
			return;
		}
		getAddress();
	});
	
	var cityDefaultOption = '<option value="0">请选择市</option>';
	var districtDefaultOption = '<option value="0">请选择区</option>';
	
	$(document).on('change', '#address_article .address-item select[name="country"]', function(){
		if(this.value==0){
			$(this).next().val(0).next().empty().append(cityDefaultOption).next().empty().append(districtDefaultOption);
			return;
		}
	});
	
	$(document).on('change', '#address_article .address-item select[name="province"]', function(){
		if(this.value==0){
			$(this).next().empty().append(cityDefaultOption).next().empty().append(districtDefaultOption);
			return;
		}
		var $curr = $(this);
		var $el = $curr.next();
		A.util.ajax({
			url : 'getRegion?id='+this.value+'&type=city',
			type : 'get',
			dataType : 'json',
			success : function(data){
				var regions = data.regions;
				$el.empty().append(cityDefaultOption);
				var arr = [];
				for(var i=0;i<regions.length;i++){
					arr.push('<option value="'+regions[i]['region_id']+'">'+regions[i]['region_name']+'</option>');
				}
				$el.append(arr.join('')).next().empty().append(districtDefaultOption);
			}
		});
	});
	
	$(document).on('change', '#address_article .address-item select[name="city"]', function(){
		if(this.value==0){
			$(this).next().empty().append(districtDefaultOption);
			return;
		}
		var $curr = $(this);
		var $el = $curr.next();
		A.util.ajax({
			url : 'getRegion?id='+this.value+'&type=district',
			type : 'get',
			dataType : 'json',
			success : function(data){
				var regions = data.regions;
				$el.empty().append(districtDefaultOption);
				var arr = [];
				for(var i=0;i<regions.length;i++){
					arr.push('<option value="'+regions[i]['region_id']+'">'+regions[i]['region_name']+'</option>');
				}
				$el.append(arr.join(''));
			}
		});
	});
	
	$(document).on(A.options.clickEvent, '#address_article .address-item .address-add', function(){
		var $el = $(this).closest('form');
		var form = {};
		form.country = $el.find('select[name="country"]').val();
		form.province = $el.find('select[name="province"]').val();
		form.city = $el.find('select[name="city"]').val();
		form.district = $el.find('select[name="district"]').val();
		
		form.consignee = $el.find('input[name="consignee"]').val();
		form.address = $el.find('input[name="address"]').val();
		form.tel = $el.find('input[name="tel"]').val();
		
		for(var k in form){
			if(form[k]==0||!form[k]){
				A.showToast('请输入完整信息');
				return false;
			}
		}
		A.util.ajax({
			url : 'saveAddress',
			type : 'post',
			data : $el.serialize(),
			dataType : 'json',
			success : function(data){
				A.showToast(data.result=='success'?'保存成功':'保存失败');
				A.Controller.section('#payment_section');
			}
		});
		
		return false;
	});
	
	$(document).on(A.options.clickEvent, '#address_article .address-item .address-del', function(){
		var _this = this;
		A.confirm('确定删除地址信息吗？', function(){
			var $el = $(_this).closest('form');
			var id = $el.find('input[name="address_id"]').val();
			A.util.ajax({
				url : 'delAddress?id='+id,
				type : 'get',
				dataType : 'json',
				success : function(data){
					getAddress();
					A.showToast(data.result=='success'?'保存成功':'保存失败');
				}
			});
		});

		return false;
	});
	
})();


(function(){
	
	var getFlow = function(){
		A.util.ajax({
			url : 'checkout',
			type : 'get',
			dataType : 'json',
			success : function(data){
				if(!data.username){
					A.Controller.modal('#login_modal');
					return;
				}
				var html = data.flows.join('');
				
				$('#payment_article #flowsContent').html(html);
				var scroller = A.Scroll('#payment_article');
				scroller.refresh();
			}
		});
	};
	
	$(document).on('articleshow', '#payment_article', function(){
		getFlow();
	});
	
	var onRadioChange = function(el){
		var $el = $(el);
		var type = $el.attr('name');
		var val = $el.val();
		A.util.ajax({
			url : 'cartsPayment?type='+type+'&val='+val,
			type : 'get',
			dataType : 'json',
			success : function(data){
				getFlow();
			}
		});
	};
	
	$(document).on(A.options.clickEvent, '#payment_article .flowBox table tr', function(e){
		if(e.target.tagName.toLowerCase()=='input'){
			onRadioChange(e.target);
			return true;
		}
		var $el = $(this);
		var $radio = $el.find('input[type="radio"]');
		console.log($radio.length);
		if($radio.prop('checked')) return;
		$radio.prop('checked', true);
		onRadioChange($radio);
		return false;
	});
	
	$(document).on(A.options.clickEvent, '#payment_section #checkBuy', function(){
		
		$('#payment_section').data('cache', false);
		A.Controller.section('#done_section');
		
	});
})();

(function(){
	
	var validateData = function(data){
		if(data.url){
			$('#done_section #done').data('toggle','html').attr('href', data.url);
		}else if(data.yinlian){
			$('#done_section .scroller').append($(data.yinlian).hide());
			$('#done_section #done').on(A.options.clickEvent, function(){
				$('#yinlianForm').submit();
			});
		}else if(data.wxqrcode){
			$('#done_article .scroller').append('<div style="text-align:center;"><img src="'+data.wxqrcode+'" style="width:200px;height:200px;"><p>本支付适用于新版本手机微信扫二维码支付</p></div>');
			$('#done_section #done').hide();
		}else if(!data.orderId){
			A.showToast('余额不足，请选择其他方式');
			A.Controller.back();
		}else{
			$('#done_section #done').hide();
		}
		var scroll = A.Scroll('#done_article');
		scroll.refresh();
	};
	
	var getFromFlow = function(){
		A.util.ajax({
			url : 'cartsDone',
			type : 'post',
			data : $('#flowsContent').serialize(),
			dataType : 'json',
			success : function(data){
				A.template("#doneTmpl").renderReplace(data, function(){
					validateData(data);
				});
			}
		});
	};
	
	var getFromId = function(id){		
		A.template("#doneTmpl").renderReplace('cartsDone?type=detail&id='+id, function(h, t, o){
			validateData(o);
		});
	};

	$(document).on('sectionload', '#done_section', function(){
		var params = A.Component.params(this);
		var id = params.id;
		(id?getFromId:getFromFlow)(id);
	});
})();

(function(){
	var page = 1;
	var getOrder = function(){
		A.template("#orderTmpl")[page==1?'renderReplace':'renderAfter']('getOrder?page='+page, function(){
			var refresh = A.Refresh('#order_article');
			refresh.refresh();
			page++;
		});
	};
	
	$(document).on('refreshInit', '#order_article', function(){
		var refresh = A.Refresh(this);
		refresh.on('pullup', function(){
			getOrder();
		});
		refresh.on('pulldown', function(){
			page = 1;
			getOrder();
		});
		getOrder();
	});
})();


(function(){
	var page = 1;
	var getFavorite = function(){
		A.template("#favoriteTmpl")[page==1?'renderReplace':'renderAfter']('getFavorite?page='+1, function(){
			var scroll = A.Scroll('#favorite_article');
			scroll.refresh();
			page++;
		});
	};
	
	$(document).on('refreshInit', '#favorite_article', function(){
		var refresh = A.Refresh(this);
		refresh.on('pullup', function(){
			getFavorite();
		});
		refresh.on('pulldown', function(){
			page = 1;
			getFavorite();
		});
		getFavorite();
		
		$(document).on(A.options.clickEvent, '[data-param-id]', function(){
			var id = $(this).data('param-id');
			var $li = $(this).closest('li');
			A.confirm('确定要删除收藏吗？', function(){
				A.util.ajax({
					url : 'delFavorite?id='+id,
					type : 'get',
					dataType : 'json',
					success : function(data){
						A.showToast('删除成功');
						$li.remove();
					}
				});
			});
			
		});
	});
	
})();

(function(){
	
	var getMyAddress = function(){
		A.template("#myaddressTmpl").renderReplace('addressList', function(){
			var scroll = A.Scroll('#myaddress_article');
			scroll.refresh();
		});
	};
	
	$(document).on('articleload', '#myaddress_article', function(){
		getMyAddress();
	});
	
	var cityDefaultOption = '<option value="0">请选择市</option>';
	var districtDefaultOption = '<option value="0">请选择区</option>';
	
	$(document).on('change', '#myaddress_article .address-item select[name="country"]', function(){
		if(this.value==0){
			$(this).next().val(0).next().empty().append(cityDefaultOption).next().empty().append(districtDefaultOption);
			return;
		}
	});
	
	$(document).on('change', '#myaddress_article .address-item select[name="province"]', function(){
		if(this.value==0){
			$(this).next().empty().append(cityDefaultOption).next().empty().append(districtDefaultOption);
			return;
		}
		var $curr = $(this);
		var $el = $curr.next();
		A.util.ajax({
			url : 'getRegion?id='+this.value+'&type=city',
			type : 'get',
			dataType : 'json',
			success : function(data){
				var regions = data.regions;
				$el.empty().append(cityDefaultOption);
				var arr = [];
				for(var i=0;i<regions.length;i++){
					arr.push('<option value="'+regions[i]['region_id']+'">'+regions[i]['region_name']+'</option>');
				}
				$el.append(arr.join('')).next().empty().append(districtDefaultOption);
			}
		});
	});
	
	$(document).on('change', '#myaddress_article .address-item select[name="city"]', function(){
		if(this.value==0){
			$(this).next().empty().append(districtDefaultOption);
			return;
		}
		var $curr = $(this);
		var $el = $curr.next();
		A.util.ajax({
			url : 'getRegion?id='+this.value+'&type=district',
			type : 'get',
			dataType : 'json',
			success : function(data){
				var regions = data.regions;
				$el.empty().append(districtDefaultOption);
				var arr = [];
				for(var i=0;i<regions.length;i++){
					arr.push('<option value="'+regions[i]['region_id']+'">'+regions[i]['region_name']+'</option>');
				}
				$el.append(arr.join(''));
			}
		});
	});
	
	$(document).on(A.options.clickEvent, '#myaddress_article .address-item .address-add', function(){
		var $el = $(this).closest('form');
		var form = {};
		form.country = $el.find('select[name="country"]').val();
		form.province = $el.find('select[name="province"]').val();
		form.city = $el.find('select[name="city"]').val();
		form.district = $el.find('select[name="district"]').val();
		
		form.consignee = $el.find('input[name="consignee"]').val();
		form.address = $el.find('input[name="address"]').val();
		form.tel = $el.find('input[name="tel"]').val();
		
		for(var k in form){
			if(form[k]==0||!form[k]){
				A.showToast('请输入完整信息');
				return false;
			}
		}
		A.util.ajax({
			url : 'saveMyAddress',
			type : 'post',
			data : $el.serialize(),
			dataType : 'json',
			success : function(data){
				A.showToast(data.result=='success'?'保存成功':'保存失败');
				getMyAddress();
			}
		});
		
		return false;
	});
	
	$(document).on(A.options.clickEvent, '#myaddress_article .address-item .address-del', function(){
		var _this = this;
		A.confirm('确定删除地址信息吗？', function(){
			var $el = $(_this).closest('form');
			var id = $el.find('input[name="address_id"]').val();
			A.util.ajax({
				url : 'delMyAddress?id='+id,
				type : 'get',
				dataType : 'json',
				success : function(data){					
					A.showToast(data.result=='success'?'保存成功':'保存失败');
					getMyAddress();
				}
			});
		});

		return false;
	});
	
})();