package cn.angeldo.amlegend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.facebook.drawee.backends.pipeline.Fresco;

public class Aminfo extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aminfo);

        //初始化fresco
        Fresco.initialize(this);


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
}
