package com.tk.lanim;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.tk.lanim.reveal.RevealActivity;
import com.tk.lanim.ripple.RippleActivity;
import com.tk.lanim.share.ShareListActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnRipple;
    private Button btnReveal;
    private Button btnShare;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRipple = (Button) findViewById(R.id.btn_ripple);
        btnReveal = (Button) findViewById(R.id.btn_reveal);
        btnShare = (Button) findViewById(R.id.btn_share);
        btnRipple.setOnClickListener(this);
        btnReveal.setOnClickListener(this);
        btnShare.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ripple:
                startActivity(new Intent(this, RippleActivity.class));
                break;
            case R.id.btn_reveal:
                startActivity(new Intent(this, RevealActivity.class));
                break;
            case R.id.btn_share:
                startActivity(new Intent(this, ShareListActivity.class));
                break;
        }
    }
}
