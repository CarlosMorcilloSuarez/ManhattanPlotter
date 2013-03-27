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

    void savePlot(String outputFileName) {
        File fileToSave = new File(outputFileName + ".png");
        try {
            // Save as PNG
            ImageIO.write(image, "png", fileToSave);

        } catch (IOException IOe) {
        }
    }
}
