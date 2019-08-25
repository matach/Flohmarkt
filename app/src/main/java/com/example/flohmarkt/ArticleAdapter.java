package com.example.flohmarkt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ArticleAdapter extends BaseAdapter {

    private MainActivity activity;
    private List<Article> data = new ArrayList<>();
    private int layoutId;
    private LayoutInflater inflater;

    public ArticleAdapter(Context context, int layoutId, List<Article> data){
        this.activity = (MainActivity) context;
        this.data = data;
        this.layoutId = layoutId;
        this.inflater = (LayoutInflater) context
                .getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Article article = data.get(i);
        View listItem = (view == null) ? inflater.inflate(this.layoutId, null) : view;

        ((TextView) listItem.findViewById(R.id.nameText)).setText(article.getName());
        ((TextView) listItem.findViewById(R.id.priceText)).setText(String.valueOf(article.getPrice() + " €"));
        return listItem;
    }
}
