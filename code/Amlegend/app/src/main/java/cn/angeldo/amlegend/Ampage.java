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

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

public class Ampage extends Activity {
    private SimpleDraweeView mytx;
    private ImageView itool;
    private ImageView btn_search;
    private RelativeLayout b_area;
    private int ichoice=-1;//选择中目标的index
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_ampage);

        mytx = (SimpleDraweeView)findViewById(R.id.my_tx);
        itool=(ImageView)findViewById(R.id.itool);
        btn_search=(ImageView)findViewById(R.id.btn_search);
        b_area=(RelativeLayout)findViewById(R.id.boom_area);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });

        try {
            for(int i=0;i<2;i++) {
                ImageView t1 = new ImageView(this);
                t1.setImageResource(R.drawable.target);
                int dwl = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
                t1.setPadding(3 * dwl, 3 * dwl, 3 * dwl, 3 * dwl);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(25 * dwl, 25 * dwl);
                lp.topMargin = (150 - 30*i) * dwl;
                lp.leftMargin = (150 - 30*i) * dwl;
                t1.setLayoutParams(lp);
                t1.setId(i);
                t1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("此处的id是", "：" + v.getId() + "元素个数为：" + b_area.getChildCount());
                        if(ichoice==v.getId()){
                            v.setBackgroundResource(0);
                            ichoice = -1;
                        }else{
                            if(ichoice > -1) {
                                b_area.getChildAt(ichoice).setBackgroundResource(0);
                            }
                            v.setBackgroundResource(R.drawable.locking);
                            ichoice=v.getId();
                        }

                    }
                });
                b_area.addView(t1);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        //绑定道具点击事件
        itool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b_area.removeViewAt(0);
                dialog_ko();
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
