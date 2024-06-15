import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

public class Renderer extends JPanel {
    private JFrame frame;
    private LinkedList<Vector_2D> cones;
    private LinkedList<Vector_2D> centers;
    private LinkedList<Vector_2D> axis;
    private final int PIXEL_PER_METER = 7;
    private int WIDTH = 900;
    private int HEIGHT = 1080;
    private final int OFFSET_X = WIDTH / 3;
    private final int OFFSET_Y = HEIGHT / 6;

    public Renderer () {
        this.frame = new JFrame("Plotter");
        frame.add(this);
        frame.setSize(WIDTH,HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        cones = new LinkedList<>();
        centers = new LinkedList<>();

    }

    public void setLists(LinkedList<Vector_2D> centers, LinkedList<Vector_2D> cones) {
        this.cones = cones;
        this.centers = centers;
    }

    public void updatePoints() {
        frame.repaint();
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
            g.fillOval(x,y, 4, 4);

        }

        // Draw centers
        g.setColor(Color.RED);
        for (Vector_2D vec : centers) {
            x = (int) (vec.x * PIXEL_PER_METER);
            y = (int) (vec.y * PIXEL_PER_METER * -1);
            x += -2 + OFFSET_X;
            y += -2 + OFFSET_Y;
            g.fillOval(x,y, 4, 4);
        }

    }

    private void initAxis () {
        axis.add(new Vector_2D(-50,0));
        axis.add(new Vector_2D(-40,0));
        axis.add(new Vector_2D(-30,0));
        axis.add(new Vector_2D(-20,0));
        axis.add(new Vector_2D(-10,0));
        axis.add(new Vector_2D(0,0));
        axis.add(new Vector_2D(10,0));
        axis.add(new Vector_2D(20,0));
        axis.add(new Vector_2D(30,0));
        axis.add(new Vector_2D(40,0));
        axis.add(new Vector_2D(50,0));

        axis.add(new Vector_2D(0,10));
        axis.add(new Vector_2D(0,0));
        axis.add(new Vector_2D(0,-10));
        axis.add(new Vector_2D(0,-20));
        axis.add(new Vector_2D(0,-30));
        axis.add(new Vector_2D(0,-40));
        axis.add(new Vector_2D(0,-50));
        axis.add(new Vector_2D(0,-60));
        axis.add(new Vector_2D(0,-70));
        axis.add(new Vector_2D(0,-80));

    }
}