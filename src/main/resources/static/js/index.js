$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	//关闭发布框
	$("#publishModal").modal("hide");
	//获取标题和内容的值
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();

	//发送异步请求post
	$.post(
		CONTEXT_PATH + "/discuss/add",
		{"title":title,"content":content},
		function(data) {
			data = $.parseJSON(data);
			//在提示框中显示返回消息
			$("#hintBody").text(data.msg);
			//显示提示框
			$("#hintModal").modal("show");
			//出现提示框并在两秒后关闭
			setTimeout(function(){
				$("#hintModal").modal("hide");
				//成功后刷新页面
				if(data.code == 0) {
					window.location.reload();
				}
			}, 2000);
		}
	);

}