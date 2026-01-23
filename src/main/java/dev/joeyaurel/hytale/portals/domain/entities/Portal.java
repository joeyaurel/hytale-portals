package dev.joeyaurel.hytale.portals.domain.entities;

import dev.joeyaurel.hytale.portals.geometry.Vector;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Portal {
    private UUID id;
    private String name;
    private UUID worldId;
    private UUID networkId; // Required - every portal must belong to a network
    private int sortOrder;
    private List<PortalBound> bounds;
    private PortalDestination destination;
    private UUID createdBy;
    private Date createdAt;

    private boolean boundsCalculated = false;
    private double minX, maxX, minY, maxY, minZ, maxZ;

    public Portal() {
        bounds = new ArrayList<>();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        if (this.id != null) {
            throw new IllegalStateException("ID is already set and cannot be changed.");
        }

        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getWorldId() {
        return worldId;
    }

    public void setWorldId(UUID worldId) {
        this.worldId = worldId;
    }

    public UUID getNetworkId() {
        return networkId;
    }

    public void setNetworkId(UUID networkId) {
        this.networkId = networkId;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public List<PortalBound> getBounds() {
        return bounds;
    }

    public void setBounds(List<PortalBound> bounds) {
        this.bounds = bounds;
    }

    public PortalDestination getDestination() {
        return destination;
    }

    public void setDestination(PortalDestination destination) {
        this.destination = destination;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isInside(Vector location) {
        if (!boundsCalculated) {
            calculateBounds();
        }

        if (!boundsCalculated) {
            return false;
        }

        double x = location.x();
        double y = location.y();
        double z = location.z();

        return x >= minX && x <= maxX &&
                y >= minY && y <= maxY &&
                z >= minZ && z <= maxZ;
    }

    private void calculateBounds() {
        if (bounds == null || bounds.size() < 2) {
            boundsCalculated = false;
            return;
        }

        minX = Double.MAX_VALUE;
        maxX = Double.MIN_VALUE;
        minY = Double.MAX_VALUE;
        maxY = Double.MIN_VALUE;
        minZ = Double.MAX_VALUE;
        maxZ = Double.MIN_VALUE;

        for (PortalBound bound : bounds) {
            int x = bound.getX();
            int y = bound.getY();
            int z = bound.getZ();

            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;
            if (z < minZ) minZ = z;
            if (z > maxZ) maxZ = z;
        }

        boundsCalculated = true;
    }
}
