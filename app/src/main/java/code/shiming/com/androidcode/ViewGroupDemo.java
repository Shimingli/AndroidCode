package code.shiming.com.androidcode;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * author： Created by shiming on 2018/4/18 15:20
 * mailbox：lamshiming@sina.com
 * todo 为了保存主要分析的代码 ，会报错
 */

public class ViewGroupDemo  extends ViewGroup{

    public ViewGroupDemo(Context context) {
        super(context);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //手势输入的验证者
        if (mInputEventConsistencyVerifier != null) {
            mInputEventConsistencyVerifier.onTouchEvent(ev, 1);
        }
        //如果事件以可访问性为焦点的视图为目标，这就是，开始
        //正常事件分派。也许一个后代会处理点击。
        if (ev.isTargetAccessibilityFocus() && isAccessibilityFocusedViewOrHost()) {
            ev.setTargetAccessibilityFocus(false);
        }

        boolean handled = false;
        if (onFilterTouchEventForSecurity(ev)) {
            final int action = ev.getAction();
            final int actionMasked = action & MotionEvent.ACTION_MASK;

            // 处理初始值。第一个点击的ACTION_DOWN 事件
            if (actionMasked == MotionEvent.ACTION_DOWN) {
                //在开始一个新的触摸手势时丢弃所有以前的状态。
                //框架可能会取消上一个手势的上或取消事件。
                //由于程序开关，ANR，或一些其他的状态变化。
                cancelAndClearTouchTargets(ev);
                resetTouchState();
            }

            //检查拦截。
            final boolean intercepted;
            if (actionMasked == MotionEvent.ACTION_DOWN
                    || mFirstTouchTarget != null) {
                //  // 判断值1：disallowIntercept = 是否禁用事件拦截的功能(默认是false)，
                // 可通过调用requestDisallowInterceptTouchEvent()修改
                final boolean disallowIntercept = (mGroupFlags & FLAG_DISALLOW_INTERCEPT) != 0;
                if (!disallowIntercept) {
                    // // 重点分析1：ViewGroup每次事件分发时，都需调用onInterceptTouchEvent()询问是否拦截事件

                    // 判断值2：intercepted= onInterceptTouchEvent(ev)返回值
                    /**
                     a. 若在onInterceptTouchEvent()中返回false（即不拦截事件），就会让第二个值为true，从而进入到条件判断的内部
                     b. 若在onInterceptTouchEvent()中返回true（即拦截事件），就会让第二个值为false，从而跳出了这个条件判断
                     intercepted=false  不拦截
                     */
                    intercepted = onInterceptTouchEvent(ev);
                    //恢复操作，以防更改
                    ev.setAction(action); // restore action in case it was changed
                } else {
                    intercepted = false;
                }
            } else {
                //没有触摸目标，这个动作不是初始的。
                //因此，此视图组继续拦截触摸。
                intercepted = true;
            }

            ///如果被拦截，启动正常事件分派。如果已经有了
            // /正在处理手势的视图，执行正常事件调度。
            /**
             a. 若在onInterceptTouchEvent()中返回false（即不拦截事件），就会让第二个值为true，从而进入到条件判断的内部
             b. 若在onInterceptTouchEvent()中返回true（即拦截事件），就会让第二个值为false，从而跳出了这个条件判断
             */
            if (intercepted || mFirstTouchTarget != null) {
                ev.setTargetAccessibilityFocus(false);
            }

            // Check for cancelation. 检查取消。
            final boolean canceled = resetCancelNextUpFlag(this)
                    || actionMasked == MotionEvent.ACTION_CANCEL;
            //如果需要，指针更新的触摸目标更新列表。
            // Update list of touch targets for pointer down, if needed.
            final boolean split = (mGroupFlags & FLAG_SPLIT_MOTION_EVENTS) != 0;
            TouchTarget newTouchTarget = null;
            boolean alreadyDispatchedToNewTouchTarget = false;
            if (!canceled && !intercepted) {

                //  /如果事件是针对accessiiblity焦点我们给它的
                //   具有可访问性焦点的视图，如果它不处理它
                //   我们清理标记，像往常一样把事件发给所有的孩子。
                // /我们正在查找可访问性集中的主机以避免保留。
                // /由于这些事件非常罕见。
                View childWithAccessibilityFocus = ev.isTargetAccessibilityFocus()
                        ? findChildWithAccessibilityFocus() : null;

                if (actionMasked == MotionEvent.ACTION_DOWN
                        || (split && actionMasked == MotionEvent.ACTION_POINTER_DOWN)
                        || actionMasked == MotionEvent.ACTION_HOVER_MOVE) {
                    //按下永远为0
                    final int actionIndex = ev.getActionIndex(); // always 0 for down
                    final int idBitsToAssign = split ? 1 << ev.getPointerId(actionIndex)
                            : TouchTarget.ALL_POINTER_IDS;

                    ///清除这个指针id的早期触摸目标，以防它们
                    //变得不同步。
                    removePointersFromTouchTargets(idBitsToAssign);

                    final int childrenCount = mChildrenCount;
                    if (newTouchTarget == null && childrenCount != 0) {
                        final float x = ev.getX(actionIndex);
                        final float y = ev.getY(actionIndex);

                        ///找到一个可以接收事件的孩子。
                        //对孩子进行前后扫描。
                        final ArrayList<View> preorderedList = buildOrderedChildList();
                        final boolean customOrder = preorderedList == null
                                && isChildrenDrawingOrderEnabled();
                        final View[] children = mChildren;
                        //，遍历了当前ViewGroup下的所有子View
                        for (int i = childrenCount - 1; i >= 0; i--) {
                            final int childIndex = customOrder
                                    ? getChildDrawingOrder(childrenCount, i) : i;
                            final View child = (preorderedList == null)
                                    ? children[childIndex] : preorderedList.get(childIndex);

                            ///如果有可访问性焦点的视图，我们希望它
                            //  获取事件，如果不处理，我们将执行一个
                            //  正常调度。我们可以做双重迭代，但这是
                            //  更安全鉴于时间表。
                            if (childWithAccessibilityFocus != null) {
                                if (childWithAccessibilityFocus != child) {
                                    continue;
                                }
                                childWithAccessibilityFocus = null;
                                i = childrenCount - 1;
                            }

                            if (!canViewReceivePointerEvents(child)
                                    || !isTransformedTouchPointInView(x, y, child, null)) {
                                ev.setTargetAccessibilityFocus(false);
                                continue;
                            }

                            newTouchTarget = getTouchTarget(child);
                            if (newTouchTarget != null) {
                                ///孩子在其范围内已经收到了触摸。
                                //给它一个新指针，除了它正在处理的指针。
                                newTouchTarget.pointerIdBits |= idBitsToAssign;
                                break;
                            }

                            resetCancelNextUpFlag(child);
                            if (dispatchTransformedTouchEvent(ev, false, child, idBitsToAssign)) {
                                //孩子想在自己的范围内接受边界。
                                mLastTouchDownTime = ev.getDownTime();
                                if (preorderedList != null) {
                                    // 子索引点预设列表，找到原始指标
                                    for (int j = 0; j < childrenCount; j++) {
                                        if (children[childIndex] == mChildren[j]) {
                                            mLastTouchDownIndex = j;
                                            break;
                                        }
                                    }
                                } else {
                                    mLastTouchDownIndex = childIndex;
                                }
                                mLastTouchDownX = ev.getX();
                                mLastTouchDownY = ev.getY();
                                newTouchTarget = addTouchTarget(child, idBitsToAssign);
                                alreadyDispatchedToNewTouchTarget = true;
                                break;
                            }

                            ///可访问性焦点没有处理事件，所以很清楚。
                            //[并]向所有的孩子正常派遣。
                            ev.setTargetAccessibilityFocus(false);
                        }
                        if (preorderedList != null) preorderedList.clear();
                    }

                    if (newTouchTarget == null && mFirstTouchTarget != null) {
                        //没有找到一个孩子来接这件事。
                        ///将指针分配给最近添加的目标。
                        newTouchTarget = mFirstTouchTarget;
                        while (newTouchTarget.next != null) {
                            newTouchTarget = newTouchTarget.next;
                        }
                        newTouchTarget.pointerIdBits |= idBitsToAssign;
                    }
                }
            }

            // Dispatch to touch targets./发送到触摸目标。
            if (mFirstTouchTarget == null) {
                // No touch targets so treat this as an ordinary view.
                //没有触及目标，所以把它当作普通的观点看待。
                handled = dispatchTransformedTouchEvent(ev, canceled, null,
                        TouchTarget.ALL_POINTER_IDS);
            } else {
                // //发送到触摸目标，不包括新的触摸目标，如果我们已经
                //     被派往。如有必要取消触摸目标。
                TouchTarget predecessor = null;
                TouchTarget target = mFirstTouchTarget;
                while (target != null) {
                    final TouchTarget next = target.next;
                    if (alreadyDispatchedToNewTouchTarget && target == newTouchTarget) {
                        handled = true;
                    } else {
                        final boolean cancelChild = resetCancelNextUpFlag(target.child)
                                || intercepted;
                        //  条件判断的内部调用了该View的dispatchTouchEvent()
                        // 即 实现了点击事件从ViewGroup到子View的传递（具体请看下面的View事件分发机制）
                        if (dispatchTransformedTouchEvent(ev, cancelChild,
                                target.child, target.pointerIdBits)) {
                            handled = true;
                        }
                        if (cancelChild) {
                            if (predecessor == null) {
                                mFirstTouchTarget = next;
                            } else {
                                predecessor.next = next;
                            }
                            target.recycle();
                            target = next;
                            continue;
                        }
                    }
                    predecessor = target;
                    target = next;
                }
            }

            //更新需要更新或取消的触摸目标列表。
            if (canceled
                    || actionMasked == MotionEvent.ACTION_UP
                    || actionMasked == MotionEvent.ACTION_HOVER_MOVE) {
                resetTouchState();
            } else if (split && actionMasked == MotionEvent.ACTION_POINTER_UP) {
                final int actionIndex = ev.getActionIndex();
                final int idBitsToRemove = 1 << ev.getPointerId(actionIndex);
                removePointersFromTouchTargets(idBitsToRemove);
            }
        }

        if (!handled && mInputEventConsistencyVerifier != null) {
            mInputEventConsistencyVerifier.onUnhandledEvent(ev, 1);
        }
        return handled;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    /**
     *将运动事件转换为特定子视图的坐标空间，
     *过滤不相关的指针ID，并在必要时重写其操作。
     *如果孩子是无效的，假设位移事件将被发送到这个ViewGroup相反。
     */
    private boolean dispatchTransformedTouchEvent(MotionEvent event, boolean cancel,
                                                  View child, int desiredPointerIdBits) {
        final boolean handled;

        //取消运动是一种特殊情况。我们不需要执行任何转换
        //或过滤。重要的是行动，而不是内容。
        final int oldAction = event.getAction();
        if (cancel || oldAction == MotionEvent.ACTION_CANCEL) {
            event.setAction(MotionEvent.ACTION_CANCEL);
            // 调用ViewGroup父类的dispatchTouchEvent()，即View.dispatchTouchEvent()
            // 因此会执行ViewGroup的onTouch() ->> onTouchEvent() ->> performClick（） ->> onClick()，
            // 即自己处理该事件，事件不会往下传递（具体请参考View事件的分发机制中的View.dispatchTouchEvent（））
            // 此处需与上面区别：子View的dispatchTouchEvent（）

            // 若点击的是空白处（即无任何View接收事件） / 拦截事件（手动复写onInterceptTouchEvent（），从而让其返回true）
            if (child == null) {
                handled = super.dispatchTouchEvent(event);
            } else {
                //调用View onTouchEvent() ->> performClick（） ->> onClick()，
                handled = child.dispatchTouchEvent(event);
            }
            event.setAction(oldAction);
            /**
             调用子View的dispatchTouchEvent后是有返回值的
             若该控件可点击，那么点击时，dispatchTouchEvent的返回值必定是true，因此会导致条件判断成立
             于是给ViewGroup的dispatchTouchEvent（）直接返回了true，即直接跳出
             即把ViewGroup的点击事件拦截掉
             */
            return handled;
        }
}
