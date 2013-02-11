import java.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */ 

public class GreedyUtility {

  public GreedyUtility() {
  }

  public static Hop getNextHop(Host currentNode, ArrayList destList) {
    Hop nextHop = new Hop(null, null);

    ArrayList neighbors = currentNode.getIncidentHosts();
    double[][] distanceArray = new double[neighbors.size()][destList.size()];
    int[] possibleForwardDestNumber = new int[neighbors.size()];

    // Calculate the distance of current host.
    double[] currentNodeDistance = new double[destList.size()];
    for (int i = 0; i < destList.size(); ++i) {
      currentNodeDistance[i] = ( (Host) destList.get(i)).getPosition().distance(
          currentNode.getPosition());
    }

    // Calculate the distance of neighbors to each host and store it in the array.
    for (int i = 0; i < neighbors.size(); ++i) {
      Host tempHost = (Host) neighbors.get(i);
      int forwardNumber = destList.size();
      for (int j = 0; j < destList.size(); ++j) {
        Host dest = (Host) destList.get(j);
        double distance = dest.getPosition().distance(tempHost.getPosition());
        if (distance > currentNodeDistance[j]) {
          // Neighbor is away from the destinatin.
          distance = -1;
          --forwardNumber;
        }
        distanceArray[i][j] = distance;
      }
      possibleForwardDestNumber[i] = forwardNumber;
    }

    // Find the neighbor can provide most forward process.
    int bestNumber = 0;
    double shortestPath = -1;
    int neighborIndex = -1;
    for (int i = 0; i < neighbors.size(); ++i) {
      if (possibleForwardDestNumber[i] > bestNumber) {
        bestNumber = possibleForwardDestNumber[i];
        neighborIndex = i;
        double sumDistance = 0;
        for (int j = 0; j < destList.size(); ++j) {
          if (distanceArray[i][j] >= 0) {
            sumDistance += distanceArray[i][j];
          }
        }
        shortestPath = sumDistance;
      }
      else if ( (possibleForwardDestNumber[i] == bestNumber) &&
               (possibleForwardDestNumber[i] != 0)) {
        // Have same number of forward possibility.
        double sumDistance = 0;
        for (int j = 0; j < destList.size(); ++j) {
          if (distanceArray[i][j] >= 0) {
            sumDistance += distanceArray[i][j];
          }
        }
        if (sumDistance < shortestPath) {
          // Find the shorter one.
          neighborIndex = i;
          shortestPath = sumDistance;
        }
      }
    }

    // Construct a new hop for the given situation.
    if (neighborIndex == -1) {
      // Cannot fowward to any destination.
      nextHop.setNode(null);
      ArrayList newDest = new ArrayList();
      for (int i = 0; i < destList.size(); ++i) {
           newDest.add(destList.get(i));
       }
      nextHop.setDestinationList(newDest);
      // stop the routing.
      return nextHop;
    }

    // Continue on the forward decision.
    ArrayList newDest = new ArrayList();
    for (int i = 0; i < destList.size(); ++i) {
      if (distanceArray[neighborIndex][i] >= 0) {
        newDest.add(destList.get(i));
      }
    }

    nextHop.setNode( (Host) neighbors.get(neighborIndex));
    nextHop.setDestinationList(newDest);

    return nextHop;

  }
}