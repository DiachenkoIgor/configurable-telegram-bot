package com.incuube.rcs.datamodel.messages.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;

@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class FileInfo {

    private String mimeType;

    private long fileSizeBytes;

    private String fileName;

    private String fileUri;


}
