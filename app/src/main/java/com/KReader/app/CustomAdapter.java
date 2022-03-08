package com.KReader.app;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private Article[] localDataSet;
    private  RecyclerViewClickListener listener;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView textView;
        private final TextView descriptionText;


        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            descriptionText = (TextView) view.findViewById(R.id.short_description);
            textView = (TextView) view.findViewById(R.id.feed_title);
            view.setOnClickListener(this);
        }


        public TextView getTextView() {
            return textView;
        }

        public TextView getDescriptionView() {
            return descriptionText;
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }


    public CustomAdapter(ArrayList<Article> dataSet, RecyclerViewClickListener listener) {
        localDataSet = dataSet.toArray(new Article[0]);

        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.feed_item, viewGroup, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        viewHolder.getTextView().setText(localDataSet[position].title);
        viewHolder.getDescriptionView().setText(localDataSet[position].description);


    }

    @Override
    public int getItemCount() {
        return localDataSet.length;
    }

    public interface RecyclerViewClickListener{
        void onClick(View v, int position);
    }

}

