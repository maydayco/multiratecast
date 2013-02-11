import java.util.ArrayList;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class Greedy
    extends RoutingAlgorithm {

  public ArrayList routing(Host ahost, Message msg){

    // Greedy routing.
    Host source = msg.getSource();
    Host currentNode = msg.getCurrentHost();
    ArrayList dests = msg.getDestinationList();
    RoutingAlgorithm algorithm = msg.getRoutingAlgorithm();
    Object data = msg.getData();
    ArrayList msgList = new ArrayList();

    ArrayList destList = new ArrayList();
    for (int i = 0; i < dests.size(); ++i) {
      Host onedest = (Host) dests.get(i);
      if (currentNode.equals(onedest)){
        //????????????????????????
        //one path finish, other continue;
        continue;
      }
      destList.add(onedest);
    }

    while (destList.size() != 0) {
      Hop nextHop = GreedyUtility.getNextHop(currentNode, destList);
      Host nextNode = nextHop.getNode();
      ArrayList nextDestionList = nextHop.getDestinationList();
      // Send message to next host.
      Message newmsg = new Message(source,
                                nextNode,
                                nextDestionList,
                                algorithm,
                                data);
      msgList.add(newmsg);
      // See if all the hosts has been covered.
      for (int i = 0; i < nextDestionList.size(); ++i) {
        destList.remove(nextDestionList.get(i));
      }
    }

    return msgList;
  }//end of rouing method, should return ArrayList of messages




}

