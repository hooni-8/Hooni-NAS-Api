package org.hooni.api.model.v1.trash;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrashItem {

    private String trashId;

    private String itemId;

    private String itemName;

    private String itemType;

    private long itemSize;

    private String extension;

    private String originalParentId;

    private String deletedAt;

    private String expiresAt;

    private int remainingDays;

    private String purgeStatus;
}
