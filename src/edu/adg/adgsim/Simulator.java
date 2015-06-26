package edu.adg.adgsim;

import Utils.*; //TODO: Optimise later
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Simulator implements Runnable {
    List<Person> people = new ArrayList<Person>();
    int numberOfPeopleOriginallyInfected = 0;

    //Values TODO: Add as parameters
    int simulationDuration = 100;
    int recoveryDays = 5;

    //State values
    int susceptible = Constants.SUSCEPTIBLE;
    int infected = Constants.INFECTED;
    int recovered = Constants.RECOVERED;

    //Histories
    List<List<Day>> histories = new ArrayList<List<Day>>();

    public Simulator(List<Person> people, int numberOfPeopleOriginallyInfected) {
        this.people = people;
        this.numberOfPeopleOriginallyInfected = numberOfPeopleOriginallyInfected;
    }

    public void run() {
        System.out.println(people.size());
        for (Person person : people) {
            System.out.println("Person " + person.getID() + " has " + person.getFriends().size() + " friends.");
        }

        //Step 1. Infect the people that should be originally infected.
        for (int i = 0; i < numberOfPeopleOriginallyInfected; i++) {
            Person person = people.get(Utils.randInt(0, people.size() - 1));
            while (person.getState() == infected) {
                person = people.get(Utils.randInt(0, people.size() - 1));
            }
            person.setState(infected);
        }

        //Step 2. Simulate with this List of people.
        for (int i = 80; i < 100; i++) {
            List<Day> history = simulate(i);
            histories.add(history);

            //Reset people
            for (Person p : people) {
                p.setState(susceptible);
            }
        }


        //Make chart
        try {
            createGraph();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Simulation method.
     * @param infectionConstant - percentage of infecting a friend
     * @return - A history of the simulation
     */
    private List<Day> simulate(int infectionConstant) {
        List<Day> days = new ArrayList<Day>();

        while(days.size() < simulationDuration) {
            //Step 1. Loop through each person, and, if the person is infected, randomly infect non-recovered friends.
            for (Person p : people) {
                if (p.getState() == infected) {
                    for (Person friend : p.getFriends()) {
                        if (r(infectionConstant) && friend.getState() != recovered) {
                            friend.setState(infected);
                        }
                    }
                }
            }

            //Step 2. Loop through each person, and, if the person is sick, add a day to his counter.
            for (Person p : people) {
                if (p.getState() == infected) {
                    p.incrementSickDays();
                }
            }

            //Step 3. Loop through each person, and, if the person is sick and his sick counter reached the limit, make the person recover.
            for (Person p : people) {
                if (p.getState() == infected) {
                    if (p.getSickDays() == recoveryDays) {
                        p.resetSickDays();
                        p.setState(recovered);
                    }
                }
            }

            //Step 4. Save the people into a day.
            List<Person> peopleToSave = new ArrayList<Person>();
            for (Person p : people) {
                Person newPerson = new Person(p.getID());
                newPerson.setFriends(p.getFriends());
                newPerson.setState(p.getState());
                peopleToSave.add(newPerson);
            }
            days.add(new Day(peopleToSave));
        }

        return days;
    }

    private void createGraph() throws IOException {
        XYSeriesCollection dataset = new XYSeriesCollection();

        for (int i = 0; i < histories.size(); i++) {
            List<Day> history = histories.get(i);

            XYSeries series = new XYSeries(i);
            series.setDescription("Percentage 1");

            for (Day day : history) {
                series.add(history.indexOf(day), Utils.getNumberOfPeopleSick(day.getPeople()));
            }

            dataset.addSeries(series);
        }

        Main.dataset = dataset;
    }

    private boolean r(int percentage) {
        return percentage > Utils.randInt(0, 100);
    }
}
