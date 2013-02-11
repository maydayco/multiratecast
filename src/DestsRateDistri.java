/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class DestsRateDistri {
  private double minRate;
  private double maxRate;
  private int distMode;
  private double startRate;
  private double rateSteps;

  public static final int RANDOM_DISTRIBUTES = 0;
  public static final int INCREASE_DISTRIBUTES = 1;
  public static final int DECREASE_DISTRIBUTES = 2;

  public DestsRateDistri(double min, double max) {
    this (min, max, RANDOM_DISTRIBUTES, 0, 0);
  }

  public DestsRateDistri(double min, double max, int mode, double start,
      double steps) {
    minRate = min;
    maxRate = max;
    distMode = mode;
    startRate = start;
    rateSteps = steps;
  }

  public double getMinRate () {
    return minRate;
  }

  public void setMinRate (double r) {
    minRate = r;
  }

  public double getMaxRate() {
    return maxRate;
  }

  public void setMaxRate(double r) {
    maxRate = r;
  }

  public int getDistMode() {
    return distMode;
  }

  public void setDistMode(int m) {
    distMode = m;
  }

  public double getStartRate() {
    return startRate;
  }

  public void setStartRate(double r) {
    startRate = r;
  }

  public double getRateSteps() {
    return rateSteps;
  }

  public void setRateSteps(double r) {
    rateSteps = r;
  }
}
