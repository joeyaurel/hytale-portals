package dev.joeyaurel.hytale.portals.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RotationUtilsTest {

    @Test
    public void testClipRotation() {
        float epsilon = 0.0001f;
        float step = (float) (Math.PI / 4.0);

        // North
        assertEquals(0f, RotationUtils.clipRotation(0.1f), epsilon);
        assertEquals(0f, RotationUtils.clipRotation(-0.1f), epsilon);

        // North-West
        assertEquals(step, RotationUtils.clipRotation(0.7f), epsilon);

        // West
        assertEquals(1.570796f, RotationUtils.clipRotation(1.5f), epsilon);

        // South-West
        assertEquals(3 * step, RotationUtils.clipRotation(2.3f), epsilon);

        // South
        assertEquals(3.14159f, RotationUtils.clipRotation(3.1f), epsilon);
        assertEquals(3.14159f, RotationUtils.clipRotation(-3.1f), epsilon);

        // South-East
        assertEquals(-3 * step, RotationUtils.clipRotation(-2.3f), epsilon);

        // East
        assertEquals(-1.570796f, RotationUtils.clipRotation(-1.5f), epsilon);

        // North-East
        assertEquals(-step, RotationUtils.clipRotation(-0.7f), epsilon);
    }
}
