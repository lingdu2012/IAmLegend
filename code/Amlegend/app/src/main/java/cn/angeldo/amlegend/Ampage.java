package cn.angeldo.amlegend;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import static cn.angeldo.amlegend.R.drawable.target;
import static cn.angeldo.amlegend.R.id.itool;

public class Ampage extends Activity {
    private SimpleDraweeView user_logo;//用户头像
    private ImageView boom_tool;//道具
    private ImageView btn_search;//搜索按钮
    private RelativeLayout boom_area;//攻击区域
    private int current_choice=-1;//选择中目标的index
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_ampage);

        user_logo = (SimpleDraweeView)findViewById(R.id.my_tx);
        boom_tool=(ImageView)findViewById(itool);
        btn_search=(ImageView)findViewById(R.id.btn_search);
        boom_area=(RelativeLayout)findViewById(R.id.boom_area);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boom_area.removeAllViews();
                boom_area.notify();
                scanTarget(4);
                Toast.makeText(getApplicationContext(), "扫描完毕", Toast.LENGTH_SHORT).show();
            }
        });

        scanTarget(2);
        //绑定道具点击事件
        boom_tool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boomTarget();
            }
        });

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
            for(int i=0;i<num;i++) {
                ImageView t1 = new ImageView(this);
                t1.setImageResource(target);
                int dwl = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
                t1.setPadding(3 * dwl, 3 * dwl, 3 * dwl, 3 * dwl);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(25 * dwl, 25 * dwl);
                lp.topMargin = (150 - 30*i) * dwl;
                lp.leftMargin = (150 - 30*i) * dwl;
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
    }  //消灭目标
    private void boomTarget(){
        Log.i("消灭的id是", "：" + current_choice);
        int targetNum=boom_area.getChildCount();
        for(int i=0;i<targetNum;i++){
            Log.i("遍历的id是", "：" + boom_area.getChildAt(i).getId()+"位置id:"+i);

            if (boom_area.getChildAt(i).getId() == current_choice) {
                boom_area.removeViewAt(i);
            }

        }
        boom_area.refreshDrawableState();
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
}
