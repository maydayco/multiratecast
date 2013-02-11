import java.util.ArrayList;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class FaceMessage
    extends Message {
  private Host sender;
  private boolean cw;
  private int totalhopcount;


  public FaceMessage(Host origsource,
                     Host sender,
                     Host host,
                     ArrayList destList,
                     RoutingAlgorithm algorithm,
                     boolean cw,
                     int hopcount,
                     Object data) {
    super(origsource,host, destList,algorithm, data);
    this.sender = sender;
    this.cw = cw;
    this.totalhopcount = hopcount;

  }

  public Host getSender() {
    return sender;
  }

  public boolean getCW() {
    return cw;
  }

  public void setCW(boolean setcw) {
    cw = setcw;
  }

  public int getTotalhopcount(){
    return totalhopcount;
  }

}
