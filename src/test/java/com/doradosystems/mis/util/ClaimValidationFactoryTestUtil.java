package com.doradosystems.mis.util;

import java.util.Date;
import java.util.Random;

import com.doradosystems.mis.domain.ClaimValidationBatch;
import com.doradosystems.mis.domain.ClaimValidationRecord;

/**
 * Factory class that creates MIS Common Pojo's for unit testing
 * 
 * @author Arthur Tolentino
 *
 */
public class ClaimValidationFactoryTestUtil {
    
    private static Random random = new Random();

    /**
     * Creates a new ClaimValidationBatch.
     * 
     * @param status
     * @return ClaimValidationBatch
     */
    public static ClaimValidationBatch newClaimValidationBatch(ClaimValidationBatch.Status status) {
        return new ClaimValidationBatch(new Long(random.nextInt(100)), new Long(random.nextInt(10)), "filename", status,
                new Long(random.nextInt(10)), "globalControlNumber", new Date(), new Date());
    }
    
    /**
     * Creates a new ClaimValidationRecord
     * @param batchId
     * @param status
     * @return ClaimValidationRecord
     */
    public static ClaimValidationRecord newClaimValidationRecord(long batchId, long runNumber, ClaimValidationRecord.Status status) {
        return new ClaimValidationRecord(new Long(random.nextInt(100)), batchId, runNumber, status,
                "claimNumber", "record", new Date(), new Date());
    }
}
