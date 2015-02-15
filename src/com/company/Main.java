package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;


public class Main {
    // Only need one instance of a Scanner of STDIN
    public final static Scanner in = new Scanner(System.in);
    final static String DATE_FORMAT = "dd-MM-yyyy";
    public final static String[] headers = {"Air_Temp:\t\t\t", "Barometric_Press:\t", "Dew_Point:\t\t\t",
            "Relative_Humidity:\t", "Wind_Dir:\t\t\t", "Wind_Gust:\t\t\t", "Wind_Speed:\t\t\t"};

    public static ArrayList<String> getData(String year, String month, String day)
        throws NumberFormatException, IOException {

        // Create URL object with txt file of appropriate year
        URL dataSource;
        try {
            dataSource = new URL("http://lpo.dt.navy.mil/data/DM/" +
                    "Environmental_Data_Deep_Moor_" + year + ".txt");
        }
        catch (MalformedURLException e) {
            return new ArrayList<String>();
        }

        // Open up reader and read one line from stream at a time
        // instead of downloading the entire file
        BufferedReader data = new BufferedReader(new InputStreamReader(
                dataSource.openStream()));

        // First line is headers, so we don't need it
        String inputLine = data.readLine();
        ArrayList<String> windData = new ArrayList<String>();
        while ((inputLine = data.readLine()) != null) {
            int month_value = Integer.parseInt(inputLine.substring(5, 7));
            int day_value = Integer.parseInt(inputLine.substring(8, 10));
            if (month_value == Integer.parseInt(month) &&
                    day_value == Integer.parseInt(day)) {
                windData.add(inputLine.substring(inputLine.indexOf('\t') + 1));
            }
            else if (month_value > Integer.parseInt(month) ||
                    (month_value == Integer.parseInt(month) && day_value >
                        Integer.parseInt(day))) {
                // Break after we processed our desired date data
                break;
            }
        }

        data.close();
        return windData;
    }

    public static ArrayList<BigDecimal> computeMean(ArrayList<String> queries) {
        ArrayList<BigDecimal> container = new ArrayList<BigDecimal>(7);

        // Maintain sum across the seven fields
        for (int i = 0; i < 7; i++) {
            container.add(i, new BigDecimal("0"));
        }

        for (String i : queries) {
            // Keeps track of where tabs are or where figures are cut off by tabs
            int tabTracker = i.indexOf('\t');

            // Keeps track of which header we are on
            int metric = 0;
            while (metric < 7) {
                if (metric == 6) {
                    container.set(6, container.get(6).add(new BigDecimal(i)));
                }
                else {
                    container.set(metric, container.get(metric).add(
                            new BigDecimal(i.substring(0, tabTracker))));
                    i = i.substring(tabTracker + 1);
                    i = i.trim();
                    tabTracker = i.indexOf('\t');
                }
                metric++;
            }
        }

        for (int i = 0; i < 7; i++) {
            container.set(i, container.get(i).divide(new BigDecimal(queries.size()),
                    2, RoundingMode.HALF_UP));
        }
        return container;
    }

    public static ArrayList<BigDecimal> computeMedian(ArrayList<String> queries) {
        // TODO: We could sort here, which is O(NlogN), but
        // we can use algorithm to reduce to O(N) by
        // maintaining a max and min heaps of equal size
        ArrayList<BigDecimal> container = new ArrayList<BigDecimal>();
        ArrayList<ArrayList<BigDecimal>> tracker = new ArrayList<ArrayList<BigDecimal>>();

        for (int i = 0; i < 7; i++) {
            container.add(i, new BigDecimal("0"));
            tracker.add(i, new ArrayList<BigDecimal>());
        }



        for (String i : queries) {
            // Keeps track of where tabs are or where figures are cut off by tabs
            int tabTracker = i.indexOf('\t');

            // Keeps track of which header we are on
            int metric = 0;
            while (metric < 7) {
                if (metric == 6) {
                    tracker.get(6).add((new BigDecimal(i)));
                }
                else {
                    tracker.get(metric).add(
                            new BigDecimal(i.substring(0, tabTracker)));
                    i = i.substring(tabTracker + 1);
                    i = i.trim();
                    tabTracker = i.indexOf('\t');
                }
                metric++;
            }
        }

        for (int i = 0; i < 7; i++) {
            Collections.sort(tracker.get(i));
            if (queries.size()%2 == 0) {
                BigDecimal lower = tracker.get(i).get(queries.size()/2);
                lower.add(tracker.get(i).get(queries.size() / 2 - 1));
                lower.divide(new BigDecimal(2), 3, RoundingMode.HALF_UP);
                container.set(i, lower);
            }
            else {
                BigDecimal lower = tracker.get(i).get(queries.size()/2);
                container.set(i, lower);
            }
        }

        return container;
    }

    public static ArrayList<ArrayList<BigDecimal>> parseData(ArrayList<String> queries) {
        ArrayList<ArrayList<BigDecimal>> data = new ArrayList<ArrayList<BigDecimal>>();
        // For respective data entry, order will be
        // Air_Temp, Barometric_Press, Dew_Point, Relative_Humidity
        // Wind_Dir, Wind_Gust, Wind_Speed
        ArrayList<BigDecimal> means = computeMean(queries);
        ArrayList<BigDecimal> medians = computeMedian(queries);

        // First entry will be means, second will be medians
        data.add(means);
        data.add(medians);
        return data;
    }

    public static boolean validDate(String date)
    {
        try {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            df.setLenient(false);
            df.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static void main(String[] args)
        throws IOException {
        ArrayList<String> navyData;

        String[] time = {"", "", ""};
        while (true) {
            System.out.println("Enter a year (2011-2014)");
            time[0] = in.nextLine();

            double year_d = Double.parseDouble(time[0]);
            if (year_d > 2014 || year_d < 2011) {
                System.out.printf("The entered year %s is invalid. Please try again with a value in" +
                        "2011-2014\n", time[0]);
                continue;
            }

            System.out.println("Enter a month");
            time[1] = in.nextLine();

            double month_d = Double.parseDouble(time[1]);
            if (month_d > 12 || month_d < 1) {
                System.out.printf("The entered month %s is invalid. Please try again with a value in " +
                        "1-12\n", time[1]);
                continue;
            }

            System.out.println("Enter a day");
            time[2] = in.nextLine();

            double day_d = Double.parseDouble(time[2]);
            if (day_d > 31 || day_d < 1) {
                System.out.printf("The entered day %s is invalid. Please try again with a value in " +
                        "1-31\n", time[2]);
                continue;
            }

            if (!validDate(time[2] + "-" + time[1] + "-" + time[0])) {
                System.out.printf("The entered date %s is invalid. Please try again\n",
                        time[2] + "-" + time[1] + "-" + time[0]);
                continue;
            }

            navyData = getData(time[0], time[1], time[2]);

            if (navyData.isEmpty()) {
                System.out.printf("Data does not exist for specified date %s\n Try another date?\n",
                        time[2] + "-" + time[1] + "-" + time[0]);
                time[0] = Main.in.nextLine();
                if (time[0].length() > 0 && time[0].toUpperCase().charAt(0) == 'Y') {
                    continue;
                }
                else {
                    break;
                }
            }
            else {
                System.out.println("Data found for date " +time[2] + "-" + time[1] + "-" + time[0] +
                        "\nProcessing now...\n");
                break;
            }
        }
        // Don't close STDIN until we get to image creation



        ArrayList<ArrayList<BigDecimal>> processed = parseData(navyData);

        ArrayList<BigDecimal> allMeans = processed.get(0);
        ArrayList<BigDecimal> allMedians = processed.get(1);

        System.out.println("The mean and median values for " + time[0] + "_"
            + time[1] + "_" + time[2] + " are:" + "\n");
        System.out.println("\t\t\t\t\t  Mean\t\t Median");
        int iter = 0;
        while (iter < 7) {
            System.out.print(headers[iter]);
            // Use of BigDecimal is awarded here, where we get accurate value
            System.out.printf("%7.2f\t\t%7.2f\n" , allMeans.get(iter).doubleValue(), allMedians.get(iter).doubleValue());
            iter++;
        }
        Grapher.visualize(processed);
    }
}
