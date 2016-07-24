package com.mesicspoark.kenan.spoark;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kenmesi on 9/7/15.
 */
public class Games {
    private String sport;
    private int numberOfPlayers;

    public Games(String sport, int numOfPlayers) {
        this.sport = sport;
        this.numberOfPlayers = numOfPlayers;
    }

    public boolean setSport(String sport) {
        this.sport = sport;
        return true;
    }

    public String getSport() {
        return this.sport;
    }

    public boolean setNumOfPlayers(int num) {
        this.numberOfPlayers = num;
        return true;
    }

    public static List<Games> getDataForList() {
        List<Games> list = new ArrayList<Games>();
        for(int i = 0; i < 20; i++) {
            Games newGame = new Games("Soccer", 20);
            list.add(newGame);
        }
        return list;
     }
}
