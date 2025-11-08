package com.aigreentick.services.storage.report.repository;

import com.aigreentick.services.storage.model.Media;
import com.aigreentick.services.storage.report.view.DailyUploadActivityView;
import com.aigreentick.services.storage.report.view.OrganisationGrowthView;
import com.aigreentick.services.storage.report.view.TopUploadingUserView;
import com.aigreentick.services.storage.report.view.UploadSummaryView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface MediaReportRepository extends JpaRepository<Media, Long> {

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

    @Query(value = """
        SELECT
            m.user_id                          AS userId,
            m.organisation_id                  AS organisationId,
            COUNT(*)                           AS uploads,
            COALESCE(SUM(m.file_size), 0)      AS totalBytes
        FROM media m
        WHERE m.is_deleted = false
          AND m.created_at >= :fromTs
          AND m.created_at <  :toTs
          AND (:orgId IS NULL OR m.organisation_id = :orgId)
        GROUP BY m.user_id, m.organisation_id
        ORDER BY uploads DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<TopUploadingUserView> findTopUploadingUsers(
            @Param("fromTs") OffsetDateTime fromTs,
            @Param("toTs")   OffsetDateTime toTs,
            @Param("orgId")  Long orgId,
            @Param("limit")  Integer limit
    );

    /**
     * Upload summary grouped by organisation, user, and media type
     * within a specific time window.
     *
     * @param fromTs start timestamp (inclusive)
     * @param toTs end timestamp (exclusive)
     * @return list of grouped upload statistics
     */
    @Query(value = """
        SELECT
            m.organisation_id             AS organisationId,
            m.user_id                     AS userId,
            m.media_type                  AS mediaType,
            COUNT(*)                      AS uploads,
            COALESCE(SUM(m.file_size), 0) AS totalBytes
        FROM media m
        WHERE m.is_deleted = false
          AND m.created_at >= :fromTs
          AND m.created_at <  :toTs
        GROUP BY m.organisation_id, m.user_id, m.media_type
        ORDER BY m.organisation_id, m.user_id;
        """, nativeQuery = true)
    List<UploadSummaryView>findUploadSummary(
            @Param("fromTs") OffsetDateTime fromTs,
            @Param("toTs")   OffsetDateTime toTs
    );

    @Query(value = """
        SELECT
            m.organisation_id AS organisationId,
            SUM(CASE WHEN m.created_at >= :currentFrom AND m.created_at < :currentTo THEN 1 ELSE 0 END) AS currentUploads,
            SUM(CASE WHEN m.created_at >= :previousFrom AND m.created_at < :previousTo THEN 1 ELSE 0 END) AS previousUploads,
            COALESCE(SUM(CASE WHEN m.created_at >= :currentFrom AND m.created_at < :currentTo THEN m.file_size ELSE 0 END), 0) AS currentBytes,
            COALESCE(SUM(CASE WHEN m.created_at >= :previousFrom AND m.created_at < :previousTo THEN m.file_size ELSE 0 END), 0) AS previousBytes
        FROM media m
        WHERE m.is_deleted = false
          AND m.created_at >= :previousFrom
          AND m.created_at <  :currentTo
        GROUP BY m.organisation_id
        """, nativeQuery = true)
    List<OrganisationGrowthView> findOrganisationGrowth(
            @Param("previousFrom") OffsetDateTime previousFrom,
            @Param("previousTo") OffsetDateTime previousTo,
            @Param("currentFrom") OffsetDateTime currentFrom,
            @Param("currentTo") OffsetDateTime currentTo
    );

}
