package com.aigreentick.services.storage.report.view;

import java.math.BigInteger;

public interface UploadSummaryView {
    BigInteger getOrganisationId();
    BigInteger getUserId();
    String getMediaType();
    BigInteger getUploads();
    BigInteger getTotalBytes();
}
