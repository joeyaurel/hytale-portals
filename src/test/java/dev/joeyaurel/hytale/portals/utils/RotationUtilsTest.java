package dev.joeyaurel.hytale.portals.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RotationUtilsTest {

    @Test
    public void testSnapRotation() {
        float epsilon = 0.0001f;
        float step = (float) (Math.PI / 4.0);

        // North
        assertEquals(0f, RotationUtils.snapRotation(0.1f), epsilon);
        assertEquals(0f, RotationUtils.snapRotation(-0.1f), epsilon);

        // North-West
        assertEquals(step, RotationUtils.snapRotation(0.7f), epsilon);

        // West
        assertEquals(1.570796f, RotationUtils.snapRotation(1.5f), epsilon);

        // South-West
        assertEquals(3 * step, RotationUtils.snapRotation(2.3f), epsilon);

        // South
        assertEquals(3.14159f, RotationUtils.snapRotation(3.1f), epsilon);
        assertEquals(3.14159f, RotationUtils.snapRotation(-3.1f), epsilon);

        // South-East
        assertEquals(-3 * step, RotationUtils.snapRotation(-2.3f), epsilon);

        // East
        assertEquals(-1.570796f, RotationUtils.snapRotation(-1.5f), epsilon);

        // North-East
        assertEquals(-step, RotationUtils.snapRotation(-0.7f), epsilon);
    }
}
