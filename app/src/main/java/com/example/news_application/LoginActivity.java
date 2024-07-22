package com.example.news_application;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.news_application.database.UserDbHelper;
import com.example.news_application.entity.UserInfo;

public class LoginActivity extends AppCompatActivity {
    private EditText login_username;
    private EditText login_password;
    private CheckBox checkBox;
    private SharedPreferences mSharedPreferences;
    private boolean isLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSharedPreferences = getSharedPreferences("user", MODE_PRIVATE);

        isLogin = mSharedPreferences.getBoolean("is_login", false);
        String username = mSharedPreferences.getString("username", null);
        String password = mSharedPreferences.getString("password", null);

        login_username = findViewById(R.id.login_username);
        login_password = findViewById(R.id.login_password);
        checkBox = findViewById(R.id.checkbox);

        if (isLogin){
            login_username.setText(username);
            login_password.setText(password);
            checkBox.setChecked(true);
        }

        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = login_username.getText().toString();
                String password = login_password.getText().toString();
                if (TextUtils.isEmpty(username) && TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
                }
                else{
                    UserInfo userInfo = UserDbHelper.getInstance(LoginActivity.this).login(username);
                    if (userInfo != null){
                        if (username.equals(userInfo.getUsername()) && password.equals(userInfo.getPassword())){
                            SharedPreferences.Editor edit = mSharedPreferences.edit();
                            edit.putBoolean("is_login", isLogin);
                            edit.putString("username", username);
                            edit.putString("password", password);
                            edit.commit();

                            UserInfo.setUserInfo(userInfo);  //Save username and password
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(LoginActivity.this, "该账号暂未注册", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isLogin = isChecked;
            }
        });
    }
}