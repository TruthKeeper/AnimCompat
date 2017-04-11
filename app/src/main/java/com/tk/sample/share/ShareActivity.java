package com.tk.sample.share;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tk.animcompat.AnimCompat;
import com.tk.animcompat.OnImageLoadListener;
import com.tk.sample.R;

public class ShareActivity extends AppCompatActivity {
    private ImageView avatar;
    private OnImageLoadListener onImageLoadListener = new OnImageLoadListener() {
        @Override
        public void onLoad(ImageView imageView) {
            String url = getString(R.string.github_avatar);
            Glide.with(ShareActivity.this)
                    .load(url)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        avatar = (ImageView) findViewById(R.id.avatar);

        if (getIntent().getBooleanExtra("mode", false)) {
            //共享元素兼容+揭示动画
            AnimCompat.responseShareAndRevealAnim(this, avatar, onImageLoadListener);
        } else {
            AnimCompat.responseShareAnim(this, avatar, onImageLoadListener);
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (AnimCompat.animInterrupt(this)) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getBooleanExtra("mode", false)) {
            //共享元素兼容+揭示动画
            AnimCompat.reverseShareAndRevealAnim(this, avatar);
        } else {
            AnimCompat.reverseShareAnim(this, avatar);
        }
    }
}
