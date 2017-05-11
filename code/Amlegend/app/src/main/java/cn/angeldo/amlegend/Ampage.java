package cn.angeldo.amlegend;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import cn.finalteam.okhttpfinal.HttpRequest;
import cn.finalteam.okhttpfinal.JsonHttpRequestCallback;
import cn.finalteam.okhttpfinal.OkHttpFinal;
import cn.finalteam.okhttpfinal.OkHttpFinalConfiguration;
import cn.finalteam.okhttpfinal.RequestParams;
import cn.finalteam.toolsfinal.JsonFormatUtils;

import static cn.angeldo.amlegend.R.drawable.target;
import static cn.angeldo.amlegend.R.id.itool;
import static com.baidu.location.h.j.s;

/**
 * 功能描述：
 * @ 检查权限
 * @ 获取当前经纬度
 * @ 初始化用户信息
 * @ 扫描攻击区域
 * @ 锁定攻击目标
 * @ 实施攻击
 * @ 更新用户信息
 * */
public class Ampage extends Activity {
    private LocationClient mLocationClient;
    private LocationApplication m;
    private SimpleDraweeView user_logo;//用户头像
    private ImageView mybird;//小鸟
    private TextView info_tip;//提示信息
    private TextView boom_num;//道具数量
    private TextView mylocation;//我的地址
    private TextView myname;//我的名字
    private TextView myscore;//我的成绩
    private ImageView boom_tool;//道具
    private ImageView btn_search;//搜索按钮
    private RelativeLayout boom_area;//攻击区域
    private int current_choice=-1;//选择中目标的index
    //"39.993604",纬度"116.484719"经度
    private double targetData[][]=new double[][]{{39.994604,116.483719},{39.992604,116.485719},{39.995604,116.486719}};
    //当前经纬度
    private String mylat;
    private String mylot;
    private int isInited=0;
    //当前提示信息
    private String tipInfo;
    //初始化类
    private Pcmm PCM=new Pcmm();
    private JSONObject jsonInfo;
    public SoundPool pool;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化fresco
        Fresco.initialize(this);
        //初始化okHttp
        OkHttpFinalConfiguration.Builder builder = new OkHttpFinalConfiguration.Builder();
        OkHttpFinal.getInstance().init(builder.build());
        //加载页面
        setContentView(R.layout.activity_ampage);
        //初始化音效对象
        if(Build.VERSION.SDK_INT>=21) {
            AudioAttributes audioAttrib = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            SoundPool.Builder builder2 = new SoundPool.Builder();
            builder2.setAudioAttributes(audioAttrib).setMaxStreams(2);
            this.pool = builder2.build();
        }else{
            this.pool=new SoundPool(2, AudioManager.STREAM_MUSIC,0);
        }
        //加载UI
        user_logo = (SimpleDraweeView)findViewById(R.id.my_tx);
        boom_tool=(ImageView)findViewById(itool);
        btn_search=(ImageView)findViewById(R.id.btn_search);
        boom_area=(RelativeLayout)findViewById(R.id.boom_area);
        info_tip=(TextView) findViewById(R.id.info_tip);
        mylocation=(TextView) findViewById(R.id.info_location);
        myname=(TextView) findViewById(R.id.i_name);
        myscore=(TextView) findViewById(R.id.i_score);
        ImageView btn_info=(ImageView)findViewById(R.id.btn_info);
        boom_num=(TextView)findViewById(R.id.boom_num);
        mybird=(ImageView) findViewById(R.id.my_bird);
        //个人信息页面
        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(),Aminfo.class);
                startActivity(intent);
                finish();
            }
        });
        //搜索按钮，默认无法点击
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mylat.length()>0 && mylot.length()>0 && !mylocation.getText().toString().equals("null")){
                    musicEffect(0);
                    btn_search.setClickable(false);
                    boom_area.removeAllViews();
                    scanTarget();
                }else{
                    Toast.makeText(getApplicationContext(), "正在定位，请稍后再试！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //绑定监听事件
        mylocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                //获取当前经纬度
                SharedPreferences pc = getSharedPreferences("Amlegend",Context.MODE_PRIVATE);
                mylat = pc.getString("plat", "none");
                mylot = pc.getString("plot", "none");
                //仅初始化一次用户信息
                if(isInited==0 && mylat.length()>0 && mylot.length()>0 && !mylocation.getText().toString().equals("null")) {
                    //初始化用户
                    userInit();
                    //启动消息线程
                    new Thread(new msgThread()).start();
                }
            }
        });
        //绑定道具点击事件
        boom_tool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boomTarget();
            }
        });
        //checkRights();
        getToken();
    }
    //检查权限
    private void checkRights(){
        SharedPreferences pc = getSharedPreferences("Amlegend",Context.MODE_PRIVATE);
        int tipInfo = pc.getInt("tipInfo",0);
        if(tipInfo==0){
            dialog_rights();
        }
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
        m.mLocationResult = mylocation;//返回并直接显示在控件
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
    public void scanTarget(){
        SharedPreferences pc = getSharedPreferences("Amlegend",Context.MODE_PRIVATE);
        String userId = pc.getString("userId", "none");
        //创建网络请求
        RequestParams params = new RequestParams();
        //表单数据
        params.addFormDataPart("userId",userId);
        params.addFormDataPart("lat",mylat);
        params.addFormDataPart("lot",mylot);

        try {
            HttpRequest.post(PCM.getTargets, params,toGetTargets);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //展示分布目标
    private void searchTarget(JSONObject jsonObject){
        try {
            //将px换算成dp
            int dwl = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
            JSONArray objArray = JSONObject.parseArray(jsonObject.getString("result"));

            for(int i=0;i<objArray.size();i++){
                ImageView t1 = new ImageView(this);
                t1.setImageResource(target);
                t1.setPadding(3 * dwl, 3 * dwl, 3 * dwl, 3 * dwl);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(25 * dwl, 25 * dwl);
                //Double.valueOf(mylat)-targetData[i][0]
                JSONObject obj = (JSONObject) objArray.get(i);
                //经度
                int lot=(int)Math.floor((Double.valueOf(mylot)-obj.getDouble("lot"))*1000);
                Log.i("添加的lot是", "："+lot);
                //纬度
                int lat=(int)Math.floor((Double.valueOf(mylat)-obj.getDouble("lat"))*1000);
                Log.i("添加的lat是", "："+lat);
                lp.topMargin = (150 - lot*10) * dwl;
                lp.leftMargin = (150 - lat*10) * dwl;
                t1.setLayoutParams(lp);
                t1.setId(obj.getInteger("user_id"));
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
        Toast.makeText(getApplicationContext(), "扫描完毕", Toast.LENGTH_SHORT).show();
        btn_search.setClickable(true);
    }
    //消灭目标
    private void boomTarget(){
        Log.i("消灭的id是", "：" + current_choice);
        if(current_choice==-1){
            Toast.makeText(getApplicationContext(), "请选择攻击目标", Toast.LENGTH_SHORT).show();
            return;
        }
        if(Integer.parseInt(boom_num.getText().toString())<=0){
            Toast.makeText(getApplicationContext(), "弹药不足！", Toast.LENGTH_SHORT).show();
            return;
        }
        int targetNum=boom_area.getChildCount();
        for(int i=0;i<targetNum;i++){
            int removeId=boom_area.getChildAt(i).getId();
            Log.i("遍历的id是", "：" + removeId+"位置id:"+i);
            if (removeId == current_choice) {
                boom_area.removeViewAt(i);

                SharedPreferences pc = getSharedPreferences("Amlegend",Context.MODE_PRIVATE);
                String userId = pc.getString("userId", "none");
                //创建网络请求
                RequestParams params = new RequestParams();
                //表单数据
                params.addFormDataPart("killerId",userId);
                params.addFormDataPart("lat",mylat);
                params.addFormDataPart("lot",mylot);
                params.addFormDataPart("userId",removeId);
                try {
                    HttpRequest.post(PCM.boomTarget, params,toBoomTarget);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
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
     */
    protected void userInit(){
        TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String phoneIME = TelephonyMgr.getDeviceId();
        if(Double.valueOf(phoneIME)<=0){
            Toast.makeText(getApplicationContext(), "请检查软件权限设置", Toast.LENGTH_SHORT).show();
            return ;
        }
        Log.i("本机标识是", "：" + phoneIME);
        Log.i("本机标识lat是", "：" + mylat);
        Log.i("本机标识lot是", "：" + mylot);
        //创建网络请求
        RequestParams params = new RequestParams();
        //表单数据
        params.addFormDataPart("markId",phoneIME);
        params.addFormDataPart("lat",mylat);
        params.addFormDataPart("lot",mylot);

        try {
            HttpRequest.post(PCM.initUser, params,toInitUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //更新用户信息
    protected void updateUser(){
        JSONArray objArray = JSONObject.parseArray(jsonInfo.getString("result"));
        JSONObject obj = (JSONObject) objArray.get(0);
        String userId=obj.getString("id");
        SharedPreferences.Editor editor = getSharedPreferences("Amlegend", Context.MODE_PRIVATE).edit();
        editor.putString("userId",userId);
        editor.commit();

        myname.setText(obj.getString("user_name"));
        myscore.setText(obj.getString("score"));
        boom_num.setText(obj.getString("tools"));

        Log.i("Amlegend","返回的用户id是："+userId);
        Log.i("Amlegend","返回的用户状态是："+obj.getInteger("status"));
        isInited=1;
        String status=obj.getString("status");
        if(status!=null){
            judgeUser(Integer.parseInt(status));
        }
    }
    //判断用户当前状态
    protected void judgeUser(int userStatus) {
        //如果处于等待复活中
        Log.i("Amlegend","用户状态是："+userStatus);
        if(userStatus==2){
            dialog_dead();
            return;
        }else if(userStatus==1){//被锁定

        }
    }
    //按键音效
    protected void musicEffect(int mtype){
        //扫描音效
        if(mtype==0) {
            try {
                final int aid = pool.load(getApplicationContext(), R.raw.music_scan, 1);
                pool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(SoundPool arg0, int arg1, int arg2) {
                        pool.play(aid, 1F, 1F, 1, 0, 1F);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            mybird.setAnimation(null);
            mybird.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.my_anim);
            animation.setRepeatCount(1);//设置重复次数
            animation.setFillAfter(true);
            mybird.setAnimation(animation);
            animation.start();
        }
        //攻击音效
        if(mtype==1){
            final int aid=pool.load(getApplicationContext(),R.raw.music_boom,1);
            pool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool arg0, int arg1, int arg2) {
                    pool.play(aid, 1F, 1F, 1, 0, 1F);
                }
            });
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
    //检查权限提示
    protected void dialog_rights() {
        new AlertDialog.Builder(this)
                .setTitle("嗨！")
                .setMessage("请保证自己成长所需要的权限！")
                .setPositiveButton("好的，知道了", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        SharedPreferences.Editor editor = getSharedPreferences("Amlegend", Context.MODE_PRIVATE).edit();
                        editor.putInt("tipInfo",1);
                        editor.commit();
                        dialoginterface.dismiss();
                    }
                }).show();
    }
    //检查权限提示
    protected void dialog_dead() {
        new AlertDialog.Builder(this)
                .setTitle("哇哦！")
                .setMessage("你被击毙了，等待复活吧！")
                .setPositiveButton("好的，知道了", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        android.os.Process.killProcess(android.os.Process
                                .myPid());
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
    //初始化用户信息
    private JsonHttpRequestCallback toInitUser = new JsonHttpRequestCallback(){
        @Override
        public void onStart() {
            Log.i("Amlegend","正在准备初始化"+PCM.initUser);
        }
        @Override
        protected void onSuccess(JSONObject jsonObject) {
            super.onSuccess(jsonObject);
            Log.i("Amlegend","返回的用户id是："+JsonFormatUtils.formatJson(jsonObject.toJSONString()));
            jsonInfo=jsonObject;
            mHandler.obtainMessage(0).sendToTarget();
        }
        //请求失败（服务返回非法JSON、服务器异常、网络异常）
        @Override
        public void onFailure(int errorCode, String msg) {
            Log.i("Amlegend","网络失败");
        }
        //请求网络结束
        @Override
        public void onFinish() {

        }
    };
    //获取目标信息
    private JsonHttpRequestCallback toGetTargets = new JsonHttpRequestCallback(){
        @Override
        public void onStart() {
            Log.i("Amlegend","正在获取周围目标"+PCM.getTargets);
        }
        @Override
        protected void onSuccess(JSONObject jsonObject) {
            super.onSuccess(jsonObject);
            Log.i("Amlegend","返回的搜索数据是："+JsonFormatUtils.formatJson(jsonObject.toJSONString()));
            searchTarget(jsonObject);
        }
        //请求失败（服务返回非法JSON、服务器异常、网络异常）
        @Override
        public void onFailure(int errorCode, String msg) {
            Log.i("Amlegend","网络失败");
        }
        //请求网络结束
        @Override
        public void onFinish() {

        }
    };
    //攻击目标
    private JsonHttpRequestCallback toBoomTarget = new JsonHttpRequestCallback(){
        @Override
        public void onStart() {
            Log.i("Amlegend","消灭目标"+PCM.boomTarget);
        }
        @Override
        protected void onSuccess(JSONObject jsonObject) {
            super.onSuccess(jsonObject);
            Log.i("Amlegend","返回的搜索数据是："+JsonFormatUtils.formatJson(jsonObject.toJSONString()));
            if(jsonObject.getInteger("code")==0){
                current_choice=-1;
                musicEffect(1);
                dialog_ko();
                userInit();
            }
        }
        //请求失败（服务返回非法JSON、服务器异常、网络异常）
        @Override
        public void onFailure(int errorCode, String msg) {
            Log.i("Amlegend","网络失败");
        }
        //请求网络结束
        @Override
        public void onFinish() {

        }
    };
    //获取提示信息
    private JsonHttpRequestCallback toTipInfo = new JsonHttpRequestCallback(){
        @Override
        public void onStart() {
            Log.i("Amlegend","获取信息"+PCM.tipInfo);
        }
        @Override
        protected void onSuccess(JSONObject jsonObject) {
            super.onSuccess(jsonObject);
            Log.i("Amlegend","返回的信息是："+JsonFormatUtils.formatJson(jsonObject.toJSONString()));
            if(jsonObject.getInteger("code")==0){
                JSONArray objArray = JSONObject.parseArray(jsonObject.getString("result"));
                tipInfo=objArray.getString(0);
                mHandler.obtainMessage(1).sendToTarget();
            }
        }
        //请求失败（服务返回非法JSON、服务器异常、网络异常）
        @Override
        public void onFailure(int errorCode, String msg) {
            Log.i("Amlegend","网络失败");
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
                    updateUser();
                    break;
                case 1:
                    Log.i("Amlegend","更新信息");
                    info_tip.setText(tipInfo);
                    break;
            }
        }
    };
    //独立线程获取提示信息
    public class msgThread implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (true) {
                try {
                    //定时检查用户状态
                    if(isInited>0) {
                        userInit();
                    }
                    //定时获取广播信息
                    Log.i("Amlegend","开始启动获取提示信息");
                    SharedPreferences pc = getSharedPreferences("Amlegend",Context.MODE_PRIVATE);
                    String userId = pc.getString("userId", "none");
                    //创建网络请求
                    RequestParams params = new RequestParams();
                    //表单数据
                    params.addFormDataPart("userId",userId);
                    params.addFormDataPart("lat",mylat);
                    params.addFormDataPart("lot",mylot);
                    try {
                        HttpRequest.post(PCM.tipInfo, params,toTipInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Thread.sleep(3000);// 执行完毕休眠，线程暂停3秒，单位毫秒

                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
    /*************
     * 加密字符串
     * 随机生成字符串当作DES加密口令（token）
     * 将加密口令（token）进行RSA加密
     * 每次将传输数据用DES进行加密
     * 将数据和加密口令(token)一起传输给服务器
     * */
    private void getToken(){
        String token="";
        //生成一个随机数
        long Temp=Math.round(Math.random()*89999999+10000000);
        String key=String.valueOf(Temp);
        Log.i("Amlegend","随机值是："+key);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("users","xiaowang");
        String tk=encryptStr(map,key);
        Log.i("Amlegend","des加密后："+tk);
        //return token;
    }
    private String encryptToken(String plainData){
        String str="";
        InputStream pubKey=null;
        String string_pubKey="";
        //读取公钥文件
        try {
            pubKey=getApplicationContext().getClass().getClassLoader().getResourceAsStream("assets/rsa_public_key.pem");
            int lenght = pubKey.available();
            byte[] buff = new byte[lenght];
            pubKey.read(buff);
            string_pubKey = new String(buff, "utf8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("Amlegend","公钥内容："+string_pubKey);
        RSAPublicKey publicKey = null;
        //处理公钥
        try {
            byte[] buffer = Base64.decode(string_pubKey, Base64.DEFAULT);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //进行RSA加密处理
        try {
            if (publicKey == null) {
                throw new NullPointerException("encrypt PublicKey is null !");
            }
            Cipher cipher = null;
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");// 此处如果写成"RSA"加密出来的信息JAVA服务器无法解析
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte plaintext[] = plainData.getBytes("UTF-8");
            byte[] output = cipher.doFinal(plaintext);
            // 必须先encode成 byte[]，再转成encodeToString，否则服务器解密会失败
            byte[] encode = Base64.encode(output, Base64.DEFAULT);
            str=new String(encode);
            Log.i("Amlegend","加密后的字符串："+s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }
    /**
     * DES加密
     * */
    private String encryptStr(Map<String,Object> map,String key){
        String str="";
        String json =  JSON.toJSONString(map);
        try {
            PCrypt pcr=new PCrypt();
            str=pcr.encrypt(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  str;
    }
}
