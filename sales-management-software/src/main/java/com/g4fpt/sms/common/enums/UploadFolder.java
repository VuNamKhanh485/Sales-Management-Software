package com.g4fpt.sms.common.enums;

public enum UploadFolder {
    PRODUCT("product-image"),
    RETURN("return-image");

    private final String folderName;

    UploadFolder(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderName() {
        return folderName;
    }
}
