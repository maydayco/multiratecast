import java.awt.geom.Point2D;
import java.awt.Graphics;
import java.io.*;
import java.util.ArrayList;

public class Host
    implements Serializable {

  private static final int DRAWRADIUS = 6;
  private static final int BOARDERWIDTH = 5;

  private Point2D.Double position;

  private ArrayList incidentHosts = null;

  public Host(Point2D.Double position) {
    incidentHosts = new ArrayList();
    this.position = position;
  }

  public Host(double posX, double posY) {
    this(new Point2D.Double(posX, posY));
  }

  public void draw(Graphics g, double scaleFactor) {
    g.fillOval( (int) (position.x * scaleFactor) - DRAWRADIUS / 2 +
               BOARDERWIDTH,
               (int) (position.y * scaleFactor) - DRAWRADIUS / 2 + BOARDERWIDTH,
               DRAWRADIUS, DRAWRADIUS);
  }

  /**
   * Draw the edges of a node.
   * @param g 2D graphic
   * @param scaleFactor the scale factor.
   */
  public void drawEdges(Graphics g, double scaleFactor) {
    for (int i = 0; i < incidentHosts.size(); ++i) {
      Host neighbor = (Host) incidentHosts.get(i);
      g.drawLine( (int) (position.x * scaleFactor) + BOARDERWIDTH,
                 (int) (position.y * scaleFactor) + BOARDERWIDTH,
                 (int) (neighbor.getPosition().x * scaleFactor) + BOARDERWIDTH,
                 (int) (neighbor.getPosition().y * scaleFactor) + BOARDERWIDTH);
    }
  }

  /**
   * Get the position of the host.
   * @return the position of the host.
   */
  public Point2D.Double getPosition() {
    return position;
  }

  public double getX() {
    return position.getX();
  }

  public double getY() {
    return position.getY();
  }

  public ArrayList getIncidentHosts() {
    return incidentHosts;
  }

  public boolean addIncidentHost(Host neighbor) {
    if (incidentHosts.indexOf(neighbor) != -1) {
      // The host already exists.
      return false;
    }
    else {
      incidentHosts.add(neighbor);
      return true;
    }
  }

  public boolean removeIncidentHost(Host neighbor) {
    if (incidentHosts.indexOf(neighbor) == -1) {
      // The host doesn't exist.
      return false;
    }
    else {
      incidentHosts.remove(neighbor);
      return true;
    }
  }

  /**
   * Check if the host is the same as another host.
   * @param aHost if it's the same as current host
   * @return true if they are the same, false otherwise.
   */
  public boolean equals(Object host) {
    if (! (host instanceof Host)) {
      return false;
    }

    Host aHost = (Host) host;
    // Note: Treat two hosts as the same if there distance is small enough.
    if ( (Math.abs(position.getX() - aHost.position.getX()) <= 1e-12) &&
        (Math.abs(position.getY() - aHost.position.getY()) <= 1e-12)) {
      return true;
    }
    else {
      return false;
    }
  }

  public Object clone() {
    Host newHost = new Host(position);

    for (int i = 0; i < incidentHosts.size(); ++i) {
      Host neighbor = (Host) incidentHosts.get(i);
      newHost.addIncidentHost(neighbor);
    }

    return newHost;
  }
}
