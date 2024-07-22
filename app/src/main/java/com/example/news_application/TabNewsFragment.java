package com.example.news_application;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.news_application.adapter.NewsListAdapter;
import com.example.news_application.entity.NewsInfo;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TabNewsFragment extends Fragment {
    private static final String BASE_URL = "https://api2.newsminer.net/svc/news/queryNewsList";
    private View rootView;
    private RecyclerView recyclerView;
    private NewsListAdapter mNewsListAdapter;
    private static final String ARG_PARAM = "title";
    private static final String ARG_CATEGORIES = "categories";
    private static final String ARG_SEARCH_QUERY = "search_query";
    private String title;
    private List<String> categories;
    private String searchQuery;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int page = 1;

    private Handler mHandler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100){
                String data = (String) msg.obj;
                NewsInfo newsInfo = new Gson().fromJson(data, NewsInfo.class);
                if (newsInfo != null){
                    if (null != mNewsListAdapter){
                        mNewsListAdapter.setListData(newsInfo.getData());
                    }
                    swipeRefreshLayout.setRefreshing(false);
                }
                else{
                    Toast.makeText(getActivity(), "Request failed, please try later.", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        }
    };

    public TabNewsFragment() {}

    public static TabNewsFragment newInstance(String param, List<String> categories, String searchQuery) {
        TabNewsFragment fragment = new TabNewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param);
        args.putStringArrayList(ARG_CATEGORIES, new ArrayList<>(categories));
        args.putString(ARG_SEARCH_QUERY, searchQuery);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_PARAM);
            categories = getArguments().getStringArrayList(ARG_CATEGORIES);
            searchQuery = getArguments().getString(ARG_SEARCH_QUERY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_tab_news, container, false);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = rootView.findViewById(R.id.recyclerView);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mNewsListAdapter.clearData();
                resetPagination();
                getHttpData();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int totalItemCount = layoutManager.getItemCount();
                    int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                    boolean isBottom = lastVisibleItem == totalItemCount - 1;

                    if (!swipeRefreshLayout.isRefreshing() && isBottom) {
                        loadMoreData();
                    }
                }
            }
        });
        return rootView;
    }

    private void loadMoreData() {
        page += 1;
        getHttpData();
    }

    private void resetPagination() {
        page = 1;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mNewsListAdapter = new NewsListAdapter(getActivity());
        recyclerView.setAdapter(mNewsListAdapter);

        mNewsListAdapter.setOnItemClickListener(new NewsListAdapter.onItemClickListener() {
            @Override
            public void onItemClick(NewsInfo.DataDTO dataDTO, int position) {
                Intent intent = new Intent(getActivity(), NewsDetailsActivity.class);
                intent.putExtra("dataDTO", dataDTO);
                startActivity(intent);
            }
        });
        getHttpData();
    }

    public void refreshData(String newSearchQuery) {
        searchQuery = newSearchQuery;
        resetPagination();
        getHttpData();
    }

    private void getHttpData() {
        if (categories == null || categories.isEmpty()) {
            Log.d("------------", "Categories are null or empty");
            return;
        }

        StringBuilder categoriesParam = new StringBuilder();
        for (String category : categories) {
            categoriesParam.append(category).append(",");
        }
        if (categoriesParam.length() > 0) {
            categoriesParam.setLength(categoriesParam.length() - 1); // Remove the trailing comma
        }

        // Get the current date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        String startDate = "";

        String wordsParam = searchQuery.isEmpty() ? "" : searchQuery;

        String requestUrl = BASE_URL + "?size=15&startDate=" + startDate + "&endDate=" + currentDate + "&words=" + wordsParam + "&categories=" + categoriesParam + "&page=" + page;
        Log.d("------------", "Request URL: " + requestUrl);

        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url(requestUrl)
                .get()
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("------------", "onFailure: " + e.toString());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("------------", "Response code: " + response.code());
                if (!response.isSuccessful()) {
                    Log.d("------------", "Request failed: " + response.message());
                    return;
                }

                try (ResponseBody responseBody = response.body()) {
                    if (responseBody != null) {
                        String data = responseBody.string();
                        Log.d("------------", "onResponse: " + data);

                        Message message = new Message();
                        message.what = 100;
                        message.obj = data;
                        mHandler.sendMessage(message);

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                NewsInfo newsInfo = new Gson().fromJson(data, NewsInfo.class);
                                if (newsInfo != null && newsInfo.getData() != null) {
                                    for (NewsInfo.DataDTO dataDTO : newsInfo.getData()) {
                                        dataDTO.setImage(dataDTO.getImage());
                                    }
                                    if (null != mNewsListAdapter) {
                                        if (page == 1) {
                                            mNewsListAdapter.setListData(newsInfo.getData());
                                        } else {
                                            mNewsListAdapter.addData(newsInfo.getData());
                                        }
                                    } else{
                                        Log.d("------------", "No more data available.");
                                        mNewsListAdapter.removeLoadingFooter();
                                    }
                                } else {
                                    Log.d("------------", "Response body is null");
                                }
                            }
                        });
                    }
                }
                catch (Exception e){
                    Log.e("------------", "Exception parsing response: " + e.getMessage());
                    page -= 1;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            }
        });
    }
}