import java.util.ArrayList;
import java.util.Vector;
import java.awt.geom.Point2D;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class PositionBasedMulticast
    extends RoutingAlgorithm {

  private double lamda;

  private Host source;
  private Host currentNode;
  private ArrayList dests;
  private RoutingAlgorithm algorithm;
  private ArrayList msgList;
  private Host sender;
  private boolean cw;
  private int totalhopcount;
  private int status;
  private Host fstart;
  private Host oldPoint; //the previous SD and face edge intersect point
  private Object data;
  final int Maxhopcount = 1500;

  private ArrayList origGreedyDests;
  private ArrayList origFaceDests;

  private ArrayList newmsgList;
  private Host newfstart;

  public PositionBasedMulticast() {
    this(0.0);
  }

  public PositionBasedMulticast(double lamda) {
    this.lamda = lamda;
  }

  public ArrayList routing(Host ahost, Message amsg) {

    PositionBasedMulticastMessage msg = (PositionBasedMulticastMessage) amsg;
    ArrayList msgList = new ArrayList();

    // Greedy Face Greedy routing. Get parameters
    source = msg.getSource();
    currentNode = msg.getCurrentHost();
    if (!currentNode.equals(ahost)) {
      // for debug purpose
      Host a = null;
    }
    dests = msg.getDestinationList();
    algorithm = msg.getRoutingAlgorithm();
    data = msg.getData();
    msgList = new ArrayList();
    sender = msg.getSender();
    cw = msg.getCW();
    totalhopcount = msg.getTotalhopcount();
    status = msg.getRoutingStatus();
    fstart = msg.getFstart();
    oldPoint = msg.getOldPoint();
    origGreedyDests = msg.getGreedyDestList();
    origFaceDests = msg.getFaceDestList();

    if (totalhopcount > 1000 ) {
      int tempi = 0;
    }
    // Max_HopCount reached, fail
    if (totalhopcount >= Maxhopcount) {
      Message amessage = new PositionBasedMulticastMessage(
          source,
          currentNode,
          null,
          fstart,
          oldPoint,
          dests,
          algorithm,
          cw,
          status,
          totalhopcount,
          origGreedyDests,
          origFaceDests,
          data);
      msgList.add(amessage);
      return msgList;
    }

    //Greedy mode or face mode routing
    ArrayList msgList1 = null;
    ArrayList msgList2 = null;
    //Greedy mode or face mode routing
    if (origGreedyDests != null) {
      msgList1 = PBM_greedy();
    }

    if (origFaceDests != null) {
      msgList2 = PBM_face();
    }

    if (msgList1 != null) {
      msgList.addAll(msgList1);
    }
    if (msgList2 != null) {
      msgList.addAll(msgList2);
    }

    return msgList;
  }

// Greedy in PBM
  private ArrayList PBM_greedy() {
    Network network = Network.getInstance();
    HostArray hostLists = network.getHostArray();
    boolean newcw = cw;
    Host newfstart = currentNode;
    oldPoint = currentNode;
    totalhopcount++;
    ArrayList newmsgList = new ArrayList();

    ArrayList faceDest = new ArrayList();
    ArrayList destList = new ArrayList();
    for (int i = 0; i < origGreedyDests.size(); ++i) {
      Host onedest = (Host) origGreedyDests.get(i);
      if (currentNode.equals(onedest)) {
        //one path finish, other continue;
        continue;
      }
      destList.add(onedest);
    }

    ArrayList hopArray = new ArrayList();
    Hop nextHop = null;
    Host nextNode = null;
    ArrayList nextDestionList = new ArrayList();

    if (destList.size() != 0) {
      // check if greedy for some destinations
      hopArray = PBMUtility.getNextHops(currentNode, sender, destList, lamda);

    }

    //destinations without closer node,
    //add to faceDest, remove from hopArray
    Hop ahop = null;
    for (int i = 0; i < hopArray.size(); ++i) {
      nextHop = (Hop) hopArray.get(i);
      nextNode = nextHop.getNode();
      if (nextNode == null) {
        ahop = nextHop;
        // should do face routing, add to faceDest
        nextDestionList = nextHop.getDestinationList();
        for (int k = 0; k < nextDestionList.size(); ++k) {
          faceDest.add(nextDestionList.get(k));
          destList.remove(nextDestionList.get(k));
        }
      }
    }
    hopArray.remove(ahop);

    ArrayList faceNodes = new ArrayList();
    boolean[] faceCW = new boolean[faceDest.size()];
    if (faceDest.size() > 0) {
      //loop through the face dests, find one face forward node for each destination
      for (int i = 0; i < faceDest.size(); ++i) {
        Host adest = (Host) faceDest.get(i);
        Host nextfaceNode = null;
        boolean nextcw;
        //Firstly, constructure the virtual edge
        Edge virtualEdge = new Edge(currentNode, adest);

        // Secondly, find out the first face edge, setup cw rule
        Edge newEdge = new Edge();
        ArrayList gg = new ArrayList();
        gg = FaceUtility.getGabrielGraph(network);
        newEdge = FaceUtility.getFirstFaceEdge(gg, currentNode, adest);
        for (int ii = 0; ii < hostLists.size(); ++ii) {
          nextfaceNode = hostLists.get(ii);
          if (nextfaceNode.equals(newEdge.b)) {
            break;
          }
        }
        if (!Edge.isRightFrom(virtualEdge, nextfaceNode)) {
          nextcw = false;
        }
        else {
          nextcw = true;
        }

        //Thirdly, add the found node to arraylist
        faceNodes.add(nextfaceNode);
        faceCW[i] = nextcw;
      }
    }

    /*
        //new we have faceDest, faceNodes, faceCW
        //optimum operation for face node
        ArrayList faceHopArray = new ArrayList();
        for ( int i = 0; i < faceNodes.size(); i++)
        {
          //optimum for common face routing node,
          //not only node, but also directions ?? cw or !cw
          Host oneFaceForwardNode = (Host) faceNodes.get(i);
          boolean found = false;
          for ( int j = 0; j < faceHopArray.size(); ++j) {
            Hop exitHop = (Hop) faceHopArray.get(j);
            Host exitNode = (Host) exitHop.getNode();
            boolean exitCW = exitHop.getCW();
     if ( oneFaceForwardNode.equals(exitNode) && (exitCW == faceCW[j]) ) {
              ArrayList exitDests = (ArrayList) exitHop.getDestinationList();
              exitDests.add(faceDest.get(i));
              exitHop.setDestinationList(exitDests);
              found = true;
              break;// break out which loop ??
            }
          }
          if ( !found ) // new node not in faceHopArray, add it
          {
            //ahop = new Hop(null, null, faceCW[i]);
            ahop = new Hop(null, null);
            ahop.setNode(oneFaceForwardNode);
            ahop.setCW(faceCW[i]);
            ArrayList adests = new ArrayList();
            adests.add(faceDest.get(i));
            ahop.setDestinationList(adests);
            faceHopArray.add(ahop);
          }
        }
     */
//new change for Rate based routing
//rate based routing do not apply optimum part
//simply copy nodes in faceNodes to faceHopArray
    ArrayList faceHopArray = new ArrayList();
    for (int i = 0; i < faceNodes.size(); i++) {
      ahop = new Hop(null, null);
      Host oneFaceForwardNode = (Host) faceNodes.get(i);
      ahop.setNode(oneFaceForwardNode);
      ahop.setCW(faceCW[i]);
      ArrayList adests = new ArrayList();
      adests.add(faceDest.get(i));
      ahop.setDestinationList(adests);
      faceHopArray.add(ahop);
    }

//Found next nodes same in greedyHopArray and faceHopArray
//save them to one message,
//remove them from both greedyHopArray and faceHopArray
    ArrayList comFaceArray = new ArrayList();
    ArrayList comGreedyArray = new ArrayList();

    if (comFaceArray.size() > 0 && comGreedyArray.size() > 0) {
      for (int i = 0; i < hopArray.size(); ++i) {
        for (int j = 0; j < faceHopArray.size(); ++j) {
          Hop hop1 = (Hop) hopArray.get(i);
          Hop hop2 = (Hop) faceHopArray.get(j);
          Host node1 = hop1.getNode();
          Host node2 = (Host) hop2.getNode();
          if (node1.equals(node2)) {
            comGreedyArray.add(hop1);
            comFaceArray.add(hop2);
          }
        }
      }
    }

    //Send out Greedy messages
    for (int k = 0; k < hopArray.size(); ++k) {
      nextHop = (Hop) hopArray.get(k);
      nextNode = nextHop.getNode();
      nextDestionList = nextHop.getDestinationList();
      if (nextDestionList.size() > 0) {
        //send out greedy message
        PositionBasedMulticastMessage pbmMsg1 = new
            PositionBasedMulticastMessage(
                source,
                currentNode,
                nextNode,
                newfstart,
                oldPoint,
                null,
                algorithm,
                newcw,
                PositionBasedMulticastMessage.GREEDY_ROUTING,
                totalhopcount,
                nextDestionList,
                null,
                data);
        newmsgList.add(pbmMsg1);
      }
    }

    //Update for face node and send message
    for (int k = 0; k < faceHopArray.size(); ++k) {
      nextHop = (Hop) faceHopArray.get(k);
      nextNode = nextHop.getNode();
      newcw = nextHop.getCW();
      nextDestionList = nextHop.getDestinationList();
      if (nextDestionList.size() > 0) {
        //send out greedy message
        PositionBasedMulticastMessage pbmMsg2 = new
            PositionBasedMulticastMessage(
                source,
                currentNode,
                nextNode,
                newfstart,
                oldPoint,
                null,
                algorithm,
                newcw,
                PositionBasedMulticastMessage.FACE_ROUTING,
                totalhopcount,
                null,
                nextDestionList,
                data);
        newmsgList.add(pbmMsg2);
      }
    }

    return newmsgList;

  } //RMB_greedy end

  private ArrayList PBM_face() {
    Network network = Network.getInstance();
    HostArray hostLists = network.getHostArray();
    boolean newcw = cw;
    Host newfstart = fstart;
    totalhopcount++;

    ArrayList newmsgList = new ArrayList();
    ArrayList destList = new ArrayList();
    ArrayList faceDest = new ArrayList();

    for (int i = 0; i < origFaceDests.size(); ++i) {
      Host adest = (Host) origFaceDests.get(i);
      if (!adest.equals(currentNode)) {
        if (FaceUtility.isClosertoDest(currentNode, fstart, adest)) {
          destList.add(adest);
        }
        else {
          faceDest.add(adest);
        }
      }
    }

    ArrayList hopArray = new ArrayList();
    Hop nextHop = null;
    Host nextNode = null;
    ArrayList nextDestionList = new ArrayList();

    if (destList.size() != 0) {
      hopArray = PBMUtility.getNextHops(currentNode, sender, destList, lamda);
    }
    //destinations without closer node,
    //add to faceDest, remove from hopArray
    Hop ahop = null;
    for (int i = 0; i < hopArray.size(); ++i) {
      nextHop = (Hop) hopArray.get(i);
      nextNode = nextHop.getNode();
      if (nextNode == null) {
        ahop = nextHop;
        // should do face routing, add to faceDest
        nextDestionList = nextHop.getDestinationList();
        for (int k = 0; k < nextDestionList.size(); ++k) {
          faceDest.add(nextDestionList.get(k));
          destList.remove(nextDestionList.get(k));
        }
      }
    }
    hopArray.remove(ahop);

    ArrayList faceNodes = new ArrayList();
    boolean[] faceCW = new boolean[faceDest.size()];
    if (faceDest.size() > 0) {
      //loop through the face dests, find one face forward node for each destination
      for (int i = 0; i < faceDest.size(); ++i) {
        Host adest = (Host) faceDest.get(i);
        Host nextfaceNode = null;
        boolean nextcw;
        //Firstly, constructure the virtual edge
        Edge virtualEdge = new Edge(newfstart, adest);

        // Secondly, find out the next face edge
        // change newcw if necessary
        Edge newEdge = new Edge();
        ArrayList gg = new ArrayList();
        gg = FaceUtility.getGabrielGraph(network);
        newEdge = FaceUtility.getNextFaceEdge(gg, sender, currentNode, cw);
        for (int j = 0; j < hostLists.size(); ++j) {
          nextfaceNode = hostLists.get(j);
          if (nextfaceNode.equals(newEdge.b)) {
            break;
          }
        }
        //Liu debug for new algorithm1
        //oldPoint stores previous intersectPoint, need transfered in message
        Point2D.Double intersectPoint = FaceUtility.intersect(virtualEdge,
            newEdge);
        Host newPoint = new Host(intersectPoint);

        if (intersectPoint != null &&
            FaceUtility.isClosertoDest(newPoint, oldPoint, adest)) {
          if (!Edge.isRightFrom(virtualEdge, nextfaceNode)) {
            newcw = !cw;
          }
          oldPoint = newPoint;
        }

        //Thirdly, add the found node to arraylist
        faceNodes.add(nextfaceNode);
        faceCW[i] = newcw;
      }
    }

    /*
        //new we have faceDest, faceNodes, faceCW
        //optimum operation for face node
        ArrayList faceHopArray = new ArrayList();
        for (int i = 0; i < faceNodes.size(); i++) {
          //optimum for common face routing node,
          //not only node, but also directions ?? cw or !cw
          Host oneFaceForwardNode = (Host) faceNodes.get(i);
          boolean found = false;
          for (int j = 0; j < faceHopArray.size(); ++j) {
            Hop exitHop = (Hop) faceHopArray.get(j);
            Host exitNode = (Host) exitHop.getNode();
            boolean exitCW = exitHop.getCW();
            if (oneFaceForwardNode.equals(exitNode) && (exitCW == faceCW[j])) {
              ArrayList exitDests = (ArrayList) exitHop.getDestinationList();
              exitDests.add(faceDest.get(i));
              exitHop.setDestinationList(exitDests);
              found = true;
              break; // break out which loop ??
            }
          }
          if (!found) { // new node not in faceHopArray, add it
            ahop = new Hop(null, null);
            ahop.setNode(oneFaceForwardNode);
            ahop.setCW(faceCW[i]);
            ArrayList adests = new ArrayList();
            adests.add(faceDest.get(i));
            ahop.setDestinationList(adests);
            faceHopArray.add(ahop);
          }
        }
     */
//new change for Rate based routing
//rate based routing do not apply optimum part
//simply copy nodes in faceNodes to faceHopArray
    ArrayList faceHopArray = new ArrayList();
    for (int i = 0; i < faceNodes.size(); i++) {
      ahop = new Hop(null, null);
      Host oneFaceForwardNode = (Host) faceNodes.get(i);
      ahop.setNode(oneFaceForwardNode);
      ahop.setCW(faceCW[i]);
      ArrayList adests = new ArrayList();
      adests.add(faceDest.get(i));
      ahop.setDestinationList(adests);
      faceHopArray.add(ahop);
    }

//Found next nodes same in greedyHopArray and faceHopArray
//save them to one message,
//remove them from both greedyHopArray and faceHopArray
    ArrayList comFaceArray = new ArrayList();
    ArrayList comGreedyArray = new ArrayList();

    if (comFaceArray.size() > 0 && comGreedyArray.size() > 0) {
      for (int i = 0; i < hopArray.size(); ++i) {
        for (int j = 0; j < faceHopArray.size(); ++j) {
          Hop hop1 = (Hop) hopArray.get(i);
          Hop hop2 = (Hop) faceHopArray.get(j);
          Host node1 = hop1.getNode();
          Host node2 = (Host) hop2.getNode();
          if (node1.equals(node2)) {
            comGreedyArray.add(hop1);
            comFaceArray.add(hop2);
          }
        }
      }
    }

    //send out greedy message
    for (int k = 0; k < hopArray.size(); ++k) {
      nextHop = (Hop) hopArray.get(k);
      nextNode = nextHop.getNode();
      nextDestionList = nextHop.getDestinationList();
      if (nextDestionList.size() > 0) {
        PositionBasedMulticastMessage pbmMsg1 = new
            PositionBasedMulticastMessage(
                source,
                currentNode,
                nextNode,
                newfstart,
                oldPoint,
                null,
                algorithm,
                newcw,
                PositionBasedMulticastMessage.GREEDY_ROUTING,
                totalhopcount,
                nextDestionList,
                null,
                data);
        newmsgList.add(pbmMsg1);
      }
    }

    //Update for face node and send message
    for (int k = 0; k < faceHopArray.size(); ++k) {
      nextHop = (Hop) faceHopArray.get(k);
      nextNode = nextHop.getNode();
      newcw = nextHop.getCW();
      nextDestionList = nextHop.getDestinationList();
      if (nextDestionList.size() > 0) {
        //send out greedy message
        PositionBasedMulticastMessage pbmMsg2 = new
            PositionBasedMulticastMessage(
                source,
                currentNode,
                nextNode,
                newfstart,
                oldPoint,
                null,
                algorithm,
                newcw,
                PositionBasedMulticastMessage.FACE_ROUTING,
                totalhopcount,
                null,
                nextDestionList,
                data);
        newmsgList.add(pbmMsg2);
      }
    }
    return newmsgList;
  } //PBM_face end

}
