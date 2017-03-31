package com.tk.lanimhelper;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;


/**
 * <pre>
 *     author : TK
 *     time   : 2017/03/28
 *     desc   : 偷梁换柱的揭示容器
 * </pre>
 */
public class RevealContainer extends FrameLayout {
    private ValueAnimator animator;
    private Path mPath = new Path();
    private RevealOptions options;
    private float mRevealRadius;
    private Animator.AnimatorListener listener;

    public RevealContainer(Context context) {
        this(context, null);
    }

    public RevealContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RevealContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(RevealOptions options) {
        this.options = options;
        animator = ValueAnimator.ofFloat(options.getStartRadius(), options.getEndRadius());
        animator.setDuration(options.getDuring());
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRevealRadius = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                setLayerType(LAYER_TYPE_HARDWARE, null);
                if (listener != null) {
                    listener.onAnimationStart(animation);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setLayerType(LAYER_TYPE_NONE, null);
                if (listener != null) {
                    listener.onAnimationEnd(animation);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                setLayerType(LAYER_TYPE_NONE, null);
                if (listener != null) {
                    listener.onAnimationCancel(animation);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                if (listener != null) {
                    listener.onAnimationRepeat(animation);
                }
            }
        });
    }

    public void startAnim() {
        startAnim(null);
    }

    public void startAnim(Animator.AnimatorListener listener) {
        if (animator != null && !animator.isRunning()) {
            animator.start();
        }
        this.listener = listener;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null) {
            animator.cancel();
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        //未初始化时不绘制children
        if (animator == null) {
            return false;
        }
        if (animator.isRunning()) {
            mPath.reset();
            mPath.addCircle(options.getCenterX(), options.getCenterY(), mRevealRadius, Path.Direction.CW);
            canvas.clipPath(mPath);
        }
        return super.drawChild(canvas, child, drawingTime);
    }

}
