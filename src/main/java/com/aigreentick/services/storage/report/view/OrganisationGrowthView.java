package com.aigreentick.services.storage.report.view;

import java.math.BigInteger;

public interface OrganisationGrowthView {
    BigInteger getOrganisationId();
    BigInteger getCurrentUploads();
    BigInteger getPreviousUploads();
    BigInteger getCurrentBytes();
    BigInteger getPreviousBytes();
}
