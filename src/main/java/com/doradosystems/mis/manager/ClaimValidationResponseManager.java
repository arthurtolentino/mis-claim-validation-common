package com.doradosystems.mis.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionStatus;

import com.doradosystems.data.manager.AbstractManager;
import com.doradosystems.mis.dao.ClaimValidationRecordDao;
import com.doradosystems.mis.dao.ClaimValidationResponseDao;
import com.doradosystems.mis.domain.ClaimValidationRecord;
import com.doradosystems.mis.domain.ClaimValidationResponse;

/**
 * Manager for manipulating {@link ClaimValidationResponse} objects.
 * 
 * @author <a href="mailto:neil.drummond@doradosystems.com">Neil Drummond</a>
 */
public class ClaimValidationResponseManager extends AbstractManager {
    
    private static final Logger LOG = LoggerFactory.getLogger(ClaimValidationResponseManager.class);

    private ClaimValidationRecordDao claimValidationRecordDao;
    private ClaimValidationResponseDao claimValidationResponseDao;
    
    public void setClaimValidationRecordDao(final ClaimValidationRecordDao claimValidationRecordDao) {
        this.claimValidationRecordDao = claimValidationRecordDao;
    }
    
    public void setClaimValidationResponseDao(final ClaimValidationResponseDao claimValidationResponseDao) {
        this.claimValidationResponseDao = claimValidationResponseDao;
    }
    
    /**
     * Constructs a new {@link ClaimValidationResponse}, stores it in the database,
     * and updates the matching {@link ClaimValidationRecord} to the
     * {@link ClaimValidationRecord.Status#COMPLETE COMPLETE} status, all in a single
     * transaction.
     * 
     * @param payload The payload to store in the {@link ClaimValidationResponse}.
     * @param record The {@link ClaimValidationRecord} that was used to create the
     * response payload.
     * @return The {@link ClaimValidationResponse} that is created.
     * @throws Exception May be rethrown if an {@link Exception} occurs.
     */
    public ClaimValidationResponse createResponse(final String payload, final ClaimValidationRecord record)
            throws Exception {
        
        final ClaimValidationResponse claimValidationResponse = new ClaimValidationResponse(null, record.getBatchId(),
                record.getRunNumber(), ClaimValidationResponse.Status.PENDING, record.getClaimNumber(), payload, null,
                null, record.getId());
        
        final TransactionStatus transaction = getTransaction();
        
        try{
            LOG.debug("Adding response: {}", claimValidationResponse);
            claimValidationResponseDao.add(claimValidationResponse);
            LOG.debug("Updating record {} to COMPLETE.", record);
            claimValidationRecordDao.updateStatus(record.getId(), ClaimValidationRecord.Status.COMPLETE);
            
            commit(transaction);
            LOG.info("Response saved: {}", claimValidationResponse);
        } catch (final Exception exception) {
            rollback(transaction);
            LOG.error("Rolling back due to exception: {}", exception.getLocalizedMessage());
            throw exception;
        }
        
        return claimValidationResponse;
    }
}
