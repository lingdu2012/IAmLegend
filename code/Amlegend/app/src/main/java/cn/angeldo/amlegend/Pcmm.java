package cn.angeldo.amlegend;

/**
 * Created by kucao on 2017/2/28.
 */

public class Pcmm {
    //主服务地址
    //public static String serverUrl="http://10.9.0.239:8104/";
    public static String serverUrl="http://api.amlegend.imgrids.com/";
    //初始化用户
    public static String initUser=serverUrl+"user/userInit";
    //获取目标信息
    public static String getTargets=serverUrl+"attack/searchTarget";
    //攻击目标
    public static String boomTarget=serverUrl+"attack/boomTarget";
    //获取用户信息
    public static String userInfo=serverUrl+"user/userInfo";
    //获取提示信息
    public static String tipInfo=serverUrl+"tip/tipInfo";

}
