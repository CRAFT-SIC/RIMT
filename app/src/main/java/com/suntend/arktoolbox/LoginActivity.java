package com.suntend.arktoolbox;

import static com.suntend.arktoolbox.RIMTUtil.FlarumUserUtil.getUser;
import static com.suntend.arktoolbox.RIMTUtil.FlarumUserUtil.setUser;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowInsets;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.suntend.arktoolbox.RIMTUtil.FileUtil;
import com.suntend.arktoolbox.RIMTUtil.FlarumUserUtil;
import com.suntend.arktoolbox.RIMTUtil.RIMTUtil;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private Handler handler = null;
    private String responseE = "";
    long[] mHits = new long[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LinearLayout Login_Board = (LinearLayout) findViewById(R.id.login_Board);
        Button Login_LoginBtn = (Button) findViewById(R.id.login_LoginBtn);
        EditText Login_Email = (EditText) findViewById(R.id.login_Email);
        EditText Login_Password = (EditText) findViewById(R.id.login_Password);
        TextView Login_Title = (TextView) findViewById(R.id.login_Title);
        getWindow().setStatusBarColor(Login_Board.getDrawingCacheBackgroundColor());
        handler = new Handler();
        Button Login_LoginBtn_E1 = (Button) findViewById(R.id.login_LoginBtn_E1);
        Login_LoginBtn_E1.setVisibility(View.VISIBLE);
        Login_LoginBtn_E1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login_Email.setText("test01@qq.com");
                Login_Password.setText("12345678");
                Login_LoginBtn.callOnClick();
            }
        });

        Login_Password.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER) {
                    Login_LoginBtn.callOnClick();
                }
                return false;
            }
        });

        Login_Title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();// 开机后运行时间
                if (mHits[0] == (mHits[mHits.length - 1] - 500)) {
                    RIMTUtil.ShowToast(getApplicationContext(),"已启用开发者模式");
                }
            }
        });

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(100);//休眠100毫秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (CheckLoginInfo().equals("Err_0")) {
                    handler.post(LoginStatus_Err_0);
                } else if (CheckLoginInfo().equals("0")) {
                    handler.post(LoginStatus_Err_1);
                } else {
                    handler.post(RequestLogin);
                }
            }
        }.start();

        Login_LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = Login_Email.getText().toString();
                String password = Login_Password.getText().toString();
                if (email.isEmpty()) {
                    handler.post(Empty_Email);
                } else if (password.isEmpty()) {
                    handler.post(Empty_Password);
                } else {
                    ProgressBar loginProgress = findViewById(R.id.login_Progress);
                    LinearLayout loginBoard = findViewById(R.id.login_Board);
                    loginProgress.setVisibility(View.VISIBLE);
                    loginBoard.setVisibility(View.GONE);
                    handler.post(Hide_Keyboard);
                    handler.post(Show_Status);
                    new Thread(() -> {
                        try {
                            Thread.sleep(10);//休眠10毫秒
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        handler.post(LoginStatus_Getting_Token);
                        String responseE = Login(email, password);
                        if (responseE.equals("Err_0")) {
                            handler.post(LoginMessage_Err_0);
                            handler.post(Hide_Status);
                        } else if (responseE.equals("Err_1")) {
                            handler.post(LoginMessage_Err_1);
                            handler.post(Hide_Status);
                        } else {
                            Gson gson = new Gson();
                            Login_Token login_token = gson.fromJson(responseE, Login_Token.class);
                            setUser(getApplicationContext(), "isLogined", "1");
                            setUser(getApplicationContext(), "password", password);
                            setUser(getApplicationContext(), "token", login_token.token);
                            setUser(getApplicationContext(), "uid", String.valueOf(login_token.userId));
                            responseE = "Err_2";
                            handler.post(LoginStatus_Getting_Userinfo);
                            responseE = User_Profile(getUser(getApplicationContext(),"uid"));
                            if (responseE.equals("Err_0")) {
                                handler.post(LoginMessage_Err_0);
                                handler.post(Hide_Status);
                            } else if (responseE.equals("Err_1")) {
                                handler.post(LoginMessage_Err_1);
                                handler.post(Hide_Status);
                            } else {
                                User_Profile_Data user_profile_data = gson.fromJson(responseE, User_Profile_Data.class);
                                setUser(getApplicationContext(), "type", user_profile_data.data.type);
                                setUser(getApplicationContext(), "email", user_profile_data.data.attributes.email);
                                setUser(getApplicationContext(), "username", user_profile_data.data.attributes.username);
                                setUser(getApplicationContext(), "displayname", user_profile_data.data.attributes.displayName);
                                setUser(getApplicationContext(), "avatarurl", user_profile_data.data.attributes.avatarUrl != null ? user_profile_data.data.attributes.avatarUrl : "https://img1.baidu.com/it/u=3834820558,1776972742&fm=253&fmt=auto&app=138&f=JPEG?w=400&h=400");
                                setUser(getApplicationContext(), "jointime", user_profile_data.data.attributes.joinTime);
                                handler.post(GetAvatar);
                                finish();
                            }
                        }
                    }).start();
                }
            }
        });
    }

    //Login_JSON
    public static class Login_Token {
        public String token;
        public int userId;
    }

    public static class User_Profile_Data {
        public data_1 data;

        public static class data_1 {
            public String type;
            public String id;
            public attributes_1 attributes;

            public static class attributes_1 {
                public String username;
                public String displayName;
                public String avatarUrl;
                public String joinTime;
                public String email;
            }
        }

        public List included;
    }

    public static class User_Profile_Data_Included {
        public String type;
        public String id;
        public attributes_1 attributes;

        public static class attributes_1 {
            public String nameSingular;
            public String namePlural;
            public String color;
        }
    }

    Runnable Empty_Email = () -> {
        EditText Login_Email = (EditText) findViewById(R.id.login_Email);
        Login_Email.requestFocus();
        Login_Email.setError("邮箱不能为空");
    };

    Runnable Empty_Password = () -> {
        EditText Login_Password = (EditText) findViewById(R.id.login_Password);
        Login_Password.requestFocus();
        Login_Password.setError("密码不能为空");
    };

    Runnable Show_Keyboard = () -> {
        EditText Login_Password = (EditText) findViewById(R.id.login_Password);
        Login_Password.requestFocus();
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(Login_Password, 0);
    };

    Runnable Hide_Keyboard = () -> {
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    };

    Runnable Show_Status = () -> {
        TextView Login_Status = (TextView) findViewById(R.id.login_Status);
        Login_Status.setVisibility(View.VISIBLE);
    };

    Runnable Hide_Status = () -> {
        TextView Login_Status = (TextView) findViewById(R.id.login_Status);
        Login_Status.setVisibility(View.GONE);
    };

    //关闭进度条开启登录框
    Runnable RequestLogin = () -> {
        EditText Login_Email = (EditText) findViewById(R.id.login_Email);
        LinearLayout Login_Board = (LinearLayout) findViewById(R.id.login_Board);
        ProgressBar Login_Progress = (ProgressBar) findViewById(R.id.login_Progress);
        Login_Progress.setVisibility(View.GONE);
        Login_Board.setVisibility(View.VISIBLE);
        Login_Email.requestFocus();
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(Login_Email, InputMethodManager.HIDE_NOT_ALWAYS);
    };

    Runnable LoginMessage_Err_0 = () -> {
        EditText Login_Password = (EditText) findViewById(R.id.login_Password);
        Login_Password.setError("连接到JSG-LLC服务器失败");
        Login_Password.callOnClick();
        LinearLayout Login_Board = (LinearLayout) findViewById(R.id.login_Board);
        ProgressBar Login_Progress = (ProgressBar) findViewById(R.id.login_Progress);
        Login_Progress.setVisibility(View.GONE);
        Login_Board.setVisibility(View.VISIBLE);
    };

    Runnable LoginMessage_Err_1 = () -> {
        Button Login_LoginBtn = (Button) findViewById(R.id.login_LoginBtn);
        TextInputLayout Login_PasswordLayout = (TextInputLayout) findViewById(R.id.login_Password_Layout);
        EditText Login_Password = (EditText) findViewById(R.id.login_Password);
        Login_Password.setError("用户名或密码错误");
        LinearLayout Login_Board = (LinearLayout) findViewById(R.id.login_Board);
        ProgressBar Login_Progress = (ProgressBar) findViewById(R.id.login_Progress);
        Login_Progress.setVisibility(View.GONE);
        Login_Board.setVisibility(View.VISIBLE);
        Login_Password.requestFocus();
        handler.post(Show_Keyboard);
    };

    Runnable LoginStatus_Err_0 = () -> {
        TextView Login_Status = (TextView) findViewById(R.id.login_Status);
        ProgressBar Login_Progress = (ProgressBar) findViewById(R.id.login_Progress);
        Login_Status.setVisibility(View.VISIBLE);
        Login_Progress.setVisibility(View.GONE);
        Login_Status.setText("连接到JSG-LLC服务器失败");
    };

    Runnable LoginStatus_Err_1 = () -> {
        TextView Login_Status = (TextView) findViewById(R.id.login_Status);
        ProgressBar Login_Progress = (ProgressBar) findViewById(R.id.login_Progress);
        Login_Status.setVisibility(View.VISIBLE);
        Login_Progress.setVisibility(View.GONE);
        Login_Status.setText("服务器目前禁止登录,请稍后再试");
    };

    Runnable LoginStatus_Getting_Token = () -> {
        TextView Login_Status = (TextView) findViewById(R.id.login_Status);
        Login_Status.setText("正在获取Token令牌");
    };

    Runnable LoginStatus_Getting_Userinfo = () -> {
        TextView Login_Status = (TextView) findViewById(R.id.login_Status);
        Login_Status.setText("正在获取用户信息");
    };

    Runnable GetAvatar = () -> {
        TextView Login_Status = (TextView) findViewById(R.id.login_Status);
        Login_Status.setText("正在获取头像");
        ImageView login_Logined = (ImageView) findViewById(R.id.login_logined);
        Glide.with(getApplicationContext()).asBitmap().addListener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                try {
                    File file = new File(FileUtil.getPackageDataDir(getApplicationContext()) + "/userdata/avatar.png");
                    FileOutputStream out = new FileOutputStream(file);
                    resource.compress(Bitmap.CompressFormat.PNG, 50, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        }).load(getUser(getApplicationContext(),"avatarurl")).into(login_Logined);

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(500);//休眠10毫秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.run();
    };

    private String sendRequest(Request request) {
        try {
            Response response = new OkHttpClient().newCall(request).execute();
            String responseBody = response.body().string();
            if (response.isSuccessful()) {
                return responseBody;
            } else {
                return "Err_" + response.code();
            }
        } catch (IOException e) {
            return "Err_0";
        }
    }

    private String CheckLoginInfo() {
        Request request = new Request.Builder()
                .url("https://api.craftsic.cn/login/CheckLoginInfo")
                .get()
                .build();
        return sendRequest(request);
    }

    private String Login(String email, String password) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("identification", email)
                .addFormDataPart("password", password)
                .build();
        Request request = new Request.Builder()
                .url("https://bbs.arktoolbox.jamsg.cn/api/token")
                .post(requestBody)
                .build();
        return sendRequest(request);
    }

    private String User_Profile(String uid) {
        Request request = new Request.Builder()
                .url("https://bbs.arktoolbox.jamsg.cn/api/users/" + uid)
                .get()
                .addHeader("Authorization", "Token " + getUser(getApplicationContext(),"token"))
                .build();
        return sendRequest(request);
    }
}