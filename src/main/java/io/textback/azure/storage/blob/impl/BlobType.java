package io.textback.azure.storage.blob.impl;

import lombok.Getter;

@Getter
public enum BlobType {
    BLOCK_BLOB("BlockBlob");

    private final String value;

    private BlobType(String value) {
        this.value = value;
    }
}
