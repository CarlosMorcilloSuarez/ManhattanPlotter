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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author WS113854
 */
class Experiment{

    static final int NO_FILE_RETURN_VALUE = 1;
    static final int BAD_FORMAT_RETURN_VALUE = 2;
    static final int MANHATTAN_EXPERIMENT = 1;
    static final int NO_POSITION_EXPERIMENT = 2;   // doesn't have to include
                                                    // chromosome and position

    Test[] tests;
    GenomeInfo genomeInfo = new GenomeInfo();
    Boolean genomeInfoOK = true;
    ArrayList<Test> testsArray = new ArrayList<Test>();
    double maxLogPValue = 0;
    int experimentType = MANHATTAN_EXPERIMENT;

    public Experiment(int experimentType){
        this.experimentType = experimentType;
    }

    int readData(String inputFileName) {


        // reads file and fills arrayList
        try {
            BufferedReader inputFile = new BufferedReader(new FileReader(inputFileName));
            String str;
            int chromosomeColumn = -1, markerNameColumn = -1, positionColumn = -1, pValueColumn = -1;

            // Use header line to identify columns
            str = inputFile.readLine();
            String columnTitle[] = str.split("\\s+");
            for (int i = 0; i < columnTitle.length; i++) {
                if (columnTitle[i].equals("CHR")) {
                    chromosomeColumn = i;
                } else if (columnTitle[i].equals("SNP")) {
                    markerNameColumn = i;
                } else if (columnTitle[i].equals("BP") || columnTitle[i].equals("POS")) {
                    positionColumn = i;
                } else if (columnTitle[i].equals("P") || columnTitle[i].equals("EMP1")) {
                    pValueColumn = i;
                }
            }

            // checks that compulsory columns are defined
            // chromosome, position and p-value in MANHATTAN_EXPERIMENT mode
            // p-value in NO_POSITION_EXPERIMENT mode
            boolean goAhead = true;
            if (experimentType ==MANHATTAN_EXPERIMENT && chromosomeColumn == -1) {
                System.out.println("Chromosome Column not defined");
                goAhead = false;
            }
            if (experimentType ==MANHATTAN_EXPERIMENT && positionColumn == -1) {
                System.out.println("Position Column not defined");
                goAhead = false;
            }
            if (pValueColumn == -1) {
                System.out.println("PValue Column not defined");
                goAhead = false;
            }
            if (!goAhead) {
                return (BAD_FORMAT_RETURN_VALUE);
            }

            // read all lines
            while ((str = inputFile.readLine()) != null) {
                String words[] = str.split("\\s+");

                Test tmpTest = new Test();


                if (experimentType == MANHATTAN_EXPERIMENT) {

                    // Translates chromosomes X -> 23 Y -> 24 XY -> 25 Mit -> 26

                    if (words[chromosomeColumn].equals("X")) {
                        words[chromosomeColumn] = "23";
                    } else if (words[chromosomeColumn].equals("Y")) {
                        words[chromosomeColumn] = "24";
                    } else if (words[chromosomeColumn].equals("XY")) {
                        words[chromosomeColumn] = "25";
                    } else if (words[chromosomeColumn].equals("Mit")) {
                        words[chromosomeColumn] = "26";
                    }

                    // exclude chromosmes outside 1-22,23,24
                    if (Integer.valueOf(words[chromosomeColumn]) > 24 || Integer.valueOf(words[chromosomeColumn]) < 1) {
                        continue;
                    }

                }
                if (words[pValueColumn].equals("NA") ||
                        Double.valueOf(words[pValueColumn]) < 0 ||
                        Double.valueOf(words[pValueColumn]) > 1) {
                    continue;
                }

                // -- Chromosome and Position info
                if (experimentType == MANHATTAN_EXPERIMENT) {
                    tmpTest.chromosome = Integer.valueOf(words[chromosomeColumn]);
                    tmpTest.position = Long.valueOf(words[positionColumn]);

                    tmpTest.coordinate = genomeInfo.chromosome[tmpTest.chromosome].initCoordinate + tmpTest.position;
                    // If there are chromosome positions larger than the chromosome lenght stored in genomeInfo
                    // calculated coordinates are not valid and genomeInfo has to be updated
                    if (tmpTest.position > genomeInfo.chromosome[tmpTest.chromosome].length) {
                        genomeInfoOK = false;
                        genomeInfo.chromosome[tmpTest.chromosome].length = tmpTest.position;
                    }

                }

                // -- Marker Name info
                if (markerNameColumn != -1) {
                    tmpTest.markerName = words[markerNameColumn];
                }

                // -- P-value info
                tmpTest.pValue = Double.valueOf(words[pValueColumn]);
                tmpTest.logPValue = -Math.log10(tmpTest.pValue);
                if (tmpTest.logPValue > maxLogPValue) {
                    maxLogPValue = tmpTest.logPValue;
                }

                testsArray.add(tmpTest);
            }
            inputFile.close();
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
            return (NO_FILE_RETURN_VALUE);
        }


        // converts arrayList to array
        tests = new Test[testsArray.size()];

        for (int i = 0; i < tests.length; i++) {
            tests[i] = testsArray.get(i);
        }
        testsArray = new ArrayList<Test>();

        // Recreates genomeInfo and recalculates tests coordinates if necesary
        if(!genomeInfoOK){
            genomeInfo.rebuild();
            for(int i = 0; i < tests.length; i++){
                tests[i].coordinate = genomeInfo.chromosome[tests[i].chromosome].initCoordinate + tests[i].position;
            }
        }


        return (0);
    }
}








