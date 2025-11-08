package com.aigreentick.services.storage.report.view;

import java.math.BigInteger;

public interface TopUploadingUserView {
    BigInteger getUserId();
    BigInteger getOrganisationId();
    BigInteger getUploads();
    BigInteger getTotalBytes();
}
