package com.doradosystems.mis.domain;

/**
 * Model class representing just those fields needed to identify
 * the {@link ClaimValidationResponse} records that go in a single
 * response file.
 * 
 * @author <a href="mailto:neil.drummond@doradosystems.com">Neil Drummond</a>
 */
public class ClaimValidationResponseIdentifier {
    
    private final Long batchId;
    private final Long runNumber;

    public ClaimValidationResponseIdentifier(final Long batchId, final Long runNumber) {
        this.batchId = batchId;
        this.runNumber = runNumber;
    }

    public Long getBatchId() {
        return batchId;
    }

    public Long getRunNumber() {
        return runNumber;
    }

    @Override
    public String toString() {
        return "ClaimValidationResponseIdentifier [batchId=" + batchId + ", runNumber=" + runNumber + "]";
    }
    
}
