import java.util.ArrayList;
import java.util.Collection;
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

public class RBMUtility {

  public RBMUtility() {
  }


  public static int findorigindex(ArrayList<Host> destList, Host adest) {
    for (int i = 0; i < destList.size(); ++i) {
      Host temphost = destList.get(i);
      if (temphost.equals(adest)) {
        return i;
      }
    }
    return -1;
  }

 //This function calculate distance from each neighbor to each destination
 //store result in distanceArray
  private static double[][] findDistanceArray
      (Host currentNode, ArrayList<Host> destList, ArrayList<Host> neighbors) {
    double[] currentNodeDistance = new double[destList.size()];
    double[][] distanceArray = new double[neighbors.size()][destList.size()];

    // Calculate the distance of current host.
    for (int i = 0; i < destList.size(); ++i) {
      currentNodeDistance[i] = (destList.get(i)).getPosition().
          distance(currentNode.getPosition());
    }
    // Calculate the distance change of neighbors to each host and store it in the array.
    for (int i = 0; i < neighbors.size(); ++i) {
      Host tempHost = neighbors.get(i);
      for (int j = 0; j < destList.size(); ++j) {
        Host dest = (Host) destList.get(j);
        double distance = dest.getPosition().distance(tempHost.getPosition());
        if (distance > currentNodeDistance[j]) {
          // Neighbor is away from the destination.
          distance = -1;
        }
        distanceArray[i][j] = distance;
      }
    }

    return distanceArray;

  }

  //find distance change for neighbors to all destinations
  //store in distanceArray
  private static double[][] findDistanceChange
      (Host currentNode, ArrayList<Host> destList, ArrayList<Host> neighbors) {
    double[] currentNodeDistance = new double[destList.size()];
    double[][] distanceArray = new double[neighbors.size()][destList.size()];

    // Calculate the distance of current host.
    for (int i = 0; i < destList.size(); ++i) {
      currentNodeDistance[i] = (destList.get(i)).getPosition().
          distance(currentNode.getPosition());
    }
    // Calculate the distance change of neighbors to each host and store it in the array.
    for (int i = 0; i < neighbors.size(); ++i) {
      Host tempHost = (Host) neighbors.get(i);
      for (int j = 0; j < destList.size(); ++j) {
        Host dest = destList.get(j);
        double distance = dest.getPosition().distance(tempHost.getPosition());
        if (distance > currentNodeDistance[j]) {
          // Neighbor is away from the destination.
          distance = -1;
        }
        else {
          distance = currentNodeDistance[j] - distance;
        }
        distanceArray[i][j] = distance;
      }
    }

    return distanceArray;

  }

  //find rate based hop for given destinations,
  //these destinations should at least have one common neighbor provide 
  // forward
  //if no common neighbor, return null
  private static RateBasedHop findRateBasedHop(
      Host currentNode, ArrayList<Host> dests, ArrayList<Host> neighbors) {
    RateBasedHop onehop = new RateBasedHop(null, null, -1);
    double cost = 0;
    double distanceArray2[][] = findDistanceChange(currentNode, dests,
        neighbors);
    int[] possibleForwardNodeNum = new int[neighbors.size()];

    double maxrate = 0;

    for (int kk = 0; kk < dests.size(); ++kk) {
      LocalRoutingHost onedest = (LocalRoutingHost) dests.get(kk);
      double rate = onedest.getRate();
      if (rate > maxrate) {
        maxrate = rate;
      }
      for (int pp = 0; pp < neighbors.size(); ++pp) {
        if (distanceArray2[pp][kk] > 0) {
          possibleForwardNodeNum[pp]++;
        }
      }
    }

    Vector<Host> selectNeighbors = new Vector<Host>();
    Vector<Host> selectCost = new Vector<Host>();
    for (int pp = 0; pp < neighbors.size(); ++pp) {
      if (possibleForwardNodeNum[pp] == dests.size()) {
        selectNeighbors.add(neighbors.get(pp));
      }
    }

    double mincostOfonegroup = Double.MAX_VALUE;

    for (int pp = 0; pp < selectNeighbors.size(); ++pp) {
      Host ahost = selectNeighbors.get(pp);
      int origindex = findorigindex(neighbors, ahost);
      double sumdistancechange = 0;
      for (int kk = 0; kk < dests.size(); ++kk) {
        if (distanceArray2[origindex][kk] > 0) {
          sumdistancechange += distanceArray2[origindex][kk];
        }
      }
      //cost function:
      //( maxRate of dests)/ [(cd1-nd1) + (cd2-nd2) + ... + (cdm-ndm)]
      // c: current node , n: selected neighbor, d1-dm: destinations

      cost = maxrate / sumdistancechange;
      if (cost < mincostOfonegroup) {
        mincostOfonegroup = cost;
        onehop.setNode(selectNeighbors.get(pp));
        onehop.setDestinationList(dests);
        onehop.setCost(cost);
      }
    }
    return onehop;

  }

  private static RateBasedHop findRateBasedHop1(
      Host currentNode, ArrayList<Host> dests, ArrayList<Host> neighbors) {
    RateBasedHop onehop = new RateBasedHop(null, null, -1);
    double cost = 0;
    double distanceArray2[][] = findDistanceChange(currentNode, dests,
        neighbors);
    int[] possibleForwardNodeNum = new int[neighbors.size()];

    double maxrate = 0;

    for (int kk = 0; kk < dests.size(); ++kk) {
      LocalRoutingHost onedest = (LocalRoutingHost) dests.get(kk);
      double rate = onedest.getRate();
      if (rate > maxrate) {
        maxrate = rate;
      }
      for (int pp = 0; pp < neighbors.size(); ++pp) {
        if (distanceArray2[pp][kk] > 0) {
          possibleForwardNodeNum[pp]++;
        }
      }
    }

    //find out neighbors provides progress for all destinations in this group
    Vector<Host> selectNeighbors = new Vector<Host>();
    Vector<Host> selectCost = new Vector<Host>();
    for (int pp = 0; pp < neighbors.size(); ++pp) {
      if (possibleForwardNodeNum[pp] == dests.size()) {
        selectNeighbors.add(neighbors.get(pp));
      }
    }

    double mincostOfonegroup = Double.MAX_VALUE;

    for (int pp = 0; pp < selectNeighbors.size(); ++pp) {
      double costForOneDest = 0;
      Host ahost = selectNeighbors.get(pp);
      int origindex = findorigindex(neighbors, ahost);
      cost = 0;
      /*
               for (int kk = 0; kk < dests.size(); ++kk) {
        if (distanceArray2[origindex][kk] > 0) {
          LocalRoutingHost onedest = (LocalRoutingHost) dests.get(kk);
          costForOneDest = onedest.getRate()/distanceArray2[origindex][kk];
          cost += costForOneDest;
        }
               }
       */
      //change to rate based routing, use Rmax
      for (int kk = 0; kk < dests.size(); ++kk) {
        if (distanceArray2[origindex][kk] > 0) {
          LocalRoutingHost onedest = (LocalRoutingHost) dests.get(kk);
          costForOneDest = maxrate / distanceArray2[origindex][kk];
          cost += costForOneDest;
        }
      }

      //cost function:
      // r1/(cd1-nd1) + r2/(cd2-nd2) + ... + rm/(cdm-ndm)
      // c: currrent node, d1-dm: destinations, n: selected neighbor
      // r1 to rm: rate of each destination
      // for rate based routing, change to:
      // Rmax/(cd1-nd1) + Rmax/(cd2-nd2) + ... + Rmax/(cdm-ndm)
      if (cost < mincostOfonegroup) {
        mincostOfonegroup = cost;
        onehop.setNode(ahost);
        onehop.setDestinationList(dests);
        onehop.setCost(cost);
      }
    }
    return onehop;

  }

  //The new cost function, given a set partition group,
  //find a neighbor node provide most distance progress
  private static RateBasedHop findRateBasedHop2(
      Host currentNode, ArrayList<Host> dests, ArrayList<Host> neighbors) {
    RateBasedHop onehop = new RateBasedHop(null, null, -1);
    double cost = 0;
    double distanceArray2[][] = findDistanceChange(currentNode, dests, neighbors);
    int[] possibleForwardNodeNum = new int[neighbors.size()];

    for (int kk = 0; kk < dests.size(); ++kk) {
      for (int pp = 0; pp < neighbors.size(); ++pp) {
        if (distanceArray2[pp][kk] > 0) {
          possibleForwardNodeNum[pp]++; /// ????????????
        }
      }
    }

    //find out neighbors provides progress for all destinations in this group
    Vector<Host> selectNeighbors = new Vector<Host>();
    Vector<Host> selectCost = new Vector<Host>();
    for (int pp = 0; pp < neighbors.size(); ++pp) {
      if (possibleForwardNodeNum[pp] == dests.size()) {
        selectNeighbors.add(neighbors.get(pp));
      }
    }

    double maxcostOfonegroup = -1;

    for (int pp = 0; pp < selectNeighbors.size(); ++pp) {
      double costForOneDest = 0;
      //the meaning of cost here is distance change
      Host ahost = selectNeighbors.get(pp);
      int origindex = findorigindex(neighbors, ahost);
      cost = 0;
      for (int kk = 0; kk < dests.size(); ++kk) {
        if (distanceArray2[origindex][kk] > 0) {
          LocalRoutingHost onedest = (LocalRoutingHost) dests.get(kk);
          costForOneDest = distanceArray2[origindex][kk];
          cost += costForOneDest;
        }
      }
      //cost function:
      // (cd1-nd1) + (cd2 - nd2) + ... + (cdm-ndm)
      // for rate based routing, it is same as cost mode 0
      if (cost > maxcostOfonegroup) {
        maxcostOfonegroup = cost;
        onehop.setNode(ahost);
        onehop.setDestinationList(dests);
        onehop.setCost(cost);
      }
    }

    double grate = RBMUtility.getMaxDestRate(dests);
    onehop.setRate(grate);
    return onehop;

  }

  public static double getMaxDestRate(ArrayList<Host> nextDestinationList) {
    double maxrate = 0;
    LocalRoutingHost nextNode = null;
    for (int i = 0; i < nextDestinationList.size(); i++) {
      nextNode = (LocalRoutingHost) nextDestinationList.get(i);
      double nextrate = nextNode.getRate();
      maxrate = nextrate >= maxrate ? nextrate : maxrate;
    }
    return maxrate;
  }

  /* This is new algorithm */
  public static ArrayList<Hop> getNextHops_new(Host currentNode, Host sender,
                                          ArrayList<Host> origdestList, int costmode) {
    ArrayList<Hop> hopArray = new ArrayList<Hop>();
    ArrayList<Host> neighbors = currentNode.getIncidentHosts();
    boolean checkf = false;
    if (neighbors.remove(sender)) {
      checkf = true;
    }

    ArrayList<Host> newdests = new ArrayList<Host>();//new destination in hops in hopArray
    Hop nextHop = new Hop(null, null);

    //create destList for calculation purpose,
    //destinations may be removed from this destList if found forwarding neighbor for it
    ArrayList<Host> destList = new ArrayList<Host>();
    for (int i = 0; i < origdestList.size(); ++i) {
      destList.add(origdestList.get(i));
    }


    //Find destinations without progress neighbors, put Hop(null, thoseDests)
    //in hopArray
    nextHop = new Hop(null, null);
    newdests = new ArrayList<Host>();
    //distanceArray[neighborIndex][destIndex]
    double[][] distanceArray = findDistanceChange(currentNode, destList,
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
      }
      possibleForwardNodeNumber[i] = num;
    }

    //Find destinations without forward node
    //eliminate them from destList array
    for (int i = 0; i < destList.size(); ++i) {
      if (possibleForwardNodeNumber[i] == 0) {
        Host onedest = destList.get(i);
        newdests.add(onedest);
      }
    }
    if (newdests.size() != 0) {
      nextHop.setNode(null);
      nextHop.setDestinationList(newdests);
      hopArray.add(nextHop);

      for (int ik = 0; ik < newdests.size(); ++ik) {
        destList.remove(newdests.get(ik));
      }
    }

    if (destList.size() == 0) {
      if (checkf) {
        neighbors.add(sender);
      }
      return hopArray;
    }

    //sort origDestList in increase order, save in increaseRateDestList
    double selectRate = 0;
    ArrayList<Host> increaseRateDestList = new ArrayList<Host>();
    increaseRateDestList.add(origdestList.get(0));
    for (int j = 1; j < destList.size(); ++j) {
      LocalRoutingHost adest = (LocalRoutingHost) destList.get(j);
      double rate = adest.getRate();
      for (int i = increaseRateDestList.size() - 1; i >= 1; i--) {
        if (rate > ( (LocalRoutingHost) increaseRateDestList.get(i)).getRate()) {
          if (i == increaseRateDestList.size() - 1) { // add last one
            increaseRateDestList.add(adest);
          }
          else { // add in middle
            increaseRateDestList.add(null);
            for (int ik = increaseRateDestList.size(); ik >= (i + 2); ik--) {
              LocalRoutingHost movehost = (LocalRoutingHost)
                  increaseRateDestList.get(ik - 1);
              increaseRateDestList.set(ik, movehost);
              increaseRateDestList.set(i, adest);
            }
          }
        }
      }
    }

    //if some neighbors are destinations,
    //select them , cover possible other destinations
    nextHop = new Hop(null, null);
    newdests = new ArrayList<Host>();
    for (int j = 0; j < origdestList.size(); ++j) {
       Host adest = (Host) origdestList.get(j);
       if (neighbors.contains(adest)) {
         nextHop = new Hop(adest, null);
         newdests = new ArrayList<Host>();
         newdests.add(adest);
         nextHop.setDestinationList(newdests);
         nextHop.setRate(((LocalRoutingHost) adest).getRate());
         destList.remove(adest);
         hopArray.add(nextHop);
       }
     }


     //see if those destinations/neighbors can cover other destinations
     //we assume all destination rates are increase by there index number
     //so we try to find max rate destination/neighbor and see if it covers others
     //and the second max rate, and so on
     for ( int i = hopArray.size()-1 ; i >= 0; i --)
     {
       Hop ahop = (Hop) hopArray.get(i);
       Host adest = (Host) ahop.getNode();
       if ( adest != null ) {
         newdests = ahop.getDestinationList();
         for (int j = 0; j < destList.size(); ++j) {
           Host onedest = (Host) destList.get(j);
           if (FaceUtility.isClosertoDest(adest, currentNode, onedest)) {
             newdests.add(onedest);
           }
         }
         ahop.setDestinationList(newdests);
         for (int k = 0; k < newdests.size(); ++k) {
           destList.remove(newdests.get(k));
         }
       }
     }

     //check if any remaining dests need to find forward node
     //if no remaining dests, return
     if (destList.size() == 0) {
       if (checkf) {
         neighbors.add(sender);
       }
       return hopArray;
     }


    //Till now, all destinations in destList should at least have one possible
    //forward neighbor, and they are not any of the destinations.
    //now we try to find neighbor node closest to max rate destination
    //select that neighbor, see if it can cover other destinations,
    //remove the covered destinations, put the neighbor node and covered dests in
    //hopArray,  repeat, until all destinations are covered
    double maxrate = 0;
    LocalRoutingHost maxrateDest = null;
    int maxrateDestIndex = 10000; //the index number of max rate destination

    while ( destList.size() > 0 ) {
      //find the destination with max rate
      maxrate = 0;
      for ( int i=0; i< destList.size(); i++ ) {
        LocalRoutingHost adest = (LocalRoutingHost) destList.get(i);
        double rate = adest.getRate();
        if ( rate > maxrate ) {
          maxrate = rate;
          maxrateDest = adest;
          maxrateDestIndex = findorigindex(origdestList, adest);
        }
      }

      //find the neighbor node provides best distance progress for it
      double maxProgress = 0;
      int maxProgressNeighborIndex = 10000;
            for ( int i = 0; i < neighbors.size(); i++ ) {
        double progress = distanceArray[i][maxrateDestIndex];
        if ( progress > maxProgress ) {
          maxProgress = progress;
          maxProgressNeighborIndex = i;
        }
      }

      //save the maxProgressNeighbor to hopArray
      //cover all destinations it can provide progress
      Host ahost = neighbors.get(maxProgressNeighborIndex);
      nextHop = new Hop(null, null);
      newdests = new ArrayList<Host>();
      nextHop.setNode(ahost);
      for ( int i = 0; i < destList.size(); i++ ) {
        Host adest = (Host) destList.get(i);
        int origi = findorigindex(origdestList, adest);
        if ( distanceArray[maxProgressNeighborIndex][origi] >= 0 ) {
          newdests.add(destList.get(i));
        }
      }
      nextHop.setDestinationList(newdests);
      nextHop.setRate(maxrate);
      hopArray.add(nextHop);

      //remove its covered destinations from destList
      for (int ik = 0; ik < newdests.size(); ++ik) {
        destList.remove(newdests.get(ik));
      }


    }// end of while loop

    return hopArray;
  }









private static Vector<ArrayList<Host>> initSet (ArrayList<Host> destList, int[] bestNodeIndex ) {
	if ( destList.size() == 0 ) {
	   return null; 	
	}
	
	//search bestNodeIndex[destList.size()] Array
	//any destination with same neighbor node index will be grouped into same subset
	//node index = -1 meaning no forwarding neighbor for this destination, so ignore this destination 
	HashMap<Integer, ArrayList<Host>> setMap = new HashMap<Integer, ArrayList<Host>>();
	
	for (int i = 0; i< destList.size(); ++i) {
		if (bestNodeIndex[i] >= 0) {
				if (setMap.containsKey(new Integer(bestNodeIndex[i]))) {
					ArrayList<Host> subsetList = setMap.get(new Integer(
							bestNodeIndex[i]));
					subsetList.add(destList.get(i));
				} else {
					ArrayList<Host> subsetList = new ArrayList<Host>();
					subsetList.add(destList.get(i));
					setMap.put(bestNodeIndex[i], subsetList);
				}
			}
	}
	
	// Construct the return vector.
	Vector<ArrayList<Host>> set = new Vector<ArrayList<Host>>();
	set.addAll(setMap.values());
	
	return set;
	
}

private static Vector<ArrayList<Host>> mergeSet(
	    	Vector<ArrayList<Host>> currentSet, 
	    	int i, int j,
		    Host currentNode, ArrayList neighbors,
		    ArrayList destList) {
	
	int len = currentSet.size();
	if ( i > len  || j > len ) {
	   return null;	
	}
	
	Vector<ArrayList<Host>> newSet = new Vector();
	ArrayList<Host> newSubset = new ArrayList<Host> ();
	
	newSubset.addAll(currentSet.get(i));
	newSubset.addAll(currentSet.get(j));
	
	//check if there is any common neighbor for new subset, 
	// if no common neighbor, mergeSet is not possible, return null
	boolean found = false;
	    
	search:
	for ( int nindex = 0; nindex < neighbors.size(); nindex ++) {
		int count = 0;
		for ( int dindex = 0; dindex < newSubset.size(); dindex ++) {
			Host onedest = (Host)newSubset.get(dindex); 
			Host oneneighbor = (Host) neighbors.get(nindex);
			double distance_curNode =  currentNode.getPosition().
	              distance(onedest.getPosition());
			double distance_curNeighbor = oneneighbor.getPosition().
			      distance(onedest.getPosition());
			if (distance_curNode > distance_curNeighbor ) {
				count ++;
			}
			if ( count >= newSubset.size()) {
				found = true;
				break search;
			}
		}
	}
	
	if ( ! found ) { return null; }
	
	int index = 0;
	for ( int k = 0; k < len ; k ++) {
		
		if ( k != i  && k != j) {
		   newSet.add(index, currentSet.get(k));
		   index++;
		} 
	}
	newSet.add(index, newSubset);
	
	return newSet;
}
	


private static double getSetCost ( 
		Vector<ArrayList<Host>> currentSet, ArrayList destList,
		int costmode, Host currentNode, ArrayList neighbors,
		ArrayList returnHopArray) {
	
	double setcost = -1;               
	double setRateSum = 0;
	double setProgressSum = 1;
	boolean ignoreflag = false;

	//numOfgroups is the number of subset in one set
	int numOfgroups = currentSet.size();
    
	//destgroup[?] is a destination subset
	
	ArrayList[] destgroup = new ArrayList[numOfgroups];
	for (int j = 0; j < numOfgroups; ++j) {
		destgroup[j] = currentSet.get(j);
	}
	setcost = 0;

	// process one subset after another
	ArrayList subsetHopArray = returnHopArray;
	
	for (int j = 0; j < numOfgroups; ++j) {
		if (ignoreflag) {
			setcost = -1;
			break;
		}
		ArrayList dests = destgroup[j];
		// find cost for one dest groups in a subset partition
		RateBasedHop onehop = null;
		if (costmode == 0) {
			onehop = findRateBasedHop(currentNode, dests, neighbors);
		} else if (costmode == 1) {
			onehop = findRateBasedHop1(currentNode, dests, neighbors);
		}

		else if (costmode == 2) {
			// this is the cost per progress function as spain team
			// m/(p1+p2+p3...+pm)
			// we change to use rate as cost
			// so the cost per progress is
			// (R1+R2+...Rm) / (p1+p2+...+pm)
			// R is the max destination rate in one group in a subset
			//
			onehop = findRateBasedHop2(currentNode, dests, neighbors);
		}
		if (onehop.getNode() == null) {
			ignoreflag = true;
			setcost = -1;
			continue;
		} else {
			if (costmode != 2) {
				setcost += onehop.getCost();
				subsetHopArray.add(onehop);
			} else {
				// only for m/(p1+p2+p3...pm)
				// only for (R1+R2+...Rm) / (p1+p2+...pm)
				// if all R=1, the two equcation is same
				
				boolean GRM = true;
				if ( GRM ) {
					// Liu change for GMR
					// It is same as ORCM_C with only difference
					// that the setRateSum is not "sum of rate"
					// but "sum of subset number " instead

					setRateSum += 1;
					setProgressSum += onehop.getCost();
					setcost = setRateSum / setProgressSum;
					subsetHopArray.add(onehop);
				} else {
					setRateSum += onehop.getRate();
					setProgressSum += onehop.getCost();
					setcost = setRateSum / setProgressSum;
					subsetHopArray.add(onehop);
				}
				
				

			}
		}

	}
	
	return setcost;
}


  private static Vector<int []> setpartition(int n) {
	    Vector<int []> subsetv = new Vector<int []>();

	    //set partition by setpart2 algorithm
	    int[] c = new int[n + 1];
	    int[] b = new int[n + 1];
	    int r = 1;
	    c[1] = 1;
	    int j = 0;
	    b[0] = 1;
	    int n1 = n - 1;

	    do {
	      while (r < n1) {
	        r = r + 1;
	        c[r] = 1;
	        j++;
	        b[j] = r;
	      }

	      for (int i = 1; i <= n - j; ++i) {
	        c[n] = i;
	        int[] set = new int[n];
	        for (int k = 0; k < n; ++k) {
	          set[k] = c[k + 1];
	        }
	        subsetv.add(set);
	      }

	      r = b[j];
	      c[r]++;
	      if (c[r] > r - j) {
	        j--;
	      }

	    }
	    while (r != 1);

	    return subsetv;
	  }





//=====================================================================









  public static ArrayList getNextHops(Host currentNode, Host sender,
                                      ArrayList origdestList, int costmode) {
    //Input:  Host currentNode,ArrayList origdestList
    //Output: ArrayList hopArray

    //Step1:
    //If one of the neighbors reached one of the destinations
    //select the neighbor, cover possible destinations
    //add the hop contains the neighbor and dests in hopArray
    //remove those destinations from destlist
    //if remaining destinations list is empty, return

    //Setp2:
    //for destinations without forward node,
    //put in hopArray with null forward node
    //remove those destinations from destlist

    //Step3:
    //set partition for remaining destinations

    //Step4:
    //calculate cost for all set partition
    //if there is no common neighbors in one subset,
    //ignore that subset
    //select the common neighbor in each subset
    //calculate the cost, select the neighbor with minimum cost
    //summary cost for that set partition
    //select min cost set partition,
    //put forward neighbor and its covered dest in hopArray

    //these two line is for debug only
    ArrayList hopArray = new ArrayList();

    //Step1:=================================================
    //If one of the neighbors reached one of the destinations
    //select the neighbor, cover possible destinations
    //add the hop contains the neighbor and dests in hopArray
    //remove those destinations from destlist
    //if remaining destinations list is empty, return

    ArrayList neighbors = currentNode.getIncidentHosts();
    boolean checkf = false;
    if (neighbors.remove(sender)) {
      checkf = true;
    }
    ArrayList alreadycovereddests = new ArrayList();
    Hop nextHop = new Hop(null, null);
    ArrayList newdests = new ArrayList();

    //create destList for calculation purpose
    ArrayList destList = new ArrayList();
    for (int i = 0; i < origdestList.size(); ++i) {
      destList.add(origdestList.get(i));
    }
    /*
        //found all neighbors that are destinations
        for (int j = 0; j < origdestList.size(); ++j) {
          Host adest = (Host) origdestList.get(j);
          if (neighbors.contains(adest)) {
            nextHop = new Hop(adest, null);
            newdests = new ArrayList();
            newdests.add(adest);
            nextHop.setDestinationList(newdests);
            destList.remove(adest);
            hopArray.add(nextHop);
          }
        }

        //see if those destination neighbor can cover other destinations
        for ( int i = 0; i < hopArray.size(); i ++)
        {
          Hop ahop = (Hop) hopArray.get(i);
          Host adest = (Host) ahop.getNode();
          newdests = ahop.getDestinationList();
            for (int j = 0; j < destList.size(); ++j) {
              Host onedest = (Host) destList.get(j);
              if (FaceUtility.isClosertoDest(adest, currentNode, onedest)) {
                newdests.add(onedest);
                alreadycovereddests.add(onedest);
              }
            }
            ahop.setDestinationList(newdests);
            for (int k = 0; k < newdests.size(); ++k) {
              destList.remove(newdests.get(k));
            }

        }


        //check if any remaining dests need to find forward node
        //if no remaining dests, return
        if (destList.size() == 0) {
          if (checkf) {
            neighbors.add(sender);
          }
          return hopArray;
        }
     */
    //Step2:==============================================
    //destination without progress will be taken out
    //put in a hop with null node.
    //destList for remaining destinations continue process
    nextHop = new Hop(null, null);
    newdests = new ArrayList();
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
      }
      possibleForwardNodeNumber[i] = num;
    }

    //Find destinations without forward node
    //eliminate them from destList array
    for (int i = 0; i < destList.size(); ++i) {
      if (possibleForwardNodeNumber[i] == 0) {
        Host onedest = (Host) destList.get(i);
        newdests.add(onedest);
        alreadycovereddests.add(onedest);
      }
    }
    if (newdests.size() != 0) {
      nextHop.setNode(null);
      nextHop.setDestinationList(newdests);
      hopArray.add(nextHop);

      for (int ik = 0; ik < newdests.size(); ++ik) {
        destList.remove(newdests.get(ik));
      }
    }

    if (destList.size() == 0) {
      if (checkf) {
        neighbors.add(sender);
      }
      return hopArray;
    }

    //Step 3: =========================================
    //setpartition
    Vector subset = setpartition(destList.size());

    //Setp 4: ==========================================
    //calculate cost for all set partition
    //if there is no common neighbors in one subset,
    //ignore that subset
    //select the common neighbor in each subset
    //calculate the cost
    //summary cost for that set partition
    //select min cost set partition,
    //put forward neighbor and its covered dest in hopArray


    int numOfDestinations = destList.size();
    int numOfsubset = subset.size();
    double minimumCost = Double.MAX_VALUE;
    ArrayList selectedHopArray = new ArrayList();

    for (int i = 0; i < numOfsubset; ++i) {
      double setcost = -1;
      double setRateSum = 0;
      double setProgressSum = 1;
      boolean ignoreflag = false;

      //find summary cost for each subset partition
      int[] oneset = (int[]) subset.get(i);
      //int numOfgroups = oneset[numOfDestinations - 1];
      int numOfgroups = 1;
      for (int j = 0; j < numOfDestinations; ++j) {
        if (oneset[j] > numOfgroups) {
          numOfgroups = oneset[j];
        }
      }

      ArrayList[] destgroup = new ArrayList[numOfgroups];
      for (int j = 0; j < numOfgroups; ++j) {
        destgroup[j] = new ArrayList();
      }
      setcost = 0;

      for (int j = 0; j < numOfDestinations; ++j) {
        int groupindex = oneset[j] - 1;
        destgroup[groupindex].add(destList.get(j));
      }

      ArrayList subsetHopArray = new ArrayList();
      for (int j = 0; j < numOfgroups; ++j) {
        if (ignoreflag) {
          setcost = -1;
          continue;
        }
        ArrayList dests = destgroup[j];
        //find cost for one dest groups in a subset partition
        RateBasedHop onehop = null;
        if (costmode == 0) {
          onehop = findRateBasedHop(currentNode, dests, neighbors);
        }
        else if (costmode == 1) {
          onehop = findRateBasedHop1(currentNode, dests, neighbors);
        }

        else if (costmode == 2) {
          //this is the cost per progress function as spain team
          //  m/(p1+p2+p3...+pm)
          //we change to use rate as cost
          // so the cost per progress is
          // (R1+R2+...Rm) / (p1+p2+...+pm)
          //R is the max destination rate in one group in a subset
          //
          onehop = findRateBasedHop2(currentNode, dests, neighbors);
        }
        if (onehop.getNode() == null) {
          ignoreflag = true;
          setcost = -1;
          continue;
        }
        else {
          if (costmode != 2) {
            setcost += onehop.getCost();
            subsetHopArray.add(onehop);
          }
          else {
            //only for m/(p1+p2+p3...pm)
            //only for (R1+R2+...Rm) / (p1+p2+...pm)
            //if all R=1, the two equcation is same
            setRateSum += onehop.getRate();
            setProgressSum += onehop.getCost();
            setcost = setRateSum / setProgressSum;
            subsetHopArray.add(onehop);

          }
        }

      }

      if ( (setcost > 0) && (setcost < minimumCost)) {
        selectedHopArray = subsetHopArray;
        minimumCost = setcost;
      }
    }

    //step 5: =========================================
    //merge with hopArray, return

    for (int i = 0; i < selectedHopArray.size(); ++i) {
      hopArray.add(selectedHopArray.get(i));
    }

    if (checkf) {
      neighbors.add(sender);
    }

    return hopArray;

  }

// Liu debug 20071023 for greedy GFG neighbor merge
public static ArrayList getNextHops_greedy(Host currentNode, Host sender,
        ArrayList origdestList, int costmode) {
	
	//Step1:==============================================
	//  some preparation 
	ArrayList hopArray = new ArrayList();
		
	ArrayList neighbors = currentNode.getIncidentHosts();
	boolean checkf = false;
	if (neighbors.remove(sender)) {
	checkf = true;
	}
	ArrayList alreadycovereddests = new ArrayList();
	Hop nextHop = new Hop(null, null);
	ArrayList newdests = new ArrayList();
	
	//create destList for calculation purpose
	ArrayList destList = new ArrayList();
	for (int i = 0; i < origdestList.size(); ++i) {
	destList.add(origdestList.get(i));
	}
	
	//Step2:==============================================
	//destination without progress will be taken out
	//put in a hop with null node.
	//destList for remaining destinations continue process
	nextHop = new Hop(null, null);
	newdests = new ArrayList();
	double[][] distanceArray = findDistanceArray(currentNode, destList,
	                   neighbors);
	int[] possibleForwardDestNumber = new int[neighbors.size()];
	int[] possibleForwardNodeNumber = new int[destList.size()];
	//for each destination, save the best forward node's index for it in following array
	int[] bestForwardNodeIndex = new int[destList.size()];
		
	//Calculate possible forward node number for each destination
	//update bestForwardNodeNumber array for each destination
	//bestForwardNodeNumber[i]=j, i is destination index, j is neighbor index 
	for (int i = 0; i < destList.size(); ++i) {
		Hop aHop = new Hop (null, null);
		ArrayList alist = new ArrayList();
		int num = 0;
		double min_distance = Double.MAX_VALUE;
		bestForwardNodeIndex[i]=-1;
		for (int j = 0; j < neighbors.size(); ++j) {
			if (distanceArray[j][i] >= 0) {
					num++;
		    }
			if ( distanceArray[j][i] < min_distance && distanceArray[j][i] >= 0) {
				min_distance = distanceArray[j][i];
				aHop.setNode((Host) neighbors.get(j));
				Host adest = (Host) destList.get(i);
				alist.clear();
				alist.add((Host) adest);
				aHop.setDestinationList(alist);
				bestForwardNodeIndex[i]=j; //best neighbor node index
			}
		}
		possibleForwardNodeNumber[i] = num;
	}
	
	//Find destinations without forward node
	//eliminate them from destList array
	//update bestForwardNodeIndex accordingly
	for (int i = 0; i < destList.size(); ++i) {
		if (possibleForwardNodeNumber[i] == 0) {
				Host onedest = (Host) destList.get(i);
				newdests.add(onedest);
				alreadycovereddests.add(onedest);
		}
	}
	
	if (newdests.size() != 0) {
			nextHop.setNode(null);
			nextHop.setDestinationList(newdests);
			hopArray.add(nextHop);

			for (int ik = 0; ik < newdests.size(); ++ik) {
				destList.remove(newdests.get(ik));
			}
	}

	if (destList.size() == 0) {
		if (checkf) {
			neighbors.add(sender);
		}
		return hopArray;
	}
	
	//Step 3: =========================================
	//get initial set , destinations with the same neighbor 
	// that provides best forward will be put in same subset
	//numOfgroups is the number of subset in one set
	//destgroup[?] is a destination subset
	Vector<ArrayList<Host>> currentSet = initSet( origdestList, bestForwardNodeIndex);
	
	
	//Setp 4: ==========================================
	//greedy set partition with cost calculate 
	//select the common neighbor in each subset
	//calculate the cost for current set
	// repeat 
	//for all subset pairs, merge and calculate new set cost
	//if new cost is less that original set cost, 
	//accept this as new set partition
	//repeat until new cost is the lowest
	//put forward neighbor and its covered dest in hopArray
	
	//this curHopArrayList will put in final return hopArray
	ArrayList curHopArrayList = new ArrayList();
	double origSetCost = getSetCost ( currentSet, destList,
			costmode, currentNode, neighbors,
			 curHopArrayList) ;
	Vector<ArrayList<Host>> bestSet = currentSet;
	
	int subsetNumber = currentSet.size();
	// loop until bestReduction = 0
	double bestReduction = 1000000000;
	double bestCost = origSetCost;
	
	while ( bestReduction > 0 ) {
		bestReduction = 0;
		for ( int i = 0 ; i < subsetNumber - 2; i++) {
			for ( int j = i+1; j < subsetNumber - 1 ; j++) {
				Vector<ArrayList<Host>> newSet = mergeSet(
				    	currentSet, i, j,
				    	currentNode, neighbors,
					    destList);
					
				double newSetCost = 0;
				if ( newSet == null ) {
					//can not find common neighbor provide progress
					//for all destinations in subset i and j  
					//newSetCost = -1;
					bestReduction = 0;
					continue;
				} else {
					ArrayList curArrayList = new ArrayList(); 
				    newSetCost = getSetCost ( newSet , destList,
							costmode, currentNode, neighbors,
							 curHopArrayList) ;
				
				    double costReduction = origSetCost - newSetCost;
				
				    if ( costReduction > bestReduction ) {
		                bestSet = newSet;
		                bestCost = newSetCost;
		                bestReduction = costReduction;
				    }
				}
			}
	    }
		
		if ( bestReduction > 0) {
			currentSet = bestSet;
			origSetCost = bestCost;
			subsetNumber = currentSet.size();
			bestReduction = origSetCost;
		}
		
	}
	
	curHopArrayList.clear();
	
	bestCost = getSetCost ( bestSet , destList,
			costmode, currentNode, neighbors,
			 curHopArrayList) ;
	
	// step 5: =========================================
	//merge with hopArray, return

	for (int i = 0; i < curHopArrayList.size(); ++i) {
			hopArray.add(curHopArrayList.get(i));
	}

	if (checkf) {
			neighbors.add(sender);
	}

	return hopArray;

	}

}



  


