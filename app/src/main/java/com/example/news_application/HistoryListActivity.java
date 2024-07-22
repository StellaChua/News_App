package com.example.news_application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.news_application.adapter.NewsListAdapter;
import com.example.news_application.database.HistoryDbHelper;
import com.example.news_application.entity.HistoryInfo;
import com.example.news_application.entity.NewsInfo;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class HistoryListActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private NewsListAdapter mNewsListAdapter;
    private List<NewsInfo.DataDTO> mDataDTOList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_list);

        recyclerView = findViewById(R.id.recyclerView);

        mNewsListAdapter = new NewsListAdapter(this);
        recyclerView.setAdapter(mNewsListAdapter);
        List<HistoryInfo> historyInfoList = HistoryDbHelper.getInstance(HistoryListActivity.this).queryHistoryListData(null);

        Gson gson = new Gson();
        for (int i = 0; i < historyInfoList.size(); i++){
            mDataDTOList.add(gson.fromJson(historyInfoList.get(i).getNews_json(), NewsInfo.DataDTO.class));
        }

        mNewsListAdapter.setListData(mDataDTOList);

        mNewsListAdapter.setOnItemClickListener(new NewsListAdapter.onItemClickListener() {
            @Override
            public void onItemClick(NewsInfo.DataDTO dataDTO, int position) {
                Intent intent = new Intent(HistoryListActivity.this, NewsDetailsActivity.class);
                intent.putExtra("dataDTO", dataDTO);
                startActivity(intent);
            }
        });

        findViewById(R.id.toolbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}