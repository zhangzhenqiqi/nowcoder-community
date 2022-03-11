// $(function () {
//     $(".follow-btn").click(follow);
// });

function follow(btn, entityType, entityId) {
    // var btn = this;
    if ($(btn).hasClass("btn-info")) {
        $.post(
            CONTEXT_PATH + "/follow",
            {
                "entityType": entityType,
                "entityId": entityId
            },
            function (data) {
                data = $.parseJSON(data);
                if (data.code == 0) {
                    // 关注TA
                    $(btn).text("已关注").removeClass("btn-info").addClass("btn-secondary");
                } else {
                    alert(data.msg)
                }
            }
        )

    } else {
        $.post(
            CONTEXT_PATH + "/unfollow",
            {
                "entityType": entityType,
                "entityId": entityId
            },
            function (data) {
                data = $.parseJSON(data);
                if (data.code == 0) {
                    // 取消关注
                    $(btn).text("关注TA").removeClass("btn-secondary").addClass("btn-info");
                } else {
                    alert(data.msg)
                }
            }
        )

    }
}