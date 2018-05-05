package code.shiming.com.androidcode;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * author： Created by shiming on 2018/4/18 15:30
 * mailbox：lamshiming@sina.com
 *
 * todo 为了保存主要分析的代码 ，会报错
 */

public class ActivityDemo extends Activity{

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            onUserInteraction();
        }
        //2、触发方法 getWindow().superDispatchTouchEvent(ev)
//        getWindow()：：：mWindow = new PhoneWindow(this);
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    /**
     * Activity中的onTouchEvent，个人理解在返回键的时候哦，退出App（或者是一个Activity的结束）
     * @param event
     * @return
     */
    public boolean onTouchEvent(MotionEvent event) {
        if (mWindow.shouldCloseOnTouch(this, event)) {
            finish();
            return true;
        }
        //只有在点击事件在Window边界外才会返回true，一般情况都返回false
        return false;
    }
    /**
     * Window中调用 mWindow.shouldCloseOnTouch(this, event)
     * // 返回true：说明事件在边界外，即 消费事件
     // 返回false：未消费（默认）
     */
    /** @hide */
    public boolean shouldCloseOnTouch(Context context, MotionEvent event) {
        if (mCloseOnTouchOutside && event.getAction() == MotionEvent.ACTION_DOWN
                && isOutOfBounds(context, event) && peekDecorView() != null) {
            return true;
        }
        // 主要是对于处理边界外点击事件的判断：是否是DOWN事件，event的坐标是否在边界内等
        return false;
    }

    /**
     定义：属于顶层View（DecorView）
     * 说明：
     a. DecorView类是PhoneWindow类的一个内部类
     b. DecorView继承自FrameLayout，是所有界面的父类
     c. FrameLayout是ViewGroup的子类，故DecorView的间接父类 = ViewGroup
     */
    private final class DecorView extends FrameLayout {
        public DecorView(@NonNull Context context) {
            super(context);
        }

        public boolean superDispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
        // 调用父类的方法 = ViewGroup的dispatchTouchEvent()
        // 即 将事件传递到ViewGroup去处理，详细请看ViewGroup的事件分发机制

    }}
    /**
     * PhoneWindow中的源码结构  mDecor
     * @param event
     * @return
     */
    public boolean superDispatchTouchEvent(MotionEvent event) {
        return mDecor.superDispatchTouchEvent(event);
    }

    /**
     * 分析1：onUserInteraction()
     * 作用：实现屏保功能
     * 注：
     *    a. 该方法为空方法
     *    b. 当此activity在栈顶时，触屏点击按home，back，menu键等都会触发此方法
     */
    public void onUserInteraction() {

    }
}
