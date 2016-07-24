package com.mesicspoark.kenan.spoark;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by kenmesi on 9/6/15.
 */
public class CustomHomeListViewAdapter extends BaseAdapter {

    private List<Games> listGames;
    private Context context;

    public CustomHomeListViewAdapter(Context context) {
        listGames = Games.getDataForList();
        this.context = context;
    }
    @Override
    public int getCount() {
        return listGames.size();
    }

    @Override
    public Games getItem(int position) {
        return listGames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView ==null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.home_list_item, parent,false);
        }
        //TextView title = (TextView) convertView.findViewById(R.id.textView1);
        TextView desc = (TextView) convertView.findViewById(R.id.sport);
        Games game = listGames.get(position);
        //title.setText("Sport");
        desc.setText(game.getSport());

        return convertView;
    }
}
