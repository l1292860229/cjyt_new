/**
 * 
 */


$('#y_discard').on(A.options.clickEvent, function(){
	console.log("取消关注");
	A.util.ajax({
		url : 'add2favorite?id='+id,
		type : 'get',
		dataType : 'json',
		success : function(data){
			A.showToast(data.msg);
		}
	});
});