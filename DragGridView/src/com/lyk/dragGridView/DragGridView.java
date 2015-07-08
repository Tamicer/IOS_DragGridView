package com.lyk.dragGridView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.CycleInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.example.draggridview.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * @blog http://blog.csdn.net/sk719887916
 * 
 * @author lyk
 * 
 */

public class DragGridView extends GridView implements OnClickListener{

	/** 默认列数 */
	public static final int DEFAULT_COLUMN = 4;
	/** 行和行之间的间隔，列和列之间的间隔 */
	public static final int UI_SPACING = 1;
	/** 列数 */
	private int mColumnNum = DEFAULT_COLUMN;
	/** 横向列数 */
	private static final int  mColumnNum_Hriztal = 6;
	/** 屏幕密度 */
	private float mDensity;
	/** data source */
	private static final int ICON_WIDTH = 80;
	private static final int ICON_HEIGHT = 94;
	private static final float DEGREE_0 = 1.8f;
	private static final float DEGREE_1 = -2.0f;
	private static final float DEGREE_2 = 2.0f;
	private static final float DEGREE_3 = -1.5f;
	private static final float DEGREE_4 = 1.5f;
	private static final int ANIMATION_DURATION = 50;
	/** 
	 * mPaddingLeftInit 
	 * */
	private int mPaddingLeftInit;
	/** 
	 * mPaddingTopInit
	 *  */
	private int mPaddingTopInit;
	/** 
	 * mPaddingRightInit
	 *  */
	private int mPaddingRightInit;
	/** 
	 * mPaddingBottomInit
	 *  */
	private int mPaddingBottomInit;
	/** 
	 * mCount 
	 * */
	private int mCount = 0;
	/**
	 * item长按响应的时间， 默认是1000毫秒
	 */
	private long dragResponseMS = 1000;
	
	/**
	 * item长按响应的时间，开启抖动动画默认时间
	 */
	private long dragResponseAM = 10;
	
	/**
	 *长按后继续拖动生效时间
	 */
	private long dragResponseCT = 5;

	/**
	 * 是否可以拖拽，默认不可以
	 */
	private boolean isDrag = false;
	
	/** 是否需要抖动 */
	private boolean mNeedShake = false;
	

	/** 是否开始抖动*/
	private boolean mStartShake = false;
	
	private boolean mAnimationEnd = true;

	/**
	 * DownX
	 */
	private int mDownX;
	
	/**
	 * DownY
	 */
	private int mDownY;
	
	/**
	 * moveX
	 */
	private int moveX;
	
	/**
	 * moveY
	 */
	private int moveY;
	/**
	 * 正在拖拽的position
	 */
	private int mDragPosition;

	/**
	 * 刚开始拖拽的item对应的View
	 */
	private View mStartDragItemView = null;
	
	/**
	 * 当前最后一个View
	 */
	private View mLastItemView = null;
	/**
	 *  删除按钮
	 */
	private ImageButton mDeleteButton;

	/**
	 * 用于拖拽的镜像，这里直接用一个ImageView
	 */
	private ImageView mDragImageView;

	/**
	 * 震动器
	 */
	private Vibrator mVibrator;

	private WindowManager mWindowManager;
	/**
	 * item镜像的布局参数
	 */
	private WindowManager.LayoutParams mWindowLayoutParams;

	/**
	 * 我们拖拽的item对应的Bitmap
	 */
	private Bitmap mDragBitmap;

	/**
	 * 按下的点到所在item的上边缘的距离
	 */
	private int mPoint2ItemTop;

	/**
	 * 按下的点到所在item的左边缘的距离
	 */
	private int mPoint2ItemLeft;

	/**
	 * DragGridView距离屏幕顶部的偏移量
	 */
	private int mOffset2Top;

	/**
	 * DragGridView距离屏幕左边的偏移量
	 */
	private int mOffset2Left;

	/**
	 * 状态栏的高度
	 */
	private int mStatusHeight;

	/**
	 * DragGridView自动向下滚动的边界值
	 */
	private int mDownScrollBorder;

	/**
	 * DragGridView自动向上滚动的边界值
	 */
	private int mUpScrollBorder;

	/**
	 * DragGridView自动滚动默认的速度
	 */
	private static final int speed = 20;

	/**
	 * DragGridListener
	 */
	private DragGridListener mDragAdapter;
	
	private int mNumColumns;
	
	private int mColumnWidth;
	
	private boolean mNumColumnsSet;
	
	private ExitMrg mEMrg;

	/**
	 * 行和行之间的间隔
	 */
	private int mHorizontalSpacing;

	/**
	 * 列和列之间的间隔
	 */
	private int mVerticalSpacing ;

	public DragGridView(Context context) {
		this(context, null);
	}

	public DragGridView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DragGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mEMrg = new ExitMrg();  
		mVibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
		mWindowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		mStatusHeight = getStatusHeight(context);
		// dex UI
		mPaddingLeftInit = (int) getResources().getDimension(R.dimen.PaddingLeft);
		mPaddingTopInit = (int) getResources().getDimension(R.dimen.PaddingTop);
		mPaddingRightInit = (int) getResources().getDimension(R.dimen.PaddingRigh);
		mPaddingBottomInit = (int) getResources().getDimension(R.dimen.PaddingBottomt);
	
		if (!mNumColumnsSet) {
			mNumColumns = AUTO_FIT;
		}

	}

	private Handler mHandler = new Handler();

	
	private Runnable mLongClickRunnable = new Runnable() {

	

		@Override
		public void run() {
			isDrag = true;
			if ( dragResponseMS > dragResponseCT) {
				
				mVibrator.vibrate(50); 
			}
			onStartAnimation();
			
			setHideSartItemView();
			mDragAdapter.setHideItem(mDragPosition);
			createDragImage(mDragBitmap, mDownX, mDownY);
		}
	};
	/**
	 * 当moveY的值大于向上滚动的边界值，触发GridView自动向上滚动 当moveY的值小于向下滚动的边界值，触发GridView自动向下滚动
	 * 否则不进行滚动
	 */
	private Runnable mScrollRunnable = new Runnable() {

		@Override
		public void run() {
			int scrollY;
			if (getFirstVisiblePosition() == 0
					|| getLastVisiblePosition() == getCount() - 1) {
				mHandler.removeCallbacks(mScrollRunnable);
			}

			if (moveY > mUpScrollBorder) {
				scrollY = speed;
				mHandler.postDelayed(mScrollRunnable, 25);
			} else if (moveY < mDownScrollBorder) {
				scrollY = -speed;
				mHandler.postDelayed(mScrollRunnable, 25);
			} else {
				scrollY = 0;
				mHandler.removeCallbacks(mScrollRunnable);
			}

			smoothScrollBy(scrollY, 10);
		}
	};
	
	private Runnable mAnimationRunnable = new Runnable() {
		@Override
		public void run() {
		  
		    	
		    	for (int i = 0; i < getChildCount(); i++) {
					final View mGridItemView = getChildAt(i);
						mDeleteButton = (ImageButton) mGridItemView.findViewById(R.id.grid_item_delte);
						mDeleteButton.setOnClickListener(DragGridView.this);
						if (mDeleteButton.getVisibility() != View.VISIBLE ) {
							mDeleteButton.setVisibility(View.VISIBLE);
						}
						if (mNeedShake) {
							mStartShake = true;
							shakeAnimation(mGridItemView);
						}
						
					}
		}
	};

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		if (adapter instanceof DragGridListener) {
			mDragAdapter = (DragGridListener) adapter;
		} else {
			throw new IllegalStateException(
					"the adapter must be implements DragGridAdapter");
		}
	}

	@Override
	public void setNumColumns(int numColumns) {
		super.setNumColumns(numColumns);
		mNumColumnsSet = true;
		this.mNumColumns = numColumns;
	}

	@Override
	public void setColumnWidth(int columnWidth) {
		super.setColumnWidth(columnWidth);
		mColumnWidth = columnWidth;
	}

	@Override
	public void setHorizontalSpacing(int horizontalSpacing) {
		super.setHorizontalSpacing(horizontalSpacing);
		this.mHorizontalSpacing = horizontalSpacing;
	}
	
	@Override
	public void setVerticalSpacing(int verticalSpacing) {
		
		if (isLandscape(getContext())) {
		
			mVerticalSpacing =  (int) getResources().getDimension(R.dimen.Hri_VerticalSpacing);

		} else {
			mVerticalSpacing =  (int) getResources().getDimension(R.dimen.VerticalSpacing);
		}
	    
		super.setVerticalSpacing(mVerticalSpacing);
		
	}



	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		if (mNumColumns == AUTO_FIT){
			if (isLandscape(getContext())) {
				mPaddingTopInit  = (int) getResources().getDimension(R.dimen.HriontalPaddingTop);
				setNumColumns(mColumnNum_Hriztal);

			} else {
				setNumColumns(mColumnNum);
			}
		}

		setPadding(mPaddingLeftInit, mPaddingTopInit, mPaddingRightInit, mPaddingBottomInit);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * 判读横竖屏
	 * 
	 * @param aContext
	 *            context
	 * @return true
	 */
	public static boolean isLandscape(Context aContext) {

		return (aContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
	}

	/**
	 * 设置响应拖拽的毫秒数，默认是1000毫秒
	 * 
	 * @param dragResponseMS
	 */
	public void setDragResponseMS(long dragResponseMS) {
		this.dragResponseMS = dragResponseMS;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mDownX = (int) ev.getX();
			mDownY = (int) ev.getY();

			// 根据按下的X,Y坐标获取所点击item的position
			mDragPosition = pointToPosition(mDownX, mDownY);
			

			if (mDragPosition == AdapterView.INVALID_POSITION) {
				return super.dispatchTouchEvent(ev);
			}
			
			mStartDragItemView = getChildAt(mDragPosition
					- getFirstVisiblePosition());
		
			//
			//performLongClick();
			if (isShowShake() && isShowDelele()) {
				dragResponseMS = dragResponseCT;
			}
			else {
				dragResponseMS = 1000 ;
			}
			

			// 使用Handler延迟dragResponseMS执行mLongClickRunnable
			mHandler.postDelayed(mLongClickRunnable, dragResponseMS);
			
			mPoint2ItemTop = mDownY - mStartDragItemView.getTop();
			mPoint2ItemLeft = mDownX - mStartDragItemView.getLeft();

			mOffset2Top = (int) (ev.getRawY() - mDownY);
			mOffset2Left = (int) (ev.getRawX() - mDownX);

			// 获取DragGridView自动向上滚动的偏移量，小于这个值，DragGridView向下滚动
			mDownScrollBorder = getHeight() / 5;
			// 获取DragGridView自动向下滚动的偏移量，大于这个值，DragGridView向上滚动
			mUpScrollBorder = getHeight() * 4 / 5;

			// 开启mDragItemView绘图缓存
			mStartDragItemView.setDrawingCacheEnabled(true);
			// 获取mDragItemView在缓存中的Bitmap对象
			mDragBitmap = Bitmap.createBitmap(mStartDragItemView
					.getDrawingCache());
			// 这一步很关键，释放绘图缓存，避免出现重复的镜像
			mStartDragItemView.destroyDrawingCache();
			
			

			break;
		case MotionEvent.ACTION_MOVE:
			int moveX = (int) ev.getX();
			int moveY = (int) ev.getY();
			if (!isTouchInItem(mStartDragItemView, moveX, moveY)) {
				
				mHandler.removeCallbacks(mLongClickRunnable);
			}
			break;
		case MotionEvent.ACTION_UP:
			mHandler.removeCallbacks(mLongClickRunnable);
			mHandler.removeCallbacks(mScrollRunnable);
			
			break;
		}
		return super.dispatchTouchEvent(ev);
	}
	
	/**
	 * @return
	 */
	private boolean isShowDelele() {
		
		return  mDeleteButton !=null && mDeleteButton.isShown();
	}

	/**
	 * 开始抖动动画
	 */
	private void onStartAnimation() {
		if (mHandler == null)  {
			return;
		}
		
		if (mAnimationRunnable == null ) {
			return;
		}
		
		
		
	    mHandler.postDelayed(mAnimationRunnable, dragResponseAM);
		
	}

	/**
	 * 停止抖动动画
	 */
	private void onStopAnimation() {
		
		if (mHandler == null) {
			return;
		}
		
		if (mAnimationRunnable == null ) {
			return;
		}
		mStartShake = false;
		
		mHandler.removeCallbacks(mAnimationRunnable);
            
	}

	/**
	 * 是否点击在GridView的item上面
	 * 
	 * @param itemView
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean isTouchInItem(View dragView, int x, int y) {
		if (dragView == null) {
			return false;
		}
		int leftOffset = dragView.getLeft();
		int topOffset = dragView.getTop();
		if (x < leftOffset || x > leftOffset + dragView.getWidth()) {
			return false;
		}

		if (y < topOffset || y > topOffset + dragView.getHeight()) {
			return false;
		}

		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (isDrag && mDragImageView != null) {
			switch (ev.getAction()) {
			case MotionEvent.ACTION_MOVE:
				moveX = (int) ev.getX();
				moveY = (int) ev.getY();
				onDragItem(moveX, moveY);
				onStartAnimation();
				break;
			case MotionEvent.ACTION_UP:
				onStopDrag();
				isDrag = false;
				onStartAnimation();
				
				break;
			}
			return true;
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * 创建拖动的镜像
	 * 
	 * @param bitmap
	 * @param downX
	 *            按下的点相对父控件的X坐标
	 * @param downY
	 *            按下的点相对父控件的X坐标
	 */
	private void createDragImage(Bitmap bitmap, int downX, int downY) {
		mWindowLayoutParams = new WindowManager.LayoutParams();
		mWindowLayoutParams.format = PixelFormat.TRANSLUCENT; 
		mWindowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
		mWindowLayoutParams.x = downX - mPoint2ItemLeft + mOffset2Left;
		mWindowLayoutParams.y = downY - mPoint2ItemTop + mOffset2Top
				- mStatusHeight;
		mWindowLayoutParams.alpha = 0.55f; 
		mWindowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

		mDragImageView = new ImageView(getContext());
		mDragImageView.setImageBitmap(bitmap);
		mWindowManager.addView(mDragImageView, mWindowLayoutParams);
	}

	/**
	 * 从界面上面移动拖动镜像
	 */
	private void removeDragImage() {
		if (mDragImageView != null) {
			mWindowManager.removeView(mDragImageView);
			mDragImageView = null;
		}
	}

	/**
	 * 拖动item，在里面实现了item镜像的位置更新，item的相互交换以及GridView的自行滚动
	 * 
	 * @param x
	 * @param y
	 */
	private void onDragItem(int moveX, int moveY) {
		
		
		mDragAdapter.setHideItem(mDragPosition);
		//setHideSartItemView();
		mWindowLayoutParams.x = moveX - mPoint2ItemLeft + mOffset2Left;
		mWindowLayoutParams.y = moveY - mPoint2ItemTop + mOffset2Top
				- mStatusHeight;
		if (mDragImageView != null) {
			mWindowManager.updateViewLayout(mDragImageView, mWindowLayoutParams); // 更新镜像的位置
		}
		onSwapItem(moveX, moveY);

		// GridView自动滚动
		mHandler.post(mScrollRunnable);
	}

	
	

	/**
	 * 交换item,并且控制item之间的显示与隐藏效果
	 * 
	 * @param moveX
	 * @param moveY
	 */
	private void onSwapItem(int moveX, int moveY) {
		// 获取我们手指移动到的那个item的position
		final int tempPosition = pointToPosition(moveX, moveY);

		// 假如tempPosition 改变了并且tempPosition不等于-1,则进行交换
		if (tempPosition != mDragPosition
				&& tempPosition != AdapterView.INVALID_POSITION
				&& mAnimationEnd) {
			mDragAdapter.reorderItems(mDragPosition, tempPosition);
			mDragAdapter.setHideItem(tempPosition);

			final ViewTreeObserver observer = getViewTreeObserver();
			observer.addOnPreDrawListener(new OnPreDrawListener() {

				@Override
				public boolean onPreDraw() {
					observer.removeOnPreDrawListener(this);
					animateReorder(mDragPosition, tempPosition);
					mDragPosition = tempPosition;
					return true;
				}
			});

		}
	}

	/**
	 * 创建移动动画
	 * 
	 * @param view
	 * @param startX
	 * @param endX
	 * @param startY
	 * @param endY
	 * @return
	 */
	private AnimatorSet createTranslationAnimations(View view, float startX,
			float endX, float startY, float endY) {
		ObjectAnimator animX = ObjectAnimator.ofFloat(view, "translationX",
				startX, endX);
		ObjectAnimator animY = ObjectAnimator.ofFloat(view, "translationY",
				startY, endY);
		AnimatorSet animSetXY = new AnimatorSet();
		animSetXY.playTogether(animX, animY);
		return animSetXY;
	}

	/**
	 * item的交换动画效果
	 * 
	 * @param oldPosition
	 * @param newPosition
	 */
	private void animateReorder(final int oldPosition, final int newPosition) {
		boolean isForward = newPosition > oldPosition;
		List<Animator> resultList = new LinkedList<Animator>();
		if (isForward) {
			for (int pos = oldPosition; pos < newPosition; pos++) {
				View view = getChildAt(pos - getFirstVisiblePosition());
				System.out.println(pos);

				if ((pos + 1) % mNumColumns == 0) {
					resultList.add(createTranslationAnimations(view,
							-view.getWidth() * (mNumColumns - 1), 0,
							view.getHeight(), 0));
				} else {
					resultList.add(createTranslationAnimations(view,
							view.getWidth(), 0, 0, 0));
				}
			}
		} else {
			for (int pos = oldPosition; pos > newPosition; pos--) {
				View view = getChildAt(pos - getFirstVisiblePosition());
				if ((pos + mNumColumns) % mNumColumns == 0) {
					resultList.add(createTranslationAnimations(view,
							view.getWidth() * (mNumColumns - 1), 0,
							-view.getHeight(), 0));
				} else {
					resultList.add(createTranslationAnimations(view,
							-view.getWidth(), 0, 0, 0));
				}
			}
		}

		AnimatorSet resultSet = new AnimatorSet();
		resultSet.playTogether(resultList);
		resultSet.setDuration(300);
		resultSet.setInterpolator(new AccelerateDecelerateInterpolator());
		resultSet.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				mAnimationEnd = false;
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				mAnimationEnd = true;
			}
		});
		resultSet.start();
	}

	/**
	 * 停止拖拽我们将之前隐藏的item显示出来，并将镜像移除
	 */
	private void onStopDrag() {
		View view = getChildAt(mDragPosition - getFirstVisiblePosition());
		if (view != null) {
			view.setVisibility(View.VISIBLE);
		}
		mDragAdapter.setHideItem(-1);
		removeDragImage();
	}

	/**
	 * 获取状态栏的高度.
	 * 
	 * @param context
	 * @return
	 */
	private static int getStatusHeight(Context context) {
		int statusHeight = 0;
		Rect localRect = new Rect();
		((Activity) context).getWindow().getDecorView()
				.getWindowVisibleDisplayFrame(localRect);
		statusHeight = localRect.top;
		if (0 == statusHeight) {
			Class<?> localClass;
			try {
				localClass = Class.forName("com.android.internal.R$dimen");
				Object localObject = localClass.newInstance();
				int i5 = Integer.parseInt(localClass
						.getField("status_bar_height").get(localObject)
						.toString());
				statusHeight = context.getResources().getDimensionPixelSize(i5);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return statusHeight;
	}
	
	@Override
	public void onClick(View v) {
		
		
		if (mDragAdapter == null) {
			return;
		}
		if (!isShowDelele()) {
			return;
		}
		onStartAnimation();
		
		if (mDragPosition == AdapterView.INVALID_POSITION) {
			return;
		}
		
		Log.e("lykDrag", "mDragPosition:" + mDragPosition + ", last:" + getLastVisiblePosition());
		if (mDragPosition != getLastVisiblePosition()) {
			
			setHideSartItemView();
			
			mLastItemView = getChildAt(getLastVisiblePosition());
			
			final int[] location = new int[2];
			mLastItemView.getLocationOnScreen(location);
			
			onDragItem(location[0], location[1]);
			onStopDrag();
			
						
		} else {
			onStopDrag();
			
		}
		mDragAdapter.removeItem(getLastVisiblePosition());
		
		
		
		
	}
	
	/**
	 * NeedShake
	 * @return
	 */
	public boolean isNeedShake() {
		return mNeedShake;
	}

	/**
	 * @param mNeedShake
	 */
	public void setNeedShake(boolean mNeedShake) {
		this.mNeedShake = mNeedShake;
	}
	
	
	/**
	 *  ShakeAnimation isRunning
	 * @return
	 */
	private boolean isShowShake() {
		
		return mNeedShake && mStartShake;
		
	}

	/**
	 * start shakeAnimation
	 * @param v
	 */
	private void shakeAnimation(final View v) {
		float rotate = 0;
		int c = mCount++ % 15;
		if (c == 0) {
			rotate = DEGREE_0;
		} else if (c == 1) {
			rotate = DEGREE_1;
		} else if (c == 2) {
			rotate = DEGREE_2;
		} else if (c == 3) {
			rotate = DEGREE_3;
		} else {
			rotate = DEGREE_4;
		}
		final RotateAnimation mra = new RotateAnimation(rotate, -rotate,
				ICON_WIDTH * mDensity / 4, ICON_HEIGHT * mDensity / 4);
		final RotateAnimation mrb = new RotateAnimation(-rotate, rotate,
				ICON_WIDTH * mDensity / 4, ICON_HEIGHT * mDensity / 4);

		mra.setDuration(ANIMATION_DURATION);
		mrb.setDuration(ANIMATION_DURATION);

		mra.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				if (mNeedShake && mStartShake) {
					mra.reset();
					v.startAnimation(mrb);
				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationStart(Animation animation) {

			}

		});

		mrb.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				if (mNeedShake && mStartShake) {
					mrb.reset();
					v.startAnimation(mra);
				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationStart(Animation animation) {

			}

		});
		v.startAnimation(mra);
	}

	public Animation shakeAnimation(int CycleTimes) {
		Animation translateAnimation = new TranslateAnimation(0, 10, 0, 10);
		translateAnimation.setInterpolator(new CycleInterpolator(CycleTimes));
		translateAnimation.setDuration(1000);
		return translateAnimation;
	}
	
	
	/**
	 * showDeleltButton
	 *//*
	private void showDeleltButton(){
		
		for (int i = 0; i < getChildCount(); i++) {
			final View mGridItemView = getChildAt(i);
				mDeleteButton = (ImageButton) mGridItemView.findViewById(R.id.grid_item_delte);
				
				mDeleteButton.setOnClickListener(DragGridView.this);
				
			    mDeleteButton.setVisibility(View.VISIBLE);
				
			}
	}*/
	
	/**
	 * HideDeleltButton
	 */
	private void setHideDeleltButton(){
		for (int i = 0; i < getChildCount(); i++) {
			final View mGridItemView = getChildAt(i);
			
				mDeleteButton = (ImageButton) mGridItemView.findViewById(R.id.grid_item_delte);
				
				mDeleteButton.setVisibility(View.GONE);
			}
	}
	
	/**
	 * HideDeleltButton
	 */
	private void setHideSartItemView(){
		if (mDragAdapter == null ) {
			return;
		}
		
		mStartDragItemView.setVisibility(View.INVISIBLE);
	}
	
	
	
	@Override    
    public boolean onKeyDown(int keyCode, KeyEvent event) {    
      if (keyCode == KeyEvent.KEYCODE_BACK) {    
              pressAgainExit();    
              return true;    
         }    
    
        return super.onKeyDown(keyCode, event);    
    } 
	
	
	 /**
	 * pressAgainExit
	 */
	private void pressAgainExit() {    
		  
	          if (mEMrg.isExit() || mDeleteButton ==null   ) {    
	                System.exit(0);
	          } else { 
	        	  setHideDeleltButton();
	        	  
	        	  if (mStartShake && mNeedShake) {
	        		  
	        		  onStopAnimation();
	        	  }
	        	 
	             
	              mEMrg.doExitInOneSecond();    
	          }
	          
	          
	      
	} 
	
	public class ExitMrg {
		private boolean isExit = false;  
		private Runnable task = new Runnable() {  
		    @Override  
		    public void run() {  
		        isExit = false;  
		    }  
		};  
		  
		public void doExitInOneSecond() {  
			
			
		    isExit = true;  
		    HandlerThread thread = new HandlerThread("doTask");  
		    thread.start();  
		    new Handler(thread.getLooper()).postDelayed(task, 1000);  
		}  
		  
		public boolean isExit() {  
		    return isExit;  
		}  
		  
		public void setExit(boolean isExit) {  
		    this.isExit = isExit;  
		}  
	}
	
	
	

}
