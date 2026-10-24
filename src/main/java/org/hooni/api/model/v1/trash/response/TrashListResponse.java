package org.hooni.api.model.v1.trash.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hooni.api.model.v1.trash.TrashItem;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrashListResponse {

    private List<TrashItem> items;

    private long totalCount;

    private int page;

    private int size;
}
