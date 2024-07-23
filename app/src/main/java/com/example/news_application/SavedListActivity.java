package com.example.news_application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.news_application.adapter.NewsListAdapter;
import com.example.news_application.database.SavedDbHelper;
import com.example.news_application.entity.NewsInfo;
import com.example.news_application.entity.SavedInfo;
import com.example.news_application.entity.UserInfo;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class SavedListActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private NewsListAdapter mNewsListAdapter;
    private List<NewsInfo.DataDTO> mDataDTOList = new ArrayList<>();
    private SavedDbHelper savedDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_list);

        toolbar = findViewById(R.id.toolbar);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mNewsListAdapter = new NewsListAdapter(this);
        recyclerView.setAdapter(mNewsListAdapter);

        loadSavedNews();

        mNewsListAdapter.setOnItemClickListener(new NewsListAdapter.onItemClickListener() {
            @Override
            public void onItemClick(NewsInfo.DataDTO dataDTO, int position) {
                Intent intent = new Intent(SavedListActivity.this, NewsDetailsActivity.class);
                intent.putExtra("dataDTO", dataDTO);
                startActivity(intent);
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadSavedNews() {
        List<SavedInfo> savedInfoList = SavedDbHelper.getInstance(SavedListActivity.this).querySavedListData(null);

        Gson gson = new Gson();
        for (SavedInfo savedInfo : savedInfoList) {
            NewsInfo.DataDTO dataDTO = gson.fromJson(savedInfo.getNews_json(), NewsInfo.DataDTO.class);
            mDataDTOList.add(dataDTO);

        }

        mNewsListAdapter.setListData(mDataDTOList);
        mNewsListAdapter.notifyDataSetChanged();
    }

    private void handleBookmarkClick(NewsListAdapter.MyHolder holder, NewsInfo.DataDTO dataDTO, int position) {
        UserInfo userInfo = UserInfo.getUserInfo();
        String username = userInfo.getUsername();
        String newsID = dataDTO.getNewsID();
        String newsJson = new Gson().toJson(dataDTO);

        try {
            if (savedDbHelper.isSaved(newsID)) {
                savedDbHelper.deleteSaved(newsID);
                mNewsListAdapter.removeItem(position);
            } else {
                savedDbHelper.addSaved(username, newsID, newsJson, true);
                mNewsListAdapter.updateBookmarkAnimation(holder, true);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error updating bookmark", Toast.LENGTH_SHORT).show();
        }
    }

}
