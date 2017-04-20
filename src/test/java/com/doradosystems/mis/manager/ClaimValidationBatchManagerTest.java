package com.doradosystems.mis.manager;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.doradosystems.exception.NotFoundException;
import com.doradosystems.mis.dao.ClaimValidationBatchDao;
import com.doradosystems.mis.dao.ClaimValidationRecordDao;
import com.doradosystems.mis.domain.ClaimValidationBatch;
import com.doradosystems.mis.domain.ClaimValidationRecord;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.ArgumentMatchers.eq;

import static com.doradosystems.mis.util.ClaimValidationFactoryTestUtil.newClaimValidationBatch;
import static com.doradosystems.mis.util.ClaimValidationFactoryTestUtil.newClaimValidationRecord;

/**
 * 
 * @author Arthur Tolentino
 *
 */
public class ClaimValidationBatchManagerTest {
	
	private ClaimValidationBatchManager manager;
	private ClaimValidationBatchDao batchDao;
	private ClaimValidationRecordDao recordDao;
	
	@Before
	public void setup() {
		batchDao = mock(ClaimValidationBatchDao.class);
		recordDao = mock(ClaimValidationRecordDao.class);
		manager = new ClaimValidationBatchManager();
		manager.setClaimValidationBatchDao(batchDao);
		manager.setClaimValidationRecordDao(recordDao);
		manager.setPollWaitInterval(120);
	}
	
	@Test
	public void pollUntilAllRecordsHaveBeenChangedFromPendingToComplete() throws NotFoundException, InterruptedException {
		ClaimValidationBatch batch = newClaimValidationBatch(ClaimValidationBatch.Status.PENDING);
		ClaimValidationRecord record = newClaimValidationRecord(batch.getId(), batch.getRunNumber(), ClaimValidationRecord.Status.COMPLETE);
		
		when(recordDao.getByBatchIdAndRunNumberOrderByUpdateDateDescending(eq(batch.getId()), eq(batch.getRunNumber()),
				eq(1))).thenReturn(Arrays.asList(record));
		when(batchDao.get(eq(batch.getId()))).thenReturn(batch);
		when(recordDao.countByBatchIdAndRunNumberAndStatus(eq(batch.getId()), eq(batch.getRunNumber()),
				eq(ClaimValidationRecord.Status.PENDING))).thenReturn(0);
		
		manager.pollUntilNoPendingRecordsRemain(batch.getId(), batch.getRunNumber(), 30000);
		
		verify(recordDao, times(1)).getByBatchIdAndRunNumberOrderByUpdateDateDescending(eq(batch.getId()),
				eq(batch.getRunNumber()), eq(1));
		verify(batchDao, times(1)).get(eq(batch.getId()));
		verify(recordDao, times(1)).countByBatchIdAndRunNumberAndStatus(eq(batch.getId()), eq(batch.getRunNumber()),
				eq(ClaimValidationRecord.Status.PENDING));
	}
	
	@Test
	public void pollUntilWaitIdleTimeExpires() throws NotFoundException, InterruptedException {
		ClaimValidationBatch batch = newClaimValidationBatch(ClaimValidationBatch.Status.PENDING);
		ClaimValidationRecord record = newClaimValidationRecord(batch.getId(), batch.getRunNumber(), ClaimValidationRecord.Status.PENDING);
		
		when(recordDao.getByBatchIdAndRunNumberOrderByUpdateDateDescending(eq(batch.getId()), eq(batch.getRunNumber()),
				eq(1))).thenReturn(Arrays.asList(record));
		when(batchDao.get(eq(batch.getId()))).thenReturn(batch);
		when(recordDao.countByBatchIdAndRunNumberAndStatus(eq(batch.getId()), eq(batch.getRunNumber()),
				eq(ClaimValidationRecord.Status.PENDING))).thenReturn(1);
		
		manager.pollUntilNoPendingRecordsRemain(batch.getId(), batch.getRunNumber(), 500);
		
		verify(recordDao, atLeastOnce()).getByBatchIdAndRunNumberOrderByUpdateDateDescending(eq(batch.getId()),
				eq(batch.getRunNumber()), eq(1));
		verify(batchDao, atLeastOnce()).get(eq(batch.getId()));
		verify(recordDao, atLeastOnce()).countByBatchIdAndRunNumberAndStatus(eq(batch.getId()), eq(batch.getRunNumber()),
				eq(ClaimValidationRecord.Status.PENDING));
	}
	

}
