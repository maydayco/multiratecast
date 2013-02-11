import java.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class RateBasedHop extends Hop {
  private double cost;

  public RateBasedHop(Host node, ArrayList dest, double acost) {
    super(node,dest);
    cost = acost;
  }

  public void setCost ( double acost ) {
    cost = acost;
  }

  public double getCost ( ) {
    return  cost;
  }

}