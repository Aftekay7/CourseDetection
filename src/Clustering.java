import java.util.LinkedList;
import java.util.Vector;

public class Clustering {
    private static float delta = 10;   //defines the radius (in meters) in which trajectory points are adjusted
    private static float theta = 0.5F;  //defines the distance within which centers are combined
    private static int epochs = 10; //number of epochs that are performed
    private static int uniteAfter = 3; //defines the number of epochs after which centers that are
    private int startingCenters = 1000; //defines the number of centers that are initialized in the first epoch
    private static LinkedList<Vector_2D> centers; //list of centers


    public LinkedList<Vector_2D> cluster(LinkedList<Vector_2D> map) {
        Vector_2D[] corners = getCorners(map);
        float

    }


    private Vector_2D[] getCorners(LinkedList<Vector_2D> map) {
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

            Vector_2D[] corners = new Vector_2D[4];
            corners[0] = new Vector_2D(min_x, min_y);
            corners[1] = new Vector_2D(min_x, max_y);
            corners[2] = new Vector_2D(max_x, min_y);
            corners[3] = new Vector_2D(max_x, max_y);

        return corners;
    }
}
