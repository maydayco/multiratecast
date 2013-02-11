import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.ArrayList;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class RateDialog extends JDialog implements ActionListener {

  private int rows = 0;
  private JLabel destHeader = new JLabel("Destination", JLabel.CENTER);
  private JLabel oldRateHeader = new JLabel("Old Rate", JLabel.CENTER);
  private JLabel newRateHeader = new JLabel("New Rate", JLabel.CENTER);
  private JLabel[] destsLabel = null;
  private JLabel[] oldRatesLabel = null;
  private JTextField[] newRatesField = null;

  public RateDialog(Frame parentFrame, String title) {
    super(parentFrame, title, true);

    //Create and initialize the buttons.
    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(this);

    final JButton setButton = new JButton("Update Rate");
    setButton.setActionCommand("Set");
    setButton.addActionListener(this);
    getRootPane().setDefaultButton(setButton);

    //Lay out the buttons from left to right.
    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
    buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
    buttonPane.add(Box.createHorizontalGlue());
    buttonPane.add(setButton);
    buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
    buttonPane.add(cancelButton);

    // The destination and rate display.
    Network network = Network.getInstance();
    HostArray dests = network.getDestinationArray();
    rows = dests.size();
    JPanel ratePanel = new JPanel();
    ratePanel.setLayout(new GridLayout(0, 3, 10, 5));

    // Header
    destHeader.setForeground(Color.BLUE);
    oldRateHeader.setForeground(Color.BLUE);
    newRateHeader.setForeground(Color.BLUE);
    ratePanel.add(destHeader);
    ratePanel.add(oldRateHeader);
    ratePanel.add(newRateHeader);

    // The rates.
    destsLabel = new JLabel[rows];
    oldRatesLabel = new JLabel[rows];
    newRatesField = new JTextField[rows];
    for (int i = 0; i < rows; ++i) {
      destsLabel[i] = new JLabel(Integer.toString(i), JLabel.CENTER);
      LocalRoutingHost host = (LocalRoutingHost) dests.get(i);
      double rate = host.getRate();
      String rateStr = Double.toString(rate);
      oldRatesLabel[i] = new JLabel(rateStr);
      newRatesField[i] = new JTextField(rateStr, 10);
      ratePanel.add(destsLabel[i]);
      ratePanel.add(oldRatesLabel[i]);
      ratePanel.add(newRatesField[i]);
    }

    //Put everything together, using the content pane's BorderLayout.
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());
    contentPane.add(ratePanel, BorderLayout.CENTER);
    contentPane.add(buttonPane, BorderLayout.PAGE_END);

    pack();
  }

  //Overridden so we can exit when window is closed
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      cancel();
    }
    super.processWindowEvent(e);
  }

  //Handle clicks on the Set and Cancel buttons.
  public void actionPerformed(ActionEvent e) {
    if ("Set".equals(e.getActionCommand())) {
      double [] rates = new double[rows];
      Network network = Network.getInstance();

      for (int i = 0; i < rows; ++i) {
        String rate = newRatesField[i].getText();
        double rateValue = 0;
        try {
          rateValue = Double.parseDouble(rate);
        } catch (Exception ex) {
          // Error, use the old one.
          LocalRoutingHost host = (LocalRoutingHost)
              network.getDestinationArray().get(i);
          rateValue = host.getRate();
        }
        rates[i] = rateValue;
      }

      network.setDestinationRate(rates);
    }

    cancel();
  }

  //Close the dialog
  void cancel() {
    dispose();
  }
}
