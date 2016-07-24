package com.mesicspoark.kenan.spoark;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by kenmesi on 8/30/15.
 */
public class MyGamesFragment extends Fragment {
    View rootView;

    public static Fragment newInstance(String text) {
        Fragment fragment = new MyGamesFragment();
        Bundle args = new Bundle();
        args.putString("TEXT", text);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.mygames_layout, container, false);
        TextView myGamesTextView = (TextView) rootView.findViewById(R.id.editText);
        myGamesTextView.setText(getArguments().getString("TEXT"));
        return rootView;

    }
}
