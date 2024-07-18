package com.infuq.common.enums;

public enum SuffixType {

    Xlsx("xlsx"),
    Pdf("pdf");

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
