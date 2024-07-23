package com.example.news_application;

import static com.example.news_application.adapter.NewsListAdapter.dpToPx;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.news_application.database.HistoryDbHelper;
import com.example.news_application.entity.HistoryInfo;
import com.example.news_application.entity.NewsInfo;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
    private TextView summarizedContentTextView;

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
        summarizedContentTextView = findViewById(R.id.summarizedContentTextView);
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
            Log.d(TAG, "NewsID: " + dataDTO.getNewsID());
            newsDate.setText(dataDTO.getPublishTime());
            newsSource.setText(dataDTO.getPublisher());

            String dataDTOJson = new Gson().toJson(dataDTO);
            HistoryDbHelper.getInstance(NewsDetailsActivity.this).addHistory(null, dataDTO.getNewsID(), dataDTOJson);

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

        fetchAISummary();
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

    private void saveSummarizedContent(String newsId, String summarizedContent) {
        SharedPreferences sharedPreferences = getSharedPreferences("SummarizedContent", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(newsId, summarizedContent);
        editor.apply();
    }

    private String getSummarizedContent(String newsId) {
        SharedPreferences sharedPreferences = getSharedPreferences("SummarizedContent", MODE_PRIVATE);
        return sharedPreferences.getString(newsId, null);
    }

    private void fetchAISummary() {
        String newsContent = dataDTO.getContent();
        String newsId = dataDTO.getNewsID();

        String savedContent = getSummarizedContent(newsId);
        if (savedContent != null) {
            summarizedContentTextView.setText(savedContent);
            return;
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("model", "glm-4");
            jsonObject.put("messages", new JSONArray()
                    .put(new JSONObject().put("role", "user").put("content", "请用中文总结以下新闻: " + newsContent))
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(
                jsonObject.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url("https://open.bigmodel.cn/api/paas/v4/chat/completions")
                .header("Content-Type", "application/json")
                .header("Authorization", "d05445b16906e2d65ec5f98d1f9b7987.30Czddx7RH49u8vl")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    try {
                        // Parse the JSON response
                        JSONObject jsonResponse = new JSONObject(responseData);
                        String content = jsonResponse.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");

                        saveSummarizedContent(newsId, content);

                        runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            summarizedContentTextView.setText(content);
                        }
                    });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                summarizedContentTextView.setText("Error parsing response");
                            }
                        });
                    }
                } else {
                    Log.d("------------", "Request failed: " + response.message());
                    return;
                }
            }
        });
    }
}