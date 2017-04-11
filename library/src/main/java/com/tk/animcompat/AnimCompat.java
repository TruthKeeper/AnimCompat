package com.tk.animcompat;

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
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import java.util.Arrays;

/**
 * <pre>
 *     author : TK
 *     time   : 2017/03/28
 *     desc   : Android L 涟漪、揭示动画、共享元素兼容工具
 * </pre>
 */
public class AnimCompat {
    public static final String REVEAL_LOCATION = "reveal_location";
    /**
     * 动画标记
     */
    public static final String ANIM_TAG = "anim_tag";
    public static final int SHARE_DURING = 450;
    public static final int REVEAL_DURING = 450;
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
    public static void requestShareAnim(@NonNull final Activity activity,
                                        @NonNull final Intent intent,
                                        @NonNull final ImageView imageView) {
        //存储位置信息
        intent.putExtra(REVEAL_LOCATION, getLocation(imageView));
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }

    /**
     * 开始上个页面传递过来的，共享元素动画
     *
     * @param activity
     * @param target
     * @param listener
     */
    public static void responseShareAnim(@NonNull final Activity activity, @NonNull final ImageView target,
                                         @NonNull final OnImageLoadListener listener) {
        responseShareAndRevealAnim(activity, target, listener, SHARE_DURING, 0);
    }

    /**
     * 开始上个页面传递过来的，共享元素动画
     *
     * @param activity
     * @param target
     * @param listener
     * @param shareDuring 共享元素动画时长
     */
    public static void responseShareAnim(@NonNull final Activity activity,
                                         @NonNull final ImageView target,
                                         @NonNull final OnImageLoadListener listener,
                                         int shareDuring) {
        responseShareAndRevealAnim(activity, target, listener, shareDuring, 0);
    }

    /**
     * 开始上个页面传递过来的，共享元素动画 + 揭示动画
     *
     * @param activity
     * @param target
     * @param listener
     */
    public static void responseShareAndRevealAnim(@NonNull final Activity activity,
                                                  @NonNull final ImageView target,
                                                  @NonNull final OnImageLoadListener listener) {
        responseShareAndRevealAnim(activity, target, listener, SHARE_DURING, REVEAL_DURING);
    }

    /**
     * 开始上个页面传递过来的共享元素动画 + 揭示动画
     *
     * @param activity
     * @param target
     * @param listener
     * @param shareDuring  共享元素动画时长
     * @param revealDuring 揭示动画时长
     */
    public static void responseShareAndRevealAnim(@NonNull final Activity activity,
                                                  @NonNull final ImageView target,
                                                  @NonNull final OnImageLoadListener listener,
                                                  final int shareDuring,
                                                  final int revealDuring) {

        //获取位置信息
        final int[] oldLocation = activity.getIntent().getIntArrayExtra(REVEAL_LOCATION);
        if (oldLocation == null) {
            throw new IllegalStateException("error response !");
        }
        final ViewGroup contentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
        target.setVisibility(View.INVISIBLE);
        if (revealDuring > 0) {
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
        //UI线程未初始化完毕，需要post
        target.post(new Runnable() {
            @Override
            public void run() {
                final int[] newLocation = getLocation(target);
                //添加一个假的装饰
                final ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
                final ImageView fakeView = new ImageView(activity);
                decorView.setTag(ANIM_TAG);
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
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .translationXBy(targetCentX - fakeCentX)
                        .translationYBy(targetCentY - fakeCentY)
                        .scaleX(scaleX)
                        .scaleY(scaleY)
                        .withLayer()
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                if (revealDuring > 0) {
                                    //需要揭示动画，自动配置
                                    int realY = targetCentY - getActionBarHeight(activity);
                                    float maxR = (float) maxDistance(targetCentX, realY);
                                    RevealOptions options = new RevealOptions.Builder()
                                            .centerX(targetCentX)
                                            .centerY(realY)
                                            .during(revealDuring)
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
                                            decorView.setTag(null);
                                        }

                                        @Override
                                        public void onAnimationCancel(Animator animation) {
                                            decorView.removeView(fakeView);
                                            target.setVisibility(View.VISIBLE);
                                            decorView.setTag(null);
                                        }

                                        @Override
                                        public void onAnimationRepeat(Animator animation) {
                                        }
                                    });
                                } else {
                                    decorView.removeView(fakeView);
                                    decorView.setTag(null);
                                    target.setVisibility(View.VISIBLE);
                                }
                            }
                        }).start();
            }
        });
    }

    public static void reverseShareAnim(@NonNull final Activity activity,
                                        @NonNull final ImageView target) {
        reverseShareAndRevealAnim(activity, target, SHARE_DURING, 0);
    }

    public static void reverseShareAnim(@NonNull final Activity activity,
                                        @NonNull final ImageView target,
                                        final int shareDuring) {
        reverseShareAndRevealAnim(activity, target, shareDuring, 0);
    }

    public static void reverseShareAndRevealAnim(@NonNull final Activity activity,
                                                 @NonNull final ImageView target) {
        reverseShareAndRevealAnim(activity, target, SHARE_DURING, REVEAL_DURING);
    }

    /**
     * 开始反向播放动画
     *
     * @param activity
     * @param target
     * @param shareDuring
     * @param revealDuring
     */
    public static void reverseShareAndRevealAnim(@NonNull final Activity activity,
                                                 @NonNull final ImageView target,
                                                 final int shareDuring,
                                                 final int revealDuring) {
        if (animInterrupt(activity)) {
            //还在response动画中
            return;
        }
        //获取位置信息，上次的位置变成了新的位置
        final int[] newLocation = activity.getIntent().getIntArrayExtra(REVEAL_LOCATION);
        if (newLocation == null) {
            throw new IllegalStateException("error reverse !");
        }
        final ViewGroup contentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
        target.setVisibility(View.INVISIBLE);
        if (revealDuring > 0) {
            //需要揭示动画
            if (!(contentView.getChildAt(0) instanceof RevealContainer)) {
                //开启页面没有揭示动画，关闭页面时附加一层，偷梁换柱
                RevealContainer revealContainer = new RevealContainer(activity);
                for (int i = 0, length = contentView.getChildCount(); i < length; i++) {
                    View child = contentView.getChildAt(i);
                    contentView.removeView(child);
                    revealContainer.addView(child);
                }
                contentView.addView(revealContainer);
            }
        }
        //在主线程就不post了
        final int[] oldLocation = getLocation(target);
        //添加一个假的装饰
        final ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        final ImageView fakeView = new ImageView(activity);
        //直接赋值drawable
        fakeView.setImageDrawable(target.getDrawable());
        decorView.setTag(ANIM_TAG);
        final ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(oldLocation[2] - oldLocation[0],
                oldLocation[3] - oldLocation[1]);
        decorView.addView(fakeView, layoutParams);
        fakeView.setTranslationX(oldLocation[0]);
        fakeView.setTranslationY(oldLocation[1]);
        final float scaleX = (newLocation[2] - newLocation[0]) * 1F / (oldLocation[2] - oldLocation[0]);
        final float scaleY = (newLocation[3] - newLocation[1]) * 1F / (oldLocation[3] - oldLocation[1]);
        final int targetCentX = newLocation[2] + newLocation[0] >> 1;
        final int targetCentY = newLocation[3] + newLocation[1] >> 1;
        final int fakeCentX = oldLocation[2] + oldLocation[0] >> 1;
        final int fakeCentY = oldLocation[3] + oldLocation[1] >> 1;

        if (revealDuring > 0) {
            //需要揭示动画，自动配置，先反向揭示在位移动画
            int realY = fakeCentY - getActionBarHeight(activity);
            float maxR = (float) maxDistance(fakeCentX, realY);
            RevealOptions options = new RevealOptions.Builder()
                    .centerX(fakeCentX)
                    .centerY(realY)
                    .during(revealDuring)
                    .startRadius(0)
                    .endRadius(maxR)
                    .build();
            final RevealContainer revealContainer = (RevealContainer) contentView.getChildAt(0);
            revealContainer.init(options);
            revealContainer.reverse(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    //// TODO: 2017/4/11  会重复绘制，暂时这么处理
                    revealContainer.setVisibility(View.INVISIBLE);
                    //位移+缩放动画
                    ViewCompat.animate(fakeView)
                            .setDuration(shareDuring)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .translationXBy(targetCentX - fakeCentX)
                            .translationYBy(targetCentY - fakeCentY)
                            .scaleX(scaleX)
                            .scaleY(scaleY)
                            .withLayer()
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    activity.finish();
                                    activity.overridePendingTransition(0, 0);
                                }
                            })
                            .start();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    revealContainer.setVisibility(View.INVISIBLE);
                    //位移+缩放动画
                    ViewCompat.animate(fakeView)
                            .setDuration(shareDuring)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .translationXBy(targetCentX - fakeCentX)
                            .translationYBy(targetCentY - fakeCentY)
                            .scaleX(scaleX)
                            .scaleY(scaleY)
                            .withLayer()
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    activity.finish();
                                    activity.overridePendingTransition(0, 0);
                                }
                            })
                            .start();
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        } else {
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
                            activity.finish();
                            activity.overridePendingTransition(0, 0);
                        }
                    })
                    .start();
        }

    }

    /**
     * <pre>
     *     动画中，可以选择拦截onBackPressed以及滑动事件
     *     优化用户体验
     * <pre/>
     *
     * @param activity
     * @return
     */
    public static boolean animInterrupt(@NonNull final Activity activity) {
        Object tag = activity.getWindow().getDecorView().getTag();
        return ANIM_TAG.equals(tag);
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
