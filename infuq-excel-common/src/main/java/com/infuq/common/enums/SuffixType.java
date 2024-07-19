package com.infuq.common.enums;

public enum SuffixType {

    xlsx("xlsx"),
    pdf("pdf");

    private String fileType;

    SuffixType() {
    }

    SuffixType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileType() {
        return fileType;
    }
}
