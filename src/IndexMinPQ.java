import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *  The <tt>IndexMinPQ</tt> class represents an indexed priority queue of generic keys.
 *  It supports the usual <em>insert</em> and <em>delete-the-minimum</em>
 *  operations, along with <em>delete</em> and <em>change-the-key</em> 
 *  methods. In order to let the client refer to items on the priority queue,
 *  an integer between 0 and NMAX-1 is associated with each key&mdash;the client
 *  uses this integer to specify which key to delete or change.
 *  It also supports methods for peeking at the minimum key,
 *  testing if the priority queue is empty, and iterating through
 *  the keys.
 *  <p>
 *  The <em>insert</em>, <em>delete-the-minimum</em>, <em>delete</em>,
 *  <em>changeKey</em>, <em>decreaseKey</em>, and <em>increaseKey</em>
 *  operations take logarithmic time.
 *  The <em>isEmpty</em>, <em>size</em>, <em>minIndex</em>, <em>minKey</em>, <em>keyOf</em>,
 *  <em>size</em>, and <em>is-empty</em> operations take constant time.
 *  Construction takes time proportional to the specified capacity.
 *  <p>
 *  This implementation uses a binary heap along with an array to associate
 *  keys with integers in the given range.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/24pq">Section 2.4</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 */
public class IndexMinPQ<Key extends Comparable<Key>> implements Iterable<Integer> {
    private int NMAX;        // maximum number of elements on PQ
    private int N;           // number of elements on PQ
    private int[] pq;        // binary heap using 1-based indexing
    private int[] qp;        // inverse of pq - qp[pq[i]] = pq[qp[i]] = i
    private Key[] keys;      // keys[i] = priority of i

   /**
     * Create an empty indexed priority queue with indices between 0 and NMAX-1.
     * @throws java.lang.IllegalArgumentException if NMAX < 0
     */
    public IndexMinPQ(int NMAX) {
        if (NMAX < 0) throw new IllegalArgumentException();
        this.NMAX = NMAX;
        keys = (Key[]) new Comparable[NMAX + 1];    // make this of length NMAX??
        pq   = new int[NMAX + 1];
        qp   = new int[NMAX + 1];                   // make this of length NMAX??
        for (int i = 0; i <= NMAX; i++) qp[i] = -1;
    }

   /**
     * Is the priority queue empty?
     */
    public boolean isEmpty() { return N == 0; }

   /**
     * Is k an index on the priority queue?
     * @throws java.lang.IndexOutOfBoundsException unless (0 &le; k < NMAX)
     */
    public boolean contains(int k) {
        if (k < 0 || k >= NMAX) throw new IndexOutOfBoundsException();
        return qp[k] != -1;
    }

   /**
     * Return the number of keys on the priority queue.
     */
    public int size() {
        return N;
    }

   /**
     * Associate key with index k.
     * @throws java.lang.IndexOutOfBoundsException unless 0 &le; k < NMAX
     * @throws java.util.NoSuchElementException if there already is an item associated with index k.
     */
    public void insert(int k, Key key) {
        if (k < 0 || k >= NMAX) throw new IndexOutOfBoundsException();
        if (contains(k)) throw new NoSuchElementException("item is already in pq");
        N++;
        qp[k] = N;
        pq[N] = k;
        keys[k] = key;
        swim(N);
    }

   /**
     * Return the index associated with a minimal key.
     * @throws java.util.NoSuchElementException if priority queue is empty.
     */
    public int minIndex() { 
        if (N == 0) throw new NoSuchElementException("Priority queue underflow");
        return pq[1];        
    }

   /**
     * Return a minimal key.
     * @throws java.util.NoSuchElementException if priority queue is empty.
     */
    public Key minKey() { 
        if (N == 0) throw new NoSuchElementException("Priority queue underflow");
        return keys[pq[1]];        
    }

   /**
     * Delete a minimal key and return its associated index.
     * @throws java.util.NoSuchElementException if priority queue is empty.
     */
    public int delMin() { 
        if (N == 0) throw new NoSuchElementException("Priority queue underflow");
        int min = pq[1];        
        exch(1, N--); 
        sink(1);
        qp[min] = -1;            // delete
        keys[pq[N+1]] = null;    // to help with garbage collection
        pq[N+1] = -1;            // not needed
        return min; 
    }

   /**
     * Return the key associated with index k.
     * @throws java.lang.IndexOutOfBoundsException unless 0 &le; k < NMAX
     * @throws java.util.NoSuchElementException no key is associated with index k
     */
    public Key keyOf(int k) {
        if (k < 0 || k >= NMAX) throw new IndexOutOfBoundsException();
        if (!contains(k)) throw new NoSuchElementException("index is not in pq");
        else return keys[k];
    }

   /**
     * Change the key associated with index k to the specified value.
     * @throws java.lang.IndexOutOfBoundsException unless 0 &le; k < NMAX
     */
    public void change(int k, Key key) {
        changeKey(k, key);
    }

   /**
     * Change the key associated with index k to the specified value.
     * @throws java.lang.IndexOutOfBoundsException unless 0 &le; k < NMAX
     * @throws java.util.NoSuchElementException no key is associated with index k
     */
    public void changeKey(int k, Key key) {
        if (k < 0 || k >= NMAX) throw new IndexOutOfBoundsException();
        if (!contains(k)) throw new NoSuchElementException("index is not in pq");
        keys[k] = key;
        swim(qp[k]);
        sink(qp[k]);
    }

   /**
     * Decrease the key associated with index k to the specified value.
     * @throws java.lang.IndexOutOfBoundsException unless 0 &le; k < NMAX
     * @throws java.lang.IllegalArgumentException if key &ge; key associated with index k
     * @throws java.util.NoSuchElementException no key is associated with index k
     */
    public void decreaseKey(int k, Key key) {
        if (k < 0 || k >= NMAX) throw new IndexOutOfBoundsException();
        if (!contains(k)) throw new NoSuchElementException("index is not in pq");
        if (keys[k].compareTo(key) <= 0) throw new IllegalArgumentException("Calling decreaseKey() with given argument would not strictly decrease the key");
        keys[k] = key;
        swim(qp[k]);
    }

   /**
     * Increase the key associated with index k to the specified value.
     * @throws java.lang.IndexOutOfBoundsException unless 0 &le; k < NMAX
     * @throws java.lang.IllegalArgumentException if key &le; key associated with index k
     * @throws java.util.NoSuchElementException no key is associated with index k
     */
    public void increaseKey(int k, Key key) {
        if (k < 0 || k >= NMAX) throw new IndexOutOfBoundsException();
        if (!contains(k)) throw new NoSuchElementException("item is not in pq");
        if (keys[k].compareTo(key) >= 0) throw new IllegalArgumentException("Calling increaseKey() with given argument would not strictly increase the key");
        keys[k] = key;
        sink(qp[k]);
    }

   /**
     * Delete the key associated with index k.
     * @throws java.lang.IndexOutOfBoundsException unless 0 &le; k < NMAX
     * @throws java.util.NoSuchElementException no key is associated with index k
     */
    public void delete(int k) {
        if (k < 0 || k >= NMAX) throw new IndexOutOfBoundsException();
        if (!contains(k)) throw new NoSuchElementException("item is not in pq");
        int index = qp[k];
        exch(index, N--);
        swim(index);
        sink(index);
        keys[k] = null;
        qp[k] = -1;
    }


   /**************************************************************
    * General helper functions
    **************************************************************/
    private boolean greater(int i, int j) {
        return keys[pq[i]].compareTo(keys[pq[j]]) > 0;
    }

    private void exch(int i, int j) {
        int swap = pq[i]; pq[i] = pq[j]; pq[j] = swap;
        qp[pq[i]] = i; qp[pq[j]] = j;
    }


   /**************************************************************
    * Heap helper functions
    **************************************************************/
    private void swim(int k)  {
        while (k > 1 && greater(k/2, k)) {
            exch(k, k/2);
            k = k/2;
        }
    }

    private void sink(int k) {
        while (2*k <= N) {
            int j = 2*k;
            if (j < N && greater(j, j+1)) j++;
            if (!greater(k, j)) break;
            exch(k, j);
            k = j;
        }
    }


   /***********************************************************************
    * Iterators
    **********************************************************************/

   /**
     * Return an iterator that iterates over all of the elements on the
     * priority queue in ascending order.
     * <p>
     * The iterator doesn't implement <tt>remove()</tt> since it's optional.
     */
    public Iterator<Integer> iterator() { return new HeapIterator(); }

    private class HeapIterator implements Iterator<Integer> {
        // create a new pq
        private IndexMinPQ<Key> copy;

        // add all elements to copy of heap
        // takes linear time since already in heap order so no keys move
        public HeapIterator() {
            copy = new IndexMinPQ<Key>(pq.length - 1);
            for (int i = 1; i <= N; i++)
                copy.insert(pq[i], keys[pq[i]]);
        }

        public boolean hasNext()  { return !copy.isEmpty();                     }
        public void remove()      { throw new UnsupportedOperationException();  }

        public Integer next() {
            if (!hasNext()) throw new NoSuchElementException();
            return copy.delMin();
        }
    }


    public static void main(String[] args) {
        // insert a bunch of strings
        String[] strings = { "it", "was", "the", "best", "of", "times", "it", "was", "the", "worst" };

        IndexMinPQ<String> pq = new IndexMinPQ<String>(strings.length);
        for (int i = 0; i < strings.length; i++) {
            pq.insert(i, strings[i]);
        }

        // delete and print each key
        while (!pq.isEmpty()) {
            int i = pq.delMin();
            StdOut.println(i + " " + strings[i]);
        }
        StdOut.println();

        // reinsert the same strings
        for (int i = 0; i < strings.length; i++) {
            pq.insert(i, strings[i]);
        }

        // print each key using the iterator
        for (int i : pq) {
            StdOut.println(i + " " + strings[i]);
        }
        while (!pq.isEmpty()) {
            pq.delMin();
        }

    }
}