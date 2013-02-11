import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class MessageServer{

  private static MessageServer msgServer;
  private ArrayList messagesIn;
  private ArrayList messagesOut;

  private MessageServer() {
    messagesIn = new ArrayList();
    messagesOut = new ArrayList();
  }

  public static MessageServer getInstance() {
    if (msgServer == null) {
      msgServer = new MessageServer();
    }

    return msgServer;
  }

  public synchronized void send(Message msg) {
    // A node send message in.
    messagesIn.add(msg);
    messagesOut = new ArrayList();

    Host nextnode = msg.getCurrentHost();
    //check path end message, this message will not
    //be forwarded to next node.
    if (nextnode == null) {
      messagesIn.remove(msg);
    }


    //update pathlist
    //Nov 26,2007 remove total hop count and total rate cost 
    //from currrent node to all next forwarding node
    //change to calculate one hop and one rate cost
    Network network = Network.getInstance();
    Pathlist apathlist = network.getPathlist();
    double temprate = 0;
    if ( nextnode != null ) {
      //apathlist.addTotalHopCount();
      if ( msg instanceof RateBasedMulticastMessage ) {
         RateBasedMulticastMessage tempmsg = (RateBasedMulticastMessage) msg;
         temprate = tempmsg.getCurrentRate();
      }
      //apathlist.addTotalRateCost( temprate );
    }
    apathlist.addpath(msg);
    network.setPathlist(apathlist);
  }

  public synchronized void reset() {
	  messagesIn.clear();
      messagesOut.clear();  
  }
  
  public synchronized ArrayList getAllMessages() {
    for ( int i = 0; i < messagesIn.size(); ++i){
      messagesOut.add((Message) messagesIn.get(i));
    }
    
    //Nov 26, 2007 new hop count and rate cost calculation
    // HashMap: Key is the host, value the largest rate.
    Network network = Network.getInstance();
    Pathlist apathlist = network.getPathlist();
    Host sendNode = null;
    Host sourceNode = null;
    Host currentNode = null;
    HashMap<Host, Double> rateMap = new HashMap<Host, Double>();
    for (int i = 0; i < messagesIn.size(); ++i) {
    	Message msg = (Message) messagesIn.get(i);
    	sendNode = null;
    	Double newRate = null;
    	if ( msg instanceof RateBasedMulticastMessage ) {
    		 RateBasedMulticastMessage tempmsg = (RateBasedMulticastMessage) msg;
    		 newRate = new Double(tempmsg.getCurrentRate());
             sendNode = (Host) tempmsg.getSender();
             sourceNode = (Host) tempmsg.getSource();
             currentNode = (Host )tempmsg.getCurrentHost();
    	}  else {
    		newRate = new Double (1);
    		if ( msg instanceof PositionBasedMulticastMessage ) {
    			PositionBasedMulticastMessage tempmsg = (PositionBasedMulticastMessage) msg;
    			sendNode = (Host) tempmsg.getSender();
    			sourceNode = (Host) tempmsg.getSource();
    			currentNode = (Host) tempmsg.getCurrentHost();
    		} else if ( msg instanceof  FaceMessage){
    			FaceMessage tempmsg = (FaceMessage) msg;
    			sendNode = (Host) tempmsg.getSender();
    			sourceNode = (Host) tempmsg.getSource();
    			currentNode = (Host) tempmsg.getCurrentHost();
    		} 
    	}
    	
    	if (rateMap.containsKey(sendNode)) {
    		// Source already exists.
    		if (rateMap.get(sendNode).compareTo(newRate) < 0) {
    			// Found a larger rate
    			rateMap.put(sendNode, newRate);
    		}
    	} else {
    		rateMap.put(sendNode, newRate);
    	}
    	
    	
    }
    
    /* original code for multiratecast,  
     * use hashmap to get one rate for multiple messages from 1 node
     */
    boolean sum_of_unicast = false;

    if ( ! sum_of_unicast ) {
    	if ( sendNode != null ) {
    		//if ( sendNode = sourceNode = currentNode ), 
    		//then it is the source start hop
    		// do not count hop count and rate cost
    		if ( ! sendNode.equals(sourceNode) || ! sendNode.equals(currentNode) ) {
    			Vector<Double> allrate = new Vector<Double>();
    			allrate.addAll(rateMap.values());

    			for (int i = 0; i < rateMap.size(); ++i) {
    				apathlist.addTotalHopCount();
    				apathlist.addTotalRateCost(allrate.get(i).doubleValue());
    			}
    		}
    	}
    } else {

    	/* new code 2008/09/26 for sum_of_unicast, below
    	 * 
    	 */

    	if ( sendNode != null ) {
    		//if ( sendNode = sourceNode = currentNode ), 
    		//then it is the source start hop
    		// do not count hop count and rate cost
    		if ( ! sendNode.equals(sourceNode) || ! sendNode.equals(currentNode) ) {
    			for (int i = 0; i < messagesIn.size(); ++i) {
    				Message msg = (Message) messagesIn.get(i);
    				Double newRate = new Double (1);
    				if ( msg instanceof RateBasedMulticastMessage ) {
    					RateBasedMulticastMessage tempmsg = (RateBasedMulticastMessage) msg;
    					newRate = new Double(tempmsg.getCurrentRate());
    				}
    				apathlist.addTotalHopCount();
    				apathlist.addTotalRateCost(newRate);
    			}
    		}
    	}
    	// new code 2008/09/26 end

    }
    
    
    messagesIn = new ArrayList();
    return messagesOut;
  }
}
