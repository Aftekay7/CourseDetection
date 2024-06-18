import java.lang.Math;

public class Vector_2D {
    private boolean nearest = false;
    private boolean deleted = false;
    private static int next_id = 0;
    int id;
    float x;
    float y;


    public Vector_2D (float x, float y) {
        this.x = x;
        this.y = y;
        this.id = next_id;
        next_id++;
    }

    public void addToThis (Vector_2D addend) {
        this.x += addend.x;
        this.y += addend.y;
    }

    public Vector_2D add (Vector_2D addend) {
        return new Vector_2D(this.x + addend.x, this.y + addend.y);
    }

    public Vector_2D sub (Vector_2D minuend) {
        return new Vector_2D(this.x - minuend.x, this.y - minuend.y);
    }

    public void scale (float scalar) {
        this.x *= scalar;
        this.y *= scalar;
    }

    public float length () {
        return (float) (Math.sqrt(this.x * this.x + this.y * this.y));

    }

    public void delete() {
        this.deleted = true;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Vector_2D copy() {
        return new Vector_2D(this.x,this.y);
    }

    public boolean isNearest() {
        return nearest;
    }

    public void setNearest(boolean nearest) {
        this.nearest = nearest;
    }

    public Vector_2D rotateCW(float alpha) {
        float x = (float) (Math.cos(alpha) * this.x + Math.sin(alpha) * this.y);
        float y = (float) (-1 * Math.sin(alpha) * this.x + Math.cos(alpha) * this.y);

        return new Vector_2D(x,y);
    }

    public Vector_2D normalize() {
        float length = this.length();
        return new Vector_2D(this.x / length, this.y / length);
    }

    public void normalizeThis() {
        float length = this.length();
        this.x /= length;
        this.y /= length;
    }

    @Override
    public String toString() {
        return "(id: " + id + ";" + x + "," + y + ")";
    }
}
