package cn.angeldo.amlegend;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.backends.pipeline.Fresco;

import cn.finalteam.okhttpfinal.HttpRequest;
import cn.finalteam.okhttpfinal.JsonHttpRequestCallback;
import cn.finalteam.okhttpfinal.OkHttpFinal;
import cn.finalteam.okhttpfinal.OkHttpFinalConfiguration;
import cn.finalteam.okhttpfinal.RequestParams;
import cn.finalteam.toolsfinal.JsonFormatUtils;

public class Aminfo extends Activity {
    //初始化类
    private Pcmm PCM=new Pcmm();
    private TextView appVersion;//软件版本
    private TextView myName;//昵称
    private TextView myScore;//得分
    private TextView myFailure;//失败
    private TextView myTools;//道具
    private TextView myStatus;//状态
    private TextView myFlashTime;//存活时间
    private JSONObject jsonInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aminfo);
        //初始化fresco
        Fresco.initialize(this);
        //初始化okHttp
        OkHttpFinalConfiguration.Builder builder = new OkHttpFinalConfiguration.Builder();
        OkHttpFinal.getInstance().init(builder.build());

        appVersion=(TextView)findViewById(R.id.app_version);
        myName=(TextView)findViewById(R.id.my_name);
        myScore=(TextView)findViewById(R.id.my_score);
        myFailure=(TextView)findViewById(R.id.my_failure);
        myStatus=(TextView)findViewById(R.id.my_status);
        myFlashTime=(TextView)findViewById(R.id.my_time);
        myTools=(TextView)findViewById(R.id.my_boom);


        appVersion.setText("版本："+getVersion());

        SharedPreferences pc = getSharedPreferences("Amlegend", Context.MODE_PRIVATE);
        String userId = pc.getString("userId", "none");
        //创建网络请求
        RequestParams params = new RequestParams();
        //表单数据
        params.addFormDataPart("userId",userId);

        try {
            HttpRequest.post(PCM.userInfo, params,toUserInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    //获取本地版本号
    private String getVersion(){
        String verCode=null;
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            verCode = info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return verCode;
    }
    //退出调用
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(),Ampage.class);
            startActivity(intent);
            finish();
        }
        return false;
    }
    //初始化用户信息
    private JsonHttpRequestCallback toUserInfo = new JsonHttpRequestCallback(){
        @Override
        public void onStart() {
            Log.i("Amlegend","正在准备初始化"+PCM.userInfo);
        }
        @Override
        protected void onSuccess(JSONObject jsonObject) {
            super.onSuccess(jsonObject);
            Log.i("Amlegend","返回的用户id是："+ JsonFormatUtils.formatJson(jsonObject.toJSONString()));
            jsonInfo=jsonObject;
            mHandler.obtainMessage(0).sendToTarget();

        }
        //请求失败（服务返回非法JSON、服务器异常、网络异常）
        @Override
        public void onFailure(int errorCode, String msg) {
            Toast.makeText(getApplicationContext(), "请检查网络连接",
                    Toast.LENGTH_SHORT).show();
        }
        //请求网络结束
        @Override
        public void onFinish() {

        }
    };
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    JSONArray objArray = JSONObject.parseArray(jsonInfo.getString("result"));
                    JSONObject obj = (JSONObject) objArray.get(0);
                    myName.setText(obj.getString("user_name"));
                    myScore.setText(obj.getString("score"));
                    myFailure.setText(obj.getString("failure"));
                    myTools.setText(obj.getString("tools"));
                    myFlashTime.setText(obj.getString("flash_time"));
                    myStatus.setText(obj.getString("statusDesc"));
                    break;
                case 1:

                    break;
            }
        }
    };
}
