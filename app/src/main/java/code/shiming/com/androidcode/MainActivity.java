package code.shiming.com.androidcode;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

/**
 * 注释源码，这个Demo，主要是看源码
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button=null;
//        button.setOnClickListener();
        /**
         * 条件2：(mViewFlags & ENABLED_MASK) == ENABLED
         * 说明：
         *     a. 该条件是判断当前点击的控件是否enable
         *     b. 由于很多View默认enable，故该条件恒定为true
         */
        // 若在onTouch（）返回true，就会让上述三个条件全部成立，
        // 从而使得View.dispatchTouchEvent（）直接返回true，事件分发结束

        // 若在onTouch（）返回false，就会使得上述三个条件不全部成立，
        // 从而使得View.dispatchTouchEvent（）中跳出If，执行onTouchEvent(event)
//        button.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return false;
//            }
//        });

//        button.setOnClickListener(new );
         //第一个判断为false，后面的都不会走，nice
        if (null != null && getNum()!= 1) {
            System.out.println("shiming ");
        }
    }

    public int getNum() {
        System.out.println("shiming "+1);
        return 1;
    }
}
