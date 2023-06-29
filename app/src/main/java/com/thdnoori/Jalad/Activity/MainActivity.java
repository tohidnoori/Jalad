package com.thdnoori.Jalad.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.thdnoori.Jalad.BuildConfig;
import com.thdnoori.Jalad.Database.Achievement;
import com.thdnoori.Jalad.Database.Pack;
import com.thdnoori.Jalad.Database.WordDb;
import com.thdnoori.Jalad.Database.WordTimerDb;
import com.thdnoori.Jalad.Model.GoodPrefs;
import com.thdnoori.Jalad.R;
import com.thdnoori.Jalad.databinding.ActivityMainBinding;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import ir.tapsell.sdk.Tapsell;

public class MainActivity extends AppCompatActivity {
    MaterialCardView start1, start2;
    TextView error;
    ActivityMainBinding binding;
    View view;
    Handler handler = new Handler();
    Boolean registered = false;
    Boolean login = false;
    Runnable runnable;
    MediaPlayer backgroundMusic, clickSound;


    @Override
    protected void onPause() {
        super.onPause();
        if (GoodPrefs.getInstance().getBoolean("soundEnable", true)) {
            backgroundMusic.stop();
            GoodPrefs.getInstance().saveInt("musicPosition", backgroundMusic.getCurrentPosition());
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (GoodPrefs.getInstance().getBoolean("soundEnable", true)) {
            if (GoodPrefs.getInstance().getInt("appMusic", 0) == 0) {
                backgroundMusic = MediaPlayer.create(MainActivity.this, R.raw.app_music);
            } else {
                backgroundMusic = MediaPlayer.create(MainActivity.this, R.raw.app_music1);
            }
            backgroundMusic.seekTo(GoodPrefs.getInstance().getInt("musicPosition", 0));
            backgroundMusic.setLooping(true);
            backgroundMusic.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoodPrefs.init(getApplicationContext());
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        jaladAnimation();
        Tapsell.initialize(getApplication(), "tlgtabhnkiemkdralidtddnpjedenfrikafococtlobqjeoalrbjiqtaiilcbcfgigoeir");
        clickSound = MediaPlayer.create(MainActivity.this, R.raw.click);
        AppUpdate appUpdate = new AppUpdate();
        appUpdate.execute();
        Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotate);
        binding.goSetting.startAnimation(animation);
        if (GoodPrefs.getInstance().getBoolean("needUpdate", false)) {
            Dialog d = new Dialog(MainActivity.this);
            d.setContentView(R.layout.alert_dialog);
            d.findViewById(R.id.alertCancel).setVisibility(View.GONE);
            ((TextView) d.findViewById(R.id.titleAlert)).setText("اپدیت جدید جلاد آمده برو نصبش کن");
            d.findViewById(R.id.alertSubmit).setOnClickListener(view -> {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.thdnoori.jalad")));
            });
            d.setCancelable(false);
            d.show();
        }
        if (GoodPrefs.getInstance().getBoolean("soundEnable", true)) {
            if (GoodPrefs.getInstance().getInt("appMusic", 0) == 0) {
                backgroundMusic = MediaPlayer.create(MainActivity.this, R.raw.app_music);
            } else {
                backgroundMusic = MediaPlayer.create(MainActivity.this, R.raw.app_music1);
            }
            backgroundMusic.seekTo(GoodPrefs.getInstance().getInt("musicPosition", 0));
            backgroundMusic.setLooping(true);
            backgroundMusic.start();
        }
        if (GoodPrefs.getInstance().getBoolean("FirstTime", true)) {
            GoodPrefs.getInstance().saveBoolean("FirstTime", false);
            GoodPrefs.getInstance().saveInt("coin", 50);
            GoodPrefs.getInstance().getBoolean("needUpdate", false);
            GoodPrefs.getInstance().saveInt("roundWord", 1);
            GoodPrefs.getInstance().saveInt("recordWord", 0);
            GoodPrefs.getInstance().saveInt("roundWord3", 1);
            GoodPrefs.getInstance().saveInt("recordWord3", 0);
            GoodPrefs.getInstance().saveInt("roundTimer", 1);
            GoodPrefs.getInstance().saveInt("recordTimer", 0);
            importAchievements();
            importPacks();
            importNormalWords();
            importWord3();
            importTimerWords();
            showRegisterForm();
        }
        view = binding.getRoot();
        binding.goScoreBoard.setOnClickListener(view -> {
            clickSound.start();
            startActivity(new Intent(MainActivity.this, ScoreBoardActivity.class));
            overridePendingTransition(R.anim.to_main1, R.anim.from_main1);
        });
        binding.goAchievement.setOnClickListener(view -> {
            clickSound.start();
            startActivity(new Intent(MainActivity.this, AchievementActivity.class));
            overridePendingTransition(R.anim.to_main1, R.anim.from_main1);
        });
        binding.startGame.setOnClickListener(view -> {
            clickSound.start();
            Dialog d = new Dialog(MainActivity.this);
            d.setContentView(R.layout.choose_game_type_dialog);
            d.findViewById(R.id.startNormalSinglePlayer).setOnClickListener(view1 -> {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                GoodPrefs.getInstance().saveString("type", "normal");
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.to_main, R.anim.from_main);
            });
            d.findViewById(R.id.startWord3SinglePlayer).setOnClickListener(view1 -> {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                GoodPrefs.getInstance().saveString("type", "word3");
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.to_main, R.anim.from_main);
            });
            d.findViewById(R.id.startTimingSinglePlayer).setOnClickListener(view1 -> {
                Intent intent = new Intent(MainActivity.this, TimerGameActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.to_main, R.anim.from_main);
            });
            d.show();
        });
        binding.start2Player.setOnClickListener(view -> {
            clickSound.start();
            Intent intent = new Intent(MainActivity.this, TowPlayerActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.to_main, R.anim.from_main);
        });
        binding.goSetting.setOnClickListener(view -> {
            clickSound.start();
            startActivity(new Intent(MainActivity.this, SettingActivity.class));
            overridePendingTransition(R.anim.to_main1, R.anim.from_main1);

        });
        binding.goStore.setOnClickListener(view -> {
            clickSound.start();
            startActivity(new Intent(MainActivity.this, StoreActivity.class));
            overridePendingTransition(R.anim.to_main1, R.anim.from_main1);
        });
        Glide.with(MainActivity.this).load(R.drawable.source).into(binding.img);
        GoodPrefs.init(getApplicationContext());
        setContentView(view);
        start1 = findViewById(R.id.startGame);
        start2 = findViewById(R.id.start2Player);
    }

    public void importNormalWords() {
        AsyncTask.execute(() -> {
            List<WordDb> list = WordDb.listAll(WordDb.class);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getWordName().length() > 3) {
                    list.get(i).delete();
                }
            }
            List<String> words = Arrays.asList(getResources().getStringArray(R.array.words));
            List<String> categories = Arrays.asList(getResources().getStringArray(R.array.word_category));
            for (int i = 0; i < words.size(); i++) {
                WordDb word = new WordDb();
                word.setWordCategory(categories.get(i));
                word.setWordName(words.get(i));
                word.save();
            }
        });
    }

    public void importTimerWords() {
        AsyncTask.execute(() -> {
            WordTimerDb.deleteAll(WordTimerDb.class);
            List<String> words = Arrays.asList(getResources().getStringArray(R.array.wordTimer));
            List<String> categories = Arrays.asList(getResources().getStringArray(R.array.word_timer_category));
            for (int i = 0; i < words.size(); i++) {
                WordTimerDb word = new WordTimerDb();
                word.setWordCategory(categories.get(i));
                word.setWordName(words.get(i));
                word.save();
            }
        });
    }

    public void importWord3() {
        AsyncTask.execute(() -> {
            List<WordDb> list = WordDb.listAll(WordDb.class);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getWordName().length() == 3) {
                    list.get(i).delete();
                }
            }
            List<String> categories = Arrays.asList(getResources().getStringArray(R.array.word3_category));
            List<String> words = Arrays.asList(getResources().getStringArray(R.array.word3));
            for (int i = 0; i < words.size(); i++) {
                WordDb word = new WordDb();
                word.setWordCategory(categories.get(i));
                word.setWordName(words.get(i));
                word.save();
            }
        });
    }

    public void importPacks() {
        AsyncTask.execute(() -> {
            Pack p = new Pack();
            p.setPurchased(true);
            p.setEnable(true);
            p.setImageResourceID(R.drawable.pack_theme_default);
            p.setName("اصلی");
            p.setPrice(0);
            p.save();

            p = new Pack();
            p.setPurchased(false);
            p.setEnable(false);
            p.setPerfName("theme1");
            p.setImageResourceID(R.drawable.pack_theme_1);
            p.setName("معبد");
            p.setPrice(50);
            p.save();

            p = new Pack();
            p.setPurchased(false);
            p.setEnable(false);
            p.setImageResourceID(R.drawable.pack_theme_2);
            p.setName("تفنگدار");
            p.setPrice(100);
            p.setPerfName("theme2");
            p.save();

            p = new Pack();
            p.setPurchased(false);
            p.setEnable(false);
            p.setImageResourceID(R.drawable.pack_theme_3);
            p.setName("شمشیر زن");
            p.setPerfName("theme3");
            p.setPrice(100);
            p.save();

            p = new Pack();
            p.setPurchased(false);
            p.setEnable(false);
            p.setImageResourceID(R.drawable.pack_theme_4);
            p.setName("رقص تابوت");
            p.setPerfName("theme4");
            p.setPrice(100);
            p.save();
        });

    }

    public void importAchievements() {
        AsyncTask.execute(() -> {
            GoodPrefs.getInstance().saveInt("helpRecord", 0);
            GoodPrefs.getInstance().saveInt("adRecord", 0);
            GoodPrefs.getInstance().saveInt("wheelRecord", 0);
            GoodPrefs.getInstance().saveInt("noMistake", 0);
            GoodPrefs.getInstance().saveBoolean("theme2", false);
            GoodPrefs.getInstance().saveBoolean("theme1", false);
            GoodPrefs.getInstance().saveBoolean("theme2", false);
            GoodPrefs.getInstance().saveBoolean("theme3", false);

            Achievement a = new Achievement();
            a.setName("رکورد کلمات مختلف");
            a.setPrice(10);
            a.setReady(false);
            a.setRecord(5);
            a.setOnce(false);
            a.setPerfName("recordWord");
            a.setCompleted(false);
            a.save();

            a = new Achievement();
            a.setName("رکورد کلمات سه حرفی");
            a.setPrice(10);
            a.setReady(false);
            a.setRecord(5);
            a.setOnce(false);
            a.setPerfName("recordWord3");
            a.setCompleted(false);
            a.save();

            a = new Achievement();
            a.setName("رکورد قسمت زمانی");
            a.setPrice(10);
            a.setReady(false);
            a.setRecord(5);
            a.setOnce(false);
            a.setPerfName("recordTimer");
            a.setCompleted(false);
            a.save();

            a = new Achievement();
            a.setName("کمک بگیر");
            a.setPrice(10);
            a.setRecord(5);
            a.setOnce(false);
            a.setCompleted(false);
            a.setPerfName("helpRecord");
            a.setReady(false);
            a.save();

            a = new Achievement();
            a.setName("تبلیغ ببین");
            a.setPrice(5);
            a.setReady(false);
            a.setRecord(5);
            a.setPerfName("adRecord");
            a.setCompleted(false);
            a.setOnce(false);
            a.save();

            a = new Achievement();
            a.setOnce(false);
            a.save();
            a.setPerfName("wheelRecord");
            a.setName("گردونه شانس رو بچرخون");
            a.setPrice(5);
            a.setRecord(5);
            a.setCompleted(false);
            a.setReady(false);
            a.save();

            a = new Achievement();
            a.setOnce(false);
            a.save();
            a.setPerfName("noMistake");
            a.setName("بدون غلط حدس بزن");
            a.setPrice(20);
            a.setRecord(5);
            a.setCompleted(false);
            a.setReady(false);
            a.save();

            a = new Achievement();
            a.setName("تم معبد را بخر");
            a.setPrice(10);
            a.setPerfName("theme1");
            a.setReady(false);
            a.setOnce(true);
            a.setCompleted(false);
            a.save();

            a = new Achievement();
            a.setName("تم تفنگدار را بخر");
            a.setPrice(10);
            a.setPerfName("theme2");
            a.setReady(false);
            a.setCompleted(false);
            a.setOnce(true);
            a.save();

            a = new Achievement();
            a.setName("تم شمشیرزن را بخر");
            a.setPrice(10);
            a.setPerfName("theme3");
            a.setCompleted(false);
            a.setReady(false);
            a.setOnce(true);
            a.save();

            a = new Achievement();
            a.setName("تم رقض تابوت را بخر");
            a.setPrice(10);
            a.setPerfName("theme4");
            a.setCompleted(false);
            a.setReady(false);
            a.setOnce(true);
            a.save();
        });
    }

    public void showRegisterForm() {
        Dialog registerFrom = new Dialog(MainActivity.this);
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
                for (int i = 0; i < ((LinearLayout) registerFrom.findViewById(R.id.dialogDetails)).getChildCount(); i++) {
                    views.clear();
                    LinearLayout ln = (LinearLayout) ((LinearLayout) registerFrom.findViewById(R.id.dialogDetails)).getChildAt(i);
                    for (int x = 0; x < ln.getChildCount(); x++)
                        views.add(ln.getChildAt(x));
                    ln.removeAllViews();
                    for (int x = views.size() - 1; x >= 0; x--)
                        ln.addView(views.get(x));
                    rows.add(ln);
                }
                ((LinearLayout) registerFrom.findViewById(R.id.dialogDetails)).removeAllViews();
                for (int i = 0; i < rows.size(); i++)
                    ((LinearLayout) registerFrom.findViewById(R.id.dialogDetails)).addView(rows.get(i));
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
                            Toast.makeText(MainActivity.this, "شما با همین نام کاربری ثبت شدید", Toast.LENGTH_SHORT).show();
                            registerFrom.dismiss();
                        }
                    } else
                        error.setText("ایمیل وارد شده ساختار معتبری ندارد.");
                } else
                    error.setText("نام کاربری و رمز عبور هر کدام کمتر از 6 کارکتر و بیشتر 20 کارکتر نمیتواند باشد");
            } else
                error.setText("پارامتر های خواسته شده را وارد کنید");
        });
        registerFrom.findViewById(R.id.cancelRegister).setOnClickListener(view1 -> {
            registerFrom.dismiss();
        });
        goLogin.setOnClickListener(view -> {
            registerFrom.dismiss();
            showLoginForm();
        });
        registerFrom.show();
    }

    public void jaladAnimation() {
        runnable = new Runnable() {
            @Override
            public void run() {
                Animation jn = AnimationUtils.loadAnimation(MainActivity.this, R.anim.blink);
                binding.jTitle.startAnimation(jn);
                new Handler().postDelayed(() -> {
                    Animation ln = AnimationUtils.loadAnimation(MainActivity.this, R.anim.blink);
                    binding.lTitle.startAnimation(ln);
                }, 700);
                new Handler().postDelayed(() -> {
                    Animation an = AnimationUtils.loadAnimation(MainActivity.this, R.anim.blink);
                    binding.aTitle.startAnimation(an);
                }, 1400);
                new Handler().postDelayed(() -> {
                    Animation dn = AnimationUtils.loadAnimation(MainActivity.this, R.anim.blink);
                    binding.dTitle.startAnimation(dn);
                    handler.postDelayed(this::run, 600);
                }, 2000);
            }
        };
        handler.post(runnable);
    }

    public void showLoginForm() {
        Dialog loginForm = new Dialog(MainActivity.this);
        loginForm.setContentView(R.layout.login_dialog);
        loginForm.setCancelable(false);
        EditText username = loginForm.findViewById(R.id.enterUsername);
        EditText password = loginForm.findViewById(R.id.enterPassword);
        error = loginForm.findViewById(R.id.showRegisterError);
        TextView goReg = loginForm.findViewById(R.id.goRegisterDialog);
        Button submit = loginForm.findViewById(R.id.submitRegister);
        Button cancel = loginForm.findViewById(R.id.cancelRegister);
        goReg.setText(Html.fromHtml("<a href=\"#\" >ثبت نام</a>"));
        if (Locale.getDefault().getDisplayLanguage().equals("فارسی")) {
            new Handler().post(() -> {
                ArrayList<View> rows = new ArrayList<>();
                ArrayList<View> views = new ArrayList<>();
                for (int i = 0; i < ((LinearLayout) loginForm.findViewById(R.id.dialogDetails)).getChildCount(); i++) {
                    views.clear();
                    LinearLayout ln = (LinearLayout) ((LinearLayout) loginForm.findViewById(R.id.dialogDetails)).getChildAt(i);
                    for (int x = 0; x < ln.getChildCount(); x++) {
                        views.add(ln.getChildAt(x));
                    }
                    ln.removeAllViews();
                    for (int x = views.size() - 1; x >= 0; x--) {
                        ln.addView(views.get(x));
                    }
                    rows.add(ln);
                }
                ((LinearLayout) loginForm.findViewById(R.id.dialogDetails)).removeAllViews();
                for (int i = 0; i < rows.size(); i++) {
                    ((LinearLayout) loginForm.findViewById(R.id.dialogDetails)).addView(rows.get(i));
                }
            });
        }
        submit.setOnClickListener(view -> {
            if (!password.getText().toString().isEmpty() && !username.getText().toString().isEmpty()) {
                if (username.getText().toString().length() >= 6 && username.getText().toString().length() <= 20 && password.getText().toString().length() >= 6 && password.getText().toString().length() <= 20) {
                    Login login1 = new Login(username.getText().toString(), password.getText().toString());
                    login1.execute();
                    if (login) {
                        GoodPrefs.getInstance().saveString("username", username.getText().toString());
                        GoodPrefs.getInstance().saveString("password", password.getText().toString());
                        Toast.makeText(MainActivity.this, "شما با همین نام کاربری ثبت شدید", Toast.LENGTH_SHORT).show();
                        loginForm.dismiss();
                    }
                } else {
                    error.setText("نام کاربری و رمز عبور هر کدام کمتر از 6 کارکتر و بیشتر 20 کارکتر نمیتواند باشد");
                }
            } else {
                error.setText("پارامتر های خواسته شده را وارد کنید");
            }
        });
        cancel.setOnClickListener(view -> {
            loginForm.dismiss();
        });
        goReg.setOnClickListener(view -> {
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
                Toast.makeText(MainActivity.this, "مشکلی رخ داده.", Toast.LENGTH_LONG).show();
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
                    }
                } else {
                    error.setText("اتصال خود را به اینترنت چک کنید");
                }
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "مشکلی رخ داده.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class AppUpdate extends AsyncTask {
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
                    .url("http://masteranime.ir/api/jalad/v3/upgrade.php?content-type=application/json")
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
                    JSONObject jsonObject = new JSONObject(String.valueOf(o));
                    if(jsonObject.getInt("upgrade")> BuildConfig.VERSION_CODE){
                        GoodPrefs.getInstance().saveBoolean("needUpdate",true );
                    }else{
                        GoodPrefs.getInstance().saveBoolean("needUpdate",false );

                    }
                }
            } catch (Exception e) {

            }
        }
    }

}