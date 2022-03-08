package com.KReader.app;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;
    SQLiteAdapter dhHelper;
    String title= "";
    String slag= "";
    String content= "";
    int id= 0;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        dhHelper =  new SQLiteAdapter(this);

        webView = findViewById(R.id.webview);
       // Toolbar t = (Toolbar) findViewById(R.id.);
        Bundle extra= getIntent().getExtras();

        if(extra!=null){
            this.title= extra.getString("title");
        }
        if(extra!=null){
            this.slag= extra.getString("link");
        }
        if(extra!=null){
            this.content= extra.getString("content");
        }

        setTitle(title);

        webView.getSettings().setJavaScriptEnabled(true);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            webView.getSettings().setAllowFileAccess(true);
            webView.getSettings().setAllowContentAccess(true);
        }

        webView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                //error receive
            }
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                // Redirect to deprecated method, so you can use it in all SDK versions
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }
        });

        if (savedItem()){
            String filenameExternal = "archive_" + String.valueOf(id) + ".mhtml";
            File file = new File(getExternalFilesDir(null).getAbsolutePath(), filenameExternal);
            System.out.println("URI: " + "file://" + file.toString());
            webView.loadUrl("file://" + file.toString());

        }
        else {
            webView.loadUrl(slag);
        }
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                WebViewActivity activity = (WebViewActivity) view.getContext();
                if(activity.savedItem()){
//                    activity.menu.findItem(R.id.savedItem).setVisible(false);
//                    activity.menu.findItem(R.id.deleteItem).setVisible(true);
                }
                else{
                    activity.menu.findItem(R.id.deleteItem).setVisible(false);
                    activity.menu.findItem(R.id.savedItem).setVisible(true);
                }
/*
                try {

                    Thread.sleep(2000);
                    view.saveWebArchive("file://" + file.getAbsolutePath() + ".mhtml");

                } catch (InterruptedException e) {

                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

                }*/
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.bottom_nav_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        this.menu=menu;

        if(savedItem()){
            menu.findItem(R.id.savedItem).setVisible(false);
            menu.findItem(R.id.deleteItem).setVisible(true);
        }
        menu.findItem(R.id.navigation_home).setVisible(false);
        menu.findItem(R.id.navigation_dashboard).setVisible(false);
        menu.findItem(R.id.navigation_notifications).setVisible(false);

        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch(item.getItemId()){
            case  R.id.savedItem:
                long result=dhHelper.insertNews(title, slag, content);

                if(result>0){
                    Toast.makeText(this, "News Saved  "+result, Toast.LENGTH_SHORT).show();
                    menu.findItem(R.id.savedItem).setVisible(false);
                    menu.findItem(R.id.deleteItem).setVisible(true);
                    String filenameExternal = "archive_" + String.valueOf(result) + ".mhtml";
                    File file = new File(getExternalFilesDir(null).getAbsolutePath(), filenameExternal);
                    webView.saveWebArchive(file.toString());
                }else{
                    Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show();
                }
                //@todo saved button click
                return true;

            case R.id.deleteItem:

                if(dhHelper.deleteNewsBySlag(slag)){
                    File file = null;
                    String filenameExternal = "archive_" + String.valueOf(id) + ".mhtml";
                    file = new File(getExternalFilesDir(null).getAbsolutePath(), filenameExternal);
                    file.delete();

                    Toast.makeText(this,"News Deleted Success!" + id, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show();
                }
                super.onBackPressed();
                //@todo delete button click
                return true;

            default: return super.onOptionsItemSelected(item);
        }
    }

    //@todo just chek slag is offline offline =true
  boolean  savedItem(){
      // return  true;
       id = dhHelper.getIdBySlag(slag);
       return dhHelper.newsBySlagIsExits(slag);
    }

}