package com.aigreentick.services.storage.report.service;


import com.aigreentick.services.storage.report.dto.OrganisationGrowthDto;
import com.aigreentick.services.storage.report.repository.MediaReportRepository;
import com.aigreentick.services.storage.report.view.OrganisationGrowthView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganisationGrowthService {

    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Asia/Kolkata");
    private static final int DEFAULT_LIMIT = 20;

    private final MediaReportRepository repo;

    public List<OrganisationGrowthDto> getOrganisationGrowth(LocalDate from, LocalDate to, Integer limit) {
        //Validation
        if (from == null || to == null) {
            throw new IllegalArgumentException("'from' and 'to' are required");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("'from' must not be after 'to'");
        }

        int effectiveLimit = (limit == null || limit <= 0) ? DEFAULT_LIMIT : limit;
        long days = ChronoUnit.DAYS.between(from, to) + 1; // inclusive

        // Compute time windows (IST → UTC Offset)
        LocalDate prevFromDate = from.minusDays(days);
        LocalDate prevToDate = from.minusDays(1);

        OffsetDateTime prevFrom = prevFromDate.atStartOfDay(BUSINESS_ZONE).toOffsetDateTime();
        OffsetDateTime prevTo   = prevToDate.plusDays(1).atStartOfDay(BUSINESS_ZONE).toOffsetDateTime();

        OffsetDateTime currentFrom = from.atStartOfDay(BUSINESS_ZONE).toOffsetDateTime();
        OffsetDateTime currentTo   = to.plusDays(1).atStartOfDay(BUSINESS_ZONE).toOffsetDateTime();

        //  Query the database
        List<OrganisationGrowthView> rows =
                repo.findOrganisationGrowth(prevFrom, prevTo, currentFrom, currentTo);

        //  Map, compute growth, and sort
        List<OrganisationGrowthDto> results = rows.stream()
                .map(r -> mapToDto(r))
                .sorted(Comparator
                        .comparing(OrganisationGrowthDto::isNew).reversed()
                        .thenComparing((a, b) -> compareGrowth(a, b)))
                .limit(effectiveLimit)
                .collect(Collectors.toList());

        return results;
    }

    private OrganisationGrowthDto mapToDto(OrganisationGrowthView r) {
        Long orgId = r.getOrganisationId() != null ? r.getOrganisationId().longValue() : null;
        long currentUploads = toLong(r.getCurrentUploads());
        long previousUploads = toLong(r.getPreviousUploads());
        long currentBytes = toLong(r.getCurrentBytes());
        long previousBytes = toLong(r.getPreviousBytes());

        boolean isNew = (previousUploads == 0 && currentUploads > 0);
        Double growthPercent = null;

        if (previousUploads > 0) {
            growthPercent = ((double)(currentUploads - previousUploads) / previousUploads) * 100.0;
        }

        return new OrganisationGrowthDto(orgId, previousUploads, currentUploads,
                previousBytes, currentBytes, growthPercent, isNew);
    }

    private static int compareGrowth(OrganisationGrowthDto a, OrganisationGrowthDto b) {
        // Sort: growth% desc, then currentUploads desc
        if (a.growthPercent() == null && b.growthPercent() == null)
            return Long.compare(b.currentUploads(), a.currentUploads());
        if (a.growthPercent() == null) return 1;
        if (b.growthPercent() == null) return -1;

        int cmp = Double.compare(b.growthPercent(), a.growthPercent());
        if (cmp != 0) return cmp;
        return Long.compare(b.currentUploads(), a.currentUploads());
    }

    private static long toLong(BigInteger val) {
        return val == null ? 0L : val.longValue();
    }
}
