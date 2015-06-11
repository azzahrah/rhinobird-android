package tv.rhinobird.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkView;
import org.xwalk.core.internal.XWalkViewInternal;


import tv.rhinobird.app.R;


public class MainActivity extends Activity{

    private static final String TAG = MainActivity.class.getSimpleName();
    private XWalkView xWalkWebView;
    private WebView webLoader;
    private static final String wrapUrl = "https://staging.rhinobird.tv/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initWeb();
        loadWeb();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = getTheme();
            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
            int color = typedValue.data;

            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            ActivityManager.TaskDescription td = new ActivityManager.TaskDescription(null, bm, color);

            setTaskDescription(td);
            bm.recycle();

        }
    }
/*
    @Override
    public void onStart() {
        loadWeb();
    }
*/
    @Override
    public void onPause() {
        super.onPause();
            if (xWalkWebView != null) {
                xWalkWebView.pauseTimers();
                xWalkWebView.onHide();
            }

    }

    @Override
    public void onStop() {
        super.onStop();
        xWalkWebView.evaluateJavascript("if(window.localStream){window.localStream.stop();}", null);
        xWalkWebView.stopLoading();
    }

   @Override
    public void onResume() {
       super.onResume();
       if (xWalkWebView != null) {
           xWalkWebView.resumeTimers();
           xWalkWebView.onShow();
       }
       //loadWeb();

    }
    @Override
    public void onRestart() {
        super.onRestart();
        xWalkWebView.resumeTimers();
        //loadWeb();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (xWalkWebView != null) {
            xWalkWebView.onDestroy();
        }
    }

    public void initWeb(){
        webLoader = (WebView) findViewById(R.id.fragment_loader_webview);
        webLoader.loadUrl("file:///android_asset/index.html");

        xWalkWebView = (XWalkView) findViewById(R.id.fragment_main_webview);
        xWalkWebView.clearCache(true);
        // turn on debugging
        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
    }

    public void loadWeb(){
        xWalkWebView.setVisibility(View.GONE);
        if (webLoader.getVisibility() == View.GONE){
            webLoader.setVisibility(View.VISIBLE);
        }
        xWalkWebView.setResourceClient(new XWalkResourceClient(xWalkWebView){
            @Override
            public void onLoadFinished(XWalkView view, String url) {
                super.onLoadFinished(xWalkWebView, url);
                xWalkWebView.setVisibility(View.VISIBLE);
                webLoader.setVisibility(View.GONE);
            }
            //@Override
/*                public void onReceivedSslError (XWalkView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(xWalkWebView, ValueCallback<Boolean> callback, error);
                callback.onReceiveValue(true);
                Log.d(TAG, error.toString());
                handler.proceed();
            }*/
            public void onReceivedSslError(XWalkViewInternal view, ValueCallback<Boolean> callback, SslError error){
                callback.onReceiveValue(true);
                Log.d(TAG, error.toString());
            }
        });
        if (DetectConnection.checkInternetConnection( this)) {
            xWalkWebView.load(wrapUrl, null);
        }
        else {
            xWalkWebView.load("file:///android_asset/error_page.html", null);
        }

    }

}
