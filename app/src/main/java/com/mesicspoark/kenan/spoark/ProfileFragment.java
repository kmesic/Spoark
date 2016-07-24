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
public class ProfileFragment extends Fragment {
    View rootView;

    public static Fragment newInstance(String text) {
        Fragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("TEXT", text);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.profile_layout, container, false);
        TextView profileTextView = (TextView) rootView.findViewById(R.id.editText);
        profileTextView.setText(getArguments().getString("TEXT"));
        return rootView;

    }
}
