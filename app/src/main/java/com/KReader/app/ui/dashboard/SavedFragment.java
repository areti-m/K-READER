package com.KReader.app.ui.dashboard;

import static java.lang.String.valueOf;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.ViewModelProvider;

import com.KReader.app.Article;
import com.KReader.app.CustomAdapter;
import com.KReader.app.R;
import com.KReader.app.SQLiteAdapter;
import com.KReader.app.WebViewActivity;
import com.KReader.app.databinding.SavedBinding;

import java.util.ArrayList;

public class SavedFragment extends Fragment {

    private SavedFragmentViewModel savedFragmentViewModel;
    private SavedBinding binding;
    private RecyclerView listView;
    private CustomAdapter customAdapter;
    MenuItem fav;

    ArrayList<Article> slagList = new ArrayList<Article>();
    private CustomAdapter.RecyclerViewClickListener listener;
    ArrayList<String> list =  new ArrayList<String>();
    SQLiteAdapter dhHelper;
    public final static int REQUEST_CODE = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        savedFragmentViewModel =
                new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(SavedFragmentViewModel.class);

        binding = SavedBinding.inflate(inflater, container, false);
        dhHelper =  new SQLiteAdapter(getContext());


        View root = binding.getRoot();

        //list.add("new item");

        listView= binding.savedListView;
        ArrayList<String> tmp = new ArrayList<String>();


        buildList();


        return root;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.deleteItem:

                // Do Activity menu item stuff here
                return true;
            default:
                break;
        }

        return false;
    }

    private void buildList() {
        slagList.clear();
        list.clear();
        slagList.addAll(new SQLiteAdapter(getContext()).queueAllNews());
        for(Article item : slagList) {
            list.add(item.title);
        }

        listener= new CustomAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent intent=new Intent(getContext(), WebViewActivity.class);
                intent.putExtra("title", slagList.get(position).title);
                intent.putExtra("link", slagList.get(position).link);
                intent.putExtra("id", slagList.get(position).id);
                intent.putExtra("content", slagList.get(position).description);


                startActivityForResult(intent, REQUEST_CODE);
            }
        };

        // Configurando o gerenciador de layout para ser uma lista.
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        listView.setLayoutManager(layoutManager);

        // Adiciona o adapter que irá anexar os objetos à lista.
        // Está sendo criado com lista vazia, pois será preenchida posteriormente.
        customAdapter = new CustomAdapter(slagList, listener);
        listView.setAdapter(customAdapter);

        // Configurando um dividr entre linhas, para uma melhor visualização.


        listView.addItemDecoration(
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        buildList();
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}