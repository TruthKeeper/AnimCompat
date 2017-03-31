package com.tk.lanim.share;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tk.lanim.R;
import com.tk.lanimhelper.LAnimHelper;
import com.tk.lanimhelper.OnImageLoadListener;

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
            LAnimHelper.responseShareAndRevealAnim(this, avatar, onImageLoadListener);
        } else {
            LAnimHelper.responseShareAnim(this, avatar, onImageLoadListener);
        }
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }
}
