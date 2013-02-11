import java.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p> 
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Hop {
  Network network;
  private Host currentHost;
  private ArrayList destination;
  private boolean cw;// Use only for face routing.
  private double dataRate;
  private int routingStatus = 0;
  //public static final int GREEDY_ROUTING = 0;
  //public static final int FACE_ROUING = 1;

  public Hop(Host node, ArrayList dest, boolean cw, int rate) {
    network = Network.getInstance();
    currentHost = node;
    destination = dest;
    this.cw = cw;
    this.dataRate = rate;
  }

  public Hop(Host node, ArrayList dest) {
    this(node, dest, false, 1);
  }

  public double getRate() {
    return dataRate;
  }

  public void setRate (double rate) {
    dataRate = rate;
  }

  public Host getNode() {
    return currentHost;
  }

  public void setNode (Host node) {
    currentHost = node;
  }

  public boolean getCW() {
    return cw;
  }

  public void setCW (boolean inputcw) {
    this.cw = inputcw;
  }

  public ArrayList getDestinationList() {
    return destination;
  }

  public void setDestinationList(ArrayList dest) {
    destination = dest;
  }

  /**
   * Test if the hop is in the route to the given destination.
   * @param destNumber
   * @return
   */
  public boolean routeToDestNumber(Host dest) {
    for (int i = 0; i < destination.size(); ++i) {
      if (((Host)destination.get(i)).equals(dest)) {
        return true;
      }
    }

    return false;
  }

  public Object clone() {
    Hop newHop = new Hop(null, null);

    Host newHost = (Host)currentHost.clone();
    ArrayList newDest = new ArrayList();
    for (int i = 0; i < destination.size(); ++i) {
      newDest.add(((Host)destination.get(i)).clone());
    }
    newHop.setNode(newHost);
    newHop.setDestinationList(newDest);

    return newHop;
  }
}
