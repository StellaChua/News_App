package com.example.news_application;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.activity.EdgeToEdge;
import androidx.annotation.ColorRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class ManageCategoriesActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private List<String> categories;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_categories);

        categories = getAllCategories();

        TableLayout tableLayout = findViewById(R.id.table_layout);
        View view;
        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                view = row.getChildAt(j);
                if (view instanceof Button) {
                    Button button = (Button) view;
                    button.setTag(R.color.blue_500, true);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toggleButtonColor(button);
                        }
                    });
                }
            }
        }


        findViewById(R.id.toolbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private List<String> getAllCategories(){
        List<String> allCategories = new ArrayList<>();
        allCategories.add("综合");
        allCategories.add("娱乐");
        allCategories.add("军事");
        allCategories.add("教育");
        allCategories.add("文化");
        allCategories.add("健康");
        allCategories.add("财经");
        allCategories.add("体育");
        allCategories.add("汽车");
        allCategories.add("科技");
        allCategories.add("社会");

        return allCategories;
    }


    private void toggleButtonColor(Button button) {
        // Load the pop animation
        Animation popAnimation = AnimationUtils.loadAnimation(this, R.anim.pop_animation);
        button.startAnimation(popAnimation);

        boolean isBlue = (boolean) button.getTag(R.color.blue_500);
        @ColorRes int newColor = isBlue ? R.color.grey : R.color.blue_500;
        button.setTag(R.color.blue_500, !isBlue);

        ColorDrawable buttonColor = new ColorDrawable(ContextCompat.getColor(this, newColor));
        button.setBackground(buttonColor);

        String category = button.getText().toString();
        if (isBlue) {
            // Remove category from selectedCategories
            categories.remove(category);
        } else {
            // Add category to selectedCategories
            categories.add(category);
        }

        updateCategoriesInFragment(categories);
    }

    private void updateCategoriesInFragment(List<String> categories) {
        TabNewsFragment fragment = (TabNewsFragment) getSupportFragmentManager().findFragmentById(R.id.tab_layout);
        if (fragment != null && fragment instanceof onCategoriesUpdatedListener) {
            ((onCategoriesUpdatedListener) fragment).onCategoriesUpdated(categories);
        }
    }
}