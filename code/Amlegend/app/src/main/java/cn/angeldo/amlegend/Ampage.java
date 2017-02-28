package cn.angeldo.amlegend;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import cn.finalteam.okhttpfinal.JsonHttpRequestCallback;

import static cn.angeldo.amlegend.R.drawable.target;
import static cn.angeldo.amlegend.R.id.itool;
public class Ampage extends Activity {
    private LocationClient mLocationClient;
    private LocationApplication m;
    private SimpleDraweeView user_logo;//用户头像
    private TextView info_tip;//提示信息
    private ImageView boom_tool;//道具
    private ImageView btn_search;//搜索按钮
    private RelativeLayout boom_area;//攻击区域
    private int current_choice=-1;//选择中目标的index
    //"39.993604",纬度"116.484719"经度
    private double targetData[][]=new double[][]{{39.994604,116.483719},{39.992604,116.485719},{39.995604,116.486719}};
    //当前经纬度
    private String mylat;
    private String mylot;
    //初始化类
    private Pcmm PCM=new Pcmm();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_ampage);

        user_logo = (SimpleDraweeView)findViewById(R.id.my_tx);
        boom_tool=(ImageView)findViewById(itool);
        btn_search=(ImageView)findViewById(R.id.btn_search);
        boom_area=(RelativeLayout)findViewById(R.id.boom_area);
        info_tip=(TextView) findViewById(R.id.info_tip);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pc = getSharedPreferences("Amlegend",Context.MODE_PRIVATE);
                mylat = pc.getString("plat", "none");
                mylot = pc.getString("plot", "none");
                Log.i("添加的lot是", "："+mylot);
                Log.i("添加的lat是", "："+mylat);
                if(mylat.length()>0 && mylot.length()>0){
                    btn_search.setClickable(false);
                    boom_area.removeAllViews();
                    scanTarget(4);
                    Toast.makeText(getApplicationContext(), "扫描完毕", Toast.LENGTH_SHORT).show();
                    btn_search.setClickable(true);
                }else{
                    Toast.makeText(getApplicationContext(), "正在定位，请稍后再试！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //初始化用户
        userInit();
        //绑定道具点击事件
        boom_tool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boomTarget();
            }
        });
        try {
            startMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //启动地图定位
    private void startMap(){
        m= (LocationApplication)getApplication();
        mLocationClient = m.mLocationClient;
        m.mLocationResult = info_tip;
        LocationClientOption loption = new LocationClientOption();
        loption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式
        loption.setCoorType("bd09ll");//返回的定位结果是百度经纬度，默认值gcj02
        loption.setOpenGps(true);//可选，默认false,设置是否使用gps
        loption.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
        loption.setIsNeedAddress(true);
        loption.setIsNeedLocationDescribe(true);
        loption.setIgnoreKillProcess(false);
        mLocationClient.setLocOption(loption);
        mLocationClient.start();
    }
    //退出调用
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            dialog_loginout();
        }
        return false;
    }
    //扫描目标
    public void scanTarget(int num){
        try {
            //将px换算成dp
            int dwl = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
            for(int i=0;i<targetData.length;i++){
                ImageView t1 = new ImageView(this);
                t1.setImageResource(target);
                t1.setPadding(3 * dwl, 3 * dwl, 3 * dwl, 3 * dwl);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(25 * dwl, 25 * dwl);
                //Double.valueOf(mylat)-targetData[i][0]
                //经度
                int lot=(int)Math.floor((Double.valueOf(mylot)-targetData[i][1])*1000);
                Log.i("添加的lot是", "："+lot);
                //纬度
                int lat=(int)Math.floor((Double.valueOf(mylat)-targetData[i][0])*1000);
                Log.i("添加的lat是", "："+lat);
                lp.topMargin = (150 - lot*10) * dwl;
                lp.leftMargin = (150 - lat*10) * dwl;
                t1.setLayoutParams(lp);
                t1.setId(100+i);
                Log.i("添加的id是", "：" + i + "实际数为：" +(100+i));
                t1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("此处的id是", "：" + v.getId() + "元素个数为：" + boom_area.getChildCount());
                        changeTarget(v.getId());
                    }
                });
                boom_area.addView(t1);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        current_choice=-1;
    }
    //消灭目标
    private void boomTarget(){
        Log.i("消灭的id是", "：" + current_choice);
        int targetNum=boom_area.getChildCount();
        for(int i=0;i<targetNum;i++){
            int removeId=boom_area.getChildAt(i).getId();
            Log.i("遍历的id是", "：" + removeId+"位置id:"+i);
            if (removeId == current_choice) {
                boom_area.removeViewAt(i);
                break;
            }
        }
        dialog_ko();
        current_choice=-1;
    }
    //更改目标
    private void changeTarget(int targetId){
        int targetNum=boom_area.getChildCount();
        for(int i=0;i<targetNum;i++){
            if(boom_area.getChildAt(i).getId() == targetId){
                if(current_choice == targetId){
                    boom_area.getChildAt(i).setBackgroundResource(0);
                    current_choice=-1;
                }else {
                    boom_area.getChildAt(i).setBackgroundResource(R.drawable.locking);
                    current_choice=targetId;
                }
            }else{
                boom_area.getChildAt(i).setBackgroundResource(0);
            }
        }
        Log.i("锁定的id是", "：" + current_choice);
    }
    /* 初始化用户
     * 已存在则直接显示
     * 不存在则进行注册
     */
    protected void userInit(){
        SharedPreferences pc=getSharedPreferences("Amlegend",Context.MODE_PRIVATE);
        String userId=pc.getString("userId","");
        if(userId.length()>2){
            String myurl=PCM.initUser;
        }else {
            TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            String szIME = TelephonyMgr.getDeviceId();
            Log.i("本机标识是", "：" + szIME);
        }
    }
    //退出提示
    protected void dialog_loginout() {
        new AlertDialog.Builder(this)
                .setTitle("喂！")
                .setMessage("就这样离开吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        android.os.Process.killProcess(android.os.Process
                                .myPid());
                    }
                })
                .setNegativeButton("取消",
                        new android.content.DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).show();
    }
    //消灭提示
    protected void dialog_ko() {
        new AlertDialog.Builder(this)
                .setTitle("KO！")
                .setMessage("成功消灭目标")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        dialoginterface.dismiss();
                    }
                })
                .show();
    }
    private JsonHttpRequestCallback checkurl = new JsonHttpRequestCallback(){
        @Override
        public void onStart() {
            Log.i("Amlegend","正在准备初始化");
        }
        @Override
        protected void onSuccess(JSONObject jsonObject) {
            super.onSuccess(jsonObject);
            // mTvResult.setText(JsonFormatUtils.formatJson(jsonObject.toJSONString()));
        }
        //请求失败（服务返回非法JSON、服务器异常、网络异常）
        @Override
        public void onFailure(int errorCode, String msg) {

        }
        //请求网络结束
        @Override
        public void onFinish() {

        }
    };
}
