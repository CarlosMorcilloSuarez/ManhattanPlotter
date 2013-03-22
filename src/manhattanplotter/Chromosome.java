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
public class Chromosome{
    String name;
    long initCoordinate;
    long length;
    Color color;
    long nameCoordinate;
    
    Chromosome(String name, long length, long initCoordinate,Color color,long nameCoordinate){
      this.initCoordinate = initCoordinate;
      this.name = name;
      this.length = length;
      this.color = color;
      this.nameCoordinate = nameCoordinate;
    }
    

}

