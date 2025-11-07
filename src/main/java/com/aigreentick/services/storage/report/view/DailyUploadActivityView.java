package com.aigreentick.services.storage.report.view;

import java.sql.Date;

public interface DailyUploadActivityView {
    Date getDay();
    Long getOrganisationId();
    Long getUserId();
    String getMediaType();
    Long getUploads();
    Long getTotalBytes();

}
