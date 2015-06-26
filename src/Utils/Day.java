package Utils;

import edu.adg.adgsim.Person;

import java.util.List;

public class Day {
    //A day is a list of people.

    private List<Person> people;

    public Day(List<Person> people) {
        this.people = people;
    }

    public List<Person> getPeople() {
        return people;
    }
}
