package dev.joeyaurel.hytale.portals.domain.entities;

import java.util.UUID;

public class PortalDestination {
    private UUID id;
    private UUID portalId;
    private double x;
    private double y;
    private double z;
    private float bodyYaw;
    private float headYaw;
    private float headPitch;

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

    public float getBodyYaw() {
        return bodyYaw;
    }

    public void setBodyYaw(float bodyYaw) {
        this.bodyYaw = bodyYaw;
    }

    public float getHeadYaw() {
        return headYaw;
    }

    public void setHeadYaw(float headYaw) {
        this.headYaw = headYaw;
    }

    public float getHeadPitch() {
        return headPitch;
    }

    public void setHeadPitch(float headPitch) {
        this.headPitch = headPitch;
    }
}
