$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});

function send_letter() {

	var toName = $("#recipient-name").val();
	var content = $("#message-text").val();
	var b = (JSON.stringify(content).length>2)
	if (b){
		$("#sendModal").modal("hide");
		$.post(
			CONTEXT_PATH+"/message/letter/send",
			{"toName":toName,"content":content},
			function (data) {
				data = $.parseJSON(data);
				if (data.code == 0) {
					$("#hintBody").text("发送成功！");
				} else {
					$("#hintBody").text(data.msg);
				}
				$("#hintModal").modal("show");
				setTimeout(function(){
					$("#hintModal").modal("hide");
					location.reload();
				}, 2000);
			}
		)
	}


}

function delete_msg() {
	// TODO 删除数据
	$(this).parents(".media").remove();
}