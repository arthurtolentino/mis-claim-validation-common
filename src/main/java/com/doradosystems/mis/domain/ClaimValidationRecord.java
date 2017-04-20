package com.doradosystems.mis.domain;

import java.io.Serializable;
import java.util.Date;

public class ClaimValidationRecord implements Serializable{
	private static final long serialVersionUID = 1L;

	/**
     * Status values for the {@link ClaimValidationRecord} class'
     * {@link ClaimValidationRecord#status status} field.
     */
    public enum Status {
        
        /**
         * Used to represent that the {@link ClaimValidationRecord} is
         * ready to be processed.
         */
        PENDING,
        
        /**
         * Used to represent that the {@link ClaimValidationRecord} has
         * been processed successfully.
         */
        COMPLETE,
        
        /**
         * Used to represent that the {@link ClaimValidationRecord} has
         * not been processed, but an attempt was made.
         */
        INCOMPLETE
    }
    
    private final Long id;
    private final Long batchId;
    private final Long runNumber;
    private final Status status;
    private final String claimNumber;
    private final String record;
    private final Date createDate;
    private final Date updatedDate;
    
    public ClaimValidationRecord(final Long id, final Long batchId, final Long runNumber,
            final Status status, final String claimNumber, final String record,
            final Date createDate, final Date updatedDate) {
        this.id = id;
        this.batchId = batchId;
        this.runNumber = runNumber;
        this.status = status;
        this.claimNumber = claimNumber;
        this.record = record;
        this.createDate = createDate;
        this.updatedDate = updatedDate;
    }

    public Long getId() {
        return id;
    }

    public Long getBatchId() {
        return batchId;
    }

    public Long getRunNumber() {
        return runNumber;
    }

    public Status getStatus() {
        return status;
    }

    public String getClaimNumber() {
        return claimNumber;
    }

    public String getRecord() {
        return record;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    @Override
    public String toString() {
        return "ClaimValidationRecord [id=" + id + ", batchId=" + batchId + ", runNumber=" + runNumber + ", status="
                + status + ", claimNumber=" + claimNumber + ", createDate=" + createDate + ", updatedDate="
                + updatedDate + "]";
    }
}
