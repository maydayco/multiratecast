import java.awt.*;
import java.awt.geom.*;
import javax.swing.JPanel;
import java.util.ArrayList;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class GraphPanel
    extends JPanel {
  BorderLayout borderLayout1 = new BorderLayout();

  private final int GRAPHWEIGHT = 580;
  private final int GRAPHHEIGHT = 580;
  private final int BOARDERWIDTH = 5;

  private final static Color white = Color.white;
  private final static Color green = Color.green;
  private final static Color black = Color.black;
  private final static Color red = Color.red;
  private final static Color blue = Color.blue;

  public GraphPanel() {
    try {
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  void jbInit() throws Exception {
    this.setLayout(borderLayout1);
  }

  // The function to render the graph
  public void paint(Graphics g) {
    super.paint(g);
    draw(g);
  }

  private void draw(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;

    g2.setPaint(white);
    g2.fill(new Rectangle2D.Double(BOARDERWIDTH, BOARDERWIDTH,
                                   GRAPHWEIGHT - BOARDERWIDTH,
                                   GRAPHHEIGHT - BOARDERWIDTH));

    Network aNetwork = Network.getInstance();

    // Don't draw for an empty network.
    if (aNetwork.getHostArray() == null) {
      return;
    }

    // Calculate the scale.
    double scaleX = GRAPHWEIGHT / aNetwork.getSizeX();
    double scaleY = GRAPHHEIGHT / aNetwork.getSizeY();
    double scale = scaleX > scaleY ? scaleY : scaleX;

    HostArray hosts = aNetwork.getHostArray();

    // Draw the incident edges first so that the host can be see clearly.
    g2.setPaint(green);
    for (int i = 0; i < hosts.size(); ++i) {
      hosts.get(i).drawEdges(g2, scale);
    }

    // Paint the host.
    g2.setPaint(black);

    for (int i = 0; i < hosts.size(); ++i) {
      hosts.get(i).draw(g2, scale);
    }

    // Paint the source and the destination.
    g2.setPaint(red);
    // Liu aNetwork.getSource().draw(g2, scale);
    HostArray sourceArray = aNetwork.getSourceArray();
    for (int i = 0; i < sourceArray.size(); ++i) {
      Host source = (Host)sourceArray.get(i);
      source.draw(g2, scale);
      g2.drawString("S" + i,
                  (int) (source.getPosition().x * scale + 15),
                  (int) (source.getPosition().y * scale + 15));
    }

    //Liu g2.drawString("S", (int) (aNetwork.getSource().getPosition().x * scale + 15),
    //Liu              (int) (aNetwork.getSource().getPosition().y * scale + 15));
    g2.setPaint(blue);
    HostArray destinationArray = aNetwork.getDestinationArray();
    for (int i = 0; i < destinationArray.size(); ++i) {
      Host destination = (Host)destinationArray.get(i);
      destination.draw(g2, scale);
      g2.drawString("D" + i,
                  (int) (destination.getPosition().x * scale + 15),
                  (int) (destination.getPosition().y * scale + 15));
    }

    // Draw the routing path.
    Network network = Network.getInstance();
    Pathlist pathlist = network.getPathlist();

    if (pathlist != null){
    for(int i = 0; i < pathlist.size(); ++i){
      Path apath = (Path) pathlist.getPath(i);
      ArrayList routingPath = apath.getPath();
      if (routingPath != null) {
        g2.setPaint(black);
        for (int j = 0; j < routingPath.size() - 1; ++j) {
            Host startHost = (Host) routingPath.get(j);
            Host endHost = (Host) routingPath.get(j + 1);
            if (startHost != null && endHost != null) {
              Point2D.Double startPosition = startHost.getPosition();
              Point2D.Double endPosision = endHost.getPosition();
              g2.drawLine( (int) (startPosition.x * scale) + BOARDERWIDTH,
                    (int) (startPosition.y * scale) + BOARDERWIDTH,
                    (int) (endPosision.x * scale) + BOARDERWIDTH,
                    (int) (endPosision.y * scale) + BOARDERWIDTH);
            }
          }
        }
      }
    }
  }
}