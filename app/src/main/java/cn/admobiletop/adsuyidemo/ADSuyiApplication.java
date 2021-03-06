package cn.admobiletop.adsuyidemo;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.tencent.bugly.crashreport.CrashReport;

import cn.admobiletop.adsuyi.ADSuyiSdk;
import cn.admobiletop.adsuyi.config.ADSuyiInitConfig;
import cn.admobiletop.adsuyidemo.activity.SplashAdActivity;
import cn.admobiletop.adsuyidemo.constant.ADSuyiDemoConstant;

/**
 * @author ciba
 * @description 描述
 * @date 2020/3/25
 */
public class ADSuyiApplication extends Application {
    private static final long OPEN_SPLASH_ACTIVITY_INTERVAL_TIME = 180 * 1000;
    private long preMillis;

    @Override
    public void onCreate() {
        super.onCreate();

        long millis = System.currentTimeMillis();

        // 初始化ADSuyi广告SDK
        ADSuyiSdk.getInstance().init(this, new ADSuyiInitConfig.Builder()
                // 设置APPID
                .appId(ADSuyiDemoConstant.APP_ID)
                // 是否开启Debug，开启会有详细的日志信息打印
                .debug(true)
                // 是否过滤第三方平台的问题广告（例如: 已知某个广告平台在某些机型的Banner广告可能存在问题，如果开启过滤，则在该机型将不再去获取该平台的Banner广告）
                .filterThirdQuestion(true)
                .build());

        Log.d("ADSuyiDemo", "ADSuyiSdk init need : " + (System.currentTimeMillis() - millis) + "ms");

        // 添加bugly初始化（该初始化与广告SDK无关，广告SDK中不包含bugly相关内容，仅供Demo错误信息收集使用）
        CrashReport.initCrashReport(getApplicationContext(), "6d9d9f24ee", true);

        // 如果有接开屏广告，可以设置应用进入后台一段时间后回到应用再次开启开屏界面，增加开屏广告收益（仅供参考，无需要可不设置）
        openSplashActivityAgain();
    }

    private void openSplashActivityAgain() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                checkNeedOpenSplashActivity(activity);
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                preMillis = System.currentTimeMillis();
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        });
    }

    /**
     * 检查是否需要再次打开开屏界面
     */
    private void checkNeedOpenSplashActivity(Activity activity) {
        long millis = System.currentTimeMillis();
        if (preMillis > 0
                && millis - preMillis > OPEN_SPLASH_ACTIVITY_INTERVAL_TIME
                && !(activity instanceof SplashAdActivity)) {
            activity.startActivity(new Intent(activity, SplashAdActivity.class));
        }
        preMillis = millis;
    }
}
