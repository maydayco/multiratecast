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

public class RateBasedMulticast
    extends RoutingAlgorithm {

  private Host source;
  private Host currentNode;
  //private                     ArrayList dests;
  private RoutingAlgorithm algorithm;
  private ArrayList msgList;
  private Host sender;
  private boolean cw;
  private int totalhopcount;
  private int status;
  private double datarate;
  private Host fstart;
  private Host oldPoint;
  private Object data;
  private int Maxhopcount = 4 * Network.getInstance().getHostArray().size();
  private int costMode;
  private ArrayList origGreedyDests;
  private ArrayList origFaceDests;

  private ArrayList newmsgList;
  private Host newfstart;

  public RateBasedMulticast() {
    this(0);
  }

  public RateBasedMulticast(int cost) {
    costMode = cost;
  }

  public ArrayList routing(Host ahost, Message amsg) {

    RateBasedMulticastMessage msg = (RateBasedMulticastMessage) amsg;
    ArrayList msgList = new ArrayList();

    // Based on Greedy Face Greedy routing. Get parameters
    source = msg.getSource();
    currentNode = msg.getCurrentHost();
    if (!currentNode.equals(ahost)) {
      // for debug purpose
      Host a = null;
    }
    //         dests = msg.getDestinationList();
    algorithm = msg.getRoutingAlgorithm();
    data = msg.getData();
    msgList = new ArrayList();
    sender = msg.getSender();
    cw = msg.getCW();
    totalhopcount = msg.getTotalhopcount();
    fstart = msg.getFstart();
    oldPoint = msg.getOldPoint();
    datarate = msg.getCurrentRate();
    origGreedyDests = msg.getGreedyDestList();
    origFaceDests = msg.getFaceDestList();

    // Max_HopCount reached, fail
    if (totalhopcount >= Maxhopcount) {
      Message amessage = new RateBasedMulticastMessage(
          source,
          currentNode,
          null,
          fstart,
          oldPoint,
          null,
          algorithm,
          cw,
          datarate,
          totalhopcount,
          origGreedyDests,
          origFaceDests,
          data);
      msgList.add(amessage);
      return msgList;
    }

    ArrayList msgList1 = null;
    ArrayList msgList2 = null;
    //Greedy mode or face mode routing
    if (origGreedyDests != null) {
      msgList1 = RBM_greedy();
    }

    if (origFaceDests != null) {
      msgList2 = RBM_face();
    }

    if (msgList1 != null) {
      msgList.addAll(msgList1);
    }
    if (msgList2 != null) {
      msgList.addAll(msgList2);
    }
    return msgList;
  }

// Greedy in RBM , based on basic GFG
  private ArrayList RBM_greedy() {
    Network network = Network.getInstance();
    HostArray hostLists = network.getHostArray();
    boolean newcw = cw;
    Host newfstart = currentNode;
    oldPoint = currentNode;
    double newrate = datarate;
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
      // greedy routing for any destinations with closer neighbor
      // this is the core part of RBM.
      // greedy set partition for destination number > 5
      if (costMode != 3) {
    	  // when to get the test result, I completely used 
    	  // greedy algorithm even for destination num < 5
    	  // here I changed it back to get some graph 
    	  // with old num < 5 graphs for paper
    	  
    	  // GFG greedy set partition
    	  
    	  hopArray = RBMUtility.getNextHops_greedy(currentNode, sender, destList,
                  costMode);
    	  
    	  
    	  // GFG compelete set partition 
    	  /*
    	  hopArray = RBMUtility.getNextHops(currentNode, sender, destList,
                  costMode);  
          */
    	  
    	  // GFG mix Greedy and complete set partition
    	  /*
    	  if ( destList.size() > 5 ) {
	        hopArray = RBMUtility.getNextHops_greedy(currentNode, sender, destList,
	                                          costMode);
    	  } else {
    		  hopArray = RBMUtility.getNextHops(currentNode, sender, destList,
                      costMode);  
    	  }
    	  */
    	  
	  } else {
		  
		  // MRM algorithm
	      hopArray = RBMUtility.getNextHops_new(currentNode, sender, destList, 4);
	  }
          	  

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

    //apply face routing for destinations without closer neighbor
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
         ArrayList faceHopArray = new ArrayList();

           //new we have faceDest, faceNodes, faceCW
           //optimum operation for face node
           for (int i = 0; i < faceNodes.size(); i++) {
             //optimum for common face routing node,
             //not only node, but also directions ?? cw or !cw
             Host oneFaceForwardNode = (Host) faceNodes.get(i);
             boolean found = false;
             for (int j = 0; j < faceHopArray.size(); ++j) {
               Hop exitHop = (Hop) faceHopArray.get(j);
               Host exitNode = (Host) exitHop.getNode();
               boolean exitCW = exitHop.getCW();
     if(oneFaceForwardNode.equals(exitNode) && (exitCW == faceCW[j])) {
     ArrayList exitDests = (ArrayList) exitHop.getDestinationList();
                 exitDests.add(faceDest.get(i));
                 exitHop.setDestinationList(exitDests);
                 found = true;
                 break; // break out which loop ??
               }
             }
             if (!found) { // new node not in faceHopArray, add it
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
    //Update for greedy node and send message
    double maxrate = 0;
    for (int k = 0; k < hopArray.size(); ++k) {
      nextHop = (Hop) hopArray.get(k);
      nextNode = nextHop.getNode();
      nextDestionList = nextHop.getDestinationList();

      //get max rate among those destinations
      maxrate = RBMUtility.getMaxDestRate(nextDestionList);

      if (nextDestionList.size() > 0) {
        //send out greedy message
        RateBasedMulticastMessage rbmMsg1 = new RateBasedMulticastMessage(
            source,
            currentNode,
            nextNode,
            newfstart,
            oldPoint,
            null,
            algorithm,
            newcw,
            maxrate,
            totalhopcount,
            nextDestionList,
            null,
            data);
        newmsgList.add(rbmMsg1);
      }
    }

    //Update for face node and send message
    for (int k = 0; k < faceHopArray.size(); ++k) {
      nextHop = (Hop) faceHopArray.get(k);
      nextNode = nextHop.getNode();
      newcw = nextHop.getCW();
      nextDestionList = nextHop.getDestinationList();
      newrate = nextHop.getRate();
      if (nextDestionList.size() > 0) {
        //send out greedy message
        RateBasedMulticastMessage rbmMsg2 = new RateBasedMulticastMessage(
            source,
            currentNode,
            nextNode,
            newfstart,
            oldPoint,
            null,
            algorithm,
            newcw,
            newrate,
            totalhopcount,
            null,
            nextDestionList,
            data);
        newmsgList.add(rbmMsg2);
      }
    }

    int i = 0;
    for (int k = 0; k < comGreedyArray.size(); ++k) {
      nextHop = (Hop) comFaceArray.get(k);
      nextNode = nextHop.getNode();
      newcw = nextHop.getCW();
      ArrayList greedyDestinationList = (ArrayList) ( (Hop) comGreedyArray.get(
          k)).
          getDestinationList();
      ArrayList faceDestinationList = nextHop.getDestinationList();
      int greedyRate = (int) ( (Hop) comGreedyArray.get(k)).getRate();
      int faceRate = (int) ( (Hop) comFaceArray.get(k)).getRate();

      newrate = (greedyRate >= faceRate ? greedyRate : faceRate);

      if (nextDestionList.size() > 0) {
        //send out greedy message
        RateBasedMulticastMessage rbmMsg2 = new RateBasedMulticastMessage(
            source,
            currentNode,
            nextNode,
            newfstart,
            oldPoint,
            null,
            algorithm,
            newcw,
            newrate,
            totalhopcount,
            greedyDestinationList,
            faceDestinationList,
            data);
        newmsgList.add(rbmMsg2);
      }

    }
    return newmsgList;

  } //RMB_greedy end

// face in RBM, gased on basic GFG
  private ArrayList RBM_face() {
    Network network = Network.getInstance();
    HostArray hostLists = network.getHostArray();
    boolean newcw = cw;
    Host newfstart = fstart;
    double newrate = datarate;
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
      if (costMode != 3) {
        hopArray = RBMUtility.getNextHops(currentNode, sender, destList,
                                          costMode);
      }
      else {
        hopArray = RBMUtility.getNextHops_new(currentNode, sender, destList, 3);
      }

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

    //new we have faceDest, faceNodes, faceCW
    //optimum operation for face node
    /*       //rate based routing do not apply optimum part
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


    if ( comFaceArray.size() > 0 && comGreedyArray.size() > 0 ) {
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
    double maxrate = 0;
    //Update for greedy node and send message
    for (int k = 0; k < hopArray.size(); ++k) {
      nextHop = (Hop) hopArray.get(k);
      nextNode = nextHop.getNode();
      nextDestionList = nextHop.getDestinationList();
      maxrate = RBMUtility.getMaxDestRate(nextDestionList);
      if (nextDestionList.size() > 0) {
        //send out greedy message
        RateBasedMulticastMessage rbmMsg1 = new RateBasedMulticastMessage(
            source,
            currentNode,
            nextNode,
            newfstart,
            oldPoint,
            null,
            algorithm,
            newcw,
            maxrate,
            totalhopcount,
            nextDestionList,
            null,
            data);
        newmsgList.add(rbmMsg1);
      }
    }

    //Update for face node and send message
    for (int k = 0; k < faceHopArray.size(); ++k) {
      nextHop = (Hop) faceHopArray.get(k);
      nextNode = nextHop.getNode();
      newcw = nextHop.getCW();
      nextDestionList = nextHop.getDestinationList();
      newrate = nextHop.getRate();
      if (nextDestionList.size() > 0) {
        //send out greedy message
        RateBasedMulticastMessage rbmMsg2 = new RateBasedMulticastMessage(
            source,
            currentNode,
            nextNode,
            newfstart,
            oldPoint,
            null,
            algorithm,
            newcw,
            newrate,
            totalhopcount,
            null,
            nextDestionList,
            data);
        newmsgList.add(rbmMsg2);
      }
    }

    return newmsgList;
  } //RBM_face end

}
