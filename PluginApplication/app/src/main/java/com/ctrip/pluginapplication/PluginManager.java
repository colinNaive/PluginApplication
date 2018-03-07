package com.ctrip.pluginapplication;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.io.File;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

/**
 * @author Zhenhua on 2018/3/7.
 * @email zhshan@ctrip.com ^.^
 */

public class PluginManager {
    //-------1:构建单例类start--------
    private static PluginManager instance = new PluginManager();

    public static PluginManager getInstance() {
        return instance;
    }
    //-------1:构建单例类end--------

    private Context context;

    public void setContext(Context context) {
        this.context = context.getApplicationContext();
    }

    public void loadPath(String path) {
        setEntryName(path);
        setClassLoader(path);
        setResources(path);
    }

    //-------2:获取插件app入口activity name start--------
    private void setEntryName(String path) {
        //得到packageManager来获取包信息
        PackageManager packageManager = context.getPackageManager();
        //参数一是apk的路径，参数二是希望得到的内容
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        //得到插件app的入口activity名称
        entryName = packageInfo.activities[0].name;
    }

    private String entryName;

    public String getEntryName() {
        return entryName;
    }
    //-------2:获取插件app入口activity name end--------


    //-------3:构造classLoader start-------------
    private DexClassLoader dexClassLoader;

    private void setClassLoader(String path) {
        //dex的缓存路径
        File dexOutFile = context.getDir("dex", Context.MODE_PRIVATE);
        dexClassLoader = new DexClassLoader(path, dexOutFile.getAbsoluteFile().getAbsolutePath(), null, context.getClassLoader());
    }

    public DexClassLoader getDexClassLoader() {
        return dexClassLoader;
    }
    //-------3:构造classLoader end-------------

    //-------4:构造resources start--------
    private Resources resources;

    public Resources getResources() {
        return resources;
    }

    public void setResources(String path) {
        //由于构建resources必须要传入AssetManager，这里先构建一个AssetManager
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = AssetManager.class.getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, path);

            resources = new Resources(assetManager, context.getResources().getDisplayMetrics(), context.getResources().getConfiguration());
        } catch (Exception e) {
        }
    }
    //-------4:构造resources end--------

}
