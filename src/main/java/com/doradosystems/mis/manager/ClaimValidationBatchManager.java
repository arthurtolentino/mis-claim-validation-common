package com.doradosystems.mis.manager;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionStatus;

import com.doradosystems.data.manager.AbstractManager;
import com.doradosystems.exception.NotFoundException;
import com.doradosystems.mis.dao.ClaimValidationBatchDao;
import com.doradosystems.mis.dao.ClaimValidationRecordDao;
import com.doradosystems.mis.domain.ClaimValidationBatch;
import com.doradosystems.mis.domain.ClaimValidationRecord;

/**
 * Manager for manipulating {@link ClaimValidationBatch} records.
 * 
 * @author <a href="mailto:neil.drummond@doradosystems.com">Neil Drummond</a>
 */
public class ClaimValidationBatchManager extends AbstractManager {
    
    private static final Logger LOG = LoggerFactory.getLogger(ClaimValidationBatchManager.class);
    
    private ClaimValidationBatchDao claimValidationBatchDao;
    private ClaimValidationRecordDao claimValidationRecordDao;
    private int pollWaitInterval;
    
    public void setClaimValidationBatchDao(final ClaimValidationBatchDao claimValidationBatchDao) {
        this.claimValidationBatchDao = claimValidationBatchDao;
    }
    
    public void setClaimValidationRecordDao(final ClaimValidationRecordDao claimValidationRecordDao) {
        this.claimValidationRecordDao = claimValidationRecordDao;
    }
    
    public void setPollWaitInterval (final int interval){
        this.pollWaitInterval = interval;
    }

    /**
     * Updates a {@link ClaimValidationBatch} record to end the current run.
     * <p>
     * If not all {@link ClaimValidationRecord ClaimValidationRecords} in the current
     * run and batch are complete, the batch and incomplete/pending records
     * will be marked PENDING, and have their runNumber incremented.
     * <p>
     * If all {@link ClaimValidationRecord ClaimValidationRecords} in the current
     * run and batch are complete, the batch will be marked as COMPLETE.
     * <p>
     * All operations are performed in a single transaction.
     * 
     * @param batch The {@link ClaimValidationBatch} to end the run for.
     * @throws Exception May be rethrown if an {@link Exception} occurs.
     */
    public void completeRun(final ClaimValidationBatch batch) throws Exception {
        final long batchId = batch.getId();
        final long runNumber = batch.getRunNumber();
        LOG.info("Completing run {} of batch {}", runNumber, batchId);
        
        final TransactionStatus transaction = getTransaction();
        
        try {
            // Count the number of complete records in this run.
            final int completedRecordCount = claimValidationRecordDao.countByBatchIdAndRunNumberAndStatus(
                    batchId, runNumber, ClaimValidationRecord.Status.COMPLETE);
            LOG.debug("There are {} COMPLETE records in batch {}, run {}.", completedRecordCount, batchId, runNumber);
            
            // Figure out the new run number based on whether or not any progress was made in the current run.
            final long newRecordRunNumber = completedRecordCount > 0 ? runNumber + 1 : runNumber;
            LOG.debug("New run for unprocessed records will be {}", newRecordRunNumber);
            
            // Update any INCOMPLETE records to PENDING with the new run number.
            final long incompleteRecordsUpdated = claimValidationRecordDao.updateStatusAndRunNumber(
                    batchId, runNumber, ClaimValidationRecord.Status.INCOMPLETE,
                    newRecordRunNumber, ClaimValidationRecord.Status.PENDING);
            LOG.debug("{} INCOMPLETE records updated", incompleteRecordsUpdated);
            
            // Update any PENDING records to the new run number.
            final long pendingRecordsUpdated = claimValidationRecordDao.updateStatusAndRunNumber(
                    batchId, runNumber, ClaimValidationRecord.Status.PENDING,
                    newRecordRunNumber, ClaimValidationRecord.Status.PENDING);
            LOG.debug("{} PENDING records updated", pendingRecordsUpdated);
            
            // Figure out the new status and runNumber of the batch based on whether
            // or not any records are in the next run.
            final ClaimValidationBatch.Status newBatchStatus;
            final long newBatchRunNumber;
            if(pendingRecordsUpdated + incompleteRecordsUpdated > 0) {
                // Still more records to process, so go to the next run.
                newBatchStatus = ClaimValidationBatch.Status.PENDING;
                newBatchRunNumber = newRecordRunNumber;
            } else {
                // All processing done, so stop and use the old run number.
                newBatchStatus = ClaimValidationBatch.Status.COMPLETE;
                newBatchRunNumber = runNumber;
            }
            LOG.debug("Batch will be set to status {}, run {}", newBatchStatus, newBatchRunNumber);
            
            // Update the batch to it's new status and runNumber
            claimValidationBatchDao.updateStatusAndRunNumber(batchId, newBatchStatus, newBatchRunNumber);
            LOG.debug("Updated batch.");
            
            // Commit the transaction.
            commit(transaction);
            LOG.info("Batch {} is now {} in run {}", batchId, newBatchStatus, newBatchRunNumber);
        } catch (final Exception exception) {
            rollback(transaction);
            LOG.error("Rolling back due to exception: {}", exception.getLocalizedMessage());
            throw exception;
        }
    }
    
    /**
     * Polls the database until there are zero pending {@link ClaimValidationRecord}s associated with 
     * the {@link ClaimValidationBatch} being evaluated. The {@link pollWaitInterval} value is the
     * number of milliseconds that the thread will sleep in between polling attempts while waiting for
     * the number of pending {@link ClaimValidationRecord}s associated with the current 
     * {@link ClaimValidationBatch} to reach 0.
     * 
     * The poll will exit either when there are no longer {@link ClaimValidationRecord}s in PENDING status
     * or the wait idle time has expired.
     * 
     * <p>
     * @param batchId - batch Id of the {@link ClaimValidationBatch} being examined
     * @param runNumber - run number of the {@link ClaimValidationBatch} being examined
     * @param waitIdleTimeInMilliseconds - max wait time before the poll expires and stops.
     *            An idle time is when none of the records in the batch have been updated 
     *            in the last N milliseconds. 
     * @throws InterruptedException if the Thread.sleep() method is interrupted
     * @throws NotFoundException 
     */
    public void pollUntilNoPendingRecordsRemain(final Long batchId, final Long runNumber,
            final long waitIdleTimeInMilliseconds) throws InterruptedException, NotFoundException {

        LOG.info("Polling run {} of batch {} until no pending records remain.", runNumber, batchId);
        while (true) {
            // find the most recently updated record
            List<ClaimValidationRecord> mostRecentlyUpdatedRecords = claimValidationRecordDao
                    .getByBatchIdAndRunNumberOrderByUpdateDateDescending(batchId, runNumber, 1);
            boolean isWaitTimeExpired = false;
            if(mostRecentlyUpdatedRecords.size() > 0) {
                ClaimValidationBatch batch = claimValidationBatchDao.get(batchId);
                ClaimValidationRecord record = mostRecentlyUpdatedRecords.get(0); 
                // find the most recent update date between a batch and claim validation record.
                // claim validation records may not be immediately processed and reflect it's state.
                Date mostRecentUpdateDate = (batch.getUpdatedDate().after(record.getUpdatedDate())
                        ? batch.getUpdatedDate() : record.getUpdatedDate());
                isWaitTimeExpired = (System.currentTimeMillis() - mostRecentUpdateDate.getTime()) > waitIdleTimeInMilliseconds;
            }
            int pendingRecordCount = claimValidationRecordDao.countByBatchIdAndRunNumberAndStatus(batchId, runNumber,
                    ClaimValidationRecord.Status.PENDING);
            
            LOG.debug("There are {} PENDING records in batch {}, run {}.", pendingRecordCount, batchId, runNumber);
            if(pendingRecordCount == 0 || isWaitTimeExpired) {
                break;
            }
            Thread.sleep(this.pollWaitInterval);
        }
    }
}
