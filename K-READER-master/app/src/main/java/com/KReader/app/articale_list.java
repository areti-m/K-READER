package com.KReader.app;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.KReader.app.ui.home.ReadFragment;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class articale_list extends AppCompatActivity {


    private RecyclerView listView;
    private CustomAdapter customAdapter;
    ArrayList<Article> list =  new ArrayList<Article>();
    private CustomAdapter.RecyclerViewClickListener listener;
    private static final String ns = null;

    ReadFragment pubRead= new ReadFragment();

    private static final String TAG_TITLE = "title";
    private static final String TAG_LINK = "link";
    private static final String TAG_PUB_DATE = "pubDate";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_articale_list);

        listView= findViewById(R.id.sub_list);


        String title= "";
        String pos="";

        Bundle extra= getIntent().getExtras();

        if(extra!=null){
            title= extra.getString("title");
        }

        setTitle(title);

        listener = new CustomAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent intent=new Intent(articale_list.this, WebViewActivity.class);
                intent.putExtra("title", list.get(position).title);
                intent.putExtra("link", list.get(position).link);
                intent.putExtra("content", list.get(position).description);

                startActivity(intent);
            }
        };

        // Configurando o gerenciador de layout para ser uma lista.
        LinearLayoutManager layoutManager = new LinearLayoutManager(articale_list.this);
        listView.setLayoutManager(layoutManager);

        // Adiciona o adapter que irá anexar os objetos à lista.
        // Está sendo criado com lista vazia, pois será preenchida posteriormente.
        customAdapter = new CustomAdapter(list, listener);
        listView.setAdapter(customAdapter);

        // Configurando um dividr entre linhas, para uma melhor visualização.
        listView.addItemDecoration(
                new DividerItemDecoration(articale_list.this, DividerItemDecoration.VERTICAL));


        if(extra!=null){
            Iterator it = extra.getStringArrayList("article_link").iterator();
            while (it.hasNext()){
                parseXML(it.next().toString());
            }
        }
    }
    public void parseXML(String url){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
                Log.d("httpRequestStarted","Call Started");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                RSSParser rssParser = new RSSParser();
                List<Article> rssItems = new ArrayList<>();
                rssItems = rssParser.getRSSFeedItems(new String(responseBody, StandardCharsets.UTF_8));
                for (final Article item : rssItems) {
                    if (item.link.toString().equals(""))
                        break;
                    // adding each child node to HashMap key => value
                    String givenDateString = item.pubdate.trim();
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
                    try {
                        Date mDate = sdf.parse(givenDateString);
                        SimpleDateFormat sdf2 = new SimpleDateFormat("EEEE, dd MMMM yyyy - hh:mm a", Locale.US);
                        item.pubdate = sdf2.format(mDate);

                    } catch (ParseException e) {
                        e.printStackTrace();

                    }
                    // adding HashList to ArrayList
                    list.add(item);
                }

                customAdapter = new CustomAdapter(list, listener);
                listView.setAdapter(customAdapter);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.d("httpRequestError",new String(errorResponse, StandardCharsets.UTF_8));

            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }



}