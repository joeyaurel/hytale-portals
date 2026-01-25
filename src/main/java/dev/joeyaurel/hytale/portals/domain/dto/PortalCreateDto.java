package dev.joeyaurel.hytale.portals.domain.dto;

import dev.joeyaurel.hytale.portals.geometry.Vector;

import java.util.List;
import java.util.UUID;

public class PortalCreateDto {
    public String name;
    public UUID worldId;
    public UUID networkId;
    public List<Vector> bounds;
    public Vector destinationPosition;
    public Vector destinationRotation;
    public UUID createdBy;

    public boolean isValid() {
        return name != null && !name.isEmpty() &&
               worldId != null &&
               networkId != null &&
               bounds != null && bounds.size() == 2 &&
               destinationPosition != null &&
               destinationRotation != null &&
               createdBy != null;
    }
}
