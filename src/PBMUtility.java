import java.util.ArrayList;
import java.util.Vector;
import java.lang.Object;
 
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class PBMUtility {
  public PBMUtility() {
    try {
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  static final double MaxDistance = 10e10;

   private static int [] initSet(int r)
   {
     int[] set = new int[r];
     for ( int i = 0; i < r; i++ )
     {
       set[i] = i+1;
     }
     return set;
   }

   private static void calculateNextSet( int n, int r, int[] set)
   {
     int i = r;
     while (set[i - 1] == n - r + i) {
       i--;
     }
     set[i - 1]++;
     for (int j = i + 1; j <= r; ++j) {
       set[j - 1] = set[i - 1] + j - i;
     }
   }

   private static double findOneSetCost(int r, int [] set,
                                 Vector oneSetVector,
                                 Host currentNode,
                                 ArrayList neighbors,
                                 ArrayList destList)
  {
    //for a set[r], find the neighbors with minimum remaining distance to dests
    //put those neighbors in vector, vector index = dest number, vector object = forward neighbor
    //return the minDistance

    //build distance array for neighbors in current set
    ArrayList setNeighbors = new ArrayList();
    for ( int i = 0; i < set.length; i++)
    {
      int index = set[i] - 1;
      Host neighbor = (Host) neighbors.get(index);
      setNeighbors.add(neighbor);
    }
    double [][] setDistanceArray = findDistanceArray(currentNode, destList, setNeighbors);
    //the setDistanceArray[r][m]

    //check if any neighbor in set is not common neighbor for all dests
    //if a neighbor is so, return a MaxDistance, hopArray = NULL
    int m = destList.size();
    for ( int i = 0; i < m; i++)
    {
      int num = 0;
      for ( int j = 0; j < r; j++)
      {
        if (setDistanceArray[j][i] < 0)
        {
          num++;
          setDistanceArray[j][i] = MaxDistance;
        }
      }
      if ( num >= r )
      {
        // All the neighbors in set cannot forward to destination[i]
        oneSetVector.clear(); // ???
        return MaxDistance;
      }
    }

    //find forward nodes provides min remain distance
    double totalCost = 0.0;

    //go thourgh destList, find a best forward neighbor for each dest
    //add the distance to totalRemainDistance
    for (int i = 0; i < m; ++i) {//loop through destList
      double minCost = setDistanceArray[0][i];//get first neighbor for each dest
      oneSetVector.add(i, setNeighbors.get(0));
      for (int j = 0; j < r; ++j) {// Loop through the neighbor
        if (minCost > setDistanceArray[j][i]) {
          // Swap
          minCost = setDistanceArray[j][i];
          oneSetVector.set(i, setNeighbors.get(j));
        }
      }
      totalCost += minCost;
    }

    return totalCost;
  }

  private static double[][] findDistanceArray
      (Host currentNode, ArrayList destList, ArrayList neighbors) {
    double[] currentNodeDistance = new double[destList.size()];
    double[][] distanceArray = new double[neighbors.size()][destList.size()];

    // Calculate the distance of current host.
    for (int i = 0; i < destList.size(); ++i) {
      currentNodeDistance[i] = ( (Host) destList.get(i)).getPosition().
          distance(
          currentNode.getPosition());
    }
    // Calculate the distance change of neighbors to each host and store it in the array.
    for (int i = 0; i < neighbors.size(); ++i) {
      Host tempHost = (Host) neighbors.get(i);
      for (int j = 0; j < destList.size(); ++j) {
        Host dest = (Host) destList.get(j);
        double distance = dest.getPosition().distance(tempHost.getPosition());
        if (distance > currentNodeDistance[j]) {
          // Neighbor is away from the destinatin.
          distance = -1;
        }
        distanceArray[i][j] = distance;
      }
    }

    return distanceArray;
  }

  private static double findDistanceCurToAll(Host currentNode, ArrayList destList)
  {
    double total = 0;
    for (int i = 0; i < destList.size(); ++i) {
      total += ( (Host) destList.get(i)).getPosition().
          distance(
          currentNode.getPosition());
    }
    return total;
  }

  /*
   * Get the C(n, r) value.
   */
  private static long getCnr(int n, int r)
  {
    if (n < r || n < 0 || r < 0) {
      // Illegal situation.
      return -1;
    }

    if (r == 0) {
      // No need to calculate.
      return 1;
    }

    /*
     * cnr = (r + 1)(r + 2) ... n / (n - r)! (if r > n/2)
     * cnr = (n - r + 1)(n - r + 2) ... n / r! (if r <= n/2)
     * Take rr = r (if r <= n/2) or (n - r) (if r > n/2)
     * Then,
     * cnr = (n - rr + 1)(rr + 2) ... n / rr!
     */
    int rr = (r <= n/2) ? r : (n - r);
    long result = 1;

    // Calculate (rr + 1)(rr + 2) ... n
    for (int i = (n - rr + 1); i <= n; ++i) {
      result *= i;
    }

    // Calculate cnr = result / rr!
    for (int i = 1; i <= rr; ++i) {
      result /= i;
    }

    return result;
  }

  private static double getMinSetCost_given_r( int r, Vector rSetVector,
                                        Host currentNode,
                                        ArrayList neighbors,
                                        ArrayList destList)
  {
    //return minimum remaining distance for a given number of forward nodes
    //forward node number = r
    int n = neighbors.size();
    long cnr = getCnr(n, r);
    double minCostFor_r = MaxDistance;

    int[] set = new int[r]; /// ?????????????????????????
    set = initSet(r);

    //loop cnr set, find one set with minimum remaining distance
    for (long i = 0; i < cnr; ++i)
    {
      Vector oneSetVector = new Vector();
      double oneSetCost;
      oneSetCost = findOneSetCost(r, set,
                                   oneSetVector,
                                   currentNode,
                                   neighbors,
                                   destList);
      if ( oneSetCost < minCostFor_r ) {
        minCostFor_r = oneSetCost;
        rSetVector.clear();
        for (int k = 0; k < oneSetVector.size(); ++k) {
          rSetVector.add(k, oneSetVector.get(k));
        }
      }
      if (i < (cnr - 1)) {
        calculateNextSet(n,r,set);
      }
    }

    return minCostFor_r;

  }

  private static double findMinCost(double lemda, Vector finalVector,
                                    Host currentNode,
                                    ArrayList neighbors,
                                    ArrayList destList)
  {//find minimum cost for currentNode to select next forward nodes
    int n = neighbors.size();
    int m = destList.size();
    int w = n > m ? m : n;

    double [] costFor_r = new double[w];

    double minCost = MaxDistance;
    double totalCurToAll = findDistanceCurToAll(currentNode, destList);
    for ( int r = 0 ; r < w; r ++ )
    {
      Vector rSetVector = new Vector();
      costFor_r[r] = getMinSetCost_given_r( r+1, rSetVector,
                                             currentNode,
                                             neighbors,
                                            destList);
      costFor_r[r] = (1.0 - lemda) * (r + 1) / n + costFor_r[r] / totalCurToAll;
      if ( costFor_r[r] < minCost ) {
        minCost = costFor_r[r];
        finalVector.clear();
        for (int k = 0; k < rSetVector.size(); ++k) {
          finalVector.add(k, rSetVector.get(k));
        }
      }
    }

    return minCost;
  }


  public static ArrayList getNextHops(Host currentNode, Host sender,
                                       ArrayList origdestList, double lamda)
  {

      ArrayList nullForwardDests = new ArrayList();
      ArrayList hopArray = new ArrayList();
      ArrayList neighbors = currentNode.getIncidentHosts();
      boolean checkf = false;
      if (neighbors.remove(sender)) {
        checkf = true;
      }
      Hop nextHop = new Hop(null, null);

      //create destList for calculation purpose
      ArrayList destList = new ArrayList();
      for (int i = 0; i < origdestList.size(); ++i) {
        destList.add(origdestList.get(i));
      }

      //calculate distanceArray
      double[][] distanceArray = findDistanceArray(currentNode, destList,
                                                   neighbors);
      int[] possibleForwardDestNumber = new int[neighbors.size()];
      int[] possibleForwardNodeNumber = new int[destList.size()];

      //Calculate possible forward node number for each destination
      for (int i = 0; i < destList.size(); ++i) {
        int num = 0;
        for (int j = 0; j < neighbors.size(); ++j) {
          if (distanceArray[j][i] >= 0) {
            num++;
          }
          else {
            distanceArray[j][i] = MaxDistance;
          }
        }
        possibleForwardNodeNumber[i] = num;
      }

      //Find destinations without forward node
      //eliminate them from destList array
      for (int i = 0; i < destList.size(); ++i) {
        if (possibleForwardNodeNumber[i] == 0) {
          Host onedest = (Host) destList.get(i);
          nullForwardDests.add(onedest);
        }
      }
      if (nullForwardDests.size() != 0) {
          for (int ik = 0; ik < nullForwardDests.size(); ++ik) {
          destList.remove(nullForwardDests.get(ik));
        }
      }

      //check if all dests have no forward neighbor, if so return
      if (destList.size() == 0) {
        if (checkf) {
          neighbors.add(sender);
        }
        nextHop.setNode(null);
        nextHop.setDestinationList(nullForwardDests);
        hopArray.add(nextHop);
        return hopArray;
      }

      //search minimum greedy cost for remaining dests
      double lemda = lamda;
      Vector forwardVector = new Vector();
      double cost = findMinCost(lemda, forwardVector,
                                currentNode,
                                neighbors,
                                destList);

      //put forward neighbors in hoparray and return
      Hop nexthop = new Hop(null, null);
      ArrayList nextDestList = new ArrayList();
      for (int i = 0; i < forwardVector.size(); ++i) {
        boolean found = false;
        Host node = (Host) forwardVector.get(i);
        for (int j = 0; j < hopArray.size(); ++j) {
          Hop listedhop = (Hop) hopArray.get(j); // ??? shallow copy ??
          Host listednode = (Host) listedhop.getNode();
          ArrayList listeddests = listedhop.getDestinationList();
          if (listednode.equals(node)) {
            listeddests.add(destList.get(i));
            ( (Hop) hopArray.get(j)).setDestinationList(listeddests);
            found = true;
          }
        }
        if (!found) {
          nexthop = new Hop(null, null);
          nexthop.setNode(node);
          nextDestList = new ArrayList();
          nextDestList.add(destList.get(i));
          nexthop.setDestinationList(nextDestList);
          hopArray.add(nexthop);
        }
      }

      nextHop = new Hop(null, null);
      if (nullForwardDests.size() != 0) {
        nextHop.setDestinationList(nullForwardDests);
        hopArray.add(nextHop);
      }

      //restore neighbors and return hopArray
      if (checkf) {
        neighbors.add(sender);
      }

      return hopArray;

    }

  private void jbInit() throws Exception {
  }

}
