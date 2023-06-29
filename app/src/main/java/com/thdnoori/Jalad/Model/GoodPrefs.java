package com.thdnoori.Jalad.Model;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GoodPrefs {
        private static GoodPrefs instance;
        private static SharedPreferences sharedPreferences;

        public GoodPrefs() {
        }

        public static void init(Context context) {
            sharedPreferences = context.getApplicationContext().getSharedPreferences("amPrefs", MODE_PRIVATE);
        }

        public static GoodPrefs getInstance() {
            if (instance == null) {
                validateInitialization();
                Class var0 =  GoodPrefs.class;
                synchronized(GoodPrefs.class) {
                    if (instance == null) {
                        instance = new GoodPrefs();
                    }
                }
            }

            return instance;
        }

        public void saveInt(String key, int value) {
            Editor editor = sharedPreferences.edit();
            editor.putInt(key, value);
            editor.apply();
        }

        public int getInt(String key, int defaultValue) {
            return this.isKeyExists(key) ? sharedPreferences.getInt(key, defaultValue) : defaultValue;
        }

        public void saveBoolean(String key, boolean value) {
            Editor editor = sharedPreferences.edit();
            editor.putBoolean(key, value);
            editor.apply();
        }

        public boolean getBoolean(String key, boolean defaultValue) {
            return this.isKeyExists(key) ? sharedPreferences.getBoolean(key, defaultValue) : defaultValue;
        }

        public void saveFloat(String key, float value) {
            Editor editor = sharedPreferences.edit();
            editor.putFloat(key, value);
            editor.apply();
        }

        public float getFloat(String key, float defaultValue) {
            return this.isKeyExists(key) ? sharedPreferences.getFloat(key, defaultValue) : defaultValue;
        }

        public void saveLong(String key, long value) {
            Editor editor = sharedPreferences.edit();
            editor.putLong(key, value);
            editor.apply();
        }

        public long getLong(String key, long defaultValue) {
            return this.isKeyExists(key) ? sharedPreferences.getLong(key, defaultValue) : defaultValue;
        }

        public void saveString(String key, String value) {
            Editor editor = sharedPreferences.edit();
            editor.putString(key, value);
            editor.apply();
        }

        public String getString(String key, String defaultValue) {
            return this.isKeyExists(key) ? sharedPreferences.getString(key, defaultValue) : defaultValue;
        }

        public <T> void saveObject(String key, T object) {
            String objectString = (new Gson()).toJson(object);
            Editor editor = sharedPreferences.edit();
            editor.putString(key, objectString);
            editor.apply();
        }

        public <T> T getObject(String key, Class<T> classType) {
            if (this.isKeyExists(key)) {
                String objectString = sharedPreferences.getString(key, (String)null);
                if (objectString != null) {
                    return (new Gson()).fromJson(objectString, classType);
                }
            }

            return null;
        }

        public <T> void saveObjectsList(String key, List<T> objectList) {
            String objectString = (new Gson()).toJson(objectList);
            Editor editor = sharedPreferences.edit();
            editor.putString(key, objectString);
            editor.apply();
        }

        public <T> List<T> getObjectsList(String key, Class<T> classType) {
            if (this.isKeyExists(key)) {
                String objectString = sharedPreferences.getString(key, (String)null);
                if (objectString != null) {
                    ArrayList<T> t = (ArrayList)(new Gson()).fromJson(objectString, (new TypeToken<List<T>>() {
                    }).getType());
                    List<T> finalList = new ArrayList();

                    for(int i = 0; i < t.size(); ++i) {
                        String s = String.valueOf(t.get(i));
                        finalList.add((new Gson()).fromJson(s, classType));
                    }

                    return finalList;
                }
            }

            return null;
        }

        public void clearSession() {
            Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
        }

        public boolean deleteValue(String key) {
            if (this.isKeyExists(key)) {
                Editor editor = sharedPreferences.edit();
                editor.remove(key);
                editor.apply();
                return true;
            } else {
                return false;
            }
        }

        public boolean isKeyExists(String key) {
            Map<String, ?> map = sharedPreferences.getAll();
            if (map.containsKey(key)) {
                return true;
            } else {
                Log.e("GoodPrefs", "No element founded in sharedPrefs with the key " + key);
                return false;
            }
        }

        private static void validateInitialization() {
            if (sharedPreferences == null) {
                throw new GoodPrefsException("GoodPrefs Library must be initialized inside your application class by calling GoodPrefs.init(getApplicationContext)");
            }
        }
    }
