package com.mesicspoark.kenan.spoark;

;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by kenmesi on 8/30/15.
 */
public class HomeFragment extends Fragment {
    View rootView;
    ListView listView;

    public static Fragment newInstance(String home) {
        Fragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("HOME", home);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.home_layout, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.homeListView);
        CustomHomeListViewAdapter customAdapter = new CustomHomeListViewAdapter(getActivity());

        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(listViewClickListener);

        return rootView;
    }

    // Create a message handling object as an anonymous class.
    private ListView.OnItemClickListener listViewClickListener = new ListView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            // Do something in response to the click
            Toast.makeText(getActivity(), "You clicked on ME!!!", Toast.LENGTH_SHORT).show();
            TextView t = (TextView) view.findViewById(R.id.level);
            t.setText("Clicked");
            Intent intent = new Intent(getActivity(), GameActivity.class);
            startActivity(intent);

        }
    };
}
