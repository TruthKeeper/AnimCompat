package com.tk.lanimhelper;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Arrays;

/**
 * <pre>
 *     author : TK
 *     time   : 2017/03/28
 *     desc   : Android L 涟漪、揭示动画、共享元素兼容工具
 * </pre>
 */
public class LAnimHelper {
    public static final String REVEAL_LOCATION = "reveal_location";
    public static final int SHARE_DURING = 500;
    public static final int REVEAL_DURING = 500;
    //涟漪

    //揭示动画

    //共享元素

    /**
     * 共享元素动画，并跳转页面
     *
     * @param activity
     * @param intent
     * @param imageView
     */
    public static void requestShareAnim(@NonNull Activity activity, @NonNull Intent intent, @NonNull ImageView imageView) {
        //存储位置信息
        intent.putExtra(REVEAL_LOCATION, getLocation(imageView));
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }

    public static void responseShareAnim(final Activity activity, final ImageView target,
                                         final OnImageLoadListener listener) {
        responseShareAndRevealAnim(activity, target, listener, SHARE_DURING, 0);
    }

    public static void responseShareAnim(final Activity activity, final ImageView target,
                                         final OnImageLoadListener listener, int shareDuring) {
        responseShareAndRevealAnim(activity, target, listener, shareDuring, 0);
    }

    public static void responseShareAndRevealAnim(final Activity activity, final ImageView target,
                                                  final OnImageLoadListener listener) {
        responseShareAndRevealAnim(activity, target, listener, SHARE_DURING, REVEAL_DURING);
    }

    public static void responseShareAndRevealAnim(final Activity activity, final ImageView target,
                                                  final OnImageLoadListener listener, final int shareDuring, final int during) {

        //获取位置信息
        final int[] oldLocation = activity.getIntent().getIntArrayExtra(REVEAL_LOCATION);
        final ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
        target.setVisibility(View.INVISIBLE);
        if (during > 0) {
            //需要揭示动画
            if (!(contentView.getChildAt(0) instanceof RevealContainer)) {
                //偷梁换柱
                RevealContainer revealContainer = new RevealContainer(activity);
                for (int i = 0, length = contentView.getChildCount(); i < length; i++) {
                    View child = contentView.getChildAt(i);
                    contentView.removeView(child);
                    revealContainer.addView(child);
                }
                contentView.addView(revealContainer);
            }
        }
        target.post(new Runnable() {
            @Override
            public void run() {
                final int[] newLocation = getLocation(target);
                //添加一个假的装饰
                final ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
                final ImageView fakeView = new ImageView(activity);
                final ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(oldLocation[2] - oldLocation[0],
                        oldLocation[3] - oldLocation[1]);
                decorView.addView(fakeView, layoutParams);
                fakeView.setTranslationX(oldLocation[0]);
                fakeView.setTranslationY(oldLocation[1]);
                //通知Activity加载图片
                listener.onLoad(fakeView);
                listener.onLoad(target);
                final float scaleX = (newLocation[2] - newLocation[0]) * 1F / (oldLocation[2] - oldLocation[0]);
                final float scaleY = (newLocation[3] - newLocation[1]) * 1F / (oldLocation[3] - oldLocation[1]);
                final int targetCentX = newLocation[2] + newLocation[0] >> 1;
                final int targetCentY = newLocation[3] + newLocation[1] >> 1;
                final int fakeCentX = oldLocation[2] + oldLocation[0] >> 1;
                final int fakeCentY = oldLocation[3] + oldLocation[1] >> 1;
                //位移+缩放动画
                ViewCompat.animate(fakeView)
                        .setDuration(shareDuring)
                        .translationXBy(targetCentX - fakeCentX)
                        .translationYBy(targetCentY - fakeCentY)
                        .scaleX(scaleX)
                        .scaleY(scaleY)
                        .withLayer()
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {

                                if (during > 0) {
                                    //需要揭示动画，自动配置
                                    int realY =targetCentY-getActionBarHeight(activity);
                                    float maxR = (float) maxDistance(targetCentX, realY);
                                    RevealOptions options = new RevealOptions.Builder()
                                            .centerX(targetCentX)
                                            .centerY(realY)
                                            .during(during)
                                            .startRadius(0)
                                            .endRadius(maxR)
                                            .build();
                                    RevealContainer revealContainer = (RevealContainer) contentView.getChildAt(0);
                                    revealContainer.init(options);
                                    revealContainer.startAnim(new Animator.AnimatorListener() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {
                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            decorView.removeView(fakeView);
                                            target.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onAnimationCancel(Animator animation) {
                                            decorView.removeView(fakeView);
                                            target.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onAnimationRepeat(Animator animation) {
                                        }
                                    });
                                }
                            }
                        }).start();
            }
        });
    }


    /**
     * 获取屏幕上一点离四个顶点的最大距离
     *
     * @param x
     * @param y
     * @return
     */
    private static double maxDistance(int x, int y) {
        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        int windowW = dm.widthPixels;
        int windowH = dm.heightPixels;
        return Math.max(Math.max(Math.hypot(x, y), Math.hypot(windowW - x, y)),
                Math.max(Math.hypot(x, windowH - y), Math.hypot(windowW - x, windowH - y)));
    }

    /**
     * 获取ActionBar高度
     *
     * @param activity
     * @return
     */
    private static int getActionBarHeight(@NonNull Activity activity) {
        if (activity instanceof AppCompatActivity) {
            ActionBar bar = ((AppCompatActivity) activity).getSupportActionBar();
            return bar == null ? 0 : bar.getHeight();
        }
        android.app.ActionBar bar = activity.getActionBar();
        return bar == null ? 0 : bar.getHeight();
    }

    /**
     * 获取详细位置
     *
     * @param view
     * @return
     */
    private static int[] getLocation(@NonNull View view) {
        int[] location = new int[2];
        view.getLocationInWindow(location);
        location = Arrays.copyOf(location, 4);
        location[2] = location[0] + view.getMeasuredWidth();
        location[3] = location[1] + view.getMeasuredHeight();
        return location;
    }

}
