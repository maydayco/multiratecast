import java.util.ArrayList;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class Path {
  private Host source;
  private Host destination;
  private ArrayList path;
  private boolean pathfinished;
  private boolean pathsuccess;
  private double length;

  public Path(Host s, Host t) {
    source = s;
    destination  = t;
    path = new ArrayList();
    pathfinished = false;
    pathsuccess = false;
    length = 0;
  }

  public Host getSource() {
    return source;
  }

  public void setSource(Host s) {
    source = s;
  }

  public Host getDestination() {
    return destination;
  }

  public void setDestination(Host d) {
    destination = d;
  }

  public ArrayList getPath() {
    return path;
  }

  public void setPath(ArrayList p) {
    path = p;
  }

  public void addNode(Host ahost){
    path.add(ahost);
  }

  public double getLength() {
    return length;
  }


  public boolean getPathsuccess(){
    return pathsuccess;
  }

  public void setPathsuccess(boolean t){
    pathsuccess = t;
  }

  public void addLength(){
    length++;
  }

  public void setLength( int l) {
    length = l;
  }
  public boolean getPathfinished(){
    return pathfinished;
  }

  public void setPathfinished(boolean f){
    pathfinished = f;
  }
}