package com.aigreentick.services.storage.report.repository;

import com.aigreentick.services.storage.report.view.DailyUploadActivityView;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface MediaReportRepository {

    @Query(value = """
        SELECT
            DATE(m.created_at)         AS day,
            m.organisation_id          AS organisationId,
            m.user_id                  AS userId,
            m.media_type               AS mediaType,
            COUNT(*)                   AS uploads,
            COALESCE(SUM(m.file_size),0) AS totalBytes
        FROM media m
        WHERE m.is_deleted = false
          AND m.created_at >= :fromTs
          AND m.created_at <  :toTs
          AND (:orgId     IS NULL OR m.organisation_id = :orgId)
          AND (:userId    IS NULL OR m.user_id = :userId)
          AND (:mediaType IS NULL OR m.media_type = :mediaType)
        GROUP BY DATE(m.created_at), m.organisation_id, m.user_id, m.media_type
        ORDER BY day ASC
        """, nativeQuery = true)
    List<DailyUploadActivityView> findDailyUploadActivity(
            @Param("fromTs") OffsetDateTime fromTs,
            @Param("toTs")   OffsetDateTime toTs,
            @Param("orgId")  Long orgId,
            @Param("userId") Long userId,
            @Param("mediaType") String mediaType
    );

}
