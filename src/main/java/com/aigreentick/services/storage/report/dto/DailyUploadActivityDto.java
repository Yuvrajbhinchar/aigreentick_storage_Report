package com.aigreentick.services.storage.report.dto;

import java.time.LocalDate;

public record DailyUploadActivityDto (
    LocalDate day,
    Long organisationId,
    Long userId,
    String mediaType,
    long uploads,
    long totalBytes
){

}
