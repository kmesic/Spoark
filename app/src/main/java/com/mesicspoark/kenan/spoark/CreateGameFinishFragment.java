package com.mesicspoark.kenan.spoark;

import android.content.Intent;
import android.location.Location;
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


/**
 * Created by kenmesi on 8/30/15.
 */
public class CreateGameFinishFragment extends Fragment {

    private static final String LOCATION_KEY = "LOCATION";
    private static final String SPORT_KEY = "SPORT";

    private View rootView;
    private String sport;
    private Location location;
    private ListView listView;

    public static Fragment newInstance(String sport, String latitude, String longitude) {
        Fragment fragment = new CreateGameFinishFragment();
        Bundle args = new Bundle();
        args.putString("SPORT", sport);
        args.putStringArray("LOCATION", new String[]{latitude, longitude});
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.finish_create_game, container, false);
        String location[] = getArguments().getStringArray(LOCATION_KEY);
        this.location = new Location("location");
        this.location.setLatitude(Double.parseDouble(location[0]));
        this.location.setLongitude(Double.parseDouble(location[1]));
        sport = getArguments().getString(SPORT_KEY);

        return rootView;
    }
}
