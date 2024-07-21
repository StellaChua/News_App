package com.example.news_application;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.news_application.databinding.ActivityMainBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_MANAGE_CATEGORIES = 1;
    private List<NewsCategories> titles = new ArrayList<>();
    private TabLayout tab_layout;
    private ViewPager2 viewPager;
    private String searchQuery = "";
    private EditText searchBar;
    private String currentSearchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titles.add(new NewsCategories("综合"));
        titles.add(new NewsCategories("娱乐"));
        titles.add(new NewsCategories("军事"));
        titles.add(new NewsCategories("教育"));
        titles.add(new NewsCategories("文化"));
        titles.add(new NewsCategories("健康"));
        titles.add(new NewsCategories("财经"));
        titles.add(new NewsCategories("体育"));
        titles.add(new NewsCategories("汽车"));
        titles.add(new NewsCategories("科技"));
        titles.add(new NewsCategories("社会"));

        tab_layout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.viewPager);
        searchBar = findViewById(R.id.search_bar);

        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                String title = titles.get(position).getTitle();
                List<String> categories;
                if ("综合".equals(title)) {
                    categories = new ArrayList<>();
                    for (NewsCategories cat : titles) {
                        categories.add(cat.getTitle());
                    }
                } else {
                    categories = new ArrayList<>();
                    categories.add(title);
                }
                return TabNewsFragment.newInstance(title, categories, currentSearchQuery);
            }

            @Override
            public int getItemCount() {
                return titles.size();
            }
        });

        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition(), false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tab_layout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(titles.get(position).getTitle());
            }
        });
        tabLayoutMediator.attach();

        viewPager.setCurrentItem(0, false);

        // Handle search button click
        findViewById(R.id.search_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });
    }

    private void setupViewPager(ViewPager2 viewPager) {
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                String title = titles.get(position).getTitle();
                List<String> categories = new ArrayList<>();
                categories.add(title);
                return TabNewsFragment.newInstance(title, categories, searchQuery);
            }

            @Override
            public int getItemCount() {
                return titles.size();
            }
        });
    }

    private void performSearch() {
        currentSearchQuery = searchBar.getText().toString().trim();
        if (!currentSearchQuery.isEmpty()) {
            // Refresh the ViewPager to apply the new search query
            viewPager.setAdapter(new FragmentStateAdapter(this) {
                @NonNull
                @Override
                public Fragment createFragment(int position) {
                    NewsCategories category = titles.get(position);
                    String title = category.getTitle();

                    List<String> allTitles = new ArrayList<>();
                    for (NewsCategories cat : titles) {
                        allTitles.add(cat.getTitle());
                    }
                    return TabNewsFragment.newInstance(title, allTitles, currentSearchQuery);
                }

                @Override
                public int getItemCount() {
                    return titles.size();
                }
            });
        }
        viewPager.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (!searchBar.getText().toString().trim().isEmpty()) {
            searchBar.setText("");
            performSearch();
        }
        super.onBackPressed();
    }
}
