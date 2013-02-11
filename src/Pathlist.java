import java.util.ArrayList;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class Pathlist {
  private static int totalhopcount;
  private static double totalratecost;
  private static ArrayList pathlist;

  public Pathlist() {
    totalhopcount = 0;
    totalratecost = 0;
    pathlist = new ArrayList();
  }

  public int getTotalHopCount () {
    return totalhopcount;
  }

  public void addTotalHopCount() {
    totalhopcount ++;
  }

  public void addTotalRateCost(double rate) {
    totalratecost = totalratecost + rate;
  }

  public double getTotalRate () {
    return totalratecost;
  }
  public void addPath(Path apath){
    pathlist.add(apath);
  }

  public int size() {
    return pathlist.size();
  }
  public boolean ifRouting_All_Finished(){
    boolean allfinish = true;

    for(int i = 0; i < pathlist.size(); ++i){
      Path apath = (Path) pathlist.get(i);
      allfinish = allfinish & apath.getPathfinished();
    }
    
    //cleanup message server, 
    //calculate the last hop message in messageIn 
    if ( allfinish ) {
      MessageServer msgServer = MessageServer.getInstance();
      ArrayList msgList = msgServer.getAllMessages(); 
    }
    
    return allfinish;
  }

  public Path getPath(int i){
   return (Path) pathlist.get(i);
  }
  public void addpath(Message message){
    Host source = message.getSource();

    ArrayList destlist = message.getDestinationList();

    //add rbm change, Aug 19, 2006
    RateBasedMulticastMessage rbmmsg = null;
    ArrayList greedylist = null;
    ArrayList facelist = null;

    if ( message instanceof RateBasedMulticastMessage ) {
      rbmmsg = (RateBasedMulticastMessage) message;
      destlist = rbmmsg.getGreedyDestList();
      facelist = rbmmsg.getFaceDestList();

      if ( destlist != null && facelist != null ) {
        destlist.addAll(facelist);
      }
      if ( destlist == null ) {
        destlist = facelist;
      }

    }

    //rbm change end


    //add pbm change, Sep 5, 2006
    PositionBasedMulticastMessage pbmmsg = null;
    greedylist = null;
    facelist = null;

    if ( message instanceof PositionBasedMulticastMessage ) {
      pbmmsg = (PositionBasedMulticastMessage) message;
      destlist = pbmmsg.getGreedyDestList();
      facelist = pbmmsg.getFaceDestList();

      if ( destlist != null && facelist != null ) {
        destlist.addAll(facelist);
      }
      if ( destlist == null ) {
        destlist = facelist;
      }

    }

    //pbm change end

    for ( int j = 0; j < pathlist.size(); ++j){
      Path apath = (Path) pathlist.get(j);
      if (apath.getSource().equals(source)){
          for ( int i = 0; i < destlist.size(); ++i){
             Host destination = (Host) destlist.get(i);
             if (apath.getDestination().equals(destination)){
               Host nextnode = message.getCurrentHost();
               apath.addNode(nextnode);
               apath.addLength();

               if(nextnode == null){
                 apath.setPathfinished(true);
                 continue;
               }

               if (nextnode.equals(destination)){
                 apath.setPathsuccess(true);
                 apath.setPathfinished(true);
                 continue;
               }


             }
          }
      }
    }
  }



}
