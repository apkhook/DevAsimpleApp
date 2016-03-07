package com.qtfreet.devasimpleapp.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qtfreet.devasimpleapp.R;
import com.qtfreet.devasimpleapp.data.bean.ImageInfo;
import com.qtfreet.devasimpleapp.view.RadioImageView;

import java.util.List;

/**
 * Created by Bear on 2016/2/5.
 */
public class GirlsAdapter extends RecyclerView.Adapter<GirlsAdapter.GirlsViewHodler> {

    private Context mContext;
    private List<ImageInfo> imageInfos;

    private OnMeiziClickListener onMeiziClickListener;

    public GirlsAdapter(Context mContext, List<ImageInfo> imageInfos) {
        this.mContext = mContext;
        this.imageInfos = imageInfos;

    }

    @Override
    public GirlsViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.girls_layout, parent, false);
        return new GirlsViewHodler(view);
    }

    @Override
    public void onBindViewHolder(GirlsViewHodler holder, int position) {

        loadImage(holder, imageInfos.get(position));
    }

    private void loadImage(GirlsViewHodler holder, ImageInfo imageInfo) {
        holder.ivGril.setOriginalSize(imageInfo.getWidth(), imageInfo.getHeight());
        Glide.with(mContext)
                .load(imageInfo.getUrl()).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop()
                .into(holder.ivGril);
    }

    @Override
    public int getItemCount() {
        return imageInfos.size();
    }

    class GirlsViewHodler extends RecyclerView.ViewHolder {

        private RadioImageView ivGril;

        public GirlsViewHodler(View itemView) {
            super(itemView);
            ivGril = (RadioImageView) itemView.findViewById(R.id.iv_gril);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMeiziClickListener.onMeiziClick(v, getAdapterPosition());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                   onMeiziClickListener.onMeiziLongClick(v,getAdapterPosition());
                    return true;
                }
            });
        }
    }

    public void setOnMeiziClickListener(OnMeiziClickListener listener) {
        this.onMeiziClickListener = listener;
    }

}
