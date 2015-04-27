package pippin;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Container;
import java.awt.Color;
import java.util.Observable;
import java.util.Properties;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.File;

public class MachineView extends Observable {
    private static final int TICK = 500;

    private MachineModel model;
    private String defaultDir, sourceDir, executableDir;
    private Properties properties = null;
    private CodeViewPanel codeViewPanel;
    private MemoryViewPanel memoryViewPanel1;
    private MemoryViewPanel memoryViewPanel2;
    private MemoryViewPanel memoryViewPanel3;
    private ControlPanel controlPanel;
    private ProcessorViewPanel processorPanel;
    private MenuBarBuilder menuBuilder;
    private JFrame frame;
    private States state;
    private boolean autoStepOn = false;

    /**
     * Main method that drives the whole simulator
     * @param args command line arguments are not used
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MachineView(new MachineModel());
            }
        });
    }

    public MachineView(MachineModel model) {
        this.model = model;
        locateDefaultDirectory();
        loadPropertiesFile();
        createAndShowGUI();
    }

    public int getData(int index) {
        return this.model.getData(index);
    }

    public int getProgramCounter() {
        return this.model.getProgramCounter();
    }

    public int getAccumulator() {
        return this.model.getAccumulator();
    }

    public int getChangedIndex() {
        return this.model.getChangedIndex();
    }

    public States getState() {
        return this.state;
    }

    public void step() {
        this.model.step();
    }

    public void toggleAutoStep() {
        this.autoStepOn = !this.autoStepOn;
    }

    public void clearAll() {
        model.clear();
    }

    public void reload() {
        //TODO implement
    }

    public void loadFile() {
        //TODO implement
    }

    public void assembleFile() {
        //TODO implement
    }

    public void execute() {
        //TODO implement
    }

    private void locateDefaultDirectory() {
        //CODE TO DISCOVER THE ECLIPSE DEFAULT DIRECTORY:
        File temp = new File("propertyfile.txt");
        if(!temp.exists()) {
            PrintWriter out;
            try {
                out = new PrintWriter(temp);
                out.close();
                defaultDir = temp.getAbsolutePath();
                temp.delete();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            defaultDir = temp.getAbsolutePath();
        }
        // change to forward slashes
        defaultDir = defaultDir.replace('\\','/');
        int lastSlash = defaultDir.lastIndexOf('/');
        defaultDir  = defaultDir.substring(0, lastSlash + 1);
    }

    private void loadPropertiesFile() {
        try { // load properties file "propertyfile.txt", if it exists
            properties = new Properties();
            properties.load(new FileInputStream("propertyfile.txt"));
            sourceDir = properties.getProperty("SourceDirectory");
            executableDir = properties.getProperty("ExecutableDirectory");
            // CLEAN UP ANY ERRORS IN WHAT IS STORED:
            if (sourceDir == null || sourceDir.length() == 0
                    || !new File(sourceDir).exists()) {
                sourceDir = defaultDir;
                    }
            if (executableDir == null || executableDir.length() == 0
                    || !new File(executableDir).exists()) {
                executableDir = defaultDir;
                    }
        } catch (Exception e) {
            // PROPERTIES FILE DID NOT EXIST
            sourceDir = defaultDir;
            executableDir = defaultDir;
        }
    }
    /**
     * Method that sets up the whole GUI and locates the individual
     * components into place. Also sets up the Menu bar. Starts a
     * swing timer ticking.
     */
    private void createAndShowGUI() {
        codeViewPanel = new CodeViewPanel(this);
        memoryViewPanel1 = new MemoryViewPanel(this, 0, 160);
        memoryViewPanel2 = new MemoryViewPanel(this, 160, 240);
        memoryViewPanel3 = new MemoryViewPanel(this, 240, Memory.DATA_SIZE);
        controlPanel = new ControlPanel(this);
        processorPanel = new ProcessorViewPanel(this);
        menuBuilder = new MenuBarBuilder(this);
        frame = new JFrame("Pippin Simulator");
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout(1,1));
        content.setBackground(Color.BLACK);
        frame.setSize(1200,600);
        JPanel center = new JPanel();
        center.setLayout(new GridLayout(1,3));

        frame.add(codeViewPanel.createCodeDisplay(),BorderLayout.LINE_START);
        frame.add(center,BorderLayout.CENTER);
        center.add(memoryViewPanel1.createMemoryDisplay());
        center.add(memoryViewPanel2.createMemoryDisplay());
        center.add(memoryViewPanel3.createMemoryDisplay());
        frame.add(controlPanel.createControlDisplay(),BorderLayout.PAGE_END);
        frame.add(processorPanel.createProcessorDisplay(),BorderLayout.PAGE_START);

        JMenuBar bar = new JMenuBar();
        frame.setJMenuBar(bar);
        bar.add(menuBuilder.createFileMenu());
        bar.add(menuBuilder.createExecuteMenu());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(WindowListenerFactory.windowClosingFactory(e -> exit()));
        frame.addWindowListener(null); // needs work
        state = States.NOTHING_LOADED;
        state.enter();
        setChanged();
        notifyObservers();
        javax.swing.Timer timer = new javax.swing.Timer(TICK, e -> {if(autoStepOn) step();});
        timer.start();
        frame.setVisible(true);
    }

    public void exit() { // method executed when user exits the program
        int decision = JOptionPane.showConfirmDialog(
                frame, "Do you really wish to exit?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (decision == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}
