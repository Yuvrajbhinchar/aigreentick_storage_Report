package com.aigreentick.services.storage.report.service;

import com.aigreentick.services.storage.report.dto.UploadSummaryDto;
import com.aigreentick.services.storage.report.repository.MediaReportRepository;
import com.aigreentick.services.storage.report.view.UploadSummaryView;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UploadSummaryService {
    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Asia/Kolkata");

    private final MediaReportRepository repo;


    /**
     * Fetch upload summary for the given date range.
     */
    public List<UploadSummaryDto> getSummary(LocalDate from, LocalDate to) {
        // Validate input
        if (from == null || to == null) {
            throw new IllegalArgumentException("Both 'from' and 'to' dates are required");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("'from' date must be before or equal to 'to' date");
        }

        // Convert LocalDate → OffsetDateTime using business timezone (IST)
        OffsetDateTime fromTs = from.atStartOfDay(BUSINESS_ZONE).toOffsetDateTime();
        OffsetDateTime toTs = to.plusDays(1).atStartOfDay(BUSINESS_ZONE).toOffsetDateTime();

        //Execute SQL via repository
        List<UploadSummaryView> rows = repo.findUploadSummary(fromTs, toTs);

        // Map projections → DTOs
        return rows.stream()
                .map(r -> new UploadSummaryDto(
                        r.getOrganisationId() != null ? r.getOrganisationId().longValue() : null,
                        r.getUserId() != null ? r.getUserId().longValue() : null,
                        r.getMediaType(),
                        r.getUploads() != null ? r.getUploads().longValue() : 0L,
                        r.getTotalBytes() != null ? r.getTotalBytes().longValue() : 0L
                ))
                .toList();

    }
}
