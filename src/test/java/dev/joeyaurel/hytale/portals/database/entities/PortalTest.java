package dev.joeyaurel.hytale.portals.database.entities;

import dev.joeyaurel.hytale.portals.geometry.Vector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Portal")
class PortalTest {

    @Nested
    @DisplayName("isInside")
    class IsInside {

        @Test
        @DisplayName("should return false when portal has no bounds")
        void testNoBounds() {
            Portal portal = createPortal(new ArrayList<>());

            boolean result = portal.isInside(new Vector(0, 0, 0));

            assertFalse(result);
        }

        @Test
        @DisplayName("should return false when portal has only one bound")
        void testSingleBound() {
            List<PortalBound> bounds = List.of(
                createBound(0, 0, 0)
            );
            Portal portal = createPortal(bounds);

            boolean result = portal.isInside(new Vector(0, 0, 0));

            assertFalse(result);
        }

        @Test
        @DisplayName("should return true when location is at the minimum corner")
        void testLocationAtMinCorner() {
            List<PortalBound> bounds = Arrays.asList(
                createBound(0, 0, 0),
                createBound(10, 10, 10)
            );
            Portal portal = createPortal(bounds);

            boolean result = portal.isInside(new Vector(0, 0, 0));

            assertTrue(result);
        }

        @Test
        @DisplayName("should return true when location is at the maximum corner")
        void testLocationAtMaxCorner() {
            List<PortalBound> bounds = Arrays.asList(
                createBound(0, 0, 0),
                createBound(10, 10, 10)
            );
            Portal portal = createPortal(bounds);

            boolean result = portal.isInside(new Vector(10, 10, 10));

            assertTrue(result);
        }

        @Test
        @DisplayName("should return true when location is in the center of bounds")
        void testLocationInCenter() {
            List<PortalBound> bounds = Arrays.asList(
                createBound(0, 0, 0),
                createBound(10, 10, 10)
            );
            Portal portal = createPortal(bounds);

            boolean result = portal.isInside(new Vector(5, 5, 5));

            assertTrue(result);
        }

        @Test
        @DisplayName("should return true when location has decimal coordinates within bounds")
        void testLocationWithDecimals() {
            List<PortalBound> bounds = Arrays.asList(
                createBound(0, 0, 0),
                createBound(10, 10, 10)
            );
            Portal portal = createPortal(bounds);

            boolean result = portal.isInside(new Vector(5.5, 7.3, 2.8));

            assertTrue(result);
        }

        @Test
        @DisplayName("should return false when location is outside on X axis")
        void testLocationOutsideX() {
            List<PortalBound> bounds = Arrays.asList(
                createBound(0, 0, 0),
                createBound(10, 10, 10)
            );
            Portal portal = createPortal(bounds);

            assertFalse(portal.isInside(new Vector(-1, 5, 5)));
            assertFalse(portal.isInside(new Vector(11, 5, 5)));
        }

        @Test
        @DisplayName("should return false when location is outside on Y axis")
        void testLocationOutsideY() {
            List<PortalBound> bounds = Arrays.asList(
                createBound(0, 0, 0),
                createBound(10, 10, 10)
            );
            Portal portal = createPortal(bounds);

            assertFalse(portal.isInside(new Vector(5, -1, 5)));
            assertFalse(portal.isInside(new Vector(5, 11, 5)));
        }

        @Test
        @DisplayName("should return false when location is outside on Z axis")
        void testLocationOutsideZ() {
            List<PortalBound> bounds = Arrays.asList(
                createBound(0, 0, 0),
                createBound(10, 10, 10)
            );
            Portal portal = createPortal(bounds);

            assertFalse(portal.isInside(new Vector(5, 5, -1)));
            assertFalse(portal.isInside(new Vector(5, 5, 11)));
        }

        @Test
        @DisplayName("should return false when location is slightly outside bounds with decimals")
        void testLocationJustOutsideWithDecimals() {
            List<PortalBound> bounds = Arrays.asList(
                createBound(0, 0, 0),
                createBound(10, 10, 10)
            );
            Portal portal = createPortal(bounds);

            assertFalse(portal.isInside(new Vector(10.1, 5, 5)));
            assertFalse(portal.isInside(new Vector(-0.1, 5, 5)));
        }

        @Test
        @DisplayName("should work correctly with negative coordinates")
        void testNegativeCoordinates() {
            List<PortalBound> bounds = Arrays.asList(
                createBound(-10, -10, -10),
                createBound(0, 0, 0)
            );
            Portal portal = createPortal(bounds);

            assertTrue(portal.isInside(new Vector(-5, -5, -5)));
            assertFalse(portal.isInside(new Vector(-11, -5, -5)));
            assertFalse(portal.isInside(new Vector(1, -5, -5)));
        }

        @Test
        @DisplayName("should work correctly when bounds span negative and positive coordinates")
        void testBoundsSpanningZero() {
            List<PortalBound> bounds = Arrays.asList(
                createBound(-5, -5, -5),
                createBound(5, 5, 5)
            );
            Portal portal = createPortal(bounds);

            assertTrue(portal.isInside(new Vector(0, 0, 0)));
            assertTrue(portal.isInside(new Vector(-5, -5, -5)));
            assertTrue(portal.isInside(new Vector(5, 5, 5)));
            assertFalse(portal.isInside(new Vector(6, 0, 0)));
        }

        @Test
        @DisplayName("should handle multiple bounds correctly by using min/max")
        void testMultipleBounds() {
            List<PortalBound> bounds = Arrays.asList(
                createBound(0, 0, 0),
                createBound(10, 5, 10),
                createBound(5, 10, 5),
                createBound(10, 10, 10)
            );
            Portal portal = createPortal(bounds);

            // The bounding box should be from (0,0,0) to (10,10,10)
            assertTrue(portal.isInside(new Vector(5, 5, 5)));
            assertTrue(portal.isInside(new Vector(0, 0, 0)));
            assertTrue(portal.isInside(new Vector(10, 10, 10)));
            assertFalse(portal.isInside(new Vector(11, 5, 5)));
        }

        @Test
        @DisplayName("should work correctly with very large coordinates")
        void testLargeCoordinates() {
            List<PortalBound> bounds = Arrays.asList(
                createBound(1000000, 1000000, 1000000),
                createBound(2000000, 2000000, 2000000)
            );
            Portal portal = createPortal(bounds);

            assertTrue(portal.isInside(new Vector(1500000, 1500000, 1500000)));
            assertFalse(portal.isInside(new Vector(999999, 1500000, 1500000)));
        }

        @ParameterizedTest
        @CsvSource({
            "0.0, 0.0, 0.0, true",
            "10.0, 10.0, 10.0, true",
            "5.5, 5.5, 5.5, true",
            "-0.1, 5.0, 5.0, false",
            "10.1, 5.0, 5.0, false",
            "5.0, -0.1, 5.0, false",
            "5.0, 10.1, 5.0, false",
            "5.0, 5.0, -0.1, false",
            "5.0, 5.0, 10.1, false"
        })
        @DisplayName("should correctly validate various coordinates")
        void testParameterizedCoordinates(double x, double y, double z, boolean expected) {
            List<PortalBound> bounds = Arrays.asList(
                createBound(0, 0, 0),
                createBound(10, 10, 10)
            );
            Portal portal = createPortal(bounds);

            assertEquals(expected, portal.isInside(new Vector(x, y, z)));
        }

        @Test
        @DisplayName("should work with bounds defined in non-sequential order")
        void testBoundsInReverseOrder() {
            List<PortalBound> bounds = Arrays.asList(
                createBound(10, 10, 10),
                createBound(0, 0, 0)
            );
            Portal portal = createPortal(bounds);

            assertTrue(portal.isInside(new Vector(5, 5, 5)));
            assertFalse(portal.isInside(new Vector(-1, 5, 5)));
            assertFalse(portal.isInside(new Vector(11, 5, 5)));
        }

        @Test
        @DisplayName("should handle single-dimensional portal (flat on one axis)")
        void testFlatPortal() {
            List<PortalBound> bounds = Arrays.asList(
                createBound(0, 5, 0),
                createBound(10, 5, 10)
            );
            Portal portal = createPortal(bounds);

            // Y is constant at 5
            assertTrue(portal.isInside(new Vector(5, 5, 5)));
            assertFalse(portal.isInside(new Vector(5, 4, 5)));
            assertFalse(portal.isInside(new Vector(5, 6, 5)));
        }
    }

    // Helper methods
    private Portal createPortal(List<PortalBound> bounds) {
        Portal portal = new Portal();
        portal.setId(UUID.randomUUID());
        portal.setName("Test Portal");
        portal.setWorldId(UUID.randomUUID());
        portal.setNetworkId(UUID.randomUUID());
        portal.setBounds(bounds);
        return portal;
    }

    private PortalBound createBound(int x, int y, int z) {
        PortalBound bound = new PortalBound();
        bound.setId(UUID.randomUUID());
        bound.setX(x);
        bound.setY(y);
        bound.setZ(z);
        return bound;
    }
}
