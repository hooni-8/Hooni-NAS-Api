package org.nas.api.model.v1.file;

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
public class File {

    private String itemId;

    private String itemName;

    private long itemSize;

    private String parentId;

    private String itemType;

    private String extension;

    private String lastModifiedAt;

}
