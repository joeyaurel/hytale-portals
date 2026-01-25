package dev.joeyaurel.hytale.portals.domain.dto;

import java.util.UUID;

public class NetworkCreateDto {
    public String name;
    public UUID createdBy;

    public boolean isValid() {
        return name != null && !name.isEmpty() &&
               createdBy != null;
    }
}
