import java.util.ArrayList;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Message {
  public Message() {
    try {
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private Host origsource; // the original source of message
  private Host currentNode;
                //the next node will process message in msgServer
                //the current node perform routing in LocalRoutingHost
                  //currentNode is atcually next forwarding neighbor
  private ArrayList destinationList;
  private RoutingAlgorithm algorithm; //an object shows algorithm
  private Object data;// real data need to be sent

  /*
  public static final int GREEDY_MODE = 0;
  public static final int FACE_MODE = 1;
  public static final int GREEDY_FACE_GREEDY_MODE = 2;
  */
  public Message(Host source, Host host, ArrayList destList,
                 RoutingAlgorithm r_algorithm, /* int routingMode,*/
                 Object data) {
    origsource = source;
    currentNode = host;
    destinationList = destList;
    algorithm = r_algorithm;
    this.data = data;
  }

  public Host getCurrentHost() {
    return currentNode;
  }

  public Host getSource() {
    return origsource;
  }
  public ArrayList getDestinationList() {
    return destinationList;
  }

  public Object getData() {
    return data;
  }

  public RoutingAlgorithm getRoutingAlgorithm(){
    return algorithm;
  }

  private void jbInit() throws Exception {
  }

}
