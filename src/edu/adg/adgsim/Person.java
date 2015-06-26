package edu.adg.adgsim;

import java.util.ArrayList;
import java.util.List;

public class Person {
    private int ID;
    private List<Person> friends = new ArrayList<Person>();
    private int destinedFriends;

    private int state;
    private int sickDays;

    public Person(int ID) {
        this.ID = ID;
    }

    //Getters

    public int getID() {
        return ID;
    }

    public List<Person> getFriends() {
        return friends;
    }

    public List<Integer> getFriendIDs() {
        List<Integer> friendIDs = new ArrayList<Integer>();
        if (friends != null) {
            for (Person p : friends) {
                friendIDs.add(p.getID());
            }
        } else {
            friendIDs = new ArrayList<Integer>();
        }
        return friendIDs;
    }

    public int getDestinedFriends() {
        return destinedFriends;
    }

    public int getState() {
        return state;
    }

    public int getSickDays() {
        return sickDays;
    }

    //Setters

    public void setFriends(List<Person> friends) {
        this.friends = friends;
    }

    public void befriend(Person person) {
        if (!friends.contains(person)) {
            this.friends.add(person);
        }
    }

    public void setDestinedFriends(int destinedFriends) {
        this.destinedFriends = destinedFriends;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void incrementSickDays() {
        this.sickDays = sickDays + 1;
    }

    public void resetSickDays() {
        this.sickDays = 0;
    }
}
