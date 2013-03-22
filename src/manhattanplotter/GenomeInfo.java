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

/**
 *
 * @author WS113854
 */
public class GenomeInfo {
  
 Chromosome[] chromosome = new Chromosome[26];
 long endCoordinate = 3079843747l;


 GenomeInfo(){
   
        // Create chromosomes 
        chromosome[1] = new Chromosome("1", 247199719l,0l,Color.BLUE,123599860l);
        chromosome[2] = new Chromosome("2", 242751149,247199719l,Color.GRAY,368575294l);
        chromosome[3] = new Chromosome("3", 199446827,489950868l,Color.BLUE,589674282l);
        chromosome[4] = new Chromosome("4", 191263063,689397695l,Color.GRAY,785029227l);
        chromosome[5] = new Chromosome("5", 180837866,880660758l,Color.BLUE,971079691l);
        chromosome[6] = new Chromosome("6", 170896993,1061498624l,Color.GRAY,1146947121l);
        chromosome[7] = new Chromosome("7", 158821424,1232395617l,Color.BLUE,1311806329l);
        chromosome[8] = new Chromosome("8", 146274826,1391217041l,Color.GRAY,1464354454l);
        chromosome[9] = new Chromosome("9", 140442298,1537491867l,Color.BLUE,1597713016l);
        chromosome[10] = new Chromosome("10", 135374737,1677934165l,Color.GRAY,1723621534l);
        chromosome[11] = new Chromosome("11", 134452384,1813308902l,Color.BLUE,1859535094l);
        chromosome[12] = new Chromosome("12", 132289534,1947761286l,Color.GRAY,1992906053l);
        chromosome[13] = new Chromosome("13", 114127980,2080050820l,Color.BLUE,2127114810l);
        chromosome[14] = new Chromosome("14", 106360585,2194178800l,Color.GRAY,2237359093l);
        chromosome[15] = new Chromosome("15", 100338915,2300539385l,Color.BLUE,2330708843l);
        chromosome[16] = new Chromosome("16", 88822254,2400878300l,Color.GRAY,2425289427l);
        chromosome[17] = new Chromosome("17", 78654742,2489700554l,Color.BLUE,2509027925l);
        chromosome[18] = new Chromosome("18", 76117153,2568355296l,Color.GRAY,2586413873l);
        chromosome[19] = new Chromosome("19", 63806651,2644472449l,Color.BLUE,2656375775l);
        chromosome[20] = new Chromosome("20", 62435965,2708279100l,Color.GRAY,2719497083l);
        chromosome[21] = new Chromosome("21", 46944323,2770715065l,Color.BLUE,2784187227l);
        chromosome[22] = new Chromosome("22", 49528953,2817659388l,Color.GRAY,2842423865l);
        chromosome[23] = new Chromosome("X", 154913754,2867188341l,Color.BLUE,2944645218l);
        chromosome[24] = new Chromosome("Y", 57741652,3022102095l,Color.GRAY,3030972921l);

     
    }

 public void rebuild(){
     for(int i=2;i<=24;i++){
         chromosome[i].initCoordinate = chromosome[i-1].initCoordinate+chromosome[i-1].length;
     }
     endCoordinate = chromosome[24].initCoordinate+chromosome[24].length;
 }
}
