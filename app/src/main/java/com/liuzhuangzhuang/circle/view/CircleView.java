package com.liuzhuangzhuang.circle.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.liuzhuangzhuang.circle.CircleViewInfoActivity;
import com.liuzhuangzhuang.circle.pojo.OrderType;
import com.liuzhuangzhuang.circle.utils.ActivityUtils;
import com.liuzhuangzhuang.circle.utils.LogUtils;

import java.util.List;

public class CircleView extends RelativeLayout {
	private static final String TAG = LogUtils.makeLogTag(CircleView.class.getSimpleName());

	private int canvasSize;
	private Paint paint;

	private static final int bianminColor = 0xff2b90d2;       // 便民服务背景颜色
	private static final int bianminLine1Color = 0xff1374b7;  // 便民服务线颜色
	private static final int bianminLine2Color = 0xff6bb2e0;  // 便民服务线颜色
	private static final int bianminPressColor = 0xff176398;  // 便民服务点击颜色
	private static final int shenghuoColor = 0xfff95d9c;      // 生活配送背景颜色
	private static final int shenghuoLine1Color = 0xffd82a70; // 生活配送线颜色
	private static final int shenghuoLine2Color = 0xfffb8eba; // 生活配送线颜色
	private static final int shenghuoPressColor = 0xffd82573; // 生活配送点击颜色

	private Bitmap mSrcB;
	private Bitmap mDstB;
	private static final Xfermode[] sModes = {
			new PorterDuffXfermode(PorterDuff.Mode.SRC),
			new PorterDuffXfermode(PorterDuff.Mode.DST),
			new PorterDuffXfermode(PorterDuff.Mode.DST_IN) };

	private int height;                // 每一行单元格的大小
	private int textSize;              // 文字大小
	private int textLineDivider;       // 文字和线的间隔
	private int currentSelected = -1;  // 当前选中项
	private int twoTextDivider;        // 两行文字的间隔

	private Mode mode = Mode.SINGLE;
	private LeftRight leftRight;

	public enum Mode {
		SINGLE, // 单列
		DOUBLE  // 双列
	}

	private enum LeftRight {
		LEFT,
		RIGHT
	}

	private List<OrderType> orderTypeList;

	public CircleView(Context context) {
		super(context);
	}

	public CircleView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	/** 绘制背景颜色 **/
	public void init(Context context, List<OrderType> orderTypeList, Mode mode) {
		this.paint = new Paint();
		this.paint.setAntiAlias(true);
		setWillNotDraw(false);
		this.orderTypeList = orderTypeList;
		this.textSize = ActivityUtils.dip2px(context, 14);
		this.twoTextDivider = ActivityUtils.dip2px(context, 20);
		this.mode = mode;
	}

	/** 绘制线 **/
	private Bitmap makeDst(int w, int h) {
		Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bm);

		if (mode == Mode.SINGLE) {
			c.drawColor(bianminColor);
		} else {
			c.drawColor(shenghuoColor);
		}

		if (mode == Mode.SINGLE) {
			// 分为N块
			height = canvasSize / orderTypeList.size();
		} else {
			// 分为N块
			height = canvasSize / orderTypeList.size() * 2;
		}

		Paint linePaint = new Paint();

		if (mode == Mode.SINGLE) {
			linePaint.setColor(bianminLine1Color);
		} else {
			linePaint.setColor(shenghuoLine1Color);
		}

		linePaint.setAntiAlias(true);

		Paint line2Paint = new Paint();
		if (mode == Mode.SINGLE) {
			line2Paint.setColor(bianminLine2Color);
		} else {
			line2Paint.setColor(shenghuoLine2Color);
		}
		line2Paint.setAntiAlias(true);

		int size;
		if (mode == Mode.SINGLE) {
			size = orderTypeList.size();
		} else {
			size = orderTypeList.size() / 2;
		}
		for (int i = 0; i < size; i++) {
			if (i != 0) {
				c.drawLine(0, height * i, canvasSize, height * i, linePaint);
				c.drawLine(0, height * i + 1, canvasSize, height * i + 1, line2Paint);
			}
		}

		Paint paint = new Paint();
		paint.setAntiAlias(true);

		if (mode == Mode.SINGLE) {
			paint.setColor(bianminPressColor);
		} else {
			paint.setColor(shenghuoPressColor);
		}

		if (currentSelected != -1) {
			int startY = height * currentSelected;
			int endY = startY + height;
			if (mode == Mode.SINGLE) {
				c.drawRect(0, startY, canvasSize, endY, paint);
			} else {
				if (leftRight == LeftRight.LEFT) {
					c.drawRect(0, startY, canvasSize / 2, endY, paint);
				} else if (leftRight == LeftRight.RIGHT) {
					c.drawRect(canvasSize / 2, startY, canvasSize, endY, paint);
				}
			}
		}

		// ///
		int center = canvasSize / 2;

		Paint textPaint = new Paint();
		textPaint.setColor(Color.WHITE);
		textPaint.setTextSize(textSize);
		textPaint.setAntiAlias(true);

		getTextLineDivider(height, textSize);

		if (mode == Mode.SINGLE) {
			for (int i = 0; i < orderTypeList.size(); i++) {
				String text = orderTypeList.get(i).getTypeName();
				float textSize = getTextSize(text);
				c.drawText(text, center - textSize / 2, height * (i + 1) - textLineDivider, textPaint);
			}
		} else {

			int sizej = orderTypeList.size() / 2;

			for (int i = 0, j = 0; i < orderTypeList.size(); i = i + 2, j++) {
				String text = orderTypeList.get(i).getTypeName();
				String text2 = orderTypeList.get(i + 1).getTypeName();
				float textSize = getTextSize(text);
				float textSize2 = getTextSize(text2);
				c.drawText(text, center - textSize - twoTextDivider, height * (j + 1) - textLineDivider, textPaint);
				c.drawText(text2, center + twoTextDivider, height * (j + 1) - textLineDivider, textPaint);
			}
		}

		return bm;
	}

	/** 画圆 画文字 **/
	private Bitmap makeSrc(int w, int h) {
		Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bm);
		Paint p = new Paint();
		p.setAntiAlias(true);

		if (mode == Mode.SINGLE) {
			p.setColor(bianminColor);
		} else {
			p.setColor(shenghuoColor);
		}

		int center = canvasSize / 2;
		c.drawCircle(center, center, canvasSize / 2, p);

		int height;
		if (mode == Mode.SINGLE) {
			height = canvasSize / orderTypeList.size();
		} else {
			height = canvasSize / orderTypeList.size() * 2;
		}

		Paint textPaint = new Paint();
		textPaint.setColor(Color.WHITE);
		textPaint.setTextSize(textSize);
		textPaint.setAntiAlias(true);

		getTextLineDivider(height, textSize);

		if (mode == Mode.SINGLE) {
			for (int i = 0; i < orderTypeList.size(); i++) {
				String text = orderTypeList.get(i).getTypeName();
				float textSize = getTextSize(text);
				c.drawText(text, center - textSize / 2, height * (i + 1) - textLineDivider, textPaint);
			}
		} else {
			for (int i = 0, j = 0; i < orderTypeList.size(); i = i + 2, j++) {
				String text = orderTypeList.get(i).getTypeName();
				String text2 = orderTypeList.get(i + 1).getTypeName();
				float textSize = getTextSize(text);
				float textSize2 = getTextSize(text2);
				c.drawText(text, center - textSize - twoTextDivider, height * (j + 1) - textLineDivider, textPaint);
				c.drawText(text2, center + twoTextDivider, height * (j + 1) - textLineDivider, textPaint);
			}
		}

		return bm;
	}

	@Override
	public void onDraw(Canvas canvas) {
		/** 获得控件高度宽度 **/
		canvasSize = getMeasuredWidth();
		if (canvas.getHeight() < canvasSize) {
			canvasSize = canvas.getHeight();
		}
		mSrcB = makeSrc(canvasSize, canvasSize);
		mDstB = makeDst(canvasSize, canvasSize);
		int sc = canvas.saveLayer(0, 0, canvasSize, canvasSize, null,
						Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG
						| Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
						| Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG);
		canvas.drawBitmap(mDstB, 0, 0, paint);
		paint.setXfermode(sModes[2]);
		canvas.drawBitmap(mSrcB, 0, 0, paint);
		paint.setXfermode(null);
		canvas.restoreToCount(sc);
	}

	/** 获得文字和分割线之间的距离 **/
	private float getTextLineDivider(int height, int textHeightSize) {
		textLineDivider = (height - textHeightSize) / 2;
		return textLineDivider;
	}

	/** 获得文字宽度 **/
	private float getTextSize(String string) {
		return string.length() * textSize;
	}

	private int getCurrentTouchPosition(float currentY) {
		return (int) (currentY / height);
	}

	/** 处理事件 **/
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:

			float currentX = event.getX();
			float currentY = event.getY();

			currentSelected = (int) (currentY / height);

			if (currentX < canvasSize / 2) {
				leftRight = LeftRight.LEFT;
			} else {
				leftRight = LeftRight.RIGHT;
			}

			invalidate();
			return true;
		case MotionEvent.ACTION_UP:
			if (currentSelected == getCurrentTouchPosition(event.getY())) {
				if (orderTypeList != null) {
					Intent intent;
					if (this.mode == Mode.SINGLE) {
						intent = dispatchIntent(orderTypeList.get(currentSelected));
					} else {
						int position = 0;
						if (leftRight == LeftRight.LEFT) {
							position = currentSelected * 2;
						} else if (leftRight == LeftRight.RIGHT) {
							position = currentSelected * 2 + 1;
						}
						intent = dispatchIntent(orderTypeList.get(position));
					}
					getContext().startActivity(intent);
				}
			}
			currentSelected = -1;
			invalidate();
			return false;
		case MotionEvent.ACTION_SCROLL:
		case MotionEvent.ACTION_OUTSIDE:
		case MotionEvent.ACTION_CANCEL:
			currentSelected = -1;
			invalidate();
			return true;
		}
		return super.onTouchEvent(event);
	}

	private Intent dispatchIntent(OrderType orderType) {
		if (orderType == null) {
			return null;
		}
		Intent intent = new Intent(getContext(), CircleViewInfoActivity.class);
		intent.putExtra("typeName", orderType.getTypeName());
		return intent;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
		int childWidthSize = getMeasuredWidth();
		// 高度和宽度一样
		heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
