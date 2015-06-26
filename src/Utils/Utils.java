package Utils;

import Exceptions.InfiniteLoopException;
import Exceptions.NotYetSupportedException;
import Exceptions.ObsoleteException;
import edu.adg.adgsim.Person;
import org.graphstream.algorithm.generator.BarabasiAlbertGenerator;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

public class Utils {
    /**
     * Creates a network of people.
     * @param peopleNum - the number of people in the network
     * @param networkType - the type of network generated
     * @param minmax - (optional) the range for friends for a random or small-world network
     * @return the people in a network
     * @throws Exceptions.NotYetSupportedException - don't try to generate a non existent network - it won't work.
     */
    public static List<Person> createSimulatedData(int peopleNum, int networkType, Range... minmax) throws NotYetSupportedException, InfiniteLoopException, ObsoleteException {
        List<Person> people = new ArrayList<Person>();
        for (int i = 0; i < peopleNum; i++) {
            people.add(new Person(i));
        }

        if (networkType == Constants.SCALE_FREE) {
            Graph graph = new SingleGraph("Barabàsi-Albert");
            // Between 1 and 3 new links per node added.
            Generator gen = new BarabasiAlbertGenerator(3);
            gen.addSink(graph);
            gen.begin();
            for(int i = 0; i < peopleNum; i++) {
                gen.nextEvents();
            }
            gen.end();
            //graph.display();

            for (int i = 0; i < peopleNum; i++) {
                Node n = graph.getNode(i);
                Collection<Edge> connections = n.getEdgeSet();
                for (Edge connection : connections) {
                    people.get(i).befriend(people.get(connection.getNode1().getIndex()));
                    people.get(connection.getNode1().getIndex()).befriend(people.get(i));
                }
            }




        } else if (networkType == Constants.RANDOM) {
            //WARNING: THIS RANDOM NETWORK GENERATOR IS OBSOLETE, AND OF NO USE IN THE REAL PROGRAM.

            throw new ObsoleteException("The random network is obsolete in this context! Everything will be simulated via Barabàsi-Albert.");

            /*

            int min = minmax[0].getMin();
            int max = minmax[0].getMax();

            //Give each person a number of destined friends
            for (Person p : people) {
                int destinedFriends = randInt(minmax[0]);
                p.setDestinedFriends(destinedFriends);
                System.out.println("Person " + p.getID() + " has " + destinedFriends + " destined friends.");
            }

            for (Person p : people) { //For each person
                System.out.println("Let's take a closer look at person " + p.getID() + ".");

                int numberOfFriends = 0;
                if(p.getFriends() != null) {
                    numberOfFriends = p.getFriends().size();
                }

                System.out.println("At this point, he has " + numberOfFriends + " friends.");

                BigInteger c = new BigInteger("0");

                //While the person does not have his destined amount of friends, add random friends (but from people who do not already have their destined amount of friends)
                while (numberOfFriends < p.getDestinedFriends()) {
                    c = c.add(new BigInteger("1")); //c++
//                    System.out.println("Beginning of Destiny Loop, because " + numberOfFriends + " < " + p.getDestinedFriends() + ".");
                    if (c.compareTo(Constants.ERROR_CONSTANT) == 1) { //Some error
                        throw new InfiniteLoopException("You got into an infinite loop! This is likely caused by the fact that there are no more people left to befriend by person " + p.getID() + ".");
                    }
                    List<Integer> doNotBefriend = p.getFriendIDs();
//                    System.out.println("He, at this point, has " + doNotBefriend.size() + " friends that he cannot befriend again.");
                    doNotBefriend.add(p.getID());
                    System.out.print("Person " + p.getID() + " cannot befriend ");
                    for (Integer i : doNotBefriend) {System.out.print(i + ", ");}
                    System.out.println();

                    Person toBefriend = people.get(randInt(0, people.size() - 1));
                    if (doNotBefriend.contains(toBefriend.getID())) {
                        continue;
                    }
                    System.out.println("The person that he will befriend is " + toBefriend.getID() + ".");

                    if (toBefriend.getDestinedFriends() != toBefriend.getFriends().size()) {
                        p.befriend(toBefriend);
                        toBefriend.befriend(p);
                    }

                    //Update number of friends
                    if (p.getFriends() != null) {
                        numberOfFriends = p.getFriends().size();
                    }
                }

                System.out.println("Person " + p.getID() + " actually got " + p.getFriends().size() + " friends.");
            }

            */

        } else if (networkType == Constants.SMALL_WORLD) {
            //This network will also not be used for the simulation.
            throw new ObsoleteException("The small-world network is obsolete in this context! Everything will be simulated via Barabàsi-Albert.");

        } else {
            throw new NotYetSupportedException("Network type " + networkType + " does not exist: Please enter a value from 0 to 2.");
        }

        return people;
    }





    //MISC. FUNCTIONS

    public static int randInt(int min, int max) {
        Random rand = new Random();

        int randInt = rand.nextInt((max - min) + 1) + min;

        return randInt;
    }

    /**
     * Returns a pseudo-random number in a Range.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param range - the range in which to generate a random integer
     * @return Integer between min and max of the range, inclusive.
     * @see Range
     */
    public static int randInt(Range range) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive

        return rand.nextInt((range.getMax() - range.getMin()) + 1) + range.getMin();
    }

    public static int getNumberOfPeopleSick(List<Person> people) {
        int n = 0;
        for (Person p : people)
            if (p.getState() == Constants.INFECTED) n++;
        return n;
    }


    //JFREECHART

    /**
     * Creates a chart with the given parameters.
     * @param dataset - The dataset with the points.
     * @param filename - The name of the file, sans extension.
     * @param title - The title of the chart.
     * @param xAxis - The label for the x-axis
     * @param yAxis - The label of the y-axis
     * @return - The file with the chart
     * @throws IOException
     */
    public static File makeChart(XYSeriesCollection dataset, String filename, String title, String xAxis, String yAxis) throws IOException {
        System.out.println("1");
        JFreeChart lineChartObject = ChartFactory.createXYLineChart(title, xAxis, yAxis, dataset, PlotOrientation.VERTICAL, true, true, false);

        System.out.println("2");

        int width = 640;
        int height = 480;
        File lineChart = new File("C:\\Users\\Irochka\\workspace\\and\\ADGSim\\" + filename + ".png");

        System.out.println("3");

        ChartUtilities.saveChartAsPNG(lineChart, lineChartObject, width, height);

        System.out.println("4");

        return lineChart;
    }
}
