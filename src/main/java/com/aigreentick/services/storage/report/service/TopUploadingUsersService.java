package com.aigreentick.services.storage.report.service;

import com.aigreentick.services.storage.report.dto.TopUploadingUserDto;
import com.aigreentick.services.storage.report.repository.MediaReportRepository;
import com.aigreentick.services.storage.report.view.TopUploadingUserView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * Service layer for Top Uploading Users report.
 * - Validates inputs
 * - Converts LocalDate (business day) into OffsetDateTime window
 * - Maps projection results into DTOs
 */
@Service
@RequiredArgsConstructor
public class TopUploadingUsersService {

    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Asia/Kolkata");
    private static final int DEFAULT_LIMIT = 10;
    private final MediaReportRepository repo;


    /**
     * Fetch top uploading users.
     *
     * @param fromDate inclusive start date (LocalDate, in business zone)
     * @param toDate   inclusive end date (LocalDate, in business zone)
     * @param orgId    optional org id filter
     * @param limit    top N results (nullable -> default)
     */
    public List<TopUploadingUserDto> getTopUploadingUsers(
            LocalDate fromDate, LocalDate toDate, Long orgId, Integer limit
    ) {
        // Basic validation
        if (fromDate == null || toDate == null) {
            throw new IllegalArgumentException("from and to dates are required");
        }
        if (fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("'from' must be on or before 'to'");
        }

        int effectiveLimit = (limit == null || limit <= 0) ? DEFAULT_LIMIT : limit;

        // Convert LocalDate range to OffsetDateTime (business day boundaries)
        // We use half-open interval: [from at 00:00, (to + 1 day) at 00:00)
        OffsetDateTime fromTs = fromDate.atStartOfDay(BUSINESS_ZONE).toOffsetDateTime();
        OffsetDateTime toTs = toDate.plusDays(1).atStartOfDay(BUSINESS_ZONE).toOffsetDateTime();

        List<TopUploadingUserView> rows = repo.findTopUploadingUsers(fromTs, toTs, orgId, effectiveLimit);

        // Map projection → DTO, convert BigInteger to Long
        return rows.stream().map(r -> {
            // conversion defensively handles nulls
            Long userId = r.getUserId() != null ? r.getUserId().longValue() : null;
            Long organisationId = r.getOrganisationId() != null ? r.getOrganisationId().longValue() : null;
            long uploads = r.getUploads() != null ? r.getUploads().longValue() : 0L;
            long totalBytes = r.getTotalBytes() != null ? r.getTotalBytes().longValue() : 0L;
            return new TopUploadingUserDto(userId, organisationId, uploads, totalBytes);
        }).toList();
    }
}
