import java.util.Iterator;
import java.util.LinkedList;
import java.lang.Math;
import java.util.Scanner;

public class Clustering {
    private static float delta = 15;   //defines the radius (in meters) in which trajectory points are adjusted
    private static final float theta = 0.01F;  //defines the distance within which centers are combined
    private static final int epochs = 200; //number of epochs that are performed
    private static int epoch = 1;

    private static float delta_sz = delta / epochs;
    private static final int uniteAfter = (7 * epochs) / 10 ; //defines the number of epochs after which centers that are
    private static int startingCenters; //defines the number of centers that are initialized in the first epoch
    private static final Vector_2D offset = new Vector_2D(15, 15);
    private static LinkedList<Vector_2D> centers;
    private static LinkedList<Vector_2D> map;
    private static Renderer renderer = new Renderer();
    private static Scanner scanner = new Scanner(System.in);

    private static final float MIN_TRACK_WIDTH = 3f;

    public static LinkedList<Vector_2D> cluster(LinkedList<Vector_2D> input) {
        map = input;
        startingCenters = map.size() * 100;
        System.out.println(map.size());
        initCenters();
        renderer.setLists(centers, map);
        renderer.updatePoints();

        for (int i = 0; i < epochs; i++) {
            scanner.nextLine();

            for (Vector_2D center : centers) {
                updateCenter(center);
            }

            if (i >= uniteAfter) {
                uniteCenters();
            }
            removeDeleted();

            renderer.updatePoints();
            epoch++;

            if (delta > 2 * MIN_TRACK_WIDTH) {
                delta -= delta_sz;
            }

        }


        return centers;
    }


    private static void initCenters() {
        centers = new LinkedList<>();


        Vector_2D v = new Vector_2D(0,0);

        for (Vector_2D vec : map) {
            centers.add(vec.add(v));
        }



        Vector_2D[] corners = getCorners(map);
        Vector_2D start = corners[0].sub(offset);

        float range_x = corners[1].x - corners[0].x + 2 * offset.x;
        float range_y = corners[1].y - corners[0].y + 2 * offset.y;

        float ratio = range_x / range_y;
        float cpld = (float) Math.sqrt(startingCenters);
        float cpl = ratio * cpld;
        float cpc = cpld / ratio;
        float stepSize_x = range_x / cpl;
        float stepSize_y = range_y / cpc;

        start.x -= stepSize_x / 2;
        start.y += stepSize_y / 2;

        Vector_2D step = new Vector_2D(stepSize_x, 0);
        Vector_2D iterator = start; //not a copy
        System.out.println("\n");

        for (int row = 0; row < cpl; row++) {
            for (int col = 0; col < cpc; col++) {
                Vector_2D c = iterator.add(step);
                centers.add(c);
                iterator = c;
            }
            start.y += stepSize_y;
            iterator = start;
        }



    }

    private static Vector_2D[] getCorners(LinkedList<Vector_2D> map) {
        float min_x = map.getFirst().x;
        float min_y = map.getFirst().y;
        float max_x = min_x;
        float max_y = min_y;

        for (Vector_2D vec : map) {
            if (vec.x < min_x) {
                min_x = vec.x;
            }
            if (vec.x > max_x) {
                max_x = vec.x;
            }

            if (vec.y < min_y) {
                min_y = vec.y;
            }
            if (vec.y > max_y) {
                max_y = vec.y;
            }
        }
        Vector_2D[] minmax = new Vector_2D[2];
        minmax[0] = new Vector_2D(min_x, min_y);
        minmax[1] = new Vector_2D(max_x, max_y);
        return minmax;
    }

    private static void updateCenter(Vector_2D center) {
        Vector_2D update = new Vector_2D(0, 0);
        Vector_2D dist;
        float weight;
        float update_count = 0;
        for (Vector_2D cone : map) {
            float distance = MathHelpers.distance(center, cone);
            if (distance < delta) {
                dist = cone.sub(center);
                weight = calcWeight(distance);
                dist.scale(weight);

                update.addToThis(dist);
                update_count++;
            }
        }

        if (update_count == 0) {
            center.delete();
            return;
        }

        update.scale((1f / update_count));
        /*
        float s = 1f / epoch;
        if (s < 0.1) {
            s = 0.1f;
        }

        update.scale(s);
         */
        center.addToThis(update);
    }


    private static void uniteCenters() {
        for (Vector_2D center : centers) {
            if (center.isDeleted()) {
                continue;
            }

            for (Vector_2D other : centers) {
                if (!other.isDeleted() && center.id != other.id && MathHelpers.distance(center, other) < theta) {
                    center.addToThis(other);
                    center.scale(0.5f);
                    other.delete();
                }
            }
        }
    }

    private static void removeDeleted() {
        Iterator<Vector_2D> it = centers.iterator();
        while (it.hasNext()) {
            Vector_2D c = it.next();
            if (c.isDeleted()) {
                it.remove();
            }
        }
    }

    private static float calcWeight(float dist) {
        float weight;
        if (dist > MIN_TRACK_WIDTH) {
            weight = 9 / (dist * dist);

        } else if (dist >= 0.5 * MIN_TRACK_WIDTH) {
            weight = 1.333f * (dist - 2.25f);

        } else {
            weight = -1f;

        }
        return weight;
    }
}
