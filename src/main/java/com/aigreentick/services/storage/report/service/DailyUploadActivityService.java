package com.aigreentick.services.storage.report.service;

import com.aigreentick.services.storage.report.dto.DailyUploadActivityDto;
import com.aigreentick.services.storage.report.repository.MediaReportRepository;
import com.aigreentick.services.storage.report.view.DailyUploadActivityView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyUploadActivityService {
    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Asia/Kolkata");

    private final MediaReportRepository repo;

    public List<DailyUploadActivityDto> getActivity(
            LocalDate from, LocalDate to,
            Long orgId, Long userId, String mediaType
    ){
        // Validation
        if (from == null || to == null) {
            throw new IllegalArgumentException("Both 'from' and 'to' dates are required");
        }
        if (!from.isBefore(to.plusDays(1))) {
            throw new IllegalArgumentException("'from' must be before 'to'");
        }

        // Convert LocalDate → OffsetDateTime (IST business day converted to UTC)
        OffsetDateTime fromTs = from.atStartOfDay(BUSINESS_ZONE).toOffsetDateTime();
        OffsetDateTime toTs   = to.plusDays(1).atStartOfDay(BUSINESS_ZONE).toOffsetDateTime();

        List<DailyUploadActivityView> rows =
                repo.findDailyUploadActivity(fromTs, toTs, orgId, userId, mediaType);

        return rows.stream().map(r -> new DailyUploadActivityDto(
                r.getDay().toLocalDate(),
                r.getOrganisationId(),
                r.getUserId(),
                r.getMediaType(),
                r.getUploads() == null ? 0 : r.getUploads(),
                r.getTotalBytes() == null ? 0 : r.getTotalBytes()
        )).toList();
    }

}
