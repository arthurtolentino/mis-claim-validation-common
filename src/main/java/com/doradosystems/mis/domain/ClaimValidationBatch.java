package com.doradosystems.mis.domain;

import java.util.Date;

public class ClaimValidationBatch {
    
    /**
     * Status values for the {@link ClaimValidationBatch} class'
     * {@link ClaimValidationBatch#status status} field.
     */
    public enum Status {
        
        /**
         * Used to represent that the {@link ClaimValidationBatch} is
         * in the process of being loaded into the database.
         */
        LOADING,
        
        /**
         * Used to represent that the {@link ClaimValidationBatch} is
         * loaded into the database, and ready to begin processing.
         */
        PENDING,
        
        /**
         * Used to represent that the {@link ClaimValidationBatch} is
         * being processed.
         */
        PROCESSING,
        
        /**
         * Used to represent that the {@link ClaimValidationBatch} has
         * finished processing.
         */
        COMPLETE,
        
        /**
         * Used to represent that there was an error while loading or
         * processing a particular {@link ClaimValidationBatch} record.
         */
        ERROR
    }

    private final Long id;
    private final Long clientId;
    private final String filename;
    private final Status status;
    private final Long runNumber;
    private final String globalControlNumber;
    private final Date createDate;
    private final Date updatedDate;
    
    public ClaimValidationBatch(final Long id, final Long clientId, final String filename, final Status status,
            final Long runNumber, final String globalControlNumber,
            final Date createDate, final Date updatedDate) {
        this.id = id;
        this.clientId = clientId;
        this.filename = filename;
        this.status = status;
        this.runNumber = runNumber;
        this.globalControlNumber = globalControlNumber;
        this.createDate = createDate;
        this.updatedDate = updatedDate;
    }

    public Long getId() {
        return id;
    }
    
    public Long getClientId() {
        return clientId;
    }

    /**
     * The {@link #filename} to use, including the path.
     * <p>
     * The path should be the relative path from the directory being scanned to the
     * location of the file.  So, if the application is scanning directory {@code /foo}, and
     * the file is in {@code /foo/bar/baz/in}, with the name {@code 837.cli}, then this property
     * should be {@code bar/baz/in/837.cli}.
     * 
     * @return The {@link #filename}.
     */
    public String getFilename() {
        return filename;
    }

    public Status getStatus() {
        return status;
    }

    public Long getRunNumber() {
        return runNumber;
    }

    public String getGlobalControlNumber() {
        return globalControlNumber;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    @Override
    public String toString() {
        return "ClaimValidationBatch [id=" + id + ", clientId=" + clientId + ", filename=" + filename + ", status="
                + status + ", runNumber=" + runNumber + ", globalControlNumber=" + globalControlNumber + ", createDate="
                + createDate + ", updatedDate=" + updatedDate + "]";
    }
    
}
