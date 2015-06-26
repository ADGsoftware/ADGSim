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
    int simulationDuration = 25;
    int recoveryDays = 5;
    int simulationNum = 1000;

    //State values
    int susceptible = Constants.SUSCEPTIBLE;
    int infected = Constants.INFECTED;
    int recovered = Constants.RECOVERED;

    //Histories
    List<List<DayInfo>> histories = new ArrayList<List<DayInfo>>();

    public Simulator(List<Person> people, int numberOfPeopleOriginallyInfected) {
        this.people = people;
        this.numberOfPeopleOriginallyInfected = numberOfPeopleOriginallyInfected;
    }

    public void run() {
//        System.out.println(people.size());
//        for (Person person : people) {
//            System.out.println("Person " + person.getID() + " has " + person.getFriends().size() + " friends.");
//        }

        //Step 1. Infect the people that should be originally infected.
        for (int i = 0; i < numberOfPeopleOriginallyInfected; i++) {
            Person person = people.get(Utils.randInt(0, people.size() - 1));
            while (person.getState() == infected) {
                person = people.get(Utils.randInt(0, people.size() - 1));
            }
            person.setState(infected);
        }

        //Step 2. Simulate with this List of people.
        for (int i = 0; i < 100; i+=1) {
            System.out.println("Running simulation for " + i + "%...");
            List<DayInfo> history = simulate(i);
            histories.add(history);

            //Reset people
            for (Person p : people) {
                p.setState(susceptible);
            }

            //Infect the people that should be originally infected.
            for (int j = 0; j < numberOfPeopleOriginallyInfected; j++) {
                Person person = people.get(Utils.randInt(0, people.size() - 1));
                while (person.getState() == infected) {
                    person = people.get(Utils.randInt(0, people.size() - 1));
                }
                person.setState(infected);
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
    private List<DayInfo> simulate(int infectionConstant) {
        //A list of all of the simulations
        List<List<Day>> allDays = new ArrayList<List<Day>>();
        //A list of all of the days, will be the averages
        List<DayInfo> dayInfos = new ArrayList<DayInfo>();
        //Popilate array with zeroes
        for (int i = 0; i < simulationDuration; i++) {
            dayInfos.add(new DayInfo(i, 0));
        }

        for (int i = 0; i < simulationNum; i++) {
            //Create a list of days for this simulation
            List<Day> days = new ArrayList<Day>();

            while (days.size() < simulationDuration) {
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

//                System.out.println("SIMULATION " + i + ", DAY " + (days.size()) + ": " + Utils.getNumberOfPeopleSick(people) + " people are sick.");
            }

            allDays.add(days);

            //Reset people
            for (Person p : people) {
                p.setState(susceptible);
            }

            //Infect the people that should be originally infected.
            for (int j = 0; j < numberOfPeopleOriginallyInfected; j++) {
                Person person = people.get(Utils.randInt(0, people.size() - 1));
                while (person.getState() == infected) {
                    person = people.get(Utils.randInt(0, people.size() - 1));
                }
                person.setState(infected);
            }
        }


        //Averaging

        //Add all numsicks
        for (List<Day> dayList : allDays) {
            for (Day day : dayList) {
                float numberOfPeopleSickOnThisDay = Utils.getNumberOfPeopleSick(day.getPeople());
                float sumOfNumbersOfPeopleSickOnThisDay = dayInfos.get(dayList.indexOf(day)).getNumSick();
                dayInfos.get(dayList.indexOf(day)).setNumSick(numberOfPeopleSickOnThisDay + sumOfNumbersOfPeopleSickOnThisDay);
            }
        }

        //Divide by the total number of simulations
        for (DayInfo dayInfo : dayInfos) {
            float sumOfNumbersOfPeopleSickOnThisDay = dayInfo.getNumSick();
            float averageNumberOfPeopleSickOnThisDay = sumOfNumbersOfPeopleSickOnThisDay / simulationNum;
            dayInfo.setNumSick(averageNumberOfPeopleSickOnThisDay);
        }



        return dayInfos;
    }

    private void createGraph() throws IOException {
        XYSeriesCollection dataset = new XYSeriesCollection();

        for (int i = 0; i < histories.size(); i++) {
//            System.out.println("Welcome to History " + i + ".");
            List<DayInfo> history = histories.get(i);

            XYSeries series = new XYSeries(i);

            series.add(0, numberOfPeopleOriginallyInfected);

            for (DayInfo day : history) {
//                System.out.println("Welcome to day " + day.getDay() + "! There are " + day.getNumSick() + " people infected.");
                series.add(day.getDay(), day.getNumSick());
            }

            dataset.addSeries(series);

            System.out.println("The maximum number of sick people is " + series.getMaxY() + ".");
        }

        Main.dataset = dataset;
    }

    private boolean r(int percentage) {
        return percentage > Utils.randInt(0, 100);
    }
}
