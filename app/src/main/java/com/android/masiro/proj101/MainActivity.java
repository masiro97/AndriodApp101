package com.android.masiro.proj101;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listview;
    EditText et;
    WebView wv;
    ProgressDialog dlg;
    Animation animation;
    LinearLayout linear;
    ArrayList<String> site;
    ArrayAdapter<String> adapter;
    int setAni = 0;
    int pos = 0;
    Handler handler = new Handler();

    public void OnButton(View v){

        wv.loadUrl("http://" + et.getText().toString());
    }
    public void setVisible(int ani) {
        if (ani == 0) {
            linear.setVisibility(View.GONE);
            wv.setVisibility(View.INVISIBLE);
            listview.setVisibility(View.VISIBLE);
        } else {
            linear.setAnimation(animation);
            animation.start();
            linear.setVisibility(View.VISIBLE);
            wv.setVisibility(View.VISIBLE);
            listview.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "즐겨찾기추가");
        menu.add(0, 2, 0, "즐겨찾기목록");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            setAni = 1;
            setVisible(setAni);
            wv.loadUrl("file:///android_asset/urladd.html");
        } else if (item.getItemId() == 2) {
            setAni = 0;
            setVisible(setAni);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listview = (ListView) findViewById(R.id.listview);
        wv = (WebView) findViewById(R.id.webview);
        wv.addJavascriptInterface(new JavaSriptMethods(), "URL");
        dlg = new ProgressDialog(this);
        et = (EditText) findViewById(R.id.editText);
        linear = (LinearLayout) findViewById(R.id.linearlayout);
        animation = AnimationUtils.loadAnimation(this, R.anim.trans);

        site = new ArrayList<String>();
        site.add(new String("<naver> www.naver.com"));
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line
                , site);

        listview.setAdapter(adapter);
        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setCacheMode(webSettings.LOAD_NO_CACHE);

        wv.loadUrl("http://www.naver.com");

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("삭제");
                dialog.setMessage("삭제하시겠습니까?");
                dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        site.remove(pos);
                        adapter.notifyDataSetChanged();
                    }
                });
                dialog.setNegativeButton("취소", null);
                dialog.show();
                return true;
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                linear.setVisibility(View.VISIBLE);
                wv.setVisibility(View.VISIBLE);
                listview.setVisibility(View.INVISIBLE);
                int startindex = site.get(position).indexOf(" ") + 1;
                String url = site.get(position).substring(startindex, site.get(position).length());
                wv.loadUrl("http://" + url);
            }
        });
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                linear.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        wv.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                dlg.setMessage("Loading...");
                dlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dlg.show();
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                et.setText(url);
                super.onPageFinished(view, url);
            }
        });

        wv.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress >= 100) dlg.dismiss();
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                result.confirm();
                return super.onJsAlert(view, url, message, result);
            }
        });
    }

    class JavaSriptMethods {

        @JavascriptInterface
        public void setVisibleURL() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    linear.setVisibility(View.VISIBLE);
                }
            });

        }

        @JavascriptInterface
        public void loadURL(final String sitename, final String url) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //원하는 기능
                    int checksite = 0;
                    int checkurl = 0;
                    for (int i = 0; i < site.size(); i++) {
                        if (site.get(i).indexOf(sitename) > -1) checksite++;
                        if (site.get(i).indexOf(url) > -1) checkurl++;
                    }

                    if (checksite == 0 && checkurl == 0) {
                        String data = "<" + sitename + "> " + url;
                        site.add(data);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), "즐겨찾기에 추가되었습니다",
                                Toast.LENGTH_SHORT).show();
                        wv.loadUrl("javascript:setMsg(null)");
                    } else wv.loadUrl("javascript:displayMsg()");

                }
            });

        }
    }
}
