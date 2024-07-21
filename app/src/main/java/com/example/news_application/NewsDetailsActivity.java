package com.example.news_application;

import static com.example.news_application.adapter.NewsListAdapter.dpToPx;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import okhttp3.OkHttpClient;

public class NewsDetailsActivity extends AppCompatActivity {
    private static final String TAG = "NewsDetailsActivity";
    private NewsInfo.DataDTO dataDTO;
    private Toolbar toolbar;
    private TextView titleTextView;
    private TextView contentTextView;
    private TextView newsDate;
    private TextView newsSource;
    private ImageView newsImageView;
    private VideoView videoView;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);

        toolbar = findViewById(R.id.toolbar);
        titleTextView = findViewById(R.id.titleTextView);
        contentTextView = findViewById(R.id.contentTextView);
        newsDate = findViewById(R.id.newsDate);
        newsSource = findViewById(R.id.newsSource);
        newsImageView = findViewById(R.id.newsImageView);
        videoView = findViewById(R.id.videoView);
        client = new OkHttpClient();

        dataDTO = getIntent().getParcelableExtra("dataDTO");

        if (dataDTO != null) {
            toolbar.setTitle(dataDTO.getTitle());
            titleTextView.setText(dataDTO.getTitle());
            Log.d(TAG, "Title: " + dataDTO.getTitle());
            Log.d(TAG, "Date: " + dataDTO.getPublishTime());
            Log.d(TAG, "Source: " + dataDTO.getPublisher());
            Log.d(TAG, "Image: " + dataDTO.getImage());
            Log.d(TAG, "Video: " + dataDTO.getVideo());
            Log.d(TAG, "Content: " + dataDTO.getContent());
            newsDate.setText(dataDTO.getPublishTime());
            newsSource.setText(dataDTO.getPublisher());

            String content = dataDTO.getContent();
            if (content != null && !content.isEmpty()) {
                contentTextView.setText(content);
            } else {
                contentTextView.setText("Content not available");
            }
            loadNewsImage(dataDTO.getImage());
            loadNewsVideo(dataDTO.getVideo());
        }

        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadNewsImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            int maxHeight = dpToPx(this, 200);
            RequestOptions options = new RequestOptions()
                    .override(Target.SIZE_ORIGINAL, maxHeight)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            Glide.with(this).load(imageUrl).apply(options).into(newsImageView);
        } else {
            Glide.with(this).load(R.mipmap.not_available).into(newsImageView);
        }
    }

    private void loadNewsVideo(String videoUrl) {
        if (videoUrl != null && !videoUrl.isEmpty()) {
            videoView.setVideoURI(Uri.parse(videoUrl));
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);
            videoView.setOnPreparedListener(mp -> videoView.start());
            videoView.setVisibility(View.VISIBLE);
            newsImageView.setVisibility(View.GONE);
        } else {
            videoView.setVisibility(View.GONE);
            newsImageView.setVisibility(View.VISIBLE);
        }
    }
}