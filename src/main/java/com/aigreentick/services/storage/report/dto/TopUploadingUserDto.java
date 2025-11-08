package com.aigreentick.services.storage.report.dto;

public record TopUploadingUserDto (
        Long userId,
        Long organisationId,
        long uploads,
        long totalBytes
){
}
