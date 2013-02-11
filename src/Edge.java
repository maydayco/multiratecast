import java.awt.geom.*;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Graphics2D;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

  public class Edge {

    public Host a;
    public Host b;
                  public Edge() {
                  this.a = null;
                  this.b = null;
                 }


                 public Edge(Host a, Host b) {
                         this.a = a;
                         this.b = b;
                 }

                 public Edge getReverse() {
                         return new Edge(b, a);
                 }

                 public boolean equals(Edge e) {
                         return (a == e.a) && (b == e.b);
                 }

                 public boolean equalsSymmetric(Edge e) {
                         return equals(e) || equals(new Edge(e.b, e.a));
                 }

                 // Does e have a Host in common with this Edge?
                 public boolean isIncidentTo(Edge e) {
                         return (getJoiningHost(this, e) != null);
                 }

                 // Is h at one end of this Edge?
                 public boolean isIncidentTo(Host h) {
                         return ((h == a) || (h == b));
                 }

                 public Edge getPointingAwayFrom(Host h) {
                         if (h == a)
                                 return this;
                         else if (h == b)
                                 return getReverse();
                         else
                                 return null;
                 }

                 public static Host getJoiningHost(Edge e, Edge f) {
                         if (f.isIncidentTo(e.a))
                                 return e.a;
                         else if (f.isIncidentTo(e.b))
                                 return e.b;
                         else
                                 return null;
                 }

                 public static boolean isIntersect(Edge e1, Edge e2) {
                   if (Line2D.linesIntersect(e1.a.getX(), e1.a.getY(),
                                              e1.b.getX(),e1.b.getY(),
                                              e2.a.getX(), e2.a.getY(),
                                              e2.b.getX(),e2.b.getY()))
                   { return true;}
                   else
                   { return false; }
                 }


                 public static boolean isRightFrom(Edge e, Host h) {
                 return (Line2D.relativeCCW(e.a.getX(), e.a.getY(),
                                            e.b.getX(), e.b.getY(),
                                            h.getX(), h.getY()) == -1);
        }

  }

