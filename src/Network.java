import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.awt.geom.Point2D;
import java.io.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class Network {

  private double sizeX;
  private double sizeY;
  private int density;
  private int sourceNumber;
  private int destinationNumber;
  private double radius;

  private HostArray hostArray;
  private HostArray sourceArray;
  private HostArray destinationArray;

  private Pathlist pathlist;

  private static Network network = null;

  public static final int INFINITE_DISTANCE = Integer.MAX_VALUE;

  private final Comparator shortestDistanceComparator = new Comparator() {
    public int compare(Object left, Object right) {
      Host a = (Host) left;
      Host b = (Host) right;

      int shortestDistanceLeft = getShortestDistance(a);
      int shortestDistanceRight = getShortestDistance(b);

      if (shortestDistanceLeft > shortestDistanceRight) {
        return +1;
      }
      else if (shortestDistanceLeft < shortestDistanceRight) {
        return -1;
      }
      else { // equal
        if (a.getX() > b.getX()) {
          return +1;
        }
        if (a.getX() < b.getX()) {
          return -1;
        }
        else {
          if (a.getY() > b.getY()) {
            return +1;
          }
          if (a.getY() < b.getY()) {
            return -1;
          }
          else {
            return 0;
          }
        }
      }
    }
  };

  private SortedSet unsettledNodes = new TreeSet(shortestDistanceComparator);
  private Set settledNodes = new HashSet();
  private Map shortestDistances = new HashMap();
  private Map predecessors = new HashMap();

  private Network() {
  }

  public static Network getInstance() {
    if (network == null) {
      network = new Network();
    }
    return network;
  }

  public void setSize(double sizeX, double sizeY) {
    this.sizeX = sizeX;
    this.sizeY = sizeY;
  }

  /**
   * Get the X size of the network.
   * @return the X size of the network.
   */
  public double getSizeX() {
    return sizeX;
  }

  /**
   * Get the Y size of the network.
   * @return the Y size of the network.
   */
  public double getSizeY() {
    return sizeY;
  }

  public void setDensity(int density) {
    this.density = density;
  }

  public void setHostArray(HostArray hostArray) {
    this.hostArray = hostArray;
  }

  public HostArray getHostArray() {
    return hostArray;
  }

  public void setSourceArray(HostArray sources) {
    sourceArray = sources;
  }

  public HostArray getSourceArray() {
    return sourceArray;
  }
/* ???
  public Host getSource(int index){
    return sourceArray.get(index);
  }

  public int getNumberOfSources(){
    return sourceArray.size();
  }
*/
  public void setDestinationArray(HostArray destinations) {
    destinationArray = destinations;
  }

  public HostArray getDestinationArray() {
    return destinationArray;
  }

  public void addHost(Host host) {
    if (hostArray == null) {
      hostArray = new HostArray();
    }
    hostArray.addHost(host);
  }

  public void removeHost(Host host) {
    hostArray.removeHost(host);
  }

  public Pathlist getPathlist() {
    return pathlist;
  }

  public void setPathlist(Pathlist apathlist){
    pathlist = apathlist;
  }
  public void setRoutingPath(Pathlist apathlist) {
    pathlist = apathlist;
  }

  public boolean ifRouting_All_Finished() {
    if (pathlist.size() == 0) return false;
    return    pathlist.ifRouting_All_Finished();
  }

  public boolean ifRouting_Just_Start(){
    return pathlist.size() == 0 ;
  }


  public void generateSimNetwork(int density, int node,
                 int sourceNumber,int destinationNumber,
                 DestsRateDistri destsRateMode, int graphAlgorithm) {
    // Initialize
    sizeX = 200;
    sizeY = 200;
    this.sourceNumber = sourceNumber;
    this.destinationNumber = destinationNumber;
    //hostArray = new HostArray();
    sourceArray = new HostArray();
    destinationArray = new HostArray();
    pathlist = new Pathlist();
    boolean connected = false;
    double minRate = destsRateMode.getMinRate();
    double rateMultiplifier = destsRateMode.getMaxRate() - minRate;

    //generateRUG(density, node);
    //checkConnectivity
    //regenerateRUG if not connected
    while (!connected) {
      hostArray = new HostArray();
      Random random = new Random();
      Random randomRate = new Random();
      while (true) {
        // Generate the hostArray.
        for (int i = 0; i < node; ++i) {
          double xPosition = random.nextDouble() * sizeX;
          double yPosition = random.nextDouble() * sizeY;
          double rate = minRate + randomRate.nextDouble() * rateMultiplifier;
          Host newHost = new LocalRoutingHost(xPosition, yPosition, rate);
          if (hostArray.findHost(newHost) == -1) {
            hostArray.addHost(newHost);
          }
        }
        // Stop if requried number of hosts has been generated.
        if (hostArray.size() >= node) {
          break;
        }
      }

      if (graphAlgorithm == 0) {
        // Strandard CRUG. Calculate the radius and generate the edge.
        radius = 0;
        int maxEdges = node * (node - 1) / 2;
        double[] edges = new double[maxEdges];
        int cutOffNumber = node * density / 2;
        Host host1 = null;
        Host host2 = null;

        int k = 0;
        for (int i = 0; i < node; ++i) {
          host1 = hostArray.get(i);
          for (int j = i + 1; j < node; ++j) {
            host2 = hostArray.get(j);
            // Calculate all the possible edges and save it to the edge array.
            edges[k++] = host1.getPosition().distance(host2.getPosition());
          }
        }

        // Sort the edges array until reach the desired number.
        for (int i = 0; i < cutOffNumber; ++i) {
          double minEdge = edges[i];
          for (int j = i + 1; j < maxEdges; ++j) {
            if (minEdge > edges[j]) {
              // Swap two edges.
              minEdge = edges[j];
              edges[j] = edges[i];
              edges[i] = minEdge;
            }
          }
        }

        // Found the radius.
        radius = edges[cutOffNumber - 1];

        calculateEdges();
      }

      //check connectivity
      connected = ShortestPath(hostArray.get(0));

 }

    // Set the source as the first node and destination as the last node.
    //add only one source
    sourceArray.addHost(hostArray.get(0));
    //add number of destinations
    for (int i = 0; i < destinationNumber; ++i) {
      Host destination = hostArray.get(i + 1);
      destinationArray.addHost(destination);
    }

    // Special handling for the destination rate distribution mode.
    double rate = destsRateMode.getStartRate();
    double rateSteps = destsRateMode.getRateSteps();
    if (destsRateMode.getDistMode() == DestsRateDistri.INCREASE_DISTRIBUTES) {
      // Increase rate.
      for (int i = 0; i < destinationArray.size(); ++i) {
        ((LocalRoutingHost)destinationArray.get(i)).setRate(rate);
        rate += rateSteps;
      }
    } else if (destsRateMode.getDistMode() == DestsRateDistri.DECREASE_DISTRIBUTES) {
      for (int i = 0; i < destinationArray.size(); ++i) {
       ((LocalRoutingHost)destinationArray.get(i)).setRate(rate);
       rate -= rateSteps;
     }
    }
    // Reset the routing path.
    pathlist = new Pathlist();

    // Clear the message buffer.
    MessageServer msgServer = MessageServer.getInstance();
    //ArrayList msg = msgServer.getAllMessages();
    //Liu Nov 30,2007
    msgServer.reset();

  }

  /**
   * Write the network to a given file
   * @param fileName the filename to save the network.
   */
  public void writeToFile(File fileName) {

    FileOutputStream outputStream;

    try {
      outputStream = new FileOutputStream(fileName);
    }
    catch (Exception e) {
      System.err.println("Error to open file output stream: " + e.getMessage());
      return;
    }

    ObjectOutputStream objectStream;
    try {
      objectStream = new ObjectOutputStream(outputStream);

      objectStream.writeDouble(sizeX);
      objectStream.writeDouble(sizeY);
      objectStream.writeInt(density);
      objectStream.writeDouble(radius);

      // Write the hosts to the file.
      int numberOfHosts = hostArray.size();
      objectStream.writeInt(numberOfHosts);
      for (int i = 0; i < numberOfHosts; ++i) {
        Host host = hostArray.get(i);
        objectStream.writeDouble(host.getX());
        objectStream.writeDouble(host.getY());
      }
// Liu modify for new structure
      if (sourceArray != null) {
        objectStream.writeInt(sourceArray.size());
        for (int i = 0; i < sourceArray.size(); ++i) {
          objectStream.writeInt(hostArray.findHost(
              (Host) sourceArray.get(i)));
        }
      }
      else {
        objectStream.writeInt( -1);
      }

      if (destinationArray != null) {
        objectStream.writeInt(destinationArray.size());
        for (int i = 0; i < destinationArray.size(); ++i) {
          objectStream.writeInt(hostArray.findHost(
                (Host) destinationArray.get(i)));
          objectStream.writeDouble(
              ((LocalRoutingHost)destinationArray.get(i)).getRate());
        }
      }
      else {
        objectStream.writeInt( -1);
      }

      objectStream.flush();
      objectStream.close();
    }
    catch (Exception e) {
      System.err.println("Error to write the file: " + e.getMessage());
    }

  }

  /**
   * Read the network from a given file
   * @param fileName tje file contains the network information.
   */
  public void readFromFile(File fileName) {

    FileInputStream inputStream;

    try {
      inputStream = new FileInputStream(fileName);
    }
    catch (Exception e) {
      System.err.println("Error to open file input stream:" + e.getMessage());
      return;
    }

    ObjectInputStream objectStream;
    try {
      objectStream = new ObjectInputStream(inputStream);

      sizeX = objectStream.readDouble();
      sizeY = objectStream.readDouble();
      density = objectStream.readInt();
      radius = objectStream.readDouble();

      int numberOfHosts = objectStream.readInt();
      hostArray = new HostArray();
      for (int i = 0; i < numberOfHosts; ++i) {
        double x = objectStream.readDouble();
        double y = objectStream.readDouble();
        hostArray.addHost(new LocalRoutingHost(x, y));
      }

      // Re-calculate the edges.
      calculateEdges();

      int sourceNumber = objectStream.readInt();
      if (sourceNumber == -1) {
        sourceArray = null;
      }
      else {
        sourceArray =  new HostArray();
        for (int i = 0; i < sourceNumber; ++i) {
          int sourceIndex = objectStream.readInt();
          Host source = (Host) hostArray.get(
              sourceIndex);
          sourceArray.addHost(source);
        }
      }

      int destinationNumber = objectStream.readInt();
      if (destinationNumber == -1) {
        destinationArray = null;
      }
      else {
        destinationArray = new HostArray();
        for (int i = 0; i < destinationNumber; ++i) {
          int destinationIndex = objectStream.readInt();
          double destinationRate = objectStream.readDouble();
          Host destination = (Host) hostArray.get(destinationIndex);
          ((LocalRoutingHost)destination).setRate(destinationRate);
          destinationArray.addHost(destination);
        }
      }
      objectStream.close();

      // Reset the routing path.
     pathlist = new Pathlist();
     // Clear the message buffer.
    MessageServer msgServer = MessageServer.getInstance();
    //Liu Nov30,2007
    //ArrayList msg = msgServer.getAllMessages();
    msgServer.reset();

    }
    catch (Exception e) {
      System.err.println("Error to read from the file: e.getMessage()");
    }
  }

  public ArrayList getClonedHostArray() {
    ArrayList clonedHosts = new ArrayList();

    for (int i = 0; i < hostArray.size(); ++i) {
      clonedHosts.add(new LocalRoutingHost(hostArray.get(i).getPosition()));
    }

    // Claculate the edges.
    for (int i = 0; i < clonedHosts.size(); ++i) {
      Host host1 = (Host) clonedHosts.get(i);
      for (int j = i + 1; j < clonedHosts.size(); ++j) {
        Host host2 = (Host) clonedHosts.get(j);
        if (host1.getPosition().distance(host2.getPosition()) <= radius) {
          // Those two hosts are incident host.
          host1.addIncidentHost(host2);
          host2.addIncidentHost(host1);
        }
      }
    }

    return clonedHosts;
  }

  private void calculateEdges() {
    // Calculate the edges.
    for (int i = 0; i < hostArray.size(); ++i) {
      Host host1 = hostArray.get(i);
      for (int j = i + 1; j < hostArray.size(); ++j) {
        Host host2 = hostArray.get(j);
        if (host1.getPosition().distance(host2.getPosition()) <= radius) {
          // Those two hosts are incident host.
          host1.addIncidentHost(host2);
          host2.addIncidentHost(host1);
        }
      }
    }
  }

  ///==================Shortest Path methods ==============//

  private Host extractMin() {
    if (unsettledNodes.isEmpty()) {
      return null;
    }

    Host min = (Host) unsettledNodes.first();
    unsettledNodes.remove(min);

    return min;
  }

  private void relaxNeighbors(Host u) {
    for (Iterator i = u.getIncidentHosts().iterator(); i.hasNext(); ) {
      Host v = (Host) i.next();

      // skip node already settled
      if (isSettled(v)) {
        continue;
      }

      //all edge weight = 1
      if (getShortestDistance(v) > (getShortestDistance(u) + 1)) { //u.getPosition().distance(v)) ))
        // assign new shortest distance and mark unsettled
        setShortestDistance(v, (getShortestDistance(u) + 1));

        // assign predecessor in shortest path
        setPredecessor(v, u);
      }
    }
  }

  private void markSettled(Host u) {
    settledNodes.add(u);
  }

  private boolean isSettled(Host v) {
    return settledNodes.contains(v);
  }

  public int getShortestDistance(Host node) {
    Integer d = (Integer) shortestDistances.get(node);
    return (d == null) ? INFINITE_DISTANCE : d.intValue();
  }

  private void setShortestDistance(Host node, int distance) {
    // this crucial step ensure no duplicates will be created in the queue
    // when an existing unsettled node is updated with a new shortest distance
    unsettledNodes.remove(node);

    shortestDistances.put(node, new Integer(distance));

    // re-balance the sorted set according to the new shortest distance found
    // (see the comparator the set was initialized with)
    unsettledNodes.add(node);
  }

  /**
   * @return the node leading to the given node on the shortest path, or
   * <code>null</code> if there is no route to the destination.
   */
  public Host getPredecessor(Host node) {
    return (Host) predecessors.get(node);
  }

  private void setPredecessor(Host a, Host b) {
    predecessors.put(a, b);
  }

  private void init(Host start) {
    settledNodes.clear();
    unsettledNodes.clear();

    shortestDistances.clear();
    predecessors.clear();

    // add source
    setShortestDistance(start, 0);
    unsettledNodes.add(start);
  }

  public Path findShortestPath(Host s, Host t) {
    Path path = new Path(s, t);
    ArrayList spList = new ArrayList();

    //algorithm  Dijkstra

    // initialize all vertices in graph
    init(s);
    Host u = null;

    // extract the node with the shortest distance
    while ( (u = extractMin()) != null) {
      //assert !isSettled(u);
      if (!isSettled(u)) {
        // destination reached, stop
        if (u == t) {
          break;
        }
        markSettled(u);
        relaxNeighbors(u);
      }
    }
    int n = predecessors.size();
    spList.add(t);
    Host aNode = (Host) predecessors.get(t);
    while (aNode != null) {
      spList.add(aNode);
      aNode = (Host) predecessors.get(aNode);
    }

    n = spList.size();

    ArrayList reverseR = new ArrayList();
    for (int w = 0; w < n; ++w) {
      Host reversenode = (Host) spList.get(n - w - 1);
      reverseR.add(reversenode);
    }

    path.setPath(reverseR);
    path.setLength(n);

    if (predecessors.size() > 0) {

    }
    return path;
  }

  public boolean ShortestPath(Host s) {
      //algorithm  Dijkstra

      // initialize all vertices in graph
      init(s);
      Host u = null;

      // extract the node with the shortest distance
      while ( (u = extractMin()) != null) {
        //assert !isSettled(u);
        if (!isSettled(u)) {
          markSettled(u);
          relaxNeighbors(u);
        }
      }

      int n = predecessors.size();
      int m = network.getHostArray().size();
      n = unsettledNodes.size();
      n = settledNodes.size();
      if ( n == m ) {
       return true;
      }
      return false;
    }


  // Get the number of successes from the routing.
  public int getSuccessDestNumber() {

    int successNumber = 0;
    for (int i = 0; i < pathlist.size(); ++i) {
      Path apath = pathlist.getPath(i);
      if (apath.getPathsuccess()) {
        successNumber += 1;
      }
    }

    return successNumber;

  }

  public void setDestinationRate(double [] rateList) {
    if (rateList.length > destinationArray.size()) {
      // set destination rate accouding up to its size by rateList order
        for ( int i = 0; i < destinationArray.size(); ++i ) {
          LocalRoutingHost dest = (LocalRoutingHost)destinationArray.get(i);
          dest.setRate(rateList[i]);
        }
      return;
    }

    for (int i = 0; i < rateList.length; ++i) {
      LocalRoutingHost dest = (LocalRoutingHost)destinationArray.get(i);
      dest.setRate(rateList[i]);
    }
  }
}
