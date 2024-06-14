public class Math {
    public float distance ( Vector_2D vec1, Vector_2D vec2) {
        Vector_2D dist = vec1.sub(vec2);
        return dist.length();
    }
}
