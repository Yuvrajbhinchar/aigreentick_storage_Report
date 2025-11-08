package com.aigreentick.services.storage.report.dto;

public record OrganisationGrowthDto(
        Long organisationId,
        long previousUploads,
        long currentUploads,
        long previousBytes,
        long currentBytes,
        Double growthPercent,
        boolean isNew
) {}