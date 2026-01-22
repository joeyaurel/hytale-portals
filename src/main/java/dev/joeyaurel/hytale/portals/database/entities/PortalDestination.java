package dev.joeyaurel.hytale.portals.database.entities;

import java.util.UUID;

public class PortalDestination {
    private UUID id;
    private UUID portalId;
    private double x;
    private double y;
    private double z;
    private float rotationX;
    private float rotationY;
    private float rotationZ;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        if (this.id != null) {
            throw new IllegalStateException("ID is already set and cannot be changed.");
        }

        this.id = id;
    }

    public UUID getPortalId() {
        return portalId;
    }

    public void setPortalId(UUID portalId) {
        this.portalId = portalId;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public float getRotationX() {
        return rotationX;
    }

    public void setRotationX(float rotationX) {
        this.rotationX = rotationX;
    }

    public float getRotationY() {
        return rotationY;
    }

    public void setRotationY(float rotationY) {
        this.rotationY = rotationY;
    }

    public float getRotationZ() {
        return rotationZ;
    }

    public void setRotationZ(float rotationZ) {
        this.rotationZ = rotationZ;
    }
}
