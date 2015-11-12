package com.liuzhuangzhuang.circle.utils;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;

import com.liuzhuangzhuang.circle.view.CircleLayout;
import com.liuzhuangzhuang.circle.view.LittleCircle;

/**
 * Created by liuzhuang on 15/11/11.
 * 速度计算来自网上,现在找不到源地址了
 */
public class Calculate {
    private static final String TAG = LogUtils.makeLogTag(Calculate.class.getSimpleName());


    private float centerX;                // 原心坐标x
    private float centerY;                // 原心坐标y

    private SpeedHandler speedHandler;    // 处理惯性的handler
    private int delayedTime = 20;         // handler处理消息的间隔
    private final int play = 0;           // 消息信号，滚动的标识
    private final int stop = 1;           // 消息信号，停止滚动的标识
    private double currentTime = 0;       // 上次记录的时间,计算一定时间所走过的弧度、计算速度.

    private float downX;                  // 手指触屏的初始x的坐标
    private float downY;                  // 手指触屏的初始y的坐标
    private float targetX;                // 移动时的x的坐标
    private float targetY;                // 移动时的y的坐标
    private float upX;                    // 放手时的x的坐标
    private float upY;                    // 放手时的y的坐标

    private float currentDegree;          // 当前的弧度(以该 view 的中心为圆点)
    private float upDegree;               // 放手时的弧度(以该 view 的中心为圆点)
    private float detaDegree;             // 当前圆盘所转的弧度(以该 view 的中心为圆点)

    private double lastMoveTime = 0;      //  最后一次手势滑过的时间

    private final float a_min = 0.001f;   // 最小加速度（当手指放手是）
    private final float a_add = 0.001f;   // 加速度增量
    private float a = a_min;              // 加速度
    public final float a_max = a_min * 5; // 最大加速度（当手指按住时）

    private double speed = 0;             // 旋转速度(度/毫秒)
    private VRecord vRecord;              // 速度计算器

    private boolean isReverse;            //  是否为顺时针旋转

    public Calculate(CircleLayout circleLayout) {
        vRecord = new VRecord();
        speedHandler = new SpeedHandler(circleLayout);
    }

    private class SpeedHandler extends Handler {

        private CircleLayout circleLayout;

        public SpeedHandler(CircleLayout circleLayout) {
            super();
            this.circleLayout = circleLayout;
        }

        @Override
        public void handleMessage(Message msg) {
            double detaTime = System.currentTimeMillis() - currentTime;
            switch (msg.what) {

                case play: {
                    if (isReverse) {
                        speed = speed - a * detaTime;
                        LogUtils.LOGD(TAG, "惯性正方向...... speed : " + speed);
                        if (speed <= 0) {
                            return;
                        } else {
                            speedHandler.sendEmptyMessageDelayed(play, delayedTime);
                        }
                    } else {
                        speed = speed + a * detaTime;
                        LogUtils.LOGD(TAG, "惯性反方向...... speed : " + speed);
                        if (speed >= 0) {
                            return;
                        } else {
                            speedHandler.sendEmptyMessageDelayed(play, delayedTime);
                        }
                    }

                    addDegree((float) (speed * detaTime + (a * detaTime * detaTime) / 2));

                    currentTime = System.currentTimeMillis();
                    circleLayout.requestLayout(detaDegree);

                    break;
                }
                case stop: {
                    speed = 0;
                    speedHandler.removeMessages(play);
                }
            }
            super.handleMessage(msg);
        }
    }

    /**
     * 获得当前触摸的view
     */
    private View getCurrentTouchView(Point point, CircleLayout circleLayout) {
        int childCount = circleLayout.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View view = circleLayout.getChildAt(i);
            int x = point.x;
            int y = point.y;
            Rect rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
            boolean inContain = rect.contains(x, y);
            if (inContain) {
                return view;
            }
        }
        return null;
    }

    public boolean onTouchEvent(MotionEvent event, CircleLayout circleLayout) {
        LogUtils.LOGD(TAG, "onTouchEvent");
        centerX = circleLayout.getCenterX();
        centerY = circleLayout.getCenterY();

        Point point = new Point();
        point.set((int) event.getX(), (int) event.getY());
        View view = getCurrentTouchView(point, circleLayout);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                LogUtils.LOGD(TAG, "ACTION_DOWN");

                downX = event.getX();
                downY = event.getY();
                currentDegree = detaDegree(centerX, centerY, downX, downY);
                vRecord.reset();
                a = a_max;

                if (view != null && view instanceof LittleCircle) {
                    LogUtils.LOGD(TAG, "点中小圆");
                    LittleCircle littleCircle = (LittleCircle) view;
                    littleCircle.setActionDown();
                    return littleCircle.dispatchChildTouchEvent(event);
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                LogUtils.LOGD(TAG, "ACTION_MOVE");

                downX = targetX = event.getX();
                downY = targetY = event.getY();
                float degree = detaDegree(centerX, centerY, targetX, targetY);

                // 滑过的弧度增量
                float incrementDegree = degree - currentDegree;

                if (incrementDegree < -270) {
                    // 如果小于-90度说明 它跨周了，需要特殊处理350->17,
                    incrementDegree = incrementDegree + 360;
                } else if (incrementDegree > 270) {
                    // 如果大于90度说明 它跨周了，需要特殊处理-350->-17,
                    incrementDegree = incrementDegree - 360;
                }

                lastMoveTime = System.currentTimeMillis();
                vRecord.add(incrementDegree, lastMoveTime);
                addDegree(incrementDegree);
                currentDegree = degree;
                circleLayout.requestLayout(detaDegree);

                if (view != null && view instanceof LittleCircle) {
                    LittleCircle littleCircle = (LittleCircle) view;
                    littleCircle.setMoved();
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                LogUtils.LOGD(TAG, "ACTION_CANCEL ACTION_UP");

                a = a_min;
                upX = event.getX();
                upY = event.getY();
                upDegree = detaDegree(centerX, centerY, upX, upY);
                speed = speed + vRecord.getSpeed(); // 放手时的速度
                speed = (speed > 0) ? Math.min(VRecord.maxSpeed, speed) : Math.max(-VRecord.maxSpeed, speed);
                if (speed > 0) {
                    isReverse = true;
                } else {
                    isReverse = false;
                }
                currentTime = System.currentTimeMillis();
                speedHandler.sendEmptyMessage(0);

                if (view != null && view instanceof LittleCircle) {
                    LogUtils.LOGD(TAG, "在小圆上放开");
                    LittleCircle littleCircle = (LittleCircle) view;
                    return littleCircle.dispatchChildTouchEvent(event);
                } else {
                    /** 重置状态 **/
                    LogUtils.LOGD(TAG, "重置状态");
                    for (int i = 0; i < circleLayout.getChildCount(); i++) {
                        View v = circleLayout.getChildAt(i);
                        if (v instanceof LittleCircle) {
                            LittleCircle littleCircle = (LittleCircle) v;
                            littleCircle.resetSelect();
                        }
                    }
                }
                break;
            }
        }
        return true;
    }

    /**
     * 增加旋转度数，如果超过360让它求余，防止该值过大造成越界
     */
    private void addDegree(float added) {
        detaDegree += added;
        if (detaDegree > 360 || detaDegree < -360) {
            detaDegree = detaDegree % 360;
        }
    }

    /**
     * 计算以(src_x,src_y)为坐标原点建立直角体系,求出(targetX,targetY)坐标与x轴的夹角
     * 主要是利用反正切函数的知识求出夹角
     * <p/>
     *              |    .
     *              |   ∕
     *              |  ∕
     *              | ∕
     *              |∕ 角度
     * -------------|-------------
     *              |
     *              |
     *              |
     *              |
     *              |
     */
    private float detaDegree(float src_x, float src_y, float target_x, float target_y) {

        float detaX = target_x - src_x;
        float detaY = target_y - src_y;
        double d;
        if (detaX != 0) {
            float tan = Math.abs(detaY / detaX);

            if (detaX > 0) {

                if (detaY >= 0) {
                    d = Math.atan(tan);

                } else {
                    d = 2 * Math.PI - Math.atan(tan);
                }

            } else {
                if (detaY >= 0) {

                    d = Math.PI - Math.atan(tan);
                } else {
                    d = Math.PI + Math.atan(tan);
                }
            }

        } else {
            if (detaY > 0) {
                d = Math.PI / 2;
            } else {
                d = -Math.PI / 2;
            }
        }

        return (float) ((d * 180) / Math.PI);
    }

    /**
     * 速度计算器
     * 原理是将最近的弧度增量和时间点记录下来，然后通过增量除以总时间求出平均值做为它的即时手势滑过的速度
     *
     * @author sun.shine
     */
    private class VRecord {

        /**
         * 数组中的有效数字(添加进二维数组的次数)
         */
        private int addCount;

        /**
         * 最大能装的数据空间
         */
        public final int length = 15;

        /**
         * 二维数组
         * 1.保存弧度增量
         * 2.保存产生这个增量的时间点
         * 15*2=30
         * ---------------------------------
         * | + + + + + + + + + + + + + + + | detadegree 弧度
         * | + + + + + + + + + + + + + + + | time 时间
         * ---------------------------------
         */
        private double[][] record = new double[length][2];

        /**
         * 最大速度
         */
        public static final double maxSpeed = 0.8;

        /**
         * 为二维数组装载数据
         * 注：通过此方法，有个特点，能把最后的length组数据记录下来，length以外的会丢失
         */
        public void add(double detadegree, double time) {
            for (int i = length - 1; i > 0; i--) {
                record[i][0] = record[i - 1][0];
                record[i][1] = record[i - 1][1];
            }
            record[0][0] = detadegree;
            record[0][1] = time;
            addCount++;
        }

        /**
         * 通过数组里所装载的数据分析出即时速度
         * 原理是：计算数组里的时间长度和增量的总数，然后求出每毫秒所走过的弧度
         * 不能超过max_speed
         */
        public double getSpeed() {
            if (addCount == 0) {
                return 0;
            }
            int maxIndex = Math.min(addCount, length) - 1;

            if ((record[0][1] - record[maxIndex][1]) == 0) {
                return 0;
            }

            double detaTime = record[0][1] - record[maxIndex][1]; // 消耗的时间
            double sumdegree = 0;
            for (int i = 0; i < length - 1; i++) {
                sumdegree += record[i][0];
            }

            double result = sumdegree / detaTime;

            if (result > 0) {
                return Math.min(result, maxSpeed);
            } else {
                return Math.max(result, -maxSpeed);
            }
        }

        /**
         * 重置
         */
        public void reset() {
            addCount = 0;
            for (int i = length - 1; i > 0; i--) {
                record[i][0] = 0;
                record[i][1] = 0;
            }
        }
    }

}
