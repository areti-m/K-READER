package com.KReader.app;


import android.support.constraint.solver.ArrayRow;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class DiaglogCustomAdapter extends BaseAdapter {
    private ArrayList<String> strings;
    private ArrayList<String> selectedCategory;


    public ArrayList<Integer> selectedPositions = new ArrayList<>();

    public DiaglogCustomAdapter(ArrayList<String> strings, ArrayList<String> selectedCategory) {
        this.strings=strings;
        this.selectedCategory=selectedCategory;
    }

    @Override
    public int getCount() {
        return strings.size();
    }

    @Override
    public Object getItem(int position) {
        return strings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CustomView customView = (convertView == null) ?
                new CustomView(parent.getContext()) : (CustomView) convertView;

        customView.display(strings.get(position), selectedPositions.contains(position));

        if(selectedCategory.contains(strings.get(position))){
            customView.display(true);
            selectedPositions.add(position);
        }



        return customView;
    }
}