/*
Copyright (c) 2011 Carlos Morcillo Suarez

This file is part of ManhattanPlotter.

ManhattanPLotter is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

ManhattanPLotter is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
*/
package manhattanplotter;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.ImageIO;

/**
 *
 * @author WS113854
 */
public class QQPlot {

    Experiment experiment;
    double[] orderedLog10ObservedValues;
    double[] orderedLog10ExpectedValues;

    double maxObservedLogValue, maxExpectedLogValue;
    String label = "";
    
    // -- confidence interval variables
    boolean confidenceInterval = false;
    double[] maxConfidenceInterval;
    double[] minConfidenceInterval;
    int  numberOfCIMarkers;
    int CI_PERMUTATIONS =  100;

    // -- graphical variables
    int xMargin = 60;
    int yMargin = 60;
    int IMAGE_WIDTH = 900;
    int IMAGE_HEIGHT = 900;
    BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);

    // constructor
    void QQPlot() {

    }

    // methods
    void setExperiment(Experiment experiment){
        this.experiment = experiment;

        // Creates ordered array with log10 observed values
        orderedLog10ObservedValues = new double[experiment.tests.length];
        for(int i=0; i<experiment.tests.length;i++){
            orderedLog10ObservedValues[i] = experiment.tests[i].logPValue;
        }
        Arrays.sort(orderedLog10ObservedValues);
        maxObservedLogValue = orderedLog10ObservedValues[orderedLog10ObservedValues.length-1];

        // Creates ordered array with log10 exepected values
        orderedLog10ExpectedValues = new double[experiment.tests.length];
        for(int i=experiment.tests.length;i>0;i--){
           orderedLog10ExpectedValues[experiment.tests.length-i] =
                   -Math.log10((double)i/(experiment.tests.length+1));
        }
        maxExpectedLogValue = orderedLog10ExpectedValues[orderedLog10ExpectedValues.length-1];

    }

    void drawPlot() {
        int xIncrement = (IMAGE_WIDTH-(xMargin*2))/(int)(maxExpectedLogValue+1);
        int yIncrement = (IMAGE_HEIGHT-(yMargin*2))/(int)(maxObservedLogValue+1);
        Graphics g = image.getGraphics();

        // Cleans image
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());

        // Draws axis lines
        g.setColor(Color.black);
        g.drawLine(xMargin, IMAGE_HEIGHT-yMargin, IMAGE_WIDTH-xMargin,IMAGE_HEIGHT-yMargin);
        g.drawLine(xMargin, IMAGE_HEIGHT-yMargin, xMargin,yMargin);

        // -- title
        g.setColor(Color.blue);
        g.setFont(new Font("arial", 1, 35));
        g.drawString(label, IMAGE_WIDTH/7, 40);

        // Draws confidence interval lines
        if (confidenceInterval) {
            g.setColor(Color.red);
            int previousMinXPos = xMargin;
            int previousMinYPos = IMAGE_HEIGHT - yMargin;
            int previousMaxXPos = xMargin;
            int previousMaxYPos = IMAGE_HEIGHT - yMargin;

            for (int i = 0; i < orderedLog10ObservedValues.length; i++) {
                int xPosition = xMargin + ((int) (orderedLog10ExpectedValues[i] * xIncrement));
                int yPosition = IMAGE_HEIGHT - (yMargin + ((int) (minConfidenceInterval[i] * yIncrement)));

                g.drawLine(previousMinXPos, previousMinYPos, xPosition, yPosition);
                previousMinXPos = xPosition;
                previousMinYPos = yPosition;

                yPosition = IMAGE_HEIGHT - (yMargin + ((int) (maxConfidenceInterval[i] * yIncrement)));
                g.drawLine(previousMaxXPos, previousMaxYPos, xPosition, yPosition);
                previousMaxXPos = xPosition;
                previousMaxYPos = yPosition;
            }
        }

        // Draws dots
        int dotRadius = 4;
        g.setColor(Color.black);
        for(int i=0; i<orderedLog10ObservedValues.length;i++){
            int xPosition = xMargin + ((int)(orderedLog10ExpectedValues[i]*xIncrement));
            int yPosition = IMAGE_HEIGHT - (yMargin + ((int)(orderedLog10ObservedValues[i]*yIncrement)));
            g.fillOval(xPosition-dotRadius, yPosition-dotRadius, dotRadius*2, dotRadius*2);
        }

        // --draw y axis scale
        // ----Calculates interval between labels in y axis
        int yInterval = 1;
        int verticalMaxValue = (int)maxObservedLogValue+1;

        if(verticalMaxValue > 20){
            yInterval = 5;
        }
        if(verticalMaxValue > 100){
            yInterval = 50;
        }
        if(verticalMaxValue > 1000){
            yInterval = verticalMaxValue;
        }

        g.setColor(Color.BLACK);
        for (int i = 0; i <= verticalMaxValue; i=i+yInterval) {
            int yPos= IMAGE_HEIGHT-(yMargin + (int)(((float)i / verticalMaxValue) * (IMAGE_HEIGHT - 2 * yMargin)));
            g.fillRect(45, yPos, 15, 3);
            g.setFont(new Font("courier", 1, 30));
            String value = String.valueOf(i);
            if(value.length()==1){
                value = " "+value;
            }
            g.drawString(value,5 , yPos+10);
        }


        // --draw x axis scale
        // ----Calculates interval between labels in x axis
        int xInterval = 1;
        int horizontalMaxValue = (int)maxExpectedLogValue+1;

        if(horizontalMaxValue > 20){
            xInterval = 5;
        }
        if(horizontalMaxValue > 100){
            xInterval = 50;
        }
        if(horizontalMaxValue > 1000){
            xInterval = verticalMaxValue;
        }

        g.setColor(Color.BLACK);
        for (int i = 0; i <= horizontalMaxValue; i=i+xInterval) {
            int xPos= (xMargin + (int)(((float)i / horizontalMaxValue) * (IMAGE_WIDTH - 2 * xMargin)));
            g.fillRect(xPos, IMAGE_HEIGHT-yMargin, 3, 15);
            g.setFont(new Font("courier", 1, 30));
            String value = String.valueOf(i);
            if(value.length()==1){
                value = " "+value;
            }
            g.drawString(value,xPos-15 , IMAGE_HEIGHT-yMargin+45);
        }

        // Draws identity line
        int endLineValue = horizontalMaxValue < verticalMaxValue ? horizontalMaxValue : verticalMaxValue;
        g.drawLine(xMargin,
                IMAGE_HEIGHT - yMargin,
                xMargin + ((int) (endLineValue * xIncrement)),
                IMAGE_HEIGHT - (yMargin + ((int) (endLineValue * yIncrement))));
    }

    void setLabel(String label) {
        this.label = label;
    }

    void activateConfidenceInterval(){
        // don't calculate if there is no experiment defined
        if(experiment == null){
            confidenceInterval = false;
            return;
        }

        // don't calculate if there are no markers in experiment
        if(experiment.tests.length == 0){
            confidenceInterval = false;
            return;
        }

        // don't need to calculate again if it is calculated with the right
        // number of markers
        //if(confidenceInterval && numberOfCIMarkers == experiment.tests.length){
        //    return;
        //}

        // calculates empirically confidence interval
        confidenceInterval = false;
        numberOfCIMarkers = experiment.tests.length;
        maxConfidenceInterval = new double[numberOfCIMarkers];
        minConfidenceInterval = new double[numberOfCIMarkers];

        double[][] randomValues = new double[CI_PERMUTATIONS][numberOfCIMarkers];

        // generates random p-values for markers a <permutation> number of times
        // order values of each permutation and records all values to a matrix
        for(int permutation=0;permutation<CI_PERMUTATIONS;permutation++){
            double randomValuesOfThisPermutation[] = new double[numberOfCIMarkers];

            // generates random p-values for this permutation
            for(int i = 0; i < numberOfCIMarkers;i++){
               randomValuesOfThisPermutation[i] = -Math.log10(Math.random());
            }

            // orders p-values
            Arrays.sort(randomValuesOfThisPermutation);

            // adds p-values to matrix
            randomValues[permutation] = randomValuesOfThisPermutation;

        }

        // Takes all values generated in every position in all permutations
        // order them and selects those in 5th and 95th centil
        double[] orderedValuesForThisMarkerPosition = new double[CI_PERMUTATIONS];
        for(int i = 0; i < numberOfCIMarkers;i++){
           for(int permutation=0;permutation<CI_PERMUTATIONS;permutation++){
              orderedValuesForThisMarkerPosition[permutation] = randomValues[permutation][i];
              Arrays.sort(orderedValuesForThisMarkerPosition);
           }
           maxConfidenceInterval[i] = orderedValuesForThisMarkerPosition[(CI_PERMUTATIONS/100)*95];
           minConfidenceInterval[i] = orderedValuesForThisMarkerPosition[(CI_PERMUTATIONS/100)*5];
        }

       confidenceInterval = true;
       
       // Cleans matrix to liberate memory
       randomValues = new double[0][0];
    }

    void savePlot(String outputFileName) {
        File fileToSave = new File(outputFileName + ".png");
        try {
            // Save as PNG
            ImageIO.write(image, "png", fileToSave);

        } catch (IOException IOe) {
        }
    }
}
