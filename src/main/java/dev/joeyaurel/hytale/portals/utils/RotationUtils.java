package dev.joeyaurel.hytale.portals.utils;

public class RotationUtils {

    /**
     * Snaps the given rotation in radians to the nearest 45-degree increment (PI/4).
     * - North is 0.
     * - East is -1.570795 (-PI/2).
     * - West is 1.570795 (PI/2).
     * - South is 3.14159 or -3.14159 (PI or -PI).
     *
     * @param radians The rotation in radians.
     * @return The snapped rotation in radians.
     */
    public static float snapRotation(float radians) {
        float step = (float) (Math.PI / 4.0); // 45 degrees
        float snapped = Math.round(radians / step) * step;

        // Handle South wrap-around (PI vs -PI)
        if (snapped > (float) Math.PI) {
            snapped -= (float) (2 * Math.PI);
        } else if (snapped <= (float) -Math.PI) {
            snapped += (float) (2 * Math.PI);
        }

        // Special case for South exactly at 3.14159 as per description
        if (Math.abs(snapped - (float) Math.PI) < 0.0001f) {
            return 3.14159f;
        }

        return snapped;
    }
}
