import java.awt.geom.Point2D;
import java.awt.Graphics;
import java.util.ArrayList;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class LocalRoutingHost
    extends Host {
  private static final double FACE_LOOP_PARA = 1.5;

  private double rate;

  public LocalRoutingHost(Point2D.Double position) {
    this(position, 1.0);
  }

  public LocalRoutingHost(double posX, double posY) {
    this(posX, posY, 1.0);
  }

  public LocalRoutingHost(Point2D.Double position, double rate) {
    super(position);
    this.rate = rate;
  }

  public LocalRoutingHost(double posX, double posY, double rate) {
    super(posX, posY);
    this.rate = rate;
  }

  public void setRate(double r) {
    rate = r;
  }

  public double getRate() {
    return rate;
  }

  public void receive(Message msg) {
    RoutingAlgorithm rr = msg.getRoutingAlgorithm();
    ArrayList newmsglist = rr.routing(this, msg);
              //get new messages need to be sent to next nodes

    for ( int i = 0; i < newmsglist.size(); ++i){
      Message amessage = (Message) newmsglist.get(i);
      this.send(amessage);
    }
  }

  public void send(Message msg){
    MessageServer msgServer = MessageServer.getInstance();
    msgServer.send(msg);
  }
}