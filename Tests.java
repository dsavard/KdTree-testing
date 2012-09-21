/**
 * Tests: Unit tests for PointSET and KdTree classes
 * 
 * @author dsavard
 *
 * Author: Daniel Savard
 * Email: Daniel.Savard@gmail.com
 * Date 2012-09-21
 * 
 */
public class Tests {
    private KdTree kdt;
    private PointSET ps;
    
    /**
     * Constuctor
     */
    public Tests() {
        kdt = new KdTree();
        ps = new PointSET();
    }

    /**
     * Randomly generate data points and insert into both classes
     * 
     * @param size number of points required
     */
    private void random(int size) {
        Point2D p;
        for (int i = 0; i < size; i++) {
            p = new Point2D(StdRandom.uniform(), StdRandom.uniform());
            ps.insert(p);
            kdt.insert(p);
        }
  
    }
    
    /**
     * Read an already existing data file.
     * Format: x y coordinates separated by a single space, on point per line.
     * 
     * @param filename (String)
     */
    public void readFile(String filename) {
        String line;
        int end;
        Point2D p;
        In in = new In(filename);

        while (in.hasNextLine()) {
            line = in.readLine();
            end = line.trim().indexOf(' ');
            if (end > 0) {
                p = new Point2D(Double.parseDouble(line.substring(0, end)),
                        Double.parseDouble(line.substring(end)));
                ps.insert(p);
                kdt.insert(p);
            }
        }
    
    }
    
    /**
     * Generate a grid of the required size and write it to the file
     * in the subdirectory data as grid-NxN.txt where N is the size of
     * the grid.
     * 
     * @param size (int) size of the grid
     */
    public void grid(int size) {
        String filename = String.format("data/grid-%dx%d.txt", size, size);
        Out out = new Out(filename);
        double gap = 1.0/size;
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                out.printf("%f %f\n", i*gap, j*gap);
            }
        }
        out.close();
    }
    
    /**
     * Print the size of the trees for both classes
     */
    public void printSize() {
        StdOut.printf("Brute size: %d\n", ps.size());
        StdOut.printf("KdTree size: %d\n", kdt.size());
    }
    
    /**
     * Find the nearest point from a randomly generated
     * query point. Compare results from both classes and
     * output any discrepancy with coordinates of the query
     * point, the nearest point for each method and the
     * computed distance to the query point.
     * 
     * @param num (int) number of query points to test
     */
    public void nearest(int num) {
        Point2D p, kdtp, psp;
        int count = 0, ecount = 0, missed = 0;
        double psTime = 0.0, kdtTime = 0.0;
        Stopwatch sw;
        
        while (count < num) {
            count++;
            p = new Point2D(StdRandom.uniform(), StdRandom.uniform());
            sw = new Stopwatch();
            psp = ps.nearest(p);
            psTime += sw.elapsedTime();
            sw = new Stopwatch();
            kdtp = kdt.nearest(p);
            kdtTime += sw.elapsedTime();
            
            if (psp.compareTo(kdtp) == 0) {
                ecount++;
            } else {
                missed++;
                StdOut.println("-----------------------------------");
                StdOut.printf("Query point: %s\n", p.toString());
                StdOut.printf("Brute point: %s\t%f\n",
                        psp.toString(), p.distanceTo(psp));
                StdOut.printf("KdTree point: %s\t%f\n",
                        kdtp.toString(), p.distanceTo(kdtp));
            }
        }
        StdOut.printf("Trials: %d\nSuccess: %d\nMissed: %d\n\n",
                num, ecount, missed);
        StdOut.printf("Timing Brute method: %f (ms/point)\n", psTime*1000/num);
        StdOut.printf("Timing KdTree method: %f (ms/point)\n", kdtTime*1000/num);
    }
    
    /**
     * Test the KdTree and PointSET classes
     * 
     * @param args if args[0] is an integer, use the randomly generated data
     *                                       points
     *             if args[0] is a string, try to open the file pointed by the
     *                                       string and read data points from
     *                                       it
     *             args[1] must be an integer and is the number of random queries
     *                     to run.
     */
    public static void main(String[] args) {
        int maxsize = 0, numTrials = 0;
        Tests t = null;
        Stopwatch sw;
        
        if (args.length > 1) {
            try {
                maxsize = Integer.parseInt(args[0]);
            }
            catch (NumberFormatException e) {
                maxsize = -1;
            }
            numTrials = Integer.parseInt(args[1]);
            t = new Tests();
            
            if (maxsize < 0) {
                StdOut.printf("Reading data points in file %s...", args[0]);
                sw = new Stopwatch();
                t.readFile(args[0]);
                StdOut.printf("done in %.2f (ms)\n", sw.elapsedTime()*1000);
            } else {
                StdOut.printf("Generating %d uniformily distributed data points.",
                        args[0]);
                sw = new Stopwatch();
                t.random(maxsize);
                StdOut.printf("done in %.2f (ms)\n", sw.elapsedTime()*1000);
            }
            StdOut.println("Number of points in data:\n");
            t.printSize();
            StdOut.printf("Randomly querying nearest point for %d points.\n",
                    numTrials);
            sw = new Stopwatch();
            t.nearest(numTrials);
            StdOut.printf("Total testing time: %.2f (ms)\n", sw.elapsedTime()*1000);
        }
    }

}
