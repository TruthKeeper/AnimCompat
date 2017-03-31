package com.tk.lanim.share;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.tk.lanim.R;
import com.tk.lanimhelper.LAnimHelper;

public class ShareListActivity extends AppCompatActivity implements SampleAdapter.OnClickListener {
    private RecyclerView recyclerview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_list);
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        SampleAdapter adapter = new SampleAdapter();
        recyclerview.setAdapter(adapter);
        recyclerview.setHasFixedSize(true);
        adapter.setOnClickListener(this);
    }

    @Override
    public void onClick(int position) {
        ImageView view = (ImageView) recyclerview.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.image);
        Intent intent = new Intent(this, ShareActivity.class);
        //额外传递参数来展示揭示动画
        intent.putExtra("mode", position % 2 == 0);
        LAnimHelper.requestShareAnim(this, intent, view);
    }
}
