package com.example.news_application.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.news_application.entity.NewsInfo;
import com.example.news_application.R;

import java.util.ArrayList;
import java.util.List;

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.MyHolder> {

    private List<NewsInfo.DataDTO> mDataDTOList = new ArrayList<>();
    private Context mContext;

    public void setListData(List<NewsInfo.DataDTO> listData){
        this.mDataDTOList = listData;
        notifyDataSetChanged();
    }

    public NewsListAdapter(Context context){
        this.mContext = context;
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_items, null);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, @SuppressLint("RecyclerView") int position) {
        NewsInfo.DataDTO dataDTO = mDataDTOList.get(position);

        holder.publishTime.setText(dataDTO.getPublishTime());
        holder.publisher.setText(dataDTO.getPublisher());
        holder.title.setText(dataDTO.getTitle());

        String imageUrl = dataDTO.getImage();
        if (imageUrl != null && !imageUrl.isEmpty()){
            int maxWidth = dpToPx(mContext, 90);
            int maxHeight = dpToPx(mContext, 90);
            Glide.with(mContext).load(imageUrl).override(maxWidth, maxHeight)
                    .fitCenter().error(R.mipmap.not_available).into(holder.image);
        }
        else {
            holder.image.setImageResource(R.mipmap.not_available);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (null != onItemClickListener){
                    onItemClickListener.onItemClick(dataDTO, position);
                }
            }
        });
    }

    public static int dpToPx(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }


    @Override
    public int getItemCount() {
        return mDataDTOList.size();
    }

    public void clearData() {
        mDataDTOList.clear();
        notifyDataSetChanged();
    }

    public void addData(List<NewsInfo.DataDTO> data) {
        if (data != null && !data.isEmpty()) {
            mDataDTOList.addAll(data);
            notifyDataSetChanged();
        }
    }

    public void removeLoadingFooter() {
        if (mDataDTOList.size() > 0 && mDataDTOList.get(mDataDTOList.size() - 1) == null) {
            mDataDTOList.remove(mDataDTOList.size() - 1);
            notifyItemRemoved(mDataDTOList.size());
        }
    }

    static class MyHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView publishTime;
        TextView publisher;
        TextView title;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            publishTime = itemView.findViewById(R.id.publishTime);
            publisher = itemView.findViewById(R.id.publisher);
            title = itemView.findViewById(R.id.title);
        }
    }

    private onItemClickListener onItemClickListener;

    public void setOnItemClickListener(NewsListAdapter.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface onItemClickListener{
        void onItemClick(NewsInfo.DataDTO dataDTO, int position);
    }
}
