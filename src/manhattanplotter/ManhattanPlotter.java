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
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;




public class ManhattanPlotter extends JFrame implements ActionListener,MouseListener{

    static String VERSION = "1.2";

    JPanel panel;
    JPanel tab1,tab2;
    JTabbedPane tabbedPane;
    JMenuBar menuBar;
    JPopupMenu popup,qqPopup;
    JMenu fileMenu,configMenu,helpMenu;
    JMenuItem openFileOption,exitOption,helpOption,aboutOption;
    JCheckBoxMenuItem qqPlotOnlyOption;
    JMenuItem zoomOut, saveGraph, saveQQGraph;
    JPanel canvas1,qqCanvas;
    JTextField  ScaleValue;
    JButton Scale, confidenceInterval;
    BufferedImage image1,image1All,image1Chromosome,qqImage;
    int CANVAS_SCALE = 140;
    int CANVAS_WIDTH = 7*CANVAS_SCALE;
    int CANVAS_HEIGHT = 3*CANVAS_SCALE;
    int QQ_CANVAS_WIDTH = 3*CANVAS_SCALE;
    int QQ_CANVAS_HEIGHT = 3*CANVAS_SCALE;


    ManhattanPlot plot1;
    QQPlot qqPlot;
    Experiment experiment;

    String directory;
    
    // Constructor
    public ManhattanPlotter() {

        // images
        image1 = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics g = image1.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, image1.getWidth(), image1.getHeight());

        image1All = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB);
        image1Chromosome = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB);

        qqImage = new BufferedImage(QQ_CANVAS_WIDTH, QQ_CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = qqImage.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, qqImage.getWidth(), qqImage.getHeight());

        
        // Window --------------------------------------------------------------
        setTitle("Manhattan Plotter");
        setBounds(100, 10, CANVAS_WIDTH+100, CANVAS_HEIGHT+165);
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        // Panel ---------------------------------------------------------------
        panel = new JPanel();
        panel.setBackground(Color.LIGHT_GRAY);
        panel.setLayout(null);
        this.setContentPane(panel);

        // TabbedPane ----------------------------------------------------------
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.LIGHT_GRAY);
        tabbedPane.setBounds(0, 0,this.getWidth(),this.getHeight());
        add(tabbedPane);

        // Tab1 ----------------------------------------------------------------
        tab1 = new JPanel();
        tab1.setLayout(null);
        tab1.setBackground(Color.LIGHT_GRAY);
        tabbedPane.addTab("Manhattan Plot", tab1);

        // Tab2 ----------------------------------------------------------------
        tab2 = new JPanel();
        tab2.setLayout(null);
        tab2.setBackground(Color.LIGHT_GRAY);
        tabbedPane.addTab("QQ Plot", tab2);

        // Activate tab1
        tabbedPane.setSelectedIndex(0);

        // Menu ----------------------------------------------------------------

        menuBar = new JMenuBar();
        setJMenuBar(menuBar);


        // Menu: File
        fileMenu = new JMenu("File", true);
        menuBar.add(fileMenu);


        // Menu: File - Open File
        openFileOption = new JMenuItem("Open File");
        openFileOption.addActionListener(this);
        fileMenu.add(openFileOption);
      
        
        // Menu: File - Exit
        exitOption = new JMenuItem("Exit");
        exitOption.addActionListener(this);
        fileMenu.add(exitOption);

        // Menu: Config
        configMenu = new JMenu("Config", true);
        menuBar.add(configMenu);

        // Menu: Config - QQ Plot Only
        qqPlotOnlyOption = new JCheckBoxMenuItem("qqPlotOnly");
        configMenu.add(qqPlotOnlyOption);
        
        // Menu: Help
        helpMenu = new JMenu("Help", true);
        menuBar.add(helpMenu);

        // Menu: Help - Help
        helpOption = new JMenuItem("Help");
        helpOption.addActionListener(this);
        helpMenu.add(helpOption);

        // Menu: Help - About
        aboutOption = new JMenuItem("About");
        aboutOption.addActionListener(this);
        helpMenu.add(aboutOption);


        // Popup Menu ----------------------------------------------------------
        popup = new JPopupMenu();
        qqPopup = new JPopupMenu();

        // Popup Menu: Zoom Out
        zoomOut = new JMenuItem("Zoom Out");
        zoomOut.addActionListener(this);
        popup.add(zoomOut);        

        // Popup Menu: Save PNG
        saveGraph = new JMenuItem("Save PNG");
        saveGraph.addActionListener(this);
        popup.add(saveGraph);

        // Popup Menu: Save QQ PNG
        saveQQGraph = new JMenuItem("Save PNG");
        saveQQGraph.setActionCommand("Save QQ PNG");
        saveQQGraph.addActionListener(this);
        qqPopup.add(saveQQGraph);

        // Canvas 
        canvas1 = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                g.drawImage(image1, 0, 0, this);
            }
        };
        canvas1.setBounds(10, 10, CANVAS_WIDTH, CANVAS_HEIGHT);
        canvas1.setBackground(Color.WHITE);
        canvas1.addMouseListener(this);
        tab1.add(canvas1);


        qqCanvas = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                g.drawImage(qqImage, 0, 0, this);
            }
        };
        qqCanvas.setBounds(10, 10, QQ_CANVAS_WIDTH, QQ_CANVAS_HEIGHT);
        qqCanvas.setBackground(Color.WHITE);
        qqCanvas.addMouseListener(this);
        tab2.add(qqCanvas);


        // buttons
        Scale = new JButton();
        Scale.setText("Scale");
        Scale.setActionCommand("Scale");
        Scale.setBounds(10, CANVAS_HEIGHT+40, 80, 25);
        Scale.addActionListener(this);
        tab1.add(Scale);

        // textfields
        ScaleValue = new JTextField();
        ScaleValue.setBounds(100, CANVAS_HEIGHT+40, 70, 25);
        tab1.add(ScaleValue);

    }

    //Methods
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        // Menu: File - Open File
        if (command.equals("Open File")) {

            if (directory == null) {
                directory = System.getProperty("user.dir");
            }

            JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
            int returnVal = fc.showOpenDialog(this);
            String inputFileName = fc.getSelectedFile().getAbsolutePath();
            directory = fc.getSelectedFile().getParent();

            // upload data into experiment
            if(qqPlotOnlyOption.isSelected()){
              experiment = new Experiment(Experiment.NO_POSITION_EXPERIMENT);
            } else {
              experiment = new Experiment(Experiment.MANHATTAN_EXPERIMENT);
            }
            int returnValue = experiment.readData(inputFileName);
            if(returnValue == Experiment.NO_FILE_RETURN_VALUE){
              JOptionPane.showMessageDialog(this, "Problem opening File", "", JOptionPane.PLAIN_MESSAGE);
              return;
            } else if (returnValue == Experiment.BAD_FORMAT_RETURN_VALUE) {
              JOptionPane.showMessageDialog(this, "Wrong File format", "", JOptionPane.PLAIN_MESSAGE);
              return;
            }

            plot1 = new ManhattanPlot(experiment);
            qqPlot = new QQPlot();

            // Creates manhattan plot and asings data of experiment
            if (experiment.experimentType == Experiment.MANHATTAN_EXPERIMENT) {
                plot1.setMode(ManhattanPlot.ALL_DATA, 0, 0, 0);
                //plot1.setExperiment(experiment);
                plot1.setLabel(fc.getSelectedFile().getName());
                plot1.setDotSize(7);
                plot1.drawPlot();
                ScaleValue.setText(String.valueOf(plot1.getYScale()));
                image1.getGraphics().drawImage(plot1.image.getScaledInstance(image1.getWidth(), -1, 4), 0, 0, this);
                image1All.getGraphics().drawImage(image1, 0, 0, this);
                updateCanvas(1);
            }

            // Creates qq plot
            qqPlot.setExperiment(experiment);
            qqPlot.setLabel(fc.getSelectedFile().getName());
            qqPlot.drawPlot();
            qqImage.getGraphics().drawImage(qqPlot.image.getScaledInstance(qqImage.getWidth(), -1, 4), 0, 0, this);
            updateCanvas(2);

        // Menu: File - Exit
        } else if (command.equals("Exit")) {
            System.exit(0);
        
        // Menu: Help - Help
        } else if (command.equals("Help")) {
            // shows help web pages with default browser
            if (!java.awt.Desktop.isDesktopSupported()) {
                JOptionPane.showMessageDialog(this, "ManhattanPloter cannot start default browser",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

            java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

            if (!desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
                JOptionPane.showMessageDialog(this, "ManhattanPlotter cannot start default browser",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

            try {
                java.net.URI uri = new java.net.URI("http://bioevo.upf.edu/~cmorcillo/tools/"
                        + "ManhattanPlotter/ManhattanPlotter.htm");
                desktop.browse(uri);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

        // Menu: Help - About
        } else if (command.equals("About")) {
            String aboutMessage = "ManhattanPlotter "+VERSION+"\n"
                    + "\n"
                    + "Copyright (c) 2011 Carlos Morcillo Suarez\n"
                    + "carlos.morcillo@upf.edu\n"
                    + "\n"
                    + "This is free software, distributed under the terms \n"
                    + "of the GNU General Public License (Documents/gpl_license.txt)\n"
                    + "http://www.gnu.org/licenses/\n"
                    + "\n"
                    + "Program, sources and aditional material can be downloaded at:\n"
                    + "http://bioevo.upf.edu/~cmorcillo/tools.htm\n";
            JOptionPane.showMessageDialog(this, aboutMessage, "About CHAVA", JOptionPane.PLAIN_MESSAGE);

        // Popup Menu: Zoom Out
        } else if (command.equals("Zoom Out")) {
           if(plot1.mode == ManhattanPlot.ALL_DATA){

           } else if (plot1.mode == ManhattanPlot.BY_CHROMOSOME){
             plot1.setMode(ManhattanPlot.ALL_DATA, 0, 0, 0);
             image1.getGraphics().drawImage(image1All, 0, 0, this);
             updateCanvas(1);
           } else if (plot1.mode == ManhattanPlot.BY_POSITION){
             plot1.setMode(ManhattanPlot.BY_CHROMOSOME, plot1.chrom, 0, 0);
             image1.getGraphics().drawImage(image1Chromosome, 0, 0, null);
             updateCanvas(1);
           }

        // Popup Menu: Save PNG
        } else if (command.equals("Save PNG")) {
            saveCanvasToPNG(image1);
        // Popup Menu: Save QQ PNG
        } else if (command.equals("Save QQ PNG")) {
            saveCanvasToPNG(qqImage);

        // JButton: Scale
        } else if (command.equals("Scale")) {
            plot1.setMode(ManhattanPlot.ALL_DATA, 0, 0, 0);
            plot1.setYScale(Integer.valueOf(ScaleValue.getText()));
            plot1.drawPlot();
            ScaleValue.setText(String.valueOf(plot1.getYScale()));
            image1.getGraphics().drawImage(plot1.image.getScaledInstance(image1.getWidth(), -1, 4), 0, 0, this);
            image1All.getGraphics().drawImage(image1, 0, 0, this);
            updateCanvas(1);

        }

    }

    private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger() && e.getSource().equals(canvas1)) {
            popup.show(e.getComponent(),
                    e.getX(), e.getY());
        } else if (e.isPopupTrigger() && e.getSource().equals(qqCanvas)) {
            qqPopup.show(e.getComponent(),
                    e.getX(), e.getY());
        }
    }
    
    public void mouseClicked(MouseEvent e) {
        maybeShowPopup(e);
        if (e.getButton() == 1 && e.getSource().equals(canvas1)) {
            if (plot1.mode == ManhattanPlot.ALL_DATA) {
                long clickedCoordinate = plot1.getCoordinateFromXPosition((float) e.getX() / CANVAS_WIDTH);
                int clickedChromosome = plot1.getChromosomeFromCoordinate(clickedCoordinate);
                plot1.setMode(ManhattanPlot.BY_CHROMOSOME, clickedChromosome, 0, 0);
                plot1.drawPlot();
                image1.getGraphics().drawImage(plot1.image.getScaledInstance(image1.getWidth(), -1, 4), 0, 0, this);
                image1Chromosome.getGraphics().drawImage(image1, 0, 0, this);
                updateCanvas(1);
            } else if (plot1.mode == ManhattanPlot.BY_CHROMOSOME) {
                long clickedCoordinate = plot1.getCoordinateFromXPosition((float) e.getX() / CANVAS_WIDTH);
                int chromosomePosition = (int)(clickedCoordinate - plot1.genomeInfo.chromosome[plot1.chrom].initCoordinate);
                plot1.setMode(ManhattanPlot.BY_POSITION, plot1.chrom,chromosomePosition , 1000000);
                plot1.drawPlot();
                image1.getGraphics().drawImage(plot1.image.getScaledInstance(image1.getWidth(), -1, 4), 0, 0, this);
                updateCanvas(1);
            } else if (plot1.mode == ManhattanPlot.BY_POSITION) {

            }
        }
    }

    public void mousePressed(MouseEvent e) {
        maybeShowPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    void updateCanvas(int index) {
        if (index == 1) {
            //canvas1.paint(canvas1.getGraphics());
            canvas1.repaint();
        }
        if (index == 2) {
            qqCanvas.repaint();
        }

    }

    void saveCanvasToPNG(BufferedImage image) {

        // Write generated image to a file

        // -- Opens window to select output file
        if (directory == null) {
            directory = System.getProperty("user.dir");
        }

        JFileChooser fc = new JFileChooser(directory);
        int returnVal = fc.showSaveDialog(this);
        if (returnVal == 0) {
            File fileToSave = fc.getSelectedFile();
            directory = fc.getSelectedFile().getParent();

            if (!fileToSave.getName().endsWith(".png")) {
                fileToSave = new File(fileToSave.getAbsoluteFile() + ".png");
            }

            // -- Writes selected file
            try {
                // Save as PNG
                ImageIO.write(image, "png", fileToSave);

            } catch (IOException IOe) {
            }
        }
    }

    public static void main(String[] args) {
        
        // Variables
        String inputFileName = "input.txt";
        String outputFileName = "graphic";
        String markersListFile = "";
        String label = "";
        int mode = ManhattanPlot.ALL_DATA;
        int chrom = 1;
        int referencePoint = 10000000;
        int window = 1000000;
        int YScale = 10;
        boolean visual = true;
        boolean qqPlot = false;
        
        
        // Read arguments
        int argument = 0;
        while (argument < args.length) {
            if (args[argument].equals("--data")) {
                argument++;
                inputFileName = args[argument];
            } else if (args[argument].equals("--out")) {
                argument++;
                outputFileName = args[argument];
            } else if (args[argument].equals("--chr")) {
                argument++;
                chrom = Integer.valueOf(args[argument]);
                mode = ManhattanPlot.BY_CHROMOSOME;
            } else if (args[argument].equals("--point")) {
                argument++;
                chrom = Integer.valueOf(args[argument]);
                argument++;
                referencePoint = Integer.valueOf(args[argument]);
                mode = ManhattanPlot.BY_POSITION;
            } else if (args[argument].equals("--window")) {
                argument++;
                window = Integer.valueOf(args[argument]);
            } else if (args[argument].equals("--label")) {
                argument++;
                label = args[argument];
            } else if (args[argument].equals("--yscale")) {
                argument++;
                YScale = Integer.valueOf(args[argument]);
            } else if (args[argument].equals("--command")) {
                visual = false;
            } else if (args[argument].equals("--qqplot")) {
                qqPlot = true;
            } else if (args[argument].equals("--markers-list")) {
                argument++;
                markersListFile = args[argument];
            } else {
                System.out.println("Unknown argument: "+args[argument]);
                System.exit(1);
            }
            argument++;
        }
        if(label.equals("")){
            label = inputFileName;
        }
        
        if (!visual) {


            // upload experiment data
            int experimentType;
            if (qqPlot) {
                experimentType = Experiment.NO_POSITION_EXPERIMENT;
            } else {
                experimentType = Experiment.MANHATTAN_EXPERIMENT;
            }
            Experiment experiment = new Experiment(experimentType);
            experiment.readData(inputFileName);

            if (qqPlot) {
                QQPlot plot = new QQPlot();
                plot.setExperiment(experiment);
                plot.setLabel(label);
                plot.drawPlot();
                plot.savePlot(outputFileName);
            } else {
                ManhattanPlot plot = new ManhattanPlot(experiment);
                plot.setMode(mode, chrom, referencePoint, window);
                plot.setLabel(label);
                plot.setYScale(YScale);
                // if there is a defined list of markers to be marked 
                if(!markersListFile.equals("")){
                    plot.markList(markersListFile);
                }
                plot.drawPlot();
                plot.savePlot(outputFileName);
            }

        } else {
            new ManhattanPlotter().setVisible(true);
        }
    }
}

