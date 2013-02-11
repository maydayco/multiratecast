import java.io.Serializable;
import java.util.ArrayList;

// it's sometimes good to have an order over the hosts
public class HostArray
    implements Serializable {

  private ArrayList hosts;

  public HostArray() {
    hosts = new ArrayList();
  }

  public HostArray(ArrayList hostList) {
    hosts = hostList;
  }

  public boolean addHost(Host h) {
    if (hosts.contains(h)) {
      return false;
    }

    hosts.add(h);
    return true;
  }

  public boolean removeHost(Host h) {
    if (!hosts.contains(h)) {
      return false;
    }

    hosts.remove(h);
    return true;
  }

  /**
   * Removes all of the nodes from this list.
   */
  public void clear() {
    hosts.clear();
  }

  /**
   * Returns the number of nodes in this list.
   * @return the number of nodes in this list.
   */
  public int size() {
    return hosts.size();
  }

  /**
   * Find a index of a host in the list.
   * @param aHost the host to be looked for
   * @return the index of the host, return -1 if not find.
   */
  public int findHost(Host aHost) {
    for (int i = 0; i < hosts.size(); ++i) {
      if (aHost.equals(hosts.get(i))) {
        return i;
      }
    }

    // Couldn't find the host in the list.
    return -1;
  }

  /**
   * Return the hosts by given index.
   * @param index the index which the host to be returned
   * @return the host by given index
   */
  public Host get(int index) {
    return (Host) hosts.get(index);
  }

  /**
   * Return all the hosts in the host array.
   * @return all the hosts in the host array.
   */
  public ArrayList getHosts() {
    return hosts;
  }
}
