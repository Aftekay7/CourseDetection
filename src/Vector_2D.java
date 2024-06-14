import java.lang.Math;

public class Vector_2D {
    float x;
    float y;


    public Vector_2D (float x, float y) {
        this.x = x;
        this.y = y;
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

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}