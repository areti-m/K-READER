package com.KReader.app.ui.home;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.ViewModelProvider;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.KReader.app.Article;
import com.KReader.app.CustomAdapter;
import com.KReader.app.CustomAdapterCategory;

import com.KReader.app.CustomView;
import com.KReader.app.DiaglogCustomAdapter;
import com.KReader.app.MainActivity;
import com.KReader.app.R;
import com.KReader.app.RSSParser;
import com.KReader.app.SQLiteAdapter;
import com.KReader.app.WebViewActivity;
import com.KReader.app.articale_list;
import com.KReader.app.databinding.ReadBinding;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;


import java.io.Console;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class ReadFragment extends Fragment  {

    private ReadViewModel readViewModel;
    private ReadBinding binding;
    private RecyclerView listView, feedListView;
    private GridView listViewDialog;
    private LinearLayout selectCategoryDialogView;
    private CustomAdapterCategory.RecyclerViewClickListener listener;

    private CustomAdapter.RecyclerViewClickListener articleListListener;
    LinearLayoutManager HorizontalLayout;

    private CustomAdapterCategory customAdapter;
    private CustomAdapter articleListCustomAdapter;
    BottomNavigationView navBar;
    Button button;
    ImageButton editButton;

    //@todo slagList and title
    ArrayList<String> list =  new ArrayList<String>();
    ArrayList<String> interestedCategory =  new ArrayList<String>();
    ArrayList<String> selectedCategory =  new ArrayList<String>();
    ArrayList<String> tmpCategory =  new ArrayList<String>();

    ArrayList<ArrayList<String>> slagList =new ArrayList<ArrayList<String>>();

    ArrayList<Article> articleList =  new ArrayList<Article>();



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        readViewModel =
                new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(ReadViewModel.class);
        binding = ReadBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        navBar = getActivity().findViewById(R.id.nav_view);
        button= binding.btn;
        editButton= binding.imageButton;
        feedListView= binding.feedList;

        buildPopupDialogList(false);


        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(!selectedCategory.isEmpty()){

                    new SQLiteAdapter(getContext()).insertInterestedCategory(selectedCategory);
                    getInterestedCategory();
                    getActionBar().setTitle("Read");
                    selectCategoryDialogView.setVisibility(View.GONE);
                    navBar.setVisibility(View.VISIBLE);
                    buildList();
                }else{
                    Toast.makeText(getContext(), "Please select at least one item", Toast.LENGTH_LONG).show();
                }
            }
        });


        editButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                   // buildList();
                //interestedCategory.clear();
                buildPopupDialogList(true);

            }
        });




        return root;
    }
    private ActionBar getActionBar() {
        return ((MainActivity) getActivity()).getSupportActionBar();
    }

    private void buildPopupDialogList(boolean edit) {

        getInterestedCategory();
        getData();

        if((interestedCategory.size()<1) || edit){
            selectedCategory= interestedCategory;

            selectCategoryDialogView.setVisibility(View.VISIBLE);
            navBar.setVisibility(View.GONE);


            getActionBar().setTitle("Select your interested categories..");

            listViewDialog= binding.lv;
            // Initialize a new ArrayAdapter
            /*ArrayAdapter<String> adapter = new ArrayAdapter(
                    getContext(),
                    android.R.layout.select_dialog_multichoice,
                    list
            );*/
            final DiaglogCustomAdapter adapter = new DiaglogCustomAdapter(list,interestedCategory);
            // Set the adapter for ListView
            listViewDialog.setAdapter(adapter);


            listViewDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {

                    String item = list.get(position);

                    int selectedIndex = adapter.selectedPositions.indexOf(position);

                    if (selectedIndex > -1) {
                        adapter.selectedPositions.remove(selectedIndex);
                        ((CustomView)v).display(false);

                        selectedCategory.remove(item);

                    } else {
                        adapter.selectedPositions.add(position);
                        ((CustomView)v).display(true);
                        selectedCategory.add(item);
                    }

                   System.out.println(selectedCategory);

                }
            });




        }else{
            buildList();
        }


    }

    private ArrayList<String> getInterestedCategory() {
        interestedCategory.clear();
        selectCategoryDialogView= binding.selectCategoryDialog;
        selectCategoryDialogView.setVisibility(View.GONE);
        interestedCategory.addAll(new SQLiteAdapter(getContext()).queueAllInterestedCategory());

        return interestedCategory;
    }

    private void buildList() {
        getData();
        articleList.clear();

        listener= new CustomAdapterCategory.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {

                Intent intent=new Intent(getContext(), articale_list.class);
                intent.putExtra("title", list.get(position));
                intent.putExtra("article_link", slagList.get(position));

                startActivity(intent);
                 }
        };

        ArrayList<String> tmpTitle =  new ArrayList<String>();
        ArrayList<ArrayList<String>> tmpSlagList =new ArrayList<ArrayList<String>>();

        for(int i=0;i<list.size() ;i++){
            for(String itemSelected: interestedCategory){
                if(list.get(i).contains(itemSelected)){
                    tmpTitle.add(list.get(i));
                    tmpSlagList.add( slagList.get(i));
                }
            }
        }
        list.clear();
        list.addAll(tmpTitle);
        slagList.clear();
        slagList.addAll(tmpSlagList);

        listView= binding.listView;
        LinearLayoutManager layoutManager =new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.HORIZONTAL,
                false);

        listView.setLayoutManager(layoutManager);
        customAdapter = new CustomAdapterCategory(list, listener);

        listView.setAdapter(customAdapter);
        listView.addItemDecoration(
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        buildFeed();
    }

    private void buildFeed() {

        ArrayList<String> feedLink= new ArrayList<>();

        for(ArrayList<String> listItem: slagList){
            for(String link: listItem){
                feedLink.add(link);
            }
        }



        Iterator it= feedLink.iterator();

        while (it.hasNext()){
            parseXML(it.next().toString());
        }


        articleListListener = new CustomAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent intent=new Intent(getContext(), WebViewActivity.class);
                intent.putExtra("title", articleList.get(position).title);
                intent.putExtra("link", articleList.get(position).link);
                intent.putExtra("content", articleList.get(position).description);

                startActivity(intent);
            }
        };

        // Configurando o gerenciador de layout para ser uma lista.
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        feedListView.setLayoutManager(layoutManager);





    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        buildList();
    }

    private void getData() {

        Map<String, String> map =
                new HashMap<String, String>();
        list.clear();
        slagList.clear();

        list.add("ΠΟΛΙΤΙΚΗ");
        slagList.add(new ArrayList<String>() {
            {
                add("http://www.news.gr/rss.ashx?catid=5");
            }});

        list.add("ΟΙΚΟΝΟΜΙΑ");
        slagList.add(new ArrayList<>(Arrays.asList(
            "https://www.euro2day.gr/rss.ashx?catid=124",
            "https://www.euro2day.gr/rss.ashx?catid=123",
            "http://www.news.gr/rss.ashx?catid=9",
            "https://www.euro2day.gr/rss.ashx?catid=144",
            "https://www.naftemporiki.gr/rssFeed?mode=section&id=1&atype=story",
            "https://www.euro2day.gr/rss.ashx?chiid=899001",
            "https://www.euro2day.gr/rss.ashx?catid=129",
            "https://www.euro2day.gr/rss.ashx?catid=131",
            "https://www.euro2day.gr/rss.ashx?chiid=899007",
            "https://www.euro2day.gr/rss.ashx?catid=122"
        )));

        list.add("ΚΟΙΝΩΝΙΑ");
        slagList.add(new ArrayList<String>() {
            {
                add("https://www.naftemporiki.gr/rssFeed?mode=section&id=3&atype=story");
            }});

        list.add("ΚΟΣΜΟΣ");
        slagList.add(new ArrayList<String>() {
            {
                add("https://www.euro2day.gr/rss.ashx?catid=125");
                add("https://www.naftemporiki.gr/rssFeed?mode=section&id=4&atype=story");
                add("https://www.news.gr/rss.ashx?catid=10");
            }});


        list.add("ΠΟΛΙΤΙΣΜΟΣ");
        slagList.add(new ArrayList<String>() {
            {
                add("https://www.naftemporiki.gr/rssFeed?mode=section&id=6&atype=story");
                add("https://www.euro2day.gr/rss.ashx?catid=135");
            }});


        list.add("ΑΘΛΗΤΙΣΜΟΣ");
        slagList.add(new ArrayList<String>() {
            {
                add("https://www.euro2day.gr/rss.ashx?chiid=899010");
                add("https://www.naftemporiki.gr/rssFeed?mode=section&id=5&atype=story");
            }});

        list.add("LIFE");
        slagList.add(new ArrayList<String>() {
            {
                add("https://www.euro2day.gr/rss.ashx?catid=128");
                add("https://www.news.gr/rss.ashx?catid=20");
                add("https://www.naftemporiki.gr/rssFeed?mode=section&id=7&atype=story");
                add("https://www.naftemporiki.gr/rssFeed?mode=section&id=104&atype=story");
                add("https://www.euro2day.gr/rss.ashx?chiid=899011");
                add("https://www.euro2day.gr/rss.ashx?chiid=899012");
                add("https://www.news.gr/rss.ashx?catid=15");
                add("https://www.news.gr/rss.ashx?catid=12");
                add("http://www.news.gr/rss.ashx?catid=16");
            }});

        list.add("ΑΠΟΨΕΙΣ");
        slagList.add(new ArrayList<String>() {
            {
                add("https://www.euro2day.gr/rss.ashx?catid=132");
                add("https://www.euro2day.gr/rss.ashx?catid=133");
            }});
        list.add("MULTIMEDIA");
        slagList.add(new ArrayList<String>() {
            {
                add("https://www.news.gr/rss.ashx?catid=30");
                add("https://www.euro2day.gr/rss.ashx?catid=133");
            }});
//        list.add("ΕΡΕΥΝΕΣ");
//        list.add("ΕΚΔΟΣΕΙΣ");
//        list.add("ΣΥΝΕΔΡΙΑ");

//        slagList.add(new ArrayList<>(Arrays.asList(
//                "http://www.news.gr/rss.ashx?catid=9",
//                "http://www.news.gr/rss.ashx?catid=5",
//                "http://www.news.gr/rss.ashx?catid=7"
//        )));


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
                    articleList.add(item);
                }

                articleListCustomAdapter = new CustomAdapter(articleList, articleListListener);
                feedListView.setAdapter(articleListCustomAdapter);
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