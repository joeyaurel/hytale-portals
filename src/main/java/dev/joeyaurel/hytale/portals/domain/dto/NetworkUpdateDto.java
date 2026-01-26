package dev.joeyaurel.hytale.portals.domain.dto;

import java.util.UUID;

public class NetworkUpdateDto {
    public UUID id;
    public String name;

    public boolean isValid() {
        return id != null && name != null && !name.isEmpty();
    }
}
