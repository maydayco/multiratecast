import java.util.ArrayList;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class RateBasedMulticastMessage
    extends FaceMessage {
  private Host fstart;
  private Host oldPoint;
  private double  currentRate = 0;
                //the rate send from previous node to currentNode
                //currentNode is actually next forwarding node
  private ArrayList greedyDestList;
  private ArrayList faceDestList;

  public RateBasedMulticastMessage(Host origsource,
                                 Host sender,
                                 Host host,
                                 Host fstart,
                                 Host oldPoint,
                                 ArrayList dests,
                                 RoutingAlgorithm algorithm,
                                 boolean cw,
                                 double rate,
                                 int totalhopcount,
                                 ArrayList greedydests,
                                 ArrayList facedests,
                                 Object data) {
   super(origsource,sender,host,null,
                     algorithm,
                     cw,
                     totalhopcount,
                     data);
    this.fstart = fstart;
    this.oldPoint = oldPoint;
    this.currentRate = currentRate;
    this.currentRate = rate;
    this.greedyDestList = greedydests;
    this.faceDestList = facedests;
  }

  public double getCurrentRate() {
   return this.currentRate;
  }


  public Host getFstart() {
    return fstart;
  }

  public Host getOldPoint() {
    return oldPoint;
  }

  public void setFstart(Host fstart){
    fstart = fstart;
  }

  public ArrayList getGreedyDestList () {
    return this.greedyDestList;
  }

  public ArrayList getFaceDestList () {
    return this.faceDestList;
  }
}
