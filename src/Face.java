

import java.util.ArrayList;

/**  
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class Face
    extends RoutingAlgorithm {

    final int Maxhopcount = 500;
    public ArrayList routing(Host ahost, Message message){

    ArrayList msgList = new ArrayList();
    FaceMessage msg = (FaceMessage) message;

    // Face routing.
    Host source = msg.getSource();
    Host currentNode = msg.getCurrentHost();
    if ( !currentNode.equals(ahost)) {
      // debug purpose
      Host a = null;
    }
    ArrayList dests = msg.getDestinationList();
    RoutingAlgorithm algorithm = msg.getRoutingAlgorithm();
    Object data = msg.getData();

    Host sender = msg.getSender();
    boolean cw = msg.getCW();
    int totalhopcount = msg.getTotalhopcount();

    // Max_HopCount reached, fail
    if (totalhopcount >= Maxhopcount)
    {
      Message amessage = new FaceMessage(source,
                     currentNode,
                     null,
                     dests,
                     algorithm,
                     cw,
                     totalhopcount,
                     data);
      msgList.add(amessage);
      return msgList;
    }

    // Face routing.
    // Perform routing for each source/dest pair
    for (int ii = 0; ii < dests.size(); ++ii) {
        Host onedest = (Host) dests.get(ii);

        if (currentNode.equals(onedest)){
          ArrayList reachdest = new ArrayList();
          reachdest.add(onedest);
          Message amessage = new FaceMessage(source,
                            currentNode,
                            null,
                            reachdest,
                            algorithm,
                            cw,
                            totalhopcount,
                            data);
             msgList.add(amessage);
             continue;
        }
        Host nextNode = null;
        boolean newcw = cw; // right hand default

          //Firstly, generate gabriel graph
          Edge virtualEdge = new Edge(source, onedest);
          Edge newEdge = new Edge();
          ArrayList gg = new ArrayList();
          Network network = Network.getInstance();
          HostArray hostLists = network.getHostArray();
          gg = FaceUtility.getGabrielGraph(network);

          // Secondly, findout the next face routing node
          // if at originate source node, find first face node and set cw
          if (currentNode.equals(source)) {
            newEdge = FaceUtility.getFirstFaceEdge(gg, source, onedest);
            // Found the next host node.
            for (int i = 0; i < hostLists.size(); ++i) {
              nextNode = hostLists.get(i);
              if (nextNode.equals(newEdge.b)) {
                break;
              }
            }
            if (!Edge.isRightFrom(virtualEdge, nextNode)) {
              newcw = false;
            }
          }

          // if at forwarding node, find next face node according to cw
          // if intersect with virtual edge line, reset cw = !cw
          else {
            newEdge = FaceUtility.getNextFaceEdge(gg, sender, currentNode, cw);
            // Found the next host node.
            for (int i = 0; i < hostLists.size(); ++i) {
              nextNode = hostLists.get(i);
              if (nextNode.equals(newEdge.b)) {
                break;
              }
            }
            if (Edge.isIntersect(virtualEdge, newEdge)) {
              newcw = !cw;
            }
          }

          totalhopcount++;
          // Finally,construct new fmsg and send back to msglist
          ArrayList faceDest = new ArrayList();
          faceDest.add(onedest);
          FaceMessage fmsg = new FaceMessage(source,
                     currentNode,
                     nextNode,
                     faceDest,
                     algorithm,
                     newcw,
                     totalhopcount,
                     data);


          msgList.add(fmsg);
       }
     return msgList;
  }//end of rouing method, should return ArrayList of messages

}

