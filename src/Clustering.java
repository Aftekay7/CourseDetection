import java.util.Iterator;
import java.util.LinkedList;
import java.lang.Math;

public class Clustering {
    private static final Vector_2D offset = new Vector_2D(15, 15);
    private static LinkedList<Vector_2D> map;
    private static LinkedList<Vector_2D> centers = new LinkedList<>();
    private static LinkedList<Vector_2D> trajectory = new LinkedList<>();
    private static Renderer renderer = new Renderer();


    private static final float MIN_TRACK_WIDTH = 3.0f;  //min track width in meter

    public static LinkedList<Vector_2D> fullTrajectory(LinkedList<Vector_2D> input) {
        map = input;
        initCentersDynamic(10, 0.5f);
        cluster(50,10,3,0.1f,0.2f);
        uniteCenters(1.5f);
        removeDeleted();
        findTrajectory();
        centers = trajectory;
        updateFrame(true,false,false);
        clustering_2(30,5f,10, 1);

        //cluster(50, 10, 3, 1f, 0.6f);
        //uniteCenters(2);
        //removeDeleted();

        //findTrajectory();
        //centers = trajectory;

        updateFrame(true, true, true);


        return trajectory;
    }

    private static void clustering_2(int epochs, float roh, float delta, float theta) {

        float weight;
        int update_count = 0;
        Vector_2D update = new Vector_2D(0, 0);
        Vector_2D buf;
        for (int i = 0; i < epochs; i++) {
            delay(1000);
            deflectCenters(0.2f);
            updateFrame(true,false,true);
        }
    }

    private static LinkedList<Vector_2D> getAllInRange(Vector_2D vec, float radius) {
        LinkedList<Vector_2D> inRange = new LinkedList<>();
        for (Vector_2D other : centers) {
            if (!other.isDeleted() && vec.id != other.id && MathHelpers.distance(vec, other) < radius) {
                inRange.add(other);
            }
        }
        return inRange;
    }


    private static void cluster(int epochs, float delta, float delta_steps, float theta, float roh) {
        float uc_x1 = epochs * 0.1f;
        float uc_x2 = epochs * 0.5f;
        float uc_x3 = epochs * 0.8f;
        float dc_x1 = epochs * 0.6f;
        float dc_x2 = epochs * 0.65f;
        int rd_frequency = 15;
        int rd_x1 = 5;
        float delta_decr = (delta_steps * delta) / epochs;

        for (int i = 0; i < epochs; i++) {

            for (Vector_2D center : centers) {
                updateCenter(center, delta);
            }
            updateFrame(true, false, false);

            if (i > uc_x1 && i < uc_x2 || i > uc_x3) {
                uniteCenters(theta);
                updateFrame(true, false, false);
            }

            if (i >= dc_x1 && i <= dc_x2) {
                deflectCenters(roh);
                updateFrame(true, false, false);
            }

            if (i % rd_frequency == 0 || i < rd_x1) {
                removeDeleted();
            }

            if (i % delta_steps == 0 && i != 0 && i < uc_x2) {
                delta -= delta_decr;
            }

        }
        removeDeleted();

    }

    private static void initCenters() {
        for (Vector_2D vec : map) {
            centers.add(vec.copy());
        }
    }


    private static void initCentersAll() {
        float count_init_centers = map.size() * 100;

        Vector_2D[] corners = getCorners(map);
        Vector_2D start = corners[0].sub(offset);

        float range_x = corners[1].x - corners[0].x + 2 * offset.x;
        float range_y = corners[1].y - corners[0].y + 2 * offset.y;

        float ratio = range_x / range_y;
        float cpld = (float) Math.sqrt(count_init_centers);
        float cpl = ratio * cpld;
        float cpc = cpld / ratio;
        float stepSize_x = range_x / cpl;
        float stepSize_y = range_y / cpc;

        start.x -= stepSize_x / 2;
        start.y += stepSize_y / 2;

        Vector_2D step = new Vector_2D(stepSize_x, 0);
        Vector_2D iterator = start; //not a copy

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

    private static void updateCenter(Vector_2D center, float delta) {
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
        center.addToThis(update);
    }


    private static void uniteCenters(float theta) {
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

    private static void deflectCenters(float roh) {
        for (Vector_2D center : centers) {
            if (center.isDeleted()) {
                continue;
            }

            for (Vector_2D other : centers) {
                if (!other.isDeleted() && center.id != other.id && MathHelpers.distance(center, other) < roh) {

                    Vector_2D dist = other.sub(center);
                    dist.scale(5f);
                    other.addToThis(dist);
                    dist.scale(-1);
                    center.addToThis(dist);
                }
            }
        }
    }

    private static void explodeCenters(float roh) {
        LinkedList<Vector_2D> buf = new LinkedList<>();
        for (Vector_2D center : centers) {
            if (center.isDeleted()) {
                continue;
            }

            for (Vector_2D other : centers) {
                if (!other.isDeleted() && center.id != other.id && MathHelpers.distance(center, other) < roh) {

                    Vector_2D dist = other.sub(center);
                    dist.scale(5);
                    Vector_2D dist2 = new Vector_2D(dist.y, dist.x);
                    dist2.scale(5);


                    buf.add(other.add(dist2));
                    other.addToThis(dist);
                    dist.scale(-1);
                    dist2.scale(-1);
                    buf.add(center.add(dist2));
                    center.addToThis(dist);


                }
            }
        }
        centers.addAll(buf);
    }

    private static void removeDeleted() {
        centers.removeIf(Vector_2D::isDeleted);
    }


    //Intervalls are [0,X_1,X_2,INFINITY]
    private static final float FUNC_INT_X_1 = 0.5f * MIN_TRACK_WIDTH;
    private static final float FUNC_INT_X_2 = MIN_TRACK_WIDTH;
    private static final float FUNC_MAX = MIN_TRACK_WIDTH * MIN_TRACK_WIDTH;    //ensures that weight(MIN_TRACK_WIDTH) == 1
    private static final float FUNC_GRADIENT = (2 / (FUNC_INT_X_2 - FUNC_INT_X_1));

    private static float calcWeight(float dist) {
        float weight;
        if (dist > FUNC_INT_X_2) {

            weight = FUNC_MAX / (dist * dist);

        } else if (dist >= FUNC_INT_X_1) {

            weight = FUNC_GRADIENT * (dist - FUNC_INT_X_2);

        } else {

            weight = -2f;

        }
        return weight;
    }


    private static void fillTrajectory(float roh) {

        LinkedList<Vector_2D> newCenters = new LinkedList<>();
        Vector_2D newCenter;
        Vector_2D next = centers.getFirst();
        Vector_2D step;
        Vector_2D left;
        Vector_2D right;
        int count;

        Iterator<Vector_2D> iterator = centers.iterator();

        while (iterator.hasNext()) {
            newCenter = next;
            next = iterator.next();


            step = next.sub(newCenter);
            count = (int) (step.length() * 1.5f);


            step.normalizeThis();
            step.scale(step.length() * 0.25f);
            newCenter = newCenter.add(step);

            step.normalizeThis();
            right = step.rotateCW(90);
            right.scale(1.5f);
            left = right.copy();
            left.scale(-1);
            step.scale(roh);

            count /= roh;
            count--;


            while (count > 0) {
                newCenter = newCenter.add(step);
                newCenters.add(newCenter);
                newCenters.add(newCenter.add(left));
                newCenters.add(newCenter.add(right));
                count--;
            }

        }

        centers.addAll(newCenters);
        updateFrame(true, false, false);
    }


    private static boolean isInFOV(Vector_2D pos, Vector_2D direction, Vector_2D other) {
        Vector_2D dist = other.sub(pos);
        dist.normalizeThis();
        float dp = MathHelpers.dotProduct(direction, dist);

        return dp > 0;
    }

    private static void updateFrame(boolean centers, boolean map, boolean traj) {
        LinkedList<Vector_2D> cpy;

        if (centers) {
            cpy = (LinkedList<Vector_2D>) Clustering.centers.clone();
            renderer.updateCenters(cpy);
        }

        if (map) {
            cpy = (LinkedList<Vector_2D>) Clustering.map.clone();
            renderer.updateMap(cpy);
        }

        if (traj) {
            cpy = (LinkedList<Vector_2D>) Clustering.trajectory.clone();
            renderer.updateTrajectory(cpy);
        }
    }

    private static void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static float fovScore(Vector_2D pos, Vector_2D direction, Vector_2D other) {
        Vector_2D POS_OTHER = other.sub(pos);
        float dist = POS_OTHER.length();
        POS_OTHER.normalizeThis();

        return MathHelpers.dotProduct(direction, POS_OTHER) / (dist * dist);
    }


    private static void initCentersDynamic(float radius, float roh) {
        float dist;
        Vector_2D newCenter;
        Vector_2D step;
        float count;
        for (Vector_2D cone : map) {


            for (Vector_2D other : map) {
                newCenter = cone;
                dist = MathHelpers.distance(cone, other);

                if (dist < radius) {
                    count = dist / roh;
                    step = other.sub(cone);
                    step.normalizeThis();
                    step.scale(roh);

                    count--;

                    while (count > 0) {
                        newCenter = newCenter.add(step);
                        centers.add(newCenter);
                        updateFrame(true, true, false);
                        count--;
                    }
                }
            }
        }
    }

    private static void findTrajectory() {

        Vector_2D looking_dir = new Vector_2D(1, 0);

        //find nearest points and add points in between
        Vector_2D center = new Vector_2D(-1, 0);
        centers.add(center);
        trajectory.add(center);

        int startId = center.id;

        Vector_2D next;
        float max_fov_score;
        float fov_score;

        while (center != null) {

            //find the likeliest next center in the FOV
            next = null;
            max_fov_score = 0;
            for (Vector_2D other : centers) {

                fov_score = fovScore(center, looking_dir, other);
                if (!other.isNearest() && center.id != other.id && fov_score > max_fov_score) {
                    max_fov_score = fov_score;
                    next = other;
                }
            }

            if (next != null) {
                trajectory.add(next);
                updateFrame(false, false, true);
                looking_dir = next.sub(center);

                if (next.id == startId) {
                    next = null;
                }
            }

            //continue search from the nearest center
            center = next;
        }
        updateFrame(true, true, true);
    }


    //Intervalls are [0,X_1,X_2,INFINITY]
    private static final float WTC_INT_X_1 = 0.5f * MIN_TRACK_WIDTH;
    private static final float WTC_INT_X_2 = MIN_TRACK_WIDTH;
    private static final float WTC_MAX = MIN_TRACK_WIDTH * MIN_TRACK_WIDTH;    //ensures that weight(MIN_TRACK_WIDTH) == 1

    private static float calcWeightToCone(float dist) {
        float weight;
        if (dist < WTC_INT_X_2) {
            weight = (float) Math.pow(dist - WTC_INT_X_1, 3);
        } else {
            weight = WTC_MAX / (dist * dist);
        }
        return weight;
    }


    private static float calcWeightToConeSimple(float dist) {
        float weight = 0;
        if (dist < 1.5f) {
            weight = -1;

        } else if (dist > 1.5f) {
            weight = 1;
        }
        return weight;
    }

    private static float calcWeightToCenter(float dist, float roh) {
        float weight;

        if (dist < WTC_INT_X_1) {
            weight = (float) (0.5f * Math.pow(dist - roh, 3));
        } else {
            weight = WTC_INT_X_1 / (dist * dist);
        }
        return weight;
    }

    private static float calcWeightToCenterSimple(float dist, float roh) {
        float weight;

        if (dist < roh) {
            weight = -1;
        } else if (dist < roh * 2) {
            weight = 0;
        } else {
            weight = 1;
        }
        return weight;
    }
}
