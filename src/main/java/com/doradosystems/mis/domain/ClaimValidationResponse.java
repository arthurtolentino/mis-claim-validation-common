package com.doradosystems.mis.domain;

import java.util.Date;

/**
 * @author kev
 *
 *  Claim Validation Object mapped from input file to database table.
 */
public class ClaimValidationResponse {
    
    /**
     * Status values for the {@link ClaimValidationResponse} class'
     * {@link ClaimValidationResponse#status status} field.
     */
    public enum Status {
        
        /**
         * Used to indicate that the {@link ClaimValidationResponse}
         * has been created, but not written to a file.
         */
        PENDING,
        
        /**
         * Used to indicate that the {@link ClaimValidationResponse}
         * has been written to a file.
         */
        COMPLETE
    }
    
    private Long id;
    private Long batchId;
    private Long runNumber;
    private Status status;
    private String claimNumber;
    private String response;
    private Date createDate;
    private Date updatedDate;
    private Long claimValidationRecordId;
    
    public ClaimValidationResponse(Long id, Long batchId, Long runNumber, Status status, String claimNumber,
            String response, Date createDate, Date updatedDate, Long claimValidationRecordId) {
        super();
        this.id = id;
        this.batchId = batchId;
        this.runNumber = runNumber;
        this.status = status;
        this.claimNumber = claimNumber;
        this.response = response;
        this.createDate = createDate;
        this.updatedDate = updatedDate;
        this.claimValidationRecordId = claimValidationRecordId;
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

    public String getResponse() {
        return response;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public Long getClaimValidationRecordId() {
        return claimValidationRecordId;
    }
    
    @Override
    public String toString() {
        return "ClaimValidationResponse [id=" + id + ", batchId=" + batchId + ", runNumber=" + runNumber + ", status="
                + status + ", claimNumber=" + claimNumber + ", createDate=" + createDate + ", updatedDate="
                + updatedDate + ", claimValidationRecordId=" + claimValidationRecordId + "]";
    }
}
