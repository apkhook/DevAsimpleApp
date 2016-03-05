package com.qtfreet.devasimpleapp.ui.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qtfreet.devasimpleapp.R;
import com.qtfreet.devasimpleapp.data.bean.ContentItemInfo;

import java.util.List;

/**
 * Created by Bear on 2016/1/29.
 */
public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {

    private Context mContext;
    private List<ContentItemInfo> itemInfos;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public ContentAdapter(Context mContext, List<ContentItemInfo> items) {
        this.mContext = mContext;
        this.itemInfos = items;
    }

    @Override
    public ContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.content_item_layout, parent, false);
        return new ContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ContentViewHolder holder, int position) {
        holder.tvContent.setText(itemInfos.get(position).getContent());
        holder.tvTime.setText(itemInfos.get(position).getTime().substring(0, 10));
        holder.tvWho.setText(itemInfos.get(position).getWho());


        if(mListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(v, holder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return itemInfos.size();
    }

    public static class ContentViewHolder extends RecyclerView.ViewHolder {

        TextView tvContent;
        CardView cvCard;
        TextView tvTime;
        TextView tvWho;

        public ContentViewHolder(View itemView) {
            super(itemView);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            cvCard = (CardView) itemView.findViewById(R.id.card_view);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            tvWho = (TextView) itemView.findViewById(R.id.tv_who);
        }
    }

}
