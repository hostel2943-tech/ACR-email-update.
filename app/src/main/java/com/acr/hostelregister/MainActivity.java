package com.acr.hostelregister;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

import androidx.activity.OnBackPressedCallback;

import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {

    private OnBackPressedCallback backPressedCallback;

    private void enableImmersiveMode() {

        Window window = getWindow();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams params =
                    window.getAttributes();

            params.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams
                    .LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;

            window.setAttributes(params);
        }

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        registerPlugin(ACRNativePlugin.class);

        super.onCreate(savedInstanceState);

        enableImmersiveMode();

        backPressedCallback = new OnBackPressedCallback(true) {

            @Override
            public void handleOnBackPressed() {

                if (getBridge() == null) {
                    finish();
                    return;
                }

                WebView webView = getBridge().getWebView();

                if (webView == null) {
                    finish();
                    return;
                }

                webView.post(() -> {

                    webView.evaluateJavascript(
                        "(function() {" +
                        " if (typeof window.acrHandleAndroidBack === 'function') {" +
                        "   return window.acrHandleAndroidBack();" +
                        " }" +
                        " window.dispatchEvent(new Event('acrAndroidBack'));" +
                        " return true;" +
                        "})()",
                        null
                    );

                });
            }
        };

        getOnBackPressedDispatcher().addCallback(
                this,
                backPressedCallback
        );
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            enableImmersiveMode();
        }
    }
}
