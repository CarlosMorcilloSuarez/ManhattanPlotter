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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 *
 * @author WS113854
 */
public class ManhattanPlot {

    // variables
    Experiment experiment;
    NumberFormat form = NumberFormat.getIntegerInstance();
    GenomeInfo genomeInfo;
    int mode = 0, chrom = 0, referencePoint = 0, window = 0;
    int YScale = 0;
    String label = "";

    // --static constants
    static final int ALL_DATA = 0;
    static final int BY_CHROMOSOME = 1;
    static final int BY_POSITION = 2;

    // -- graphical variables
    int margin = 70;
    int IMAGE_WIDTH = 2100;
    int IMAGE_HEIGHT = 900;
    BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
    long coordinateInitGraph = 0;
    long coordinateEndGraph = 0;
    int verticalMaxValue = 0;
    int dotSize = 7;

    // constructor
    ManhattanPlot(Experiment experiment){
        this.experiment = experiment;
        this.genomeInfo = experiment.genomeInfo;
    }

    //methods
    void setMode(int mode, int chrom, int referencePoint, int window) {
        this.mode = mode;
        this.chrom = chrom;
        this.referencePoint = referencePoint;
        this.window = window;
        if (mode == ALL_DATA) {
            coordinateInitGraph = 0;
            coordinateEndGraph = genomeInfo.endCoordinate;
        } else if (mode == BY_CHROMOSOME) {
            coordinateInitGraph = genomeInfo.chromosome[chrom].initCoordinate;
            coordinateEndGraph = genomeInfo.chromosome[chrom].initCoordinate + genomeInfo.chromosome[chrom].length;
        } else if (mode == BY_POSITION) {
            long coordinateReference = genomeInfo.chromosome[chrom].initCoordinate + referencePoint;
            coordinateInitGraph = coordinateReference - window;
            coordinateEndGraph = coordinateReference + window;
        }
    }


    void setYScale(int YScale){
        this.YScale = YScale;
    }

    void setDotSize(int dotSize){
        this.dotSize = dotSize;
    }

    int getYScale(){
        return(verticalMaxValue);
    }

    void setLabel(String label){
        this.label = label;
    }
    


    void drawPlot() {
        // draws graphic
        long ppbbCovered = coordinateEndGraph - coordinateInitGraph;


        Graphics g = image.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        ArrayList<Integer> markedTests = new ArrayList<Integer>();

        // Calculates y scale
        verticalMaxValue = experiment.maxLogPValue < 10?10:(int)experiment.maxLogPValue+1;
        verticalMaxValue = YScale !=0?YScale:verticalMaxValue;
        
        // -- plot points included in the graph       
        for (int i = 0; i < experiment.tests.length; i++) {

            // excludes all tests outside current coordinates  
            if (experiment.tests[i].coordinate < coordinateInitGraph || experiment.tests[i].coordinate > coordinateEndGraph) {
                continue;
            }


            // plots unmarked tests and lists marked tests to be plotted at the end

            if (experiment.tests[i].type == 1) {
                markedTests.add(i);
            } else {
                g.setColor(genomeInfo.chromosome[experiment.tests[i].chromosome].color);
                g.fillOval(margin + (int) (((double) experiment.tests[i].coordinate - coordinateInitGraph) / ppbbCovered * (IMAGE_WIDTH - 2 * margin)),
                        IMAGE_HEIGHT - margin - ((int) (experiment.tests[i].logPValue / verticalMaxValue * (IMAGE_HEIGHT - 2 * margin))),
                        dotSize, dotSize);
            }
            

            
        }

        // plots marked tests
        for (int testIndex = 0; testIndex < markedTests.size(); testIndex++) {
            int m = markedTests.get(testIndex);
            g.setColor(Color.RED);
            g.fillOval(margin + (int) (((double) experiment.tests[m].coordinate - coordinateInitGraph) / ppbbCovered * (IMAGE_WIDTH - 2 * margin)),
                    IMAGE_HEIGHT - margin - ((int) (experiment.tests[m].logPValue / verticalMaxValue * (IMAGE_HEIGHT - 2 * margin))),
                    dotSize*2, dotSize*2);
        }      
        
        // --draw y axis scale
        // ----Calculates interval between labels in y axis
        int interval = 1;
        if(verticalMaxValue > 20){
            interval = 5;
        }
        if(verticalMaxValue > 100){
            interval = 50;
        }
        if(verticalMaxValue > 1000){
            interval = verticalMaxValue;
        }

        g.setColor(Color.BLACK);
        for (int i = 0; i <= verticalMaxValue; i=i+interval) {
            int yPos= IMAGE_HEIGHT-(margin + (int)(((float)i / verticalMaxValue) * (IMAGE_HEIGHT - 2 * margin)));
            g.fillRect(45, yPos, 15, 3);
            g.setFont(new Font("courier", 1, 30));
            String value = String.valueOf(i);
            if(value.length()==1){
                value = " "+value;
            }
            g.drawString(value,5 , yPos+10);
        }
        
        // -- title
        g.setFont(new Font("arial", 1, 40));
        g.drawString(label, IMAGE_WIDTH/6, 40);
        
        
        // -- draw chromosome names or bottom position
        g.setFont(new Font("arial", 0, 30));
        if (mode == BY_POSITION) {
          g.setColor(Color.RED);
          g.drawString(genomeInfo.chromosome[chrom].name,IMAGE_WIDTH/6,IMAGE_HEIGHT - margin / 4);
          g.setColor(Color.BLUE);
          g.drawString(form.format(referencePoint),IMAGE_WIDTH/6+100,IMAGE_HEIGHT - margin / 4);
          g.setColor(Color.BLACK);
          g.drawString("+/- : "+form.format(window),IMAGE_WIDTH/6+500,IMAGE_HEIGHT - margin / 4);
        } else {           
            for (int i = 1; i <= 24; i++) {
                g.setColor(genomeInfo.chromosome[i].color);
                g.drawString(genomeInfo.chromosome[i].name,
                        margin + (int) (((double) (genomeInfo.chromosome[i].nameCoordinate - coordinateInitGraph)) / ppbbCovered * (IMAGE_WIDTH - 2 * margin)), IMAGE_HEIGHT - margin / 4);
            }
        }
        


        // --red line for the significance level 5e-7
        g.setColor(Color.RED);
        g.drawLine(margin,IMAGE_HEIGHT-margin-((int) (-Math.log10(5e-7)/verticalMaxValue*(IMAGE_HEIGHT-2*margin))),
                IMAGE_WIDTH-margin,IMAGE_HEIGHT-margin-((int) (-Math.log10(5e-7)/verticalMaxValue*(IMAGE_HEIGHT-2*margin))));
        
           
       
    }
    
    void savePlot(String outputFileName) {
        File fileToSave = new File(outputFileName + ".png");
        try {
            // Save as PNG
            ImageIO.write(image, "png", fileToSave);

        } catch (IOException IOe) {
        }
    }
    
    int getChromosomeFromCoordinate(long coordinate) {
        int i;
        for (i = 1; i <= 24; i++) {
            if (coordinate > genomeInfo.chromosome[i].initCoordinate &&
                    coordinate < genomeInfo.chromosome[i].initCoordinate + genomeInfo.chromosome[i].length) {
                break;
            }
        }
        return (i);
    }
    
    long getCoordinateFromXPosition(float fractionX){
        return((long) (coordinateInitGraph + ((coordinateEndGraph - coordinateInitGraph) * 
                ((fractionX*IMAGE_WIDTH)-margin)/
                (IMAGE_WIDTH-2*margin)  )));
        
        
        
        
        //(((float)(fractionX*IMAGE_WIDTH)-margin)/(IMAGE_WIDTH-2*margin))*(coordinateEndGraph - coordinateInitGraph)+coordinateInitGraph);
    }
    
    void markTest(int testIndex){
        experiment.tests[testIndex].type=1;
    }
    
    void markTest(String rscode){
        for(int i=0;i<experiment.tests.length;i++){
            if(experiment.tests[i].markerName.equals(rscode)){
                experiment.tests[i].type = 1;
                return;
            }
        }
    
    }
    
    void markList(String markersListFile_) {
        try {
            BufferedReader inputFile = new BufferedReader(new FileReader(markersListFile_));
            String line = "";
            while ((line = inputFile.readLine()) != null) {
                line = line.split("\\s+")[0];
                markTest(line);
            }
            inputFile.close();
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }

    }
    
    void unmarkAllTests(){
        for(int testIndex=0;testIndex<experiment.tests.length;testIndex++){
            experiment.tests[testIndex].type=0;
        }
    }

}



