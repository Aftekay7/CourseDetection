import javax.swing.*;
import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

public class Renderer extends JPanel {
    private JFrame frame;
    private LinkedList<Vector_2D> cones;
    private LinkedList<Vector_2D> centers;
    private LinkedList<Vector_2D> trajectory;
    private LinkedList<Vector_2D> axis;
    private final int PIXEL_PER_METER = 10;
    private int WIDTH = 1200;
    private int HEIGHT = 1080;
    private final int OFFSET_X = WIDTH / 3;
    private final int OFFSET_Y = HEIGHT / 6;

    public Renderer() {
        this.frame = new JFrame("Plotter");
        frame.add(this);
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        cones = new LinkedList<>();
        centers = new LinkedList<>();
        trajectory = new LinkedList<>();

    }

    public void updateCenters(LinkedList<Vector_2D> newCones) {
        this.centers = newCones;
        repaint();
    }

    public void updateTrajectory(LinkedList<Vector_2D> newTraj) {
        this.trajectory = newTraj;
        repaint();
    }
    public void updateMap(LinkedList<Vector_2D> newMap) {
        this.cones = newMap;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Set background color
        this.setBackground(Color.WHITE);
        int x;
        int y;

        // Draw cones
        g.setColor(Color.BLACK);
        for (Vector_2D vec : cones) {
            x = (int) (vec.x * PIXEL_PER_METER);
            y = (int) (vec.y * PIXEL_PER_METER * -1);
            x += -2 + OFFSET_X;
            y += -2 + OFFSET_Y;
            g.fillOval(x, y, 4, 4);

        }

        // Draw centers
        g.setColor(Color.RED);
        for (Vector_2D vec : centers) {
            if (vec.isDeleted()) {
                continue;
            }
            x = (int) (vec.x * PIXEL_PER_METER);
            y = (int) (vec.y * PIXEL_PER_METER * -1);
            x += -2 + OFFSET_X;
            y += -2 + OFFSET_Y;
            g.fillOval(x, y, 4, 4);
        }

        if (trajectory.size() > 1) {
            //Draw Trajectory
            g.setColor(Color.BLUE);
            Iterator<Vector_2D> iterator = trajectory.iterator();
            Vector_2D P_1;
            Vector_2D P_2 = trajectory.getFirst();
            int P1_X;
            int P1_Y;
            int P2_X;
            int P2_Y;
            while (iterator.hasNext()) {
                P_1 = P_2;
                P_2 = iterator.next();
                P1_X = (int) (P_1.x * PIXEL_PER_METER) + OFFSET_X;
                P1_Y = (int) (-1 * P_1.y * PIXEL_PER_METER) + OFFSET_Y;
                P2_X = (int) (P_2.x * PIXEL_PER_METER) + OFFSET_X;
                P2_Y = (int) (-1 * P_2.y * PIXEL_PER_METER) + OFFSET_Y;

                g.drawLine(P1_X, P1_Y, P2_X, P2_Y);
            }
        }

    }

    private void initAxis() {
        axis.add(new Vector_2D(-50, 0));
        axis.add(new Vector_2D(-40, 0));
        axis.add(new Vector_2D(-30, 0));
        axis.add(new Vector_2D(-20, 0));
        axis.add(new Vector_2D(-10, 0));
        axis.add(new Vector_2D(0, 0));
        axis.add(new Vector_2D(10, 0));
        axis.add(new Vector_2D(20, 0));
        axis.add(new Vector_2D(30, 0));
        axis.add(new Vector_2D(40, 0));
        axis.add(new Vector_2D(50, 0));

        axis.add(new Vector_2D(0, 10));
        axis.add(new Vector_2D(0, 0));
        axis.add(new Vector_2D(0, -10));
        axis.add(new Vector_2D(0, -20));
        axis.add(new Vector_2D(0, -30));
        axis.add(new Vector_2D(0, -40));
        axis.add(new Vector_2D(0, -50));
        axis.add(new Vector_2D(0, -60));
        axis.add(new Vector_2D(0, -70));
        axis.add(new Vector_2D(0, -80));

    }
}