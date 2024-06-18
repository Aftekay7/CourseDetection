public class MathHelpers {
    public static float distance ( Vector_2D vec1, Vector_2D vec2) {
        Vector_2D dist = vec1.sub(vec2);
        return dist.length();
    }

    public static float dotProduct ( Vector_2D vec1, Vector_2D vec2) {
        return vec1.x * vec2.x + vec1.y * vec2.y;
    }
}
