package com.tk.sample.share;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tk.sample.R;

/**
 * <pre>
 *     author : TK
 *     time   : 2017/03/28
 *     desc   : adapter
 * </pre>
 */
public class SampleAdapter extends RecyclerView.Adapter<SampleAdapter.Holder> {
    private OnClickListener onClickListener;

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sample, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        Glide.with(holder.itemView.getContext())
                .load(holder.itemView.getResources().getString(R.string.github_avatar))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.image);
        holder.content.setText(position % 2 == 0 ? "共享元素兼容+揭示动画" : "共享元素兼容动画");
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    class Holder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView content;


        public Holder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            content = (TextView) itemView.findViewById(R.id.content);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickListener != null) {
                        onClickListener.onClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    interface OnClickListener {
        void onClick(int position);
    }
}
