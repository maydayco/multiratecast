import java.awt.geom.*;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class FaceUtility {

  /*
   * Angle <)abc
   */

  private static double getFirstAngle(Host a, Host b, Host c) {
    double ux = a.getX() - b.getX();
    double uy = a.getY() - b.getY();
    double ulen = Math.sqrt(ux * ux + uy * uy);
    double vx = c.getX() - b.getX();
    double vy = c.getY() - b.getY();
    double vlen = Math.sqrt(vx * vx + vy * vy);
    double phi = Math.acos( (ux * vx + uy * vy) / ulen / vlen);

    if (Double.isNaN(phi)) { // can happen in special cases
      double cos = (ux * vx + uy * vy) / ulen / vlen;
      if (cos < -1) {
        cos = -1;
      }
      else if (cos > 1) {
        cos = 1;
      }
      phi = Math.acos(cos);
    }

    return phi;
  }

  private static double getAngle(Host a, Host b, Host c) {
    double ux = a.getX() - b.getX();
    double uy = a.getY() - b.getY();
    double ulen = Math.sqrt(ux * ux + uy * uy);
    double vx = c.getX() - b.getX();
    double vy = c.getY() - b.getY();
    double vlen = Math.sqrt(vx * vx + vy * vy);
    double phi = Math.acos( (ux * vx + uy * vy) / ulen / vlen);

    if (Double.isNaN(phi)) { // can happen in special cases
      double cos = (ux * vx + uy * vy) / ulen / vlen;
      if (cos < -1) {
        cos = -1;
      }
      else if (cos > 1) {
        cos = 1;
      }
      phi = Math.acos(cos);
    }

    if (Line2D.relativeCCW(b.getX(), b.getY(), a.getX(),
                           a.getY(), c.getX(), c.getY()) == -1) {
      phi = 2 * Math.PI - phi;
    }

    return phi;
  }

  /*
   * Inner (smaller) angle <)abc
   */
  private static double getInnerAngle(Host a, Host b, Host c) {
    double angle = getAngle(a, b, c);
    if (angle > Math.PI) {
      angle = Math.PI - angle;
    }
    return angle;
  }

  private static double getInnerAngle2(Host a, Host b, Host c) {
    double angle = getAngle(a, b, c);
    if (angle > Math.PI) {
      angle = Math.PI - angle;
    }
    return angle;
  }

  public static Point2D.Double intersect(Edge s, Edge e) {
                 if (!Line2D.linesIntersect(s.a.getX(), s.a.getY(),
                                            s.b.getX(), s.b.getY(),
                                            e.a.getX(), e.a.getY(),
                                            e.b.getX(), e.b.getY()))
                        return null;
                 double a1 = s.a.getX() - s.b.getY();
                 double b1 = s.b.getX() - s.a.getX();
                 double c1 = s.a.getX() * s.b.getY() - s.b.getX() * s.a.getY();
                 double a2 = e.a.getY() - e.b.getY();
                 double b2 = e.b.getX() - e.a.getX();
                 double c2 = e.a.getX() * e.b.getY() - e.b.getX() * e.a.getY();

                 double px = b1*c2 - c1*b2;
                 double py = c1*a2 - a1*c2;
                 double pz = a1*b2 - b1*a2;
                 return new Point2D.Double(px/pz, py/pz);
 }

 public static Edge getFirstFaceEdge(ArrayList g, Host source, Host dest) {
    Host sourceInGG = null;

    // Find the source node in Garbriel Graph.
    for (int i = 0; i < g.size(); ++i) {
      sourceInGG = (Host) g.get(i);
      if (sourceInGG.equals(source)) {
        break;
      }
    }
    ArrayList neighbors = sourceInGG.getIncidentHosts();
    int deg = neighbors.size();
    Host bestHost = null;
    double bestPhi = 7;
    for (int i = 0; i < deg; i++) {
      Host host = (Host) neighbors.get(i);
      double phi = getFirstAngle(dest, source, host);
      if (phi < bestPhi) {
        bestPhi = phi;
        bestHost = host;
      }
    }
    return new Edge(source, bestHost);
  }


  public static Edge getNextFaceEdge(ArrayList g, Host sender, Host currentNode,
                                     boolean cw) {
    Host currentNodeInGG = null;
    Edge e = new Edge(sender, currentNode);
    cw = !cw;

    // Find the source node in Garbriel Graph.
    for (int i = 0; i < g.size(); ++i) {
      currentNodeInGG = (Host) g.get(i);
      if (currentNodeInGG.equals(currentNode)) {
        break;
      }
    }
    ArrayList neighbors = currentNodeInGG.getIncidentHosts();

    int deg = neighbors.size();
    if (deg == 1) {
      return new Edge(e.b, e.a);
    }
    Host bestHost = null;
    double bestPhi = 7;
    if (!cw) {
      bestPhi = -1;
    }
    for (int i = 0; i < deg; i++) {
      Host host = (Host) neighbors.get(i);
      if (!host.equals(e.a)) {
        double phi = getAngle(e.a, e.b, host);
        if (cw) {
          if (phi < bestPhi) {
            bestPhi = phi;
            bestHost = host;
          }
        }
        else {
          if (phi > bestPhi) {
            bestPhi = phi;
            bestHost = host;
          }
        }
      }
    }
    return new Edge(e.b, bestHost);
  }

  public static ArrayList getGabrielGraph(Network network) {
    /*
         * Special notes: The generation of Gabriel graph has an assumption that the
     * radius for all the nodes are the same.
     */
    ArrayList ggHosts = network.getClonedHostArray();

    // Setup a removed host array to speed up the process.
    ArrayList[] removedHosts = new ArrayList[ggHosts.size()];
    for (int i = 0; i < removedHosts.length; ++i) {
      removedHosts[i] = new ArrayList();
    }

    // Calculate the Gabriel graph.
    for (int i = 0; i < ggHosts.size(); ++i) {
      Host host1 = (Host) ggHosts.get(i);
      ArrayList neighbors = host1.getIncidentHosts();

      if (neighbors.size() <= 1) {
        // Less than two neighbors.
        continue;
      }
      for (int j = 0; j < neighbors.size(); ++j) {
        Host host2 = (Host) neighbors.get(j);
        Point2D.Double ggCenter = new Point2D.Double( (host1.getX() +
            host2.getX()) / 2,
            (host1.getY() + host2.getY()) / 2);
        double ggRadius = host1.getPosition().distance(ggCenter);
        for (int k = 0; k < neighbors.size(); ++k) {
          Host host3 = (Host) neighbors.get(k);
          if (host3.equals(host2)) {
            continue;
          }
          if (host3.getPosition().distance(ggCenter) <= ggRadius) {
            // Find a point with the diameter of (host1, host2)
            removedHosts[i].add(host2);
          }
        }
      }
    }

    // Remove the edges.
    for (int i = 0; i < ggHosts.size(); ++i) {
      Host host1 = (Host) ggHosts.get(i);
      for (int j = 0; j < removedHosts[i].size(); ++j) {
        Host host2 = (Host) removedHosts[i].get(j);
        host1.removeIncidentHost(host2);
        host2.removeIncidentHost(host1);
      }
    }

    return ggHosts;
  }

  public static Host getVirtualDest(ArrayList dests) {

    Host virtualhost = null;
    double x = 0;
    double y = 0;

    if ( dests.size() == 1) {
     return  (Host) dests.get(0);
    }

    for (int i = 0; i < dests.size(); i++) {
      Host host = (Host) dests.get(i);
      x += host.getX();
      y += host.getY();
    }

    if (dests.size() == 0) {
      return null;
    }
    else {
      x = x / dests.size();
      y = y / dests.size();
    }
    virtualhost = new Host(x, y);
    return virtualhost;

  }

  public static boolean isClosertoDest(Host a, Host b, Host Vdest) {
    double da = a.getPosition().distance(Vdest.getPosition());
    double db = b.getPosition().distance(Vdest.getPosition());
    if (da < db) {
      return true;
    }
    else {
      return false;
    }
  }

}
