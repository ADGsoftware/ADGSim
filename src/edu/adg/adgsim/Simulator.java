package edu.adg.adgsim;

import Utils.*; //TODO: Optimise later
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Simulator implements Runnable {
    List<Person> people = new ArrayList<Person>();
    int numberOfPeopleOriginallyInfected = 0;

    //GIVEN HISTORY. THIS IS GENERATED FOR NOW, BUT WILL BE PASSED AS A PARAMETER IN TOMCAT.
    List<DayInfo> givenHistory = new ArrayList<DayInfo>();

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
        //GENERATE GIVEN HISTORY
//        givenHistory = simulate(19);
        DayInfo day1 = new DayInfo(0, 1);
        DayInfo day2 = new DayInfo(1, 2);
        DayInfo day3 = new DayInfo(2, 5);
        DayInfo day4 = new DayInfo(3, 10);
        DayInfo day5 = new DayInfo(4, 10);
        DayInfo day6 = new DayInfo(5, 11);
        DayInfo day7 = new DayInfo(6, 11);
        DayInfo day8 = new DayInfo(7, 12);
        DayInfo day9 = new DayInfo(8, 11);

        givenHistory.add(day1);
        givenHistory.add(day2);
        givenHistory.add(day3);
        givenHistory.add(day4);
        givenHistory.add(day5);
        givenHistory.add(day6);
        givenHistory.add(day7);
        givenHistory.add(day8);
        givenHistory.add(day9);



        //Step 1. Infect the people that should be originally infected.
        for (int i = 0; i < numberOfPeopleOriginallyInfected; i++) {
            Person person = people.get(Utils.randInt(0, people.size() - 1));
            while (person.getState() == infected) {
                person = people.get(Utils.randInt(0, people.size() - 1));
            }
            person.setState(infected);
        }

        System.out.println("Warming up...|    20%   |    30%   |    40%   |    50%   |    60%   |    70%   |    80%   |    90%   |COMPLETE!|");
        System.out.print("0");

        //Step 2. Simulate with this List of people.
        for (int i = 0; i < 100; i+=1) {
            System.out.print("0");
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

        System.out.print("|COMPLETE!|");
        System.out.println();


        Float[] percentageAndError = findClosestMatch(givenHistory, histories);

        System.out.println("The given history is closest to " + percentageAndError[0] + "% with offset [unknown], with an error of " + percentageAndError[1] + ".");

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
                //Step 2. Loop through each person, and, if the person is infected, add a day to his counter.
                //Step 3. Loop through each person, and, if the person is infected and his sick counter reached the limit, make the person recover.
                for (Person p : people) {
                    if (p.getState() == infected) {
                        for (Person friend : p.getFriends()) {
                            if (r(infectionConstant) && friend.getState() != recovered) {
                                friend.setState(infected);
                            }
                        }
                        p.incrementSickDays();
                        if (p.getSickDays() == recoveryDays) {
                            p.resetSickDays();
                            p.setState(recovered);
                        }
                    }
                }

                /*
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
                */

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


    /*


    DOES NOT WORK!!!!!! DO NOT USE THIS METHOD!!!!



     */
    /**
     * Returns the closest match with an offset. THIS ASSUMES THAT ALL GIVEN HISTORIES DO NOT HAVE ANY 0-SICK DAYS IN THE BEGINNING.
     * @param history - the given history
     * @param histories - the simulated histories
     * @return
     * /
    private Float[] findClosestMatchWithOffset(List<DayInfo> history, List<List<DayInfo>> histories) {
        int maxOffset = simulationDuration - history.size();

        Float[][] offsetMatches = new Float[maxOffset][2];

        for (int i = 0; i < maxOffset - 1; i++) {
            System.out.println("At offset " + i + ".");
            List<DayInfo> offsetHistory = new ArrayList<DayInfo>();

            //Populate list
            for (int j = 0; j < history.size(); j++) {
                offsetHistory.add(new DayInfo(j, 0));
            }

            //Finish list with zeroes
            for (int j = history.size(); j < simulationDuration; j++) {
                history.add(new DayInfo(j, 0));
            }

            history.get(23);

            //Shift all list values from original list by the offset amount
            for (int k = 0; k < offsetHistory.size() - i; k++) {
                offsetHistory.set(k, history.get(k + i));
            }

            Float[] matchForThisOffset = findClosestMatch(offsetHistory, histories);
            offsetMatches[i][0] = matchForThisOffset[0];
            offsetMatches[i][1] = matchForThisOffset[1];
        }

        float error = Float.MAX_VALUE;
        float percentage = 0;
        float offset = 0;
        for (int i = 0; i < offsetMatches.length; i++)  {
            if (offsetMatches[i][1] < error) {
                error = offsetMatches[i][1];
                percentage = offsetMatches[i][0];
                offset = i;
            }
        }

        Float[] errorAndPercentageAndOffset = new Float[2];
        errorAndPercentageAndOffset[0] = error;
        errorAndPercentageAndOffset[1] = percentage;
        errorAndPercentageAndOffset[2] = offset;

        return errorAndPercentageAndOffset;
    }

    */

    private Float[] findClosestMatch(List<DayInfo> history, List<List<DayInfo>> histories) {
        List<Float> percentageScores = new ArrayList<Float>();

        for (List<DayInfo> percentage : histories) {
//            System.out.println("Doing percentage number " + histories.indexOf(percentage) + ".");
            float percentageScore = 0;
            for (int i = 0; i < history.size(); i++) {
//                System.out.println("Comparing day " + i + "...");
                float valueForThisPercentage = percentage.get(i).getNumSick();
                float valueForGivenHistory = history.get(i).getNumSick();
                float difference = Utils.getDifference(valueForThisPercentage, valueForGivenHistory);
                percentageScore += difference;
            }
            percentageScores.add(percentageScore);
        }

        for (Float percentageScore : percentageScores) {
            System.out.println("Variation from " + percentageScores.indexOf(percentageScore) + ": " + percentageScore + ".");
        }

        int percentage = percentageScores.indexOf(Collections.min(percentageScores));
        Float[] percentageAndError = new Float[2];
        percentageAndError[0] = (float)percentage;
        percentageAndError[1] = Collections.min(percentageScores);

        return percentageAndError;
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

//            System.out.println("The maximum number of sick people is " + series.getMaxY() + ".");
        }

        Main.dataset = dataset;
    }

    private boolean r(int percentage) {
        return percentage > Utils.randInt(0, 100);
    }
}
