layui.use(['form','jquery','jquery_cookie'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);

    form.on("submit(saveBtn)",function (data){

        /*发送Ajax*/
        $.ajax({
            type:"post",
            url:ctx+"/user/setting",
            //数据传输
            data:{
                userName:data.field.userName,
                phone:data.field.phone,
                email:data.field.email,
                trueName:data.field.trueName,
                id:data.field.id
            },
            dataType:"json",
            success:function (msg) {
                if(msg.code==200){
                    layer.msg("修改成功",function () {
                        //清空cookie
                        $.removeCookie("userIdStr",{domain:"localhost",path:"/crm"});
                        $.removeCookie("userName",{domain:"localhost",path:"/crm"});
                        $.removeCookie("trueName",{domain:"localhost",path:"/crm"});
                        //页面跳转
                        window.parent.location.href=ctx+"/index";
                    });
                }else{
                    //修改失败，信息提示
                    layer.msg(msg.msg);
                }
            }
        })
        return false;
    })
});