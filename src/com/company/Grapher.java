package com.company;

/**
 * Created by oniken on 2/15/15.
 */

import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Scanner;

public class Grapher {
    public static void visualize(ArrayList<ArrayList<BigDecimal>> processed) {
        // Create a simple bar graph
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int i = 0; i < 7; i++) {
            dataset.setValue(processed.get(0).get(i).doubleValue(), "Mean", Main.headers[i]);
            dataset.setValue(processed.get(1).get(i).doubleValue(), "Median", Main.headers[i]);
        }
        JFreeChart chart = ChartFactory.createBarChart
                ("Means and Medians of Lake Pend Oreille Metrics",
                        "Metrics", "Units", dataset,
                        PlotOrientation.VERTICAL, true, true, false);


        System.out.println("\nSave file as .png file (Y/N)?");
        String userInput = Main.in.nextLine();
        while (true) {
            if (userInput.length() > 0 && userInput.toUpperCase().charAt(0) == 'Y') {
                try {
                    System.out.println("Specify directory to save .png file:");
                    userInput = Main.in.nextLine();
                    userInput = userInput.trim();
                    if (userInput.length() > 0 &&
                            userInput.charAt(userInput.length() - 1) == '/') {
                        userInput = userInput.substring(0, userInput.length() - 1);
                    }
                    ChartUtilities.saveChartAsPNG(new File(userInput + "/Lake_Pend_Oreille_Metrics_bar.png"),
                            chart, 1200, 800);
                    System.out.println("Bar graph successfully created as " +
                            userInput + "/Lake_Pend_Oreille_Metrics_bar.png");
                    break;
                } catch (Exception e) {
                    System.out.println("Problem occurred creating chart.\nTry again (Y/N)?");
                    userInput = Main.in.nextLine();
                    if (userInput.length() > 0 && userInput.toUpperCase().charAt(0) == 'Y') {
                        continue;
                    }
                    else {
                        break;
                    }
                }
            }
            else {
                break;
            }
        }
        // Close STDIN when we get here
        Main.in.close();
    }
}
