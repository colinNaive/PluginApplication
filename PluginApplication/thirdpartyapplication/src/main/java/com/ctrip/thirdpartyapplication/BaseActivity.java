package com.ctrip.thirdpartyapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.ctrip.standard.AppInterface;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Zhenhua on 2018/3/3.
 * @email zhshan@ctrip.com ^.^
 */

public class BaseActivity extends Activity implements AppInterface {

    protected Activity that;

    @Override
    public void attach(@NotNull Activity activity) {
        //上下文注入进来了
        this.that = activity;
    }

    /**
     * super.setContentView(layoutResID)最终调用的是系统给我们注入的上下文
     */
    @Override
    public void setContentView(int layoutResID) {
        if (that == null) {
            super.setContentView(layoutResID);
        } else {
            that.setContentView(layoutResID);
        }
    }

    @Override
    public <T extends View> T findViewById(int id) {
        if (that == null) {
            return super.findViewById(id);
        } else {
            return that.findViewById(id);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public ClassLoader getClassLoader() {
        if (that == null) {
            return super.getClassLoader();
        } else {
            return that.getClassLoader();
        }
    }

    @NonNull
    @Override
    public LayoutInflater getLayoutInflater() {
        if (that == null) {
            return super.getLayoutInflater();
        } else {
            return that.getLayoutInflater();
        }
    }


    @Override
    public WindowManager getWindowManager() {
        if (that == null) {
            return super.getWindowManager();
        } else {
            return that.getWindowManager();
        }
    }

    @Override
    public Window getWindow() {
        if (that == null) {
            return super.getWindow();
        } else {
            return that.getWindow();
        }
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (that == null) {
            super.onCreate(savedInstanceState);
        } else {
//            that.onCreate(savedInstanceState);
        }
    }

    public void onStart() {
        if (that == null) {
            super.onStart();
        } else {
//            that.onStart();
        }
    }

    public void onDestroy() {
        if (that == null) {
            super.onDestroy();
        } else {
//            that.onDestroy();
        }
    }

    public void onPause() {
        if (that == null) {
            super.onPause();
        } else {
//            that.onPause();
        }
    }

    public void onResume() {
        if (that == null) {
            super.onResume();
        } else {
//            that.onResume();
        }
    }

}
