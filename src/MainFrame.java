import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class MainFrame
    extends JFrame {
  JPanel contentPane;
  JMenuBar jMenuBar1 = new JMenuBar();
  JMenu jMenuFile = new JMenu();
  JMenuItem jMenuFileOpen = new JMenuItem();
  JMenuItem jMenuFileSave = new JMenuItem();
  JMenuItem jMenuFileSaveAs = new JMenuItem();
  JMenuItem jMenuFileExit = new JMenuItem();
  JMenu jMenuHelp = new JMenu();
  JMenuItem jMenuHelpAbout = new JMenuItem();
  JToolBar jToolBar = new JToolBar();
  JButton jButton1 = new JButton();
  JButton jButton2 = new JButton();
  JButton jButton3 = new JButton();
  ImageIcon image1;
  ImageIcon image2;
  ImageIcon image3;
  JLabel statusBar = new JLabel();
  BorderLayout borderLayout1 = new BorderLayout();

  //Create a file chooser
  JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
  File fileName = null;

  // The GUI widgets used for setup
  JPanel textPanel;
  JPanel graphPanel;
  JSplitPane splitPane;
  //JLabel xLabel = new JLabel("The size, Dim-X:");
  //JLabel yLabel = new JLabel("The size, Dim-Y:");
  //JTextField xTextField = new JTextField(8);
  //JTextField yTextField = new JTextField(8);
  JLabel nodeDensityLabel = new JLabel("The average density:");
  JTextField nodeDensityTextField = new JTextField(8);
  JLabel nodeLabel = new JLabel("Number of nodes");
  JTextField nodeTextField = new JTextField(8);
  // Liu add for multiple source
  JLabel sourceNumberLabel = new JLabel("Number of Sources:");
  JTextField sourceNumberTextField = new JTextField(8);
  // add end
  JLabel destinationNumberLabel = new JLabel("Number of destinations");
  JTextField destinationNumberTextField = new JTextField(8);
  JLabel minRateLabel = new JLabel("Minimum rate");
  JTextField minRateNumberTextField = new JTextField(8);
  JLabel maxRateLabel = new JLabel("Maximum rate");
  JTextField maxRateNumberTextField = new JTextField(8);
  JLabel graphLabel = new JLabel("The algorithm to generate the graph:");
  JComboBox graphComboBox = new JComboBox();
  JLabel rateSelectLabel = new JLabel("Dest rate distribution");
  JRadioButton randomRadioButton = new JRadioButton("Random distribution");
  JRadioButton increaseRadioButton = new JRadioButton("Increasing order");
  JRadioButton decreaseRadioButton = new JRadioButton("Decreasing order");
  JLabel startingRateLabel = new JLabel("Start rate");
  JTextField startingRateNumberTextField = new JTextField(4);
  JLabel rateStepLabel = new JLabel("Step by");
  JTextField rateStepNumberTextField = new JTextField(4);
  JButton rateButton = new JButton("Manual Adjust Rate...");

  JLabel routingLabel = new JLabel("The Routing Algorithm:");
  JComboBox routingComboBox = new JComboBox();
  JLabel parameterLabel = new JLabel("lamda");
  JTextField parameterTextField = new JTextField(4);

  JButton graphButton = new JButton("Generate Graph");
  JButton stepButton = new JButton("Step Routing");
  JButton routingButton = new JButton("Run Routing");

  JButton testButton = new JButton("Automated Test");

  // The frame size.
  private static final int FRAMEWIDTH = 950;
  private static final int FRAMEHEIGHT = 700;
  private static final int GRAPHPANELWIDTH = 600;
  private static final int GRAPHWIDTH = 580;

  //Construct the frame
  public MainFrame() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  //Component initialization
  private void jbInit() throws Exception {
    image1 = new ImageIcon(MainFrame.class.getResource("openFile.gif"));
    image2 = new ImageIcon(MainFrame.class.getResource("closeFile.gif"));
    image3 = new ImageIcon(MainFrame.class.getResource("help.gif"));

    //setIconImage(Toolkit.getDefaultToolkit().createImage(MainFrame.class.getResource("[Your Icon]")));
    contentPane = (JPanel)this.getContentPane();
    contentPane.setLayout(borderLayout1);
    this.setSize(new Dimension(FRAMEWIDTH, FRAMEHEIGHT));
    this.setTitle("The routing simulator tool");
    statusBar.setText("Welcome to the routing simulator tool!");
    jMenuFile.setText("File");
    jMenuFileOpen.setText("Open");
    jMenuFileOpen.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuFileOpen_actionPerformed(e);
      }
    });
    jMenuFileSave.setText("Save");
    jMenuFileSave.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuFileSave_actionPerformed(e);
      }
    });
    jMenuFileSaveAs.setText("Save As");
    jMenuFileSaveAs.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuFileSaveAs_actionPerformed(e);
      }
    });
    jMenuFileExit.setText("Exit");
    jMenuFileExit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuFileExit_actionPerformed(e);
      }
    });
    jMenuHelp.setText("Help");
    jMenuHelpAbout.setText("About");
    jMenuHelpAbout.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuHelpAbout_actionPerformed(e);
      }
    });
    jButton1.setIcon(image1);
    jButton1.setToolTipText("Open File");
    jButton1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuFileOpen_actionPerformed(e);
      }
    });
    jButton2.setIcon(image2);
    jButton2.setToolTipText("Save File");
    jButton2.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuFileSave_actionPerformed(e);
      }
    });
    jButton3.setIcon(image3);
    jButton3.setToolTipText("Help");
    jButton3.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuHelpAbout_actionPerformed(e);
      }
    });
    jToolBar.add(jButton1);
    jToolBar.add(jButton2);
    jToolBar.add(jButton3);
    jMenuFile.add(jMenuFileOpen);
    jMenuFile.add(jMenuFileSave);
    jMenuFile.add(jMenuFileSaveAs);
    jMenuFile.addSeparator();
    jMenuFile.add(jMenuFileExit);
    jMenuHelp.add(jMenuHelpAbout);
    jMenuBar1.add(jMenuFile);
    jMenuBar1.add(jMenuHelp);
    this.setJMenuBar(jMenuBar1);
    contentPane.add(jToolBar, BorderLayout.NORTH);
    contentPane.add(statusBar, BorderLayout.SOUTH);

    // Construct the text panel.
    textPanel = new JPanel();
    textPanel.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    Insets inset = new Insets(2, 10, 2, 10);
    c.insets = inset;
    /*
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.EAST;
        textPanel.add(xLabel, c);
        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        textPanel.add(xTextField, c);
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.EAST;
        textPanel.add(yLabel, c);
        c.gridx = 1;
        c.gridy = 1;
        c.anchor = GridBagConstraints.WEST;
        textPanel.add(yTextField, c);
     */
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 1;
    c.anchor = GridBagConstraints.EAST;
    textPanel.add(nodeDensityLabel, c);

    c.gridx = 1;
    c.gridy = 0;
    c.gridwidth = 2;
    c.anchor = GridBagConstraints.WEST;
    nodeDensityTextField.setText("10");
    textPanel.add(nodeDensityTextField, c);

    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 1;
    c.anchor = GridBagConstraints.EAST;
    textPanel.add(nodeLabel, c);

    c.gridx = 1;
    c.gridy = 1;
    c.gridwidth = 2;
    c.anchor = GridBagConstraints.WEST;
    nodeTextField.setText("50");
    textPanel.add(nodeTextField, c);

    c.gridx = 0;
    c.gridy = 2;
    c.gridwidth = 1;
    c.anchor = GridBagConstraints.EAST;
    textPanel.add(sourceNumberLabel, c);

    c.gridx = 1;
    c.gridy = 2;
    c.gridwidth = 2;
    c.anchor = GridBagConstraints.WEST;
    sourceNumberTextField.setText("1");
    textPanel.add(sourceNumberTextField, c);

    c.gridx = 0;
    c.gridy = 3;
    c.gridwidth = 1;
    c.anchor = GridBagConstraints.EAST;
    textPanel.add(destinationNumberLabel, c);

    c.gridx = 1;
    c.gridy = 3;
    c.gridwidth = 2;
    c.anchor = GridBagConstraints.WEST;
    destinationNumberTextField.setText("5");
    textPanel.add(destinationNumberTextField, c);

    c.gridx = 0;
    c.gridy = 4;
    c.gridwidth = 1;
    c.anchor = GridBagConstraints.EAST;
    textPanel.add(minRateLabel, c);

    c.gridx = 1;
    c.gridy = 4;
    c.gridwidth = 2;
    c.anchor = GridBagConstraints.WEST;
    minRateNumberTextField.setText("1");
    textPanel.add(minRateNumberTextField, c);

    c.gridx = 0;
    c.gridy = 5;
    c.gridwidth = 1;
    c.anchor = GridBagConstraints.EAST;
    textPanel.add(maxRateLabel, c);

    c.gridx = 1;
    c.gridy = 5;
    c.gridwidth = 2;
    c.anchor = GridBagConstraints.WEST;
    maxRateNumberTextField.setText("100");
    textPanel.add(maxRateNumberTextField, c);

    c.gridx = 0;
    c.gridy = 6;
    c.gridwidth = 3;
    c.anchor = GridBagConstraints.WEST;
    textPanel.add(graphLabel, c);

    graphComboBox.addItem("Standard CURG");
    //graphComboBox.addItem("Unrestricted proximity CURG");
    //graphComboBox.addItem("Weighted proximity CURG");
    c.gridx = 0;
    c.gridy = 7;
    c.gridwidth = 3;
    c.anchor = GridBagConstraints.CENTER;
    textPanel.add(graphComboBox, c);

    c.gridx = 0;
    c.gridy = 8;
    c.gridwidth = 1;
    c.anchor = GridBagConstraints.WEST;
    textPanel.add(rateSelectLabel, c);

    ButtonGroup group = new ButtonGroup();
    group.add(randomRadioButton);
    group.add(increaseRadioButton);
    group.add(decreaseRadioButton);

    c.gridx = 0;
    c.gridy = 9;
    c.gridwidth = 1;
    c.insets = new Insets(0, 25, 0, 0);
    c.anchor = GridBagConstraints.WEST;
    randomRadioButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        startingRateNumberTextField.setEnabled(false);
        rateStepNumberTextField.setEnabled(false);
      }
    });
    textPanel.add(randomRadioButton, c);

    c.gridx = 0;
    c.gridy = 10;
    c.gridwidth = 1;
    c.anchor = GridBagConstraints.WEST;
    increaseRadioButton.setSelected(true);
    increaseRadioButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        startingRateNumberTextField.setEnabled(true);
        rateStepNumberTextField.setEnabled(true);
      }
    });
    textPanel.add(increaseRadioButton, c);

    c.gridx = 0;
    c.gridy = 11;
    c.gridwidth = 1;
    c.anchor = GridBagConstraints.WEST;
    decreaseRadioButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        startingRateNumberTextField.setEnabled(true);
        rateStepNumberTextField.setEnabled(true);
      }
    });
    textPanel.add(decreaseRadioButton, c);

    c.gridx = 2;
    c.gridy = 8;
    c.insets = new Insets(0, 0, 0, 0);
    c.anchor = GridBagConstraints.EAST;
    textPanel.add(startingRateLabel, c);

    c.gridx = 2;
    c.gridy = 9;
    c.anchor = GridBagConstraints.EAST;
    startingRateNumberTextField.setText("5");
    textPanel.add(startingRateNumberTextField, c);

    c.gridx = 2;
    c.gridy = 10;
    c.anchor = GridBagConstraints.EAST;
    textPanel.add(rateStepLabel, c);

    c.gridx = 2;
    c.gridy = 11;
    c.anchor = GridBagConstraints.EAST;
    rateStepNumberTextField.setText("5");
    textPanel.add(rateStepNumberTextField, c);

    // Rate manual adjust button
    rateButton.setEnabled(false);
    rateButton.setForeground(Color.RED);
    rateButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        rateButton_actionPerformed(e);
      }
    });
    c.gridx = 0;
    c.gridy = 12;
    c.gridwidth = 3;
    c.anchor = GridBagConstraints.CENTER;
    textPanel.add(rateButton, c);

    c.gridx = 0;
    c.gridy = 13;
    c.anchor = GridBagConstraints.WEST;
    textPanel.add(routingLabel, c);

    routingComboBox.addItem("Greedy");
    routingComboBox.addItem("Face");
    routingComboBox.addItem("Position-Based-Multicast");
    routingComboBox.addItem("Rate-Based-Multicast");
    routingComboBox.addItem("MST-Based-Multiratecast");

    // Set up the default. The parameter must be set along with the comboBox.
    routingComboBox.setSelectedIndex(4);
    parameterLabel.setText("lamda");
    parameterLabel.setEnabled(true);
    parameterTextField.setEditable(true);

    routingComboBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // Reset the network.
        resetNetwork();

        // Setup the parameter box
        int selection = routingComboBox.getSelectedIndex();
        switch (selection) {
          case 2:
            parameterLabel.setText("lamda");
            parameterTextField.setText("");
            parameterLabel.setEnabled(true);
            parameterTextField.setEditable(true);
            break;
          case 3:
            parameterLabel.setText("cost mode");
            parameterTextField.setText("");
            parameterLabel.setEnabled(true);
            parameterTextField.setEditable(true);
            break;
          case 4:
              parameterLabel.setText("cost mode");
              parameterTextField.setText("");
              parameterLabel.setEnabled(true);
              parameterTextField.setEditable(true);
              break;
          default:
            parameterLabel.setText("");
            parameterTextField.setText("");
            parameterLabel.setEnabled(false);
            parameterTextField.setEditable(false);
        }
      }
    });
    c.gridx = 0;
    c.gridy = 14;
    c.gridwidth = 2;
    c.anchor = GridBagConstraints.WEST;
    textPanel.add(routingComboBox, c);

    graphButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        graphButton_actionPerformed(e);
      }
    });

    c.gridx = 1;
    c.gridy = 13;
    c.anchor = GridBagConstraints.EAST;
    textPanel.add(parameterLabel, c);

    /*    parameterTextField.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            // Reset the network.
            resetNetwork();
          }
        });*/
    c.gridx = 1;
    c.gridy = 14;
    c.anchor = GridBagConstraints.EAST;
    textPanel.add(parameterTextField, c);

    c.gridx = 0;
    c.gridy = 15;
    c.gridwidth = 3;
    c.anchor = GridBagConstraints.CENTER;
    textPanel.add(graphButton, c);

    stepButton.setEnabled(false);
    stepButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        stepButton_actionPerformed(e);
      }
    });
    c.gridx = 0;
    c.gridy = 16;
    c.gridwidth = 3;
    textPanel.add(stepButton, c);

    routingButton.setEnabled(false);
    routingButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        routingButton_actionPerformed(e);
      }
    });
    c.gridx = 0;
    c.gridy = 17;
    c.gridwidth = 3;
    textPanel.add(routingButton, c);

    testButton.setForeground(Color.BLUE);
    testButton.setEnabled(true);
    testButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        testButton_actionPerformed(e);
      }
    });
    c.gridx = 0;
    c.gridy = 18;
    c.gridwidth = 3;
    textPanel.add(testButton, c);

    // Add the splitted panel for the choice and graph panel.
    graphPanel = new GraphPanel();
    splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, graphPanel,
                               textPanel);
    splitPane.setOneTouchExpandable(true);
    splitPane.setDividerLocation(GRAPHPANELWIDTH);
    //Provide minimum sizes for the two components in the split pane
    Dimension minimumSize = new Dimension(100, 510);
    textPanel.setMinimumSize(minimumSize);
    graphPanel.setMinimumSize(new Dimension(GRAPHWIDTH, GRAPHWIDTH));
    contentPane.add(splitPane, BorderLayout.CENTER);

  }

  //File | Exit action performed
  public void jMenuFileExit_actionPerformed(ActionEvent e) {
    System.exit(0);
  }

  //Help | About action performed
  public void jMenuHelpAbout_actionPerformed(ActionEvent e) {
    MainFrame_AboutBox dlg = new MainFrame_AboutBox(this);
    Dimension dlgSize = dlg.getPreferredSize();
    Dimension frmSize = getSize();
    Point loc = getLocation();
    dlg.setLocation( (frmSize.width - dlgSize.width) / 2 + loc.x,
                    (frmSize.height - dlgSize.height) / 2 + loc.y);
    dlg.setModal(true);
    dlg.show();
  }

  //Overridden so we can exit when window is closed
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      jMenuFileExit_actionPerformed(null);
    }
  }

  public void rateButton_actionPerformed(ActionEvent e) {
//    RateDialog.showDialog(this, "Manual Rate Update");
    RateDialog dlg = new RateDialog(this, "Manual Rate Update");
    Dimension dlgSize = dlg.getSize();
    Dimension frmSize = getSize();
    Point loc = getLocation();
    dlg.setLocation( (frmSize.width - dlgSize.width) / 2 + loc.x,
                    (frmSize.height - dlgSize.height) / 2 + loc.y);
    dlg.setModal(true);
    dlg.show();
  }

  //Start graph action performed
  public void graphButton_actionPerformed(ActionEvent e) {
    // Disable the button first.
    graphButton.setEnabled(false);
    contentPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    // Get the value from the input.
    //String xString = xTextField.getText();
    //String yString = yTextField.getText();
    String densityString = nodeDensityTextField.getText();
    String nodeString = nodeTextField.getText();
    //Liu add for multiple source
    String sourceNumberString = sourceNumberTextField.getText();
    //Liu add end
    String destinationNumberString = destinationNumberTextField.getText();
    int graphAlgorithm = graphComboBox.getSelectedIndex();
    String minRateNumberString = minRateNumberTextField.getText();
    String maxRateNumberString = maxRateNumberTextField.getText();
    String startingRateNumberString = startingRateNumberTextField.getText();
    String rateStepNumberString = rateStepNumberTextField.getText();

    //double x = 1000; // Default
    //double y = 1000;
    int density = 10;
    int nodeNumber = 50;
    int sourceNumber = 1;
    int destinationNumber = 5;
    double minRate = 1.0;
    double maxRate = 100.0;
    double startRate = 5.0;
    double rateSteps = 5.0;
    try {
      //x = Double.parseDouble(xString);
      //y = Double.parseDouble(yString);
      density = Integer.parseInt(densityString);
      nodeNumber = Integer.parseInt(nodeString);
      // Liu add for multiple sources
      sourceNumber = Integer.parseInt(sourceNumberString);
      // Liu add end
      destinationNumber = Integer.parseInt(destinationNumberString);
      sourceNumber = Integer.parseInt(sourceNumberString);
      minRate = Double.parseDouble(minRateNumberString);
      maxRate = Double.parseDouble(maxRateNumberString);
      startRate = Double.parseDouble(startingRateNumberString);
      rateSteps = Double.parseDouble(rateStepNumberString);
    }
    catch (Exception exception) {
      // Do nothing at this moment.
    }

    int rateDistriMode = 0;
    if (increaseRadioButton.isSelected()) {
      rateDistriMode = 1;
    }
    else if (decreaseRadioButton.isSelected()) {
      rateDistriMode = 2;
    }
    DestsRateDistri destRateDistri = new DestsRateDistri(minRate, maxRate,
        rateDistriMode, startRate, rateSteps);

    // 1st step, generate the simulate network.
    statusBar.setText("Generating the graph. Target node: " +
                      nodeNumber + " with density " + density + ".");
    Network network = Network.getInstance();
    network.generateSimNetwork(density, nodeNumber, sourceNumber,
                               destinationNumber, destRateDistri,
                               graphAlgorithm);

    // Draw the network.
    repaint();

    rateButton.setEnabled(true);
    graphButton.setEnabled(true);
    stepButton.setEnabled(true);
    routingButton.setEnabled(true);
    contentPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
  }

  //Step routing action performed
  public void stepButton_actionPerformed(ActionEvent e) {
    Network network = Network.getInstance();
    Pathlist pathlist = network.getPathlist();

    // Test if routing has already finished.then display result
    if (network.ifRouting_All_Finished()) {
      String routingResult = "The routing result is: ";
      for (int i = 0; i < pathlist.size(); ++i) {
        Path apath = (Path) pathlist.getPath(i);
        ArrayList routingPath = apath.getPath();
        if (routingPath != null) {
          //routingResult += "D" + i + ": ";
          if (apath.getPathsuccess()) {
            //routingResult += "Pass. ";
          }
          else {
        	routingResult += "D" + i + ": ";  
            routingResult += "Fail. ";
          }
        }
      }
      
      routingResult += "TotalHopCount: " + pathlist.getTotalHopCount();
      routingResult += "TotalRate: " + pathlist.getTotalRate();
      statusBar.setText(routingResult);
      stepButton.setEnabled(false);
      routingButton.setEnabled(false);

      return;

    }

    if (network.ifRouting_Just_Start()) {
      // First time start the routing.
      if (network.getHostArray().size() <= 2) {
        System.err.println("No enough hosts in the network;");
        return;
      }

      if (network.getSourceArray() == null ||
          network.getDestinationArray() == null) {
        System.err.println("The source or destination is not defined.");
        return;
      }

      HostArray sourceList = network.getSourceArray();
      HostArray destList = network.getDestinationArray();

      int numberOfSource = sourceList.size();
      int numberOfDest = destList.size();
      ArrayList dests = new ArrayList();
      for (int j = 0; j < numberOfDest; ++j) {
        dests.add( (Host) destList.get(j));
      }

      // Initailize the routing path.
      //Pathlist pathlist = network.getPathlist();

      for (int i = 0; i < numberOfSource; ++i) {
        for (int j = 0; j < numberOfDest; ++j) {
          Host source = sourceList.get(i);
          Host dest = destList.get(j);
          Path apath = new Path(source, dest);
          pathlist.addPath(apath);
        }
      }
      network.setRoutingPath(pathlist);

      //get routing algorithm
      String selectAlgorithm = (String) routingComboBox.getSelectedItem();
      statusBar.setText("Start routing by using algorithm: " + selectAlgorithm);

      // Constructe the routing protocol.
      MessageServer msgServer = MessageServer.getInstance();

      if (selectAlgorithm.equals("Greedy")) {
        RoutingAlgorithm routingMode = new Greedy();
        for (int i = 0; i < numberOfSource; ++i) {
          Host source = sourceList.get(i);
          Message msg = new Message(source, source, dests,
                                    routingMode, null);
          msgServer.send(msg);
        }
      }
      else if (selectAlgorithm.equals("Face")) {
        RoutingAlgorithm routingMode = new Face();
        for (int k = 0; k < sourceList.size(); ++k) {
          for (int i = 0; i < destList.size(); ++i) {
            ArrayList singleDestList = new ArrayList();
            singleDestList.add(destList.get(i));
            FaceMessage fmsg = new FaceMessage(sourceList.get(k),
                                               sourceList.get(k),
                                               sourceList.get(k),
                                               singleDestList,
                                               routingMode,
                                               true, 0, null);
            msgServer.send(fmsg);
          }
        }
      }
      else if (selectAlgorithm.equals("Position-Based-Multicast")) {
        String lamdaString = parameterTextField.getText();
        double lamda = 0.0;
        try {
          lamda = Double.parseDouble(lamdaString);
        }
        catch (Exception exception) {
          // Do thing, set the lamda is 0 is the string is invalid.
        }
        RoutingAlgorithm routingMode = new PositionBasedMulticast(lamda);
        for (int i = 0; i < sourceList.size(); ++i) {
        	PositionBasedMulticastMessage pbmMsg = new
            PositionBasedMulticastMessage(
                sourceList.get(i),
                sourceList.get(i),
                sourceList.get(i),
                sourceList.get(i),
                sourceList.get(i),
                null,
                routingMode,
                true,
                PositionBasedMulticastMessage.GREEDY_ROUTING,
                0,
                dests,
                null,
                null);
          msgServer.send(pbmMsg);
        }
      }
      // new code 2008/09/27 for sum_of_unicast
      else if (selectAlgorithm.equals("Rate-Based-Multicast")) {
    	  
    	  boolean sum_of_unicast = false;
          
          if ( sum_of_unicast ) {
    		  String costString = parameterTextField.getText();
    		  int costMode = 2;
    		  try {
    			  costMode = Integer.parseInt(costString);
    		  }
    		  catch (Exception exception) {
    			  // Do thing, set the cost mode is 0 is the string is invalid.
    		  }
    		  RoutingAlgorithm routingMode = new RateBasedMulticast(costMode);
    		  double grate = RBMUtility.getMaxDestRate(dests);
    		  for (int i = 0; i < sourceList.size(); ++i) {
    			  // process one dest at a time
    			  for ( int j = 0; j < dests.size(); ++j ) {
    				  ArrayList newdests = new ArrayList ();
    				  newdests.add(dests.get(j));
    				  grate = RBMUtility.getMaxDestRate(newdests);
    				  RateBasedMulticastMessage rbmMsg = new RateBasedMulticastMessage(
    						  sourceList.get(i), sourceList.get(i),
    						  sourceList.get(i), sourceList.get(i),
    						  sourceList.get(i), null, routingMode, true,
    						  grate, // rate
    						  0, // hop count
    						  newdests, null, null);
    				  msgServer.send(rbmMsg);
    			  }
    		  }
    	  } else {
    	      // original multicast rate based multicast 
    		  String costString = parameterTextField.getText();
    		  int costMode = 2;
    		  try {
    			  costMode = Integer.parseInt(costString);
    		  }
    		  catch (Exception exception) {
    			  // Do thing, set the cost mode is 0 is the string is invalid.
    		  }
    		  RoutingAlgorithm routingMode = new RateBasedMulticast(costMode);
    		  double grate = RBMUtility.getMaxDestRate(dests);
    		  for (int i = 0; i < sourceList.size(); ++i) {
    			  RateBasedMulticastMessage rbmMsg = new RateBasedMulticastMessage(
    					  sourceList.get(i),
    					  sourceList.get(i),
    					  sourceList.get(i),
    					  sourceList.get(i),
    					  sourceList.get(i),
    					  null,
    					  routingMode,
    					  true,
    					  grate, // rate
    					  0, // hop count
    					  dests,
    					  null,
    					  null);
    			  msgServer.send(rbmMsg);
    		  }
    	  }

      }  // end of else if Rate based multicast
    }

    //???????
    // not just start, perform Routing.
    MessageServer msgServer = MessageServer.getInstance();
    ArrayList msgList = msgServer.getAllMessages();
    for (int i = 0; i < msgList.size(); ++i) {
      Message msg = (Message) msgList.get(i);
      LocalRoutingHost node = (LocalRoutingHost) msg.getCurrentHost();
      node.receive(msg);
    }

    repaint();
  }

  //Start routing action performed
  public void routingButton_actionPerformed(ActionEvent e) {
    Network network = Network.getInstance();
    Pathlist pathlist = network.getPathlist();
    
    // Test if routing has already finished.
    
    // New three lines for computation time
    // Liu add for test GMR execution time
    // just use one stepButton_actionPerformed(e) line
    // end the routing , comment out the original three lines
    stepButton_actionPerformed(e);
    
    // original three lines, for normal routing
    /*
    while (!network.ifRouting_All_Finished()) {
      stepButton_actionPerformed(e);
    }
    */
        //result display
    String routingResult = "The routing result is: ";
    for (int i = 0; i < pathlist.size(); ++i) {
      Path apath = (Path) pathlist.getPath(i);
      ArrayList routingPath = apath.getPath();
      if (routingPath != null) {
        // routingResult += "D" + i + ": ";
        if (apath.getPathsuccess()) {
          // routingResult += "Pass. ";
        }
        else {
          routingResult += "D" + i + ": ";
          routingResult += "Fail. ";
        }
      }
    }
    routingResult += ". Rest of the destinations pass.";
    routingResult += "TotalHopCount: " + pathlist.getTotalHopCount();
    routingResult += "  TotalRate: " + pathlist.getTotalRate();

    statusBar.setText(routingResult);
    stepButton.setEnabled(false);
    routingButton.setEnabled(false);

    return;
    
    
  }

  public void jMenuFileOpen_actionPerformed(ActionEvent e) {
    int returnVal = fc.showOpenDialog(this);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File file = fc.getSelectedFile();
      Network simNetwork = Network.getInstance();
      simNetwork.readFromFile(file);

      // Enable the routing button.
      routingButton.setEnabled(true);
      stepButton.setEnabled(true);
      rateButton.setEnabled(true);
      repaint();
    }
  }

  public void jMenuFileSave_actionPerformed(ActionEvent e) {

    if (fileName == null) {
      jMenuFileSaveAs_actionPerformed(e);
    }
    else {
      Network simNetwork = Network.getInstance();
      simNetwork.writeToFile(fileName);
    }
  }

  public void jMenuFileSaveAs_actionPerformed(ActionEvent e) {
    int returnVal = fc.showSaveDialog(this);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File fileName = fc.getSelectedFile();
      Network simNetwork = Network.getInstance();
      simNetwork.writeToFile(fileName);
    }
  }

  private void autotestreset() {
    // Reset the routing path.
    Network network = Network.getInstance();
    Pathlist apathlist = new Pathlist();
    network.setPathlist(apathlist);

    // Clear the message buffer.
    MessageServer msgServer = MessageServer.getInstance();
    msgServer.reset();

    // Enable the routing buttons.
    stepButton.setEnabled(true);
    routingButton.setEnabled(true);

  }

  private void generateTestGraph(
      int[] density, int[] node, int[] destNumber, int[] sourceNumber,
      int RUNNUMBER, String graphpath, DestsRateDistri destsRateDistri) {

    for (int h = 0; h < sourceNumber.length; ++h) {
      for (int i = 0; i < destNumber.length; ++i) {
        for (int j = 0; j < node.length; ++j) {
          for (int k = 0; k < density.length; ++k) {
            for (int l = 0; l < RUNNUMBER; ++l) {

              // Debug output
              System.out.println("destNumber: " + destNumber[i] +
                                 " | nodeNumber: " + node[j] +
                                 " | density: " + density[k] +
                                 " | runNumber: " + l);

              // Generate graph
              Network network = Network.getInstance();
              network.generateSimNetwork(density[k], node[j],
                                         sourceNumber[h], destNumber[i],
                                         destsRateDistri, 0);
              String graphbakName = graphpath;
              graphbakName += "graph_source" + sourceNumber[h] +
                  "_dest" + destNumber[i] + "_den" + density[k] +
                  "_node" + node[j] + "_" + l + ".bak";
              File graphNameFile = new File(graphbakName);

              Network simNetwork = network.getInstance();
              simNetwork.writeToFile(graphNameFile);

            }
          }
        }
      }
    }
  }

  /* This routine read pregenerated testgraph and execute 
   * required testing
   *     testProtocol = 0  PBM test only
   *     testProtocol = 1  RBM test only
   *     estProtocol = 2  both PBM and RBM test
   *     
   *     destination rate is in given rateList array
   *    
   */
  public void readAndRun(
      int testProtocol,
      int[] density, int[] node, int[] destNumber, int[] sourceNumber,
      int RUNNUMBER, String graphpath, double [] rateList,
      File resultFile, File averageResultFile, ActionEvent e) {

    //generate detail result file
    /*
    PrintWriter outputStream = null;
    try {
      outputStream = new PrintWriter(
          new FileOutputStream(resultFile));
    }
    catch (FileNotFoundException exception) {
      System.out.println("Error opening the file");
      System.exit(0); ;
    }
    */

    //generate average result file
    PrintWriter outputStream2 = null;
    try {
      outputStream2 = new PrintWriter(
          new FileOutputStream(averageResultFile));
    }
    catch (FileNotFoundException exception) {
      System.out.println("Error opening the file");
      System.exit(0); ;
    }

    //set routing parameter
    double[] lamdaValues = {
        0.0, 0.4, 1.0};
    int[] costModes = {
        0, 1, 2, 3};

    //result for each loop
    int[] gfgHopCount = new int[lamdaValues.length];
    long[] gfgTime = new long[lamdaValues.length];
    
    int[] rbm_HopCount = new int[costModes.length];
    double[] rbm_TotalRate = new double[costModes.length];
    long[] rbmTime = new long[costModes.length];

    //average result of RUNNUMBER' loop for each configration
    double[] gfgHopCount_average = new double[lamdaValues.length];
    double[] rbm_HopCount_average = new double[costModes.length];
    double[] rbm_TotalRate_average = new double[costModes.length];

    //write the top line into the result file
    /*
    String lineToWrite =
        "Source Number, Destination Number, Nodes, Average Degree, ";
    for (int i = 0; i < lamdaValues.length; ++i) {
      lineToWrite = lineToWrite + "GFG (lamda: " + lamdaValues[i] +
          ") Hop Count, ";
    }
    for (int i = 0; i < costModes.length; ++i) {
      lineToWrite = lineToWrite + "RBM (mode: " + costModes[i] +
          ") Hop Count, ";
    }
    for (int i = 0; i < costModes.length; ++i) {
      lineToWrite = lineToWrite + "RBM (mode: " + costModes[i] +
          ") Total Rate, ";
    }

    outputStream.println(lineToWrite);
    */
    //write the top line into the average result file
    String lineToWrite2 =
        "Source Number, Destination Number, Nodes, Average Degree, ";
    if ( testProtocol == 0 || testProtocol == 2) {
	    for (int i = 0; i < lamdaValues.length; ++i) {
	      lineToWrite2 = lineToWrite2 + "GFG_ave (lamda: " + lamdaValues[i] +
	          ") Hop Count, " + "Time_" + lamdaValues[i] + ", "  ;
	    }
    }
    if ( testProtocol == 1 || testProtocol == 2) {
	    for (int i = 0; i < costModes.length; ++i) {
	      lineToWrite2 = lineToWrite2 + "RBM_Hop_ave (mode: " + costModes[i] +
	          ") Hop Count, " + "Time, ";
	    }
	    for (int i = 0; i < costModes.length; ++i) {
	      lineToWrite2 = lineToWrite2 + "RBM_Rate_ave (mode: " + costModes[i] +
	          ") Total Rate, ";
	    }
    }
    //lineToWrite2 = lineToWrite2 + "Time, ";
    
    outputStream2.println(lineToWrite2);

    //loop through all parameters to run test

    for (int h = 0; h < sourceNumber.length; ++h) {
      for (int i = 0; i < destNumber.length; ++i) {
        for (int j = 0; j < node.length; ++j) {
          for (int k = 0; k < density.length; ++k) {
            //clear average test result
            for (int il = 0; il < gfgHopCount_average.length; ++il) {
              gfgHopCount_average[il] = 0;
            }
            for (int i1 = 0; i1 < rbm_HopCount_average.length; ++i1) {
              rbm_HopCount_average[i1] = 0;
            }
            for (int i1 = 0; i1 < rbm_TotalRate_average.length; ++i1) {
              rbm_TotalRate_average[i1] = 0;
            }

            long multipleRunTime = 0L;
            for (int l = 0; l < RUNNUMBER; ++l) {
              // Debug output on screen
              System.out.println("destNumber: " + destNumber[i] +
                                 " | nodeNumber: " + node[j] +
                                 " | density: " + density[k] +
                                 " | runNumber: " + l);
              // Read graph
              String graphbakName = graphpath + "graph_source" + sourceNumber[h] +
                  "_dest" + destNumber[i] + "_den" + density[k] +
                  "_node" + node[j] + "_" + l + ".bak";
              File graphNameFile = new File(graphbakName);
              Network network = Network.getInstance();
              network.readFromFile(graphNameFile);
              network.setDestinationRate(rateList);

              //set passflag
              boolean passflag = true;

              if ( testProtocol == 0 || testProtocol == 2) {
	            	 
	              //PBM routing
	              for (int il = 0; il < lamdaValues.length; ++il) {
	                routingComboBox.setSelectedIndex(2);
	                Double lamda = new Double(lamdaValues[il]);
	                parameterTextField.setText(lamda.toString());
	                autotestreset();
	                long oneRunTime = System.currentTimeMillis();
	                routingButton_actionPerformed(e);
	                oneRunTime = System.currentTimeMillis() - oneRunTime;
	                multipleRunTime += oneRunTime;
	                gfgHopCount[il] = network.getPathlist().getTotalHopCount();
	                gfgHopCount_average[il] += gfgHopCount[il];
	                //hop count larger than 400 may be fail, check and set passflag false
	                if (gfgHopCount[il] >= 400) {
	                  // check and display failure result
	                  Pathlist pathlist = network.getPathlist();
	                  boolean ifPass = true;
	                  for (int it = 0; it < pathlist.size(); ++it) {
	                    Path apath = (Path) pathlist.getPath(it);
	                    ArrayList routingPath = apath.getPath();
	                    if (routingPath != null) {
	                      if (!apath.getPathsuccess()) {
	                        passflag = false;
	                      }
	                    }
	                  }
	
	                  if (!ifPass) {
	                    passflag = false;
	                  }
	                }
	              } //end of PBM run loop
              }

              if ( testProtocol == 1 || testProtocol == 2) {
	              //RBM routing
	              for (int costModeIndex = 0; costModeIndex < costModes.length;
	                   ++costModeIndex) {
	                routingComboBox.setSelectedIndex(3);
	                parameterTextField.setText(new Integer(costModes[costModeIndex]).
	                                           toString());
	                autotestreset();
	                
	                long oneRunTime = System.currentTimeMillis();
	                routingButton_actionPerformed(e);
	                oneRunTime = System.currentTimeMillis() - oneRunTime;
	                multipleRunTime += oneRunTime;
	                
	                rbm_HopCount[costModeIndex] = network.getPathlist().
	                    getTotalHopCount();
	                rbm_TotalRate[costModeIndex] = network.getPathlist().
	                    getTotalRate();
	                rbm_HopCount_average[costModeIndex] += rbm_HopCount[
	                    costModeIndex];
	                rbm_TotalRate_average[costModeIndex] += rbm_TotalRate[
	                    costModeIndex];
	                // if hop count larger than 400, may be failure, check and set passflag
	                if (rbm_HopCount[costModeIndex] >= 400) {
	                  // check and display failure result
	                  Pathlist pathlist = network.getPathlist();
	                  boolean ifPass = true;
	                  for (int it = 0; it < pathlist.size(); ++it) {
	                    Path apath = (Path) pathlist.getPath(it);
	                    ArrayList routingPath = apath.getPath();
	                    if (routingPath != null) {
	                      if (!apath.getPathsuccess()) {
	                        ifPass = false;
	                      }
	                    }
	                  }
	
	                  if (!ifPass) {
	                    passflag = false;
	                  }
	                }
	              } //end of RBM run loop

              }
              //save failure graph in another location
              String failurepath =
                  "C:\\Liu\\workspace\\RateSimulate\\TestGraph\\Failure";
              String failuregraph = failurepath +
                  "graph_source" + sourceNumber[h] +
                  "_dest" + destNumber[i] +
                  "_den" + density[k] +
                  "_node" + node[j] +
                  "_" + l + ".bakfail";
              File failNameFile = new File(failuregraph);
              if (!passflag) {
                Network simNetwork = network.getInstance();
                simNetwork.writeToFile(failNameFile);
              }

              //write detail test result into result file
              /*
              lineToWrite = sourceNumber[h] + "," +
                  destNumber[i] + ", " +
                  node[j] + ", " +
                  density[k] + ", ";
              for (int il = 0; il < gfgHopCount.length; ++il) {
                lineToWrite = lineToWrite + gfgHopCount[il] + ", ";
              }
              for (int i1 = 0; i1 < rbm_HopCount.length; ++i1) {
                lineToWrite = lineToWrite + rbm_HopCount[i1] + ", ";
              }
              for (int i1 = 0; i1 < rbm_TotalRate.length; ++i1) {
                lineToWrite = lineToWrite + rbm_TotalRate[i1] + ", ";
              }

              outputStream.println(lineToWrite);
              outputStream.flush();
              */
            } //end of runnumber loop
 
            
            //write average test result into average result file
            lineToWrite2 = sourceNumber[h] + "," +
                destNumber[i] + ", " +
                node[j] + ", " +
                density[k] + ", ";
            double average = 0;

            if ( testProtocol == 0 || testProtocol == 2 ) {
	            for (int il = 0; il < gfgHopCount_average.length; ++il) {
	              average = gfgHopCount_average[il]/RUNNUMBER;
	              lineToWrite2 = lineToWrite2 + average + ", " + multipleRunTime + ",";
	            }
            }
            
            if ( testProtocol == 1 || testProtocol == 2 ) {
	            for (int i1 = 0; i1 < rbm_HopCount_average.length; ++i1) {
	              average = rbm_HopCount_average[i1]/RUNNUMBER;
	              lineToWrite2 = lineToWrite2 + average + ", " + multipleRunTime + ",";;
	            }
	            for (int i1 = 0; i1 < rbm_TotalRate_average.length; ++i1) {
	              average = rbm_TotalRate_average[i1]/RUNNUMBER;
	              lineToWrite2 = lineToWrite2 + average + ", ";
	            }
            }
            
            // Add the timing information
            lineToWrite2 = lineToWrite2 + multipleRunTime + ", ";
            
            outputStream2.println(lineToWrite2);
            outputStream2.flush();

          } //end of density loop
        } //end of node loop
      } //end of destnum loop
    } //end of sourcenum loop

    /*
    outputStream.close();
    */
    outputStream2.close();
  }

  public void testButton_actionPerformed(ActionEvent e) {

	//  generateGraphFlag true, only generate graph, do not run test 
	// if false, only test with existing graph, do not generate graph
	  
	// generate graph only
	//boolean generateGraphFlag = true;
	  
	// run test only
	boolean generateGraphFlag = false;
	
	/*
    String graphpath =
    	"c:\\Liu\\workspace\\RateSimulate\\TestGraph_Auto_GMR_UniSUm\\";
    */
	
    String graphpath =
        "C:\\Liu\\workspace\\RateSimulate\\TestGraph_Auto_5\\";
    
	
    /*
	String graphpath =
        "c:\\Liu\\workspace\\RateSimulate\\TestGraph_Auto_LargeNetwork\\";
    */
    
    System.out.println("grapth path is " + graphpath);

    //generate graphs according to given parameters , save in graph path
    /*
    int[] density = {8, 12, 16, 20, 24, 28, 32};
    int[] node = { 500,800,1000};
    int[] sourceNumber = {1};
    //int[] destNumber = null;
    //int[] destNumber = { 10,20,30,40,50};
    //int[] destNumber = {5};
    int[] destNumber = {6};
    final int RUNNUMBER = 50;
    */
    
    // for SD change for given network with 6 dests
    int[] density = {24};
    int[] node = { 300 };
    int[] sourceNumber = {1};
    int[] destNumber = {5};
    final int RUNNUMBER = 50;
    
    
    /*
    int[] density = {8, 12, 16, 20, 24, 28, 32};
    int[] node = {500};
    int[] sourceNumber = {1};
    int[] destNumber = {5};
    final int RUNNUMBER = 50;
    */
    
    
    // for GMR vs. UniSum
    /*
    int[] density = {10};
    int[] node = {1000};
    int[] sourceNumber = {1};
    int[] destNumber = {5};
    final int RUNNUMBER = 50;
    */
    
    // This line is no use now, but keep it
    DestsRateDistri destsRateDistri = new DestsRateDistri(1, 1, 0, 1,
            0);

    // Disable the button first.
    testButton.setEnabled(false);
    contentPane.setCursor(Cursor.getPredefinedCursor(Cursor.
        WAIT_CURSOR));

    //generate and save graph, all nodes's rate = 1
    if ( generateGraphFlag ) {
     generateTestGraph( density, node, destNumber, sourceNumber,
        RUNNUMBER, graphpath, destsRateDistri);
    }
    else {
	   //read the already generated graphs and run routing test
	   //save parameters to text file & save graph
    	
    	/* The following part is for automatic test for 
    	 * destination Number = 10, 20,30,40,50
    	 * standard devision = 100,300,500,700 
    	 * average rate = 1000
    	 * The rate was previously generated by matlab 
    	 * and saved as rate<num> file for different number of destinations 
    	 * 
    	 * This execution is marked by boolean rateTest2 
     	 */
    	boolean rateTest2 = false;
    	
    	if ( rateTest2 ) {
    		String resultpath = "c:\\Liu\\workspace\\RateSimulate\\TestResult_Auto_sumUnicast\\";
    		System.out.println("result path is " + resultpath);

    		// Jan 1, 2008 , rate for dest from 10 to 50, sd from 100 to 700
    		// int[] destNumber = { 10,20,30,40,50};
    		// define one destionation number at a time
    		final int DEST_NUMBER = 10;
    		destNumber = new int[1];
    		destNumber[0] = DEST_NUMBER;

    		int[] sdmatch  = {100, 300,500, 700};
    		//int[] sdmatch  = {300};
    		double [][] rateList = new double[4][DEST_NUMBER];
    		String rateFile =
    			"C:\\Liu\\Jennifer_Thesis\\rateProj\\Simulator\\Rate_generator\\rate" + DEST_NUMBER + ".csv";

    		readRateList ( rateFile, DEST_NUMBER, rateList );


    		for ( int ir = 0; ir < rateList.length; ir++ ){
    			String resultname = resultpath + "test1Result_destNumber_" + DEST_NUMBER + "_sd_" + sdmatch[ir] +".csv";
    			File resultFile = new File(resultname);

    			String averageResultName = resultpath + "testResult_destNumber_" + DEST_NUMBER + "_sd_" + sdmatch[ir] + ".csv";
    			File averageResultFile = new File(averageResultName);
    			// testProtocol 0 PBM only, 1 RBM only, 2 both
    			double [] currentRateList = rateList[ir];
    			int testProtocol = 1;
    			readAndRun( testProtocol,
    					density, node, destNumber, sourceNumber,
    					RUNNUMBER, graphpath, currentRateList,
    					resultFile, averageResultFile, e);
    		}

    	}
	    /* The folloiwng part is to test all generated graph 
	     * by given destination rate destribution 
	     * We test networks with 6 destinations now
	     * 
	     * The execution of this part is marked by boolean rateTest1
	     */
    	
    	boolean rateTest1 = true;
    	
    	if ( rateTest1 ) {
    		//String resultpath =
    			//"c:\\Liu\\workspace\\RateSimulate\\TestResult_Auto_5\\";
    		//String resultpath = "c:\\Liu\\workspace\\RateSimulate\\TestResult_Auto_sumUnicast\\";
    		String resultpath = "c:\\Liu\\workspace\\RateSimulate\\TestResult_Auto_5_GMR\\";
    		
    		System.out.println("result path is " + resultpath);

    		
    		// mild rate for 5
    		// 0, 7.14,   16.96,   24.32,   33.91  
    		double [][] rateList = { 
    				{100,100,100,100,100}, 
    		   		{97,103,111,96,93},
    		   		{90,80,115,120,95},
    		   		{70,110,127,79,114},
    		   		{130,120,50,120,80}
    		   	    };
    		
    		
    	
    		
    		// drastic rate for destination = 5
    		// 26.11,  361.23,  790.57,  2104.02,  4368.04  
    		/*
    		double [][] rateList = { 
	    		{2190,	2280,	2223,	2230,	2260},
	    		{1822,	2022,	2223,	2422,	2622},
	    		{1222,	1722,	2223,	2722,	3222},
	    		{380,	751,	1500,	2920,	5560},
	    		{1,		10,		100,	1000,	10000},
	           };
    		*/
    		// rate for destination number = 6
    		/*
    		double [][] rateList = {
    			{18518.5, 18518.5, 18518.5, 18518.5, 18518.5, 18518.5},
	    		{18001, 18200, 18400, 18600, 18800, 19110},
	    		{8511,  12500, 16500, 20500, 24500, 28600},
	    		{2011,  6900,  13200, 20000, 30000, 39000},
	    		{751,   2010,  8300,  14550, 30500, 55000},
	    		{1,     10,    100,   1000,  10000, 100000},
	            };
            */
    		
    		
    		 //double [][] rateList = { 
    		 //	{1822,	2022,	2223,	2422,	2622},
	    	 // };


    	    for ( int ir = 0; ir < rateList.length; ir++ ){
    	    String resultname = resultpath + "testResult_" + ir +".csv";
    	    File resultFile = new File(resultname);
    	
    	    String averageResultName = resultpath + "testResult_average_" + ir + ".csv";
    	    File averageResultFile = new File(averageResultName);
    	    // testProtocol 0 PBM only, 1 RBM only, 2 both
    	    double [] currentRateList = rateList[ir];
    	    int testProtocol = 2;
    	    readAndRun( testProtocol,
    	        density, node, destNumber, sourceNumber,
    	        RUNNUMBER, graphpath, currentRateList,
    	        resultFile, averageResultFile, e);
    	    }

    	}
    	
    	/* The folloiwng part is to test generated graph
    	 * to compare GMR(ORCM_C) and SumofUnicast cost  
	     * destination rate all equal to 1
	     * We test networks with 5,10,25,50 destinations now
	     * 
	     * The execution of this part is marked by 
	     * boolean rateTest3
	     */
    	
    	boolean rateTest3 = false;
    	
    	if ( rateTest3 ) {
    		//String resultpath =
    			//"c:\\Liu\\workspace\\RateSimulate\\TestResult_Auto_5\\";
    			String resultpath = "c:\\Liu\\workspace\\RateSimulate\\TestResult_Auto_GMR_Unisum\\";
    		
    		System.out.println("result path is " + resultpath);

    			
    		// rate for destination number = 5, 10,25,50
    		// The list should be extended for large numbers ,
    		// here is 5 for example
    		
    		double [][] rateList = { 
    				{1,1,1,1,1},
	    	     };


    		for ( int ir = 0; ir < rateList.length; ir++ ){
    	    	String resultname = resultpath + "testResult_" + ir +".csv";
    	    	File resultFile = new File(resultname);
    	
    	    	String averageResultName = resultpath + "testResult_average_" + ir + ".csv";
    	    	File averageResultFile = new File(averageResultName);
    	    	// testProtocol 0 PBM only, 1 RBM only, 2 both
    	    	double [] currentRateList = rateList[ir];
    	    	int testProtocol = 1;
    	    	readAndRun( testProtocol,
    	    			density, node, destNumber, sourceNumber,
    	    			RUNNUMBER, graphpath, currentRateList,
    	    			resultFile, averageResultFile, e);
    	    }

    	}

    }
    
    
    // Update the info after finish
    statusBar.setText("Automated testing finished.");

    repaint();

    testButton.setEnabled(true);

    contentPane.setCursor(Cursor.getPredefinedCursor(Cursor.
        DEFAULT_CURSOR));

    // repaint
    repaint();

// DestsRateDistri destsRateDistri = new DestsRateDistri(1, 1,
//      DestsRateDistri.DECREASE_DISTRIBUTES, 1, 0);


  }

  private void resetNetwork() {
    // Reset the routing path.
    Network network = Network.getInstance();
    if (network != null) {
      Pathlist apathlist = new Pathlist();
      network.setPathlist(apathlist);
      /*
                if (routingComboBox.getSelectedIndex() == 4) {
                  network.setDestinationRate();
                }
                else if (routingComboBox.getSelectedIndex() == 5) {
                  network.setDestinationRate2();
                }
                else {
                  network.clearDestinationRate();
                }
       */
      // Clear the message buffer.
      MessageServer msgServer = MessageServer.getInstance();
      msgServer.reset();

      // Enable the routing buttons.
      // Liu modify
      //if (routingComboBox.getSelectedIndex() != 0) {
      //  stepButton.setEnabled(true);
      //}
      stepButton.setEnabled(true);
      routingButton.setEnabled(true);
    }
  }
  
  private void readRateList(String fileName, int destNumber, 
		  double[][] rateList) {
	  try {
		  BufferedReader in
	   		= new BufferedReader(new FileReader(fileName));
		  
		  // Read the rate.
		  for (int i = 0; i < destNumber; ++i) {
			  String line = in.readLine();
			  StringTokenizer st = new StringTokenizer(line, ",");
			  int j = 0;
			  while (st.hasMoreTokens()) {
				  rateList[j][i] = Double.parseDouble(st.nextToken());
				  j++;
			  }
		  }
	  } catch (FileNotFoundException e) {
		  System.out.println("File not find for name: " + fileName);
		  System.out.println(e.getMessage());
	  } catch (IOException e) {
		  System.out.println("IO Exception while reading file: " + fileName);
		  System.out.println(e.getMessage());
	  } catch (NoSuchElementException e) {
		  System.out.println("Problem with the file: " + fileName);
		  System.out.println(e.getMessage());		  
	  }
	  
	  // For display purpose.
	  System.out.println("The array is: ");
	  for (int i = 0; i < rateList.length; ++i) {
		  	for (int j = 0; j < rateList[0].length; ++j) {
			  System.out.print(rateList[i][j]);
		  	  System.out.print("\t");
	  		}
	  		System.out.println();
	  }
  }
}
