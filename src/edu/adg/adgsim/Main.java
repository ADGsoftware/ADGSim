package edu.adg.adgsim;

import java.io.File;
import java.io.IOException;
import java.util.List;

import Exceptions.InfiniteLoopException;
import Exceptions.NotYetSupportedException;
import Exceptions.ObsoleteException;
import Utils.*;
import org.jfree.data.xy.XYSeriesCollection;

public class Main {

    //Random global graph variables that are here just for testing; will be removed when this will be on TomCat
    public static XYSeriesCollection dataset = new XYSeriesCollection();



    public static void main(String[] args) {
        int numberOfPeople = 100;
        int minFriends = 2;
        int maxFriends = 5;
        int numberOfPeopleOriginallyInfected = 1;
        try {
            new Main().init(numberOfPeople, minFriends, maxFriends);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init(int numberOfPeople, int minFriends, int maxFriends) throws NotYetSupportedException, InfiniteLoopException, ObsoleteException, InterruptedException, IOException {
        System.out.println("Hello, world!");
        List<Person> people = Utils.createSimulatedData(numberOfPeople, Constants.BARABÀSI_ALBERT, new Range(minFriends, maxFriends)); //Well, what can I say? You better support unicode.
        Thread thread = new Thread(new Simulator(people, 1));
        thread.setName("Simulator");
        thread.start();
        thread.join();

        File lineChart = Utils.makeChart(dataset, "graph", "Title", "x-axis", "y-axis");
    }
}
