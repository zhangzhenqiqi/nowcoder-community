$(function () { //页面加载完之后执行此匿名函数
    $("#topBtn").click(setTop);
    $("#wonderfulBtn").click(setWonderful);
    $("#deleteBtn").click(setDelete);
})

function like(btn, entityType, entityId, entityUserId, postId) {
    $.post(
        CONTEXT_PATH + "/like",
        {
            "entityType": entityType,
            "entityId": entityId,
            "entityUserId": entityUserId,
            "postId": postId
        },
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus == 1 ? '已赞' : '赞');
            } else {
                alert(data.msg)
            }
        }
    );
}

function setTop() {
    $.post(
        CONTEXT_PATH + "/discuss/top",
        {
            "postId": $("#postId").val()
        },

        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                $('#topBtn').attr("disabled", "disabled");
            } else {
                alert(data.msg);
            }
        }
    );
}

function setWonderful() {
    $.post(
        CONTEXT_PATH + "/discuss/wonderful",
        {
            "postId": $("#postId").val()
        },

        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                $('#wonderfulBtn').attr("disabled", "disabled");
            } else {
                alert(data.msg);
            }
        }
    );
}

function setDelete() {
    $.post(
        CONTEXT_PATH + "/discuss/delete",
        {
            "postId": $("#postId").val()
        },

        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                // $('#deleteBtn').attr("disabled", "disabled");
                location.href = CONTEXT_PATH + "/index";
            } else {
                alert(data.msg);
            }
        }
    );
}