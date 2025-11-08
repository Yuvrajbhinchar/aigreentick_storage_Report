package com.aigreentick.services.storage.report.dto;

public record UploadSummaryDto(
    Long organisationId,
    Long userId,
    String mediaType,
    long uploads,
    long totalBytes
){}
