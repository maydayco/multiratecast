import java.util.ArrayList;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class PositionBasedMulticastMessage
    extends FaceMessage {
  public static final int GREEDY_ROUTING = 0;
  public static final int FACE_ROUTING = 1;

  private int routingStatus = 0;
  private Host fstart;
  private Host oldPoint;
  private ArrayList greedyDestList;
  private ArrayList faceDestList;

  public PositionBasedMulticastMessage(Host origsource,
                                 Host sender,
                                 Host host,
                                 Host fstart,
                                 Host oldPoint,
                                 ArrayList destList,
                                 RoutingAlgorithm algorithm,
                                 boolean cw,
                                 int routingStatus,
                                 int totalhopcount,
                                 ArrayList greedydests,
                                 ArrayList facedests,
                                 Object data) {
   super(origsource,sender,host,destList,
                     algorithm,
                     cw,
                     totalhopcount,
                     data);
    this.routingStatus = routingStatus;
    //fstart is the face start node
    this.fstart = fstart;
    //oldPoint is the SourcDest ine and previous face egde intersection point
    this.oldPoint = oldPoint;
    this.greedyDestList = greedydests;
    this.faceDestList = facedests;
  }

  public int getRoutingStatus() {
    return routingStatus;
  }
  public Host getFstart() {
    return fstart;
  }

  public Host getOldPoint() {
    return oldPoint;
  }
  public void setRoutingStatus(int status) {
    routingStatus = status;
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
