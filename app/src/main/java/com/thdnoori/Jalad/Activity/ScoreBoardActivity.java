package com.thdnoori.Jalad.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thdnoori.Jalad.Adapter.ScoreAdapter;
import com.thdnoori.Jalad.Model.GoodPrefs;
import com.thdnoori.Jalad.Model.Score;
import com.thdnoori.Jalad.R;
import com.thdnoori.Jalad.databinding.ActivityScoreBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ScoreBoardActivity extends AppCompatActivity {
    ScoreAdapter adapter;
    Boolean registered = false;
    Boolean login = false;
    ActivityScoreBinding binding;
    TextView error;
    MediaPlayer backgroundMusic;
    List<Score> scoreList = new ArrayList<>();
    Dialog dialog;

    @Override
    protected void onPause() {
        super.onPause();
        if (GoodPrefs.getInstance().getBoolean("soundEnable", true)) {
            backgroundMusic.stop();
            GoodPrefs.getInstance().saveInt("musicPosition", backgroundMusic.getCurrentPosition());
        }
    }

    protected void onRestart() {
        super.onRestart();
        if (GoodPrefs.getInstance().getBoolean("soundEnable", true)) {
            if(GoodPrefs.getInstance().getInt("appMusic",0)==0){
                backgroundMusic = MediaPlayer.create(ScoreBoardActivity.this, R.raw.app_music);
            }else {
                backgroundMusic = MediaPlayer.create(ScoreBoardActivity.this, R.raw.app_music1);
            }
            backgroundMusic.seekTo(GoodPrefs.getInstance().getInt("musicPosition", 0));
            backgroundMusic.setLooping(true);
            backgroundMusic.start();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScoreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (GoodPrefs.getInstance().getBoolean("soundEnable", true)) {
            if(GoodPrefs.getInstance().getInt("appMusic",0)==0){
                backgroundMusic = MediaPlayer.create(ScoreBoardActivity.this, R.raw.app_music);
            }else {
                backgroundMusic = MediaPlayer.create(ScoreBoardActivity.this, R.raw.app_music1);
            }
            backgroundMusic.seekTo(GoodPrefs.getInstance().getInt("musicPosition", 0));
            backgroundMusic.setLooping(true);
            backgroundMusic.start();
        }
        if (GoodPrefs.getInstance().getString("username", "").equals("") && GoodPrefs.getInstance().getString("password", "").equals("")){
            dialog = new Dialog(ScoreBoardActivity.this);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_dialog);
            dialog.findViewById(R.id.alertSubmit).setOnClickListener(view -> {
                dialog.dismiss();
                showRegisterForm();
            });
            dialog.findViewById(R.id.alertCancel).setOnClickListener(view -> {
            startActivity(new Intent(ScoreBoardActivity.this,MainActivity.class));
            finish();
            });
            dialog.show();
        }else{
            Update update = new Update();
            update.execute();
        }
    }

    public class Update extends AsyncTask {
        Response response;
        OkHttpClient client;
        Request request;



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MediaType mediaType = MediaType.parse("application/json");
            String jsonStr = "{\n" +
                    "    \"username\":\""+GoodPrefs.getInstance().getString("username","")+"\",\n" +
                    "    \"password\":\""+GoodPrefs.getInstance().getString("password","")+"\",\n" +
                    "    \"email\":\""+GoodPrefs.getInstance().getString("email","null")+"\",\n" +
                    "    \"record\":\""+GoodPrefs.getInstance().getInt("recordWord",0)+"\",\n" +
                    "    \"record3\":\""+GoodPrefs.getInstance().getInt("recordWord3",0)+"\"\n" +
                    "}";
            client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            request = new Request.Builder()
                    .url("http://masteranime.ir/api/jalad/v1/update.php?content-type=application/json&username=\"" + GoodPrefs.getInstance().getString("username", "") + "\"")
                    .post(RequestBody.create(mediaType, jsonStr))
                    .build();
        }


        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    return jsonData;
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            try {
                if (o != null) {
                    JSONObject jsonObject = new JSONObject(String.valueOf(o));
                    if (jsonObject.getInt("status") == 1) {
                        GetScores getScores = new GetScores();
                        getScores.execute();
                    }
                } else {
                    Dialog d = new Dialog(ScoreBoardActivity.this);
                    d.setContentView(R.layout.alert_dialog);
                    ((TextView)d.findViewById(R.id.titleAlert)).setText("اینترنت نداری برو وصل کن دوباره بیا");
                    d.findViewById(R.id.alertCancel).setVisibility(View.GONE);
                    d.findViewById(R.id.alertSubmit).setOnClickListener(view -> {
                        startActivity(new Intent(ScoreBoardActivity.this,MainActivity.class));
                    });
                    d.setCancelable(false);
                    d.show();
                }
            } catch (Exception e) {
                Toast.makeText(ScoreBoardActivity.this, "مشکلی رخ داده.", Toast.LENGTH_LONG).show();
            }
        }
    }
    public class GetScores extends AsyncTask {
        Response response;
        OkHttpClient client;
        Request request;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            request = new Request.Builder()
                    .url("http://masteranime.ir/api/jalad/v1/select.php?content-type=application/json&username=\"" + GoodPrefs.getInstance().getString("username","") + "\"")
                    .get()
                    .build();
        }


        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    return jsonData;
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            try {
                if (o != null) {
                    scoreList.clear();
                    JSONObject jsonObject = new JSONObject(String.valueOf(o));
                    if (jsonObject.getInt("status")==1) {
                        JSONObject js = jsonObject.getJSONObject("mydata");
                        JSONArray ja = jsonObject.getJSONArray("data");
                        Score s = new Score(js.getString("username"),js.getString("record"),js.getInt("rank"));
                        scoreList.add(s);
                        for (int i =0;i<ja.length();i++){
                            JSONObject js1 = ja.getJSONObject(i);
                            Score s1 = new Score(js1.getString("username"),js1.getString("record"),0);
                            scoreList.add(s1);
                        }
                        adapter = new ScoreAdapter(ScoreBoardActivity.this,scoreList);
                        binding.scoreRec.setAdapter(adapter);
                        binding.scoreRec.setLayoutManager(new LinearLayoutManager(ScoreBoardActivity.this, RecyclerView.VERTICAL,false));
                    }
                } else {
                    Toast.makeText(ScoreBoardActivity.this, "اتصال خود را به اینترنت چک کنید", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(ScoreBoardActivity.this, "مشکلی رخ داده.", Toast.LENGTH_LONG).show();
            }
        }
    }
    public void  showRegisterForm() {
        Dialog registerFrom = new Dialog(ScoreBoardActivity.this);
        registerFrom.setContentView(R.layout.register_dialog);
        registerFrom.setCancelable(false);
        EditText email = registerFrom.findViewById(R.id.enterEmail);
        EditText username = registerFrom.findViewById(R.id.enterUsername);
        EditText password = registerFrom.findViewById(R.id.enterPassword);
        error = registerFrom.findViewById(R.id.showRegisterError);
        TextView goLogin = registerFrom.findViewById(R.id.goLoginDialog);
        goLogin.setText(Html.fromHtml("<a href=\"#\" >ورود کنید</a>"));
        if (Locale.getDefault().getDisplayLanguage().equals("فارسی")) {
            new Handler().post(() -> {
                ArrayList<View> rows = new ArrayList<>();
                ArrayList<View> views = new ArrayList<>();
                for (int i = 0; i <((LinearLayout) registerFrom.findViewById(R.id.dialogDetails)).getChildCount(); i++){
                    views.clear();
                    LinearLayout ln = (LinearLayout) ((LinearLayout) registerFrom.findViewById(R.id.dialogDetails)).getChildAt(i);
                    for(int x = 0; x < ln.getChildCount(); x++) {
                        views.add(ln.getChildAt(x));
                    }
                    ln.removeAllViews();
                    for(int x = views.size() - 1; x >= 0; x--) {
                        ln.addView(views.get(x));
                    }
                    rows.add(ln);
                }
                ((LinearLayout) registerFrom.findViewById(R.id.dialogDetails)).removeAllViews();
                for (int i = 0; i<rows.size();i++){
                    ((LinearLayout) registerFrom.findViewById(R.id.dialogDetails)).addView(rows.get(i));
                }
            });
        }
        registerFrom.findViewById(R.id.submitRegister).setOnClickListener(view -> {
            if (!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty() && !username.getText().toString().isEmpty()) {
                if (username.getText().toString().length() >= 6 && username.getText().toString().length() <= 20 && password.getText().toString().length() >= 6 && password.getText().toString().length() <= 20) {
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                        Register register = new Register(username.getText().toString(), password.getText().toString(), email.getText().toString());
                        register.execute();
                        if (registered) {
                            GoodPrefs.getInstance().saveString("username", username.getText().toString());
                            GoodPrefs.getInstance().saveString("email", email.getText().toString());
                            GoodPrefs.getInstance().saveString("password", password.getText().toString());
                            Toast.makeText(ScoreBoardActivity.this, "شما با همین نام کاربری ثبت شدید", Toast.LENGTH_SHORT).show();
                            registerFrom.dismiss();
                            Update update = new Update();
                            update.execute();
                        }
                    } else {
                        error.setText("ایمیل وارد شده ساختار معتبری ندارد.");
                    }
                } else {
                    error.setText("نام کاربری و رمز عبور هر کدام کمتر از 6 کارکتر و بیشتر 20 کارکتر نمیتواند باشد");
                }
            } else {
                error.setText("پارامتر های خواسته شده را وارد کنید");
            }
        });
        registerFrom.findViewById(R.id.cancelRegister).setOnClickListener(view1 -> {
            registerFrom.dismiss();
            startActivity(new Intent(ScoreBoardActivity.this,MainActivity.class));
            finish();
        });
        goLogin.setOnClickListener(view -> {
            registerFrom.dismiss();
            showLoginForm();
        });
        registerFrom.show();
    }
    public void  showLoginForm() {
        Dialog loginForm = new Dialog(ScoreBoardActivity.this);
        loginForm.setContentView(R.layout.login_dialog);
        loginForm.setCancelable(false);
        EditText username = loginForm.findViewById(R.id.enterUsername);
        EditText password = loginForm.findViewById(R.id.enterPassword);
        error = loginForm.findViewById(R.id.showRegisterError);
        TextView goReg = loginForm.findViewById(R.id.goRegisterDialog);
        goReg.setText(Html.fromHtml("<a href=\"#\" >ثبت نام</a>"));
        if (Locale.getDefault().getDisplayLanguage().equals("فارسی")) {
            new Handler().post(() -> {
                ArrayList<View> rows = new ArrayList<>();
                ArrayList<View> views = new ArrayList<>();
                for (int i = 0; i <((LinearLayout) loginForm.findViewById(R.id.dialogDetails)).getChildCount(); i++){
                    views.clear();
                    LinearLayout ln = (LinearLayout) ((LinearLayout) loginForm.findViewById(R.id.dialogDetails)).getChildAt(i);
                    for(int x = 0; x < ln.getChildCount(); x++) {
                        views.add(ln.getChildAt(x));
                    }
                    ln.removeAllViews();
                    for(int x = views.size() - 1; x >= 0; x--) {
                        ln.addView(views.get(x));
                    }
                    rows.add(ln);
                }
                ((LinearLayout) loginForm.findViewById(R.id.dialogDetails)).removeAllViews();
                for (int i = 0; i<rows.size();i++){
                    ((LinearLayout) loginForm.findViewById(R.id.dialogDetails)).addView(rows.get(i));
                }
            });
        }
        loginForm.findViewById(R.id.submitRegister).setOnClickListener(view -> {
            if (!password.getText().toString().isEmpty() && !username.getText().toString().isEmpty()) {
                if (username.getText().toString().length() >= 6 && username.getText().toString().length() <= 20 && password.getText().toString().length() >= 6 && password.getText().toString().length() <= 20) {
                    Login login1 = new Login(username.getText().toString(), password.getText().toString());
                    login1.execute();
                    if (login) {
                        GoodPrefs.getInstance().saveString("username", username.getText().toString());
                        GoodPrefs.getInstance().saveString("password", password.getText().toString());
                        Toast.makeText(ScoreBoardActivity.this, "شما با همین نام کاربری ثبت شدید", Toast.LENGTH_SHORT).show();
                        loginForm.dismiss();
                        Update update = new Update();
                        update.execute();
                    }
                } else {
                    error.setText("نام کاربری و رمز عبور هر کدام کمتر از 6 کارکتر و بیشتر 20 کارکتر نمیتواند باشد");
                }
            } else {
                error.setText("پارامتر های خواسته شده را وارد کنید");
            }
        });
        loginForm.findViewById(R.id.cancelRegister).setOnClickListener(view -> {
            loginForm.dismiss();
            startActivity(new Intent(ScoreBoardActivity.this,MainActivity.class));
            finish();
        });
        goReg.setOnClickListener(view ->{
            loginForm.dismiss();
            showRegisterForm();
        });
        loginForm.show();
    }
    public class Register extends AsyncTask {
        Response response;
        OkHttpClient client;
        Request request;
        String username, password, email;

        public Register(String username, String password, String email) {
            this.username = username;
            this.password = password;
            this.email = email;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MediaType mediaType = MediaType.parse("application/json");
            String jsonStr = "{\n" +
                    "    \"username\":\"" + username + "\",\n" +
                    "    \"password\":\"" + password + "\",\n" +
                    "    \"email\":\"" + email + "\"\n" +
                    "}";
            client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            request = new Request.Builder()
                    .url("http://masteranime.ir/api/jalad/v1/create.php?content-type=application/json&username=\"" + username + "\"")
                    .post(RequestBody.create(mediaType, jsonStr))
                    .build();
        }


        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    return jsonData;
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            try {
                if (o != null) {
                    JSONObject jsonObject = new JSONObject(String.valueOf(o));
                    if (jsonObject.getInt("status") == 0) {
                        registered = false;
                        error.setText("این نام کاربری قبلا ثبت شده");
                    } else {
                        registered = true;
                    }
                } else {
                    error.setText("اتصال خود را به اینترنت چک کنید");
                }
            } catch (Exception e) {
                Toast.makeText(ScoreBoardActivity.this, "مشکلی رخ داده.", Toast.LENGTH_LONG).show();
            }
        }
    }
    public class Login extends AsyncTask {
        Response response;
        OkHttpClient client;
        Request request;
        String username, password;

        public Login(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MediaType mediaType = MediaType.parse("application/json");
            String jsonStr = "{\n" +
                    "    \"username\":\"" + username + "\",\n" +
                    "    \"password\":\"" + password + "\"\n" +
                    "}";
            client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            request = new Request.Builder()
                    .url("http://masteranime.ir/api/jalad/v1/login.php?content-type=application/json&username=\"" + username + "\"")
                    .post(RequestBody.create(mediaType, jsonStr))
                    .build();
        }


        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    return jsonData;
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            try {
                if (o != null) {
                    JSONObject jsonObject = new JSONObject(String.valueOf(o));
                    if (jsonObject.getInt("status") == 0) {
                        login = false;
                        error.setText("نام کاربریی با این رمز عبور در سیستم وجود ندارد");
                    } else {
                        login = true;
                        JSONObject js = jsonObject.getJSONObject("data");
                        GoodPrefs.getInstance().saveInt("recordWord", Integer.parseInt(js.getString("record")));
                        GoodPrefs.getInstance().saveString("email", js.getString("email"));
                    }
                } else {
                    error.setText("اتصال خود را به اینترنت چک کنید");
                }
            } catch (Exception e) {
                Toast.makeText(ScoreBoardActivity.this, "مشکلی رخ داده.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
