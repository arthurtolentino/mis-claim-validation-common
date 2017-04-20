package com.doradosystems.mis.dao;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import com.doradosystems.data.dao.AbstractDao;
import com.doradosystems.exception.NotFoundException;
import com.doradosystems.exception.UniqueConstraintException;
import com.doradosystems.mis.domain.ClaimValidationRecord;
import com.doradosystems.mis.domain.ClaimValidationRecord.Status;

/**
 * DAO for performing operations on {@link ClaimValidationRecord} records.
 * 
 * @author <a href="mailto:neil.drummond@doradosystems.com">Neil Drummond</a>
 */
public class ClaimValidationRecordDao extends AbstractDao {
    
    /**
     * SQL for inserting a single {@link ClaimValidationRecord}.
     */
    private static final String INSERT_SQL =
            "insert into claim_validation_record" +
                    "(batch_id, run_number, status, claim_number, record) " +
                    "value(?, ?, ?, ?, ?)";
    
    /**
     * SQL for selecting a single {@link ClaimValidationRecord} by it's
     * {@link ClaimValidationRecord#getId() id}.
     */
    private static final String SELECT_CLAIM_VALIDATION_RECORD_SQL =
            "select batch_id, run_number, status, claim_number, record, create_date, updated_date " +
                    "from claim_validation_record where id = ?";
    
    /**
     * SQL for selecting all {@link ClaimValidationRecord ClaimValidationRecords}
     * for a particular {@link ClaimValidationRecord#getBatchId() batchId} and
     * {@link ClaimValidationRecord#getRunNumber() runNumber}.
     */
    private static final String SELECT_CLAIM_VALIDATION_RECORD_BY_BATCH_AND_RUN_SQL =
            "select id, status, claim_number, record, create_date, updated_date " +
                    "from claim_validation_record where batch_id = ? and run_number = ?";
    
    private static final String SELECT_CLAIM_VALIDATION_RECORD_BY_BATCH_AND_RUN_ORDERED_BY_UPDATE_DATE_DESC_SQL =
            "select id, status, claim_number, record, create_date, updated_date "+
                    "from claim_validation_record where batch_id = ? and run_number = ? order by updated_date desc limit ?";
    
    /**
     * SQL to count the number of {@link ClaimValidationRecord ClaimValidationRecords}
     * with a particular {@link ClaimValidationRecord#getBatchId() batchId},
     * {@link ClaimValidationRecord#getRunNumber() runNumber}, and
     * {@link ClaimValidationRecord#getStatus() status}.
     */
    private static final String COUNT_CLAIM_VALIDATION_RECORD_BY_BATCH_AND_RUN_AND_STATUS_SQL =
            "select count(0) as total " +
                    "from claim_validation_record where " +
                    "batch_id = ? and run_number = ? and status = ?";
    
    /**
     * SQL to update the {@link ClaimValidationRecord#getStatus() status} of
     * a {@link ClaimValidationRecord}.
     */
    private static final String UPDATE_CLAIM_VALIDATION_RECORD_STATUS_SQL =
            "update claim_validation_record set status = ? " +
                    "where id = ?";
    
    /**
     * SQL to update {@link ClaimValidationRecord ClaimValidationRecords'}
     * {@link ClaimValidationRecord#getStatus() status} and
     * {@link ClaimValidationRecord#getRunNumber() runNumber} fields in bulk.
     */
    private static final String UPDATE_CLAIM_VALIDATION_RECORD_STATUS_AND_RUN_SQL =
            "update claim_validation_record " +
                    "set run_number = ?, status = ? " +
                    "where batch_id = ? and run_number = ? and status = ?";
    
    /**
     * Constructs a new {@link ClaimValidationRecordDao} using the provided
     * {@link DataSource} to initialize the {@link JdbcTemplate}.
     * 
     * @param dataSource The {@link DataSource} to use to initialize the
     * {@link JdbcTemplate}.
     */
    public ClaimValidationRecordDao(final DataSource dataSource){
        super(dataSource);
    }
    
    /**
     * Inserts a {@link ClaimValidationRecord} into the database.
     * <p>
     * Note: this ignores the {@link ClaimValidationRecord#getId() id},
     * {@link ClaimValidationRecord#getCreateDate() createDate}, and
     * {@link ClaimValidationRecord#getUpdatedDate() updatedDate} fields
     * of the provided record.
     * 
     * @param claimValidationRecord The {@link ClaimValidationRecord} to insert.
     * @return The {@link ClaimValidationRecord#getId() id} that was assigned to
     * the record.
     */
    public long add(final ClaimValidationRecord claimValidationRecord) throws UniqueConstraintException {
        return super.add(
                connection -> {
                    final PreparedStatement preparedStatement =
                            connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
                    preparedStatement.setLong(1, claimValidationRecord.getBatchId());
                    preparedStatement.setLong(2, claimValidationRecord.getRunNumber());
                    preparedStatement.setString(3, claimValidationRecord.getStatus().toString());
                    preparedStatement.setString(4, claimValidationRecord.getClaimNumber());
                    preparedStatement.setString(5, claimValidationRecord.getRecord());
                    return preparedStatement;
                });
    }

    /**
     * Retrieves a {@link ClaimValidationRecord} by it's
     * {@link ClaimValidationRecord#getId() id}.
     * 
     * @param id The {@link ClaimValidationRecord#getId() id} of the
     * {@link ClaimValidationRecord} to retrieve.
     * @return The {@link ClaimValidationRecord}.
     */
    public ClaimValidationRecord get(final long id) throws NotFoundException {
        return super.get(
                SELECT_CLAIM_VALIDATION_RECORD_SQL,
                (rs, count) -> {
                    return new ClaimValidationRecord(
                            id,
                            rs.getLong("batch_id"),
                            rs.getLong("run_number"),
                            Status.valueOf(rs.getString("status")),
                            rs.getString("claimNumber"),
                            rs.getString("record"),
                            rs.getTimestamp("createDate"),
                            rs.getTimestamp("updatedDate")
                            );
                }, id);
    }
    
    /**
     * Retrieves a {@link List} of all {@link ClaimValidationRecord ClaimValidationRecords}
     * that match the given {@link ClaimValidationRecord#getBatchId() batchId}
     * and {@link ClaimValidationRecord#getRunNumber() runNumber}.
     * 
     * @param batchId The {@link ClaimValidationRecord#getBatchId() batchId} to search for.
     * @param runNumber The {@link ClaimValidationRecord#getRunNumber() runNumber} to search
     * for.
     * @return A {@link List} of {@link ClaimValidationRecord ClaimValidationRecords}.
     */
    public List<ClaimValidationRecord> getByBatchIdAndRunNumber(final Long batchId, final Long runNumber) {
        return jdbcTemplate.query(
                SELECT_CLAIM_VALIDATION_RECORD_BY_BATCH_AND_RUN_SQL,
                new Object[]{ batchId, runNumber },
                (rs, count) -> {
                    return new ClaimValidationRecord(
                            rs.getLong("id"),
                            batchId,
                            runNumber,
                            Status.valueOf(rs.getString("status")),
                            rs.getString("claim_number"),
                            rs.getString("record"),
                            rs.getTimestamp("create_date"),
                            rs.getTimestamp("updated_date")
                            );
                });
    }
    
    /**
     * Retrieves a {@link List} of all {@link ClaimValidationRecord ClaimValidationRecords}
     * that match the given {@link ClaimValidationRecord#getBatchId() batchId}
     * and {@link ClaimValidationRecord#getRunNumber() runNumber}. The list is ordered by the
     * updated date column in descending order. The total elements in the list returned is
     * defined by the method parameter 'limit'.
     * 
     * @param batchId
     * @param runNumber
     * @param limit - top N records of the result that will be returned
     * @return A {@link List} of {@link ClaimValidationRecord ClaimValidationRecords}.
     */
    public List<ClaimValidationRecord> getByBatchIdAndRunNumberOrderByUpdateDateDescending(final Long batchId,
            final Long runNumber, int limit) {
        return jdbcTemplate.query(
                SELECT_CLAIM_VALIDATION_RECORD_BY_BATCH_AND_RUN_ORDERED_BY_UPDATE_DATE_DESC_SQL,
                new Object[]{ batchId, runNumber, limit },
                (rs, count) -> {
                    return new ClaimValidationRecord(
                            rs.getLong("id"),
                            batchId,
                            runNumber,
                            Status.valueOf(rs.getString("status")),
                            rs.getString("claim_number"),
                            rs.getString("record"),
                            rs.getTimestamp("create_date"),
                            rs.getTimestamp("updated_date")
                            );
                });
    }
    
    /**
     * Counts the number of {@link ClaimValidationRecord ClaimValidationRecords}
     * matching the given {@link ClaimValidationRecord#getBatchId() batchId},
     * {@link ClaimValidationRecord#getRunNumber() runNumber}, and
     * {@link ClaimValidationRecord#getStatus() status}.
     * 
     * @param batchId The {@link ClaimValidationRecord#getBatchId() batchId} to
     * look for.
     * @param runNumber The {@link ClaimValidationRecord#getRunNumber() runNumber}
     * to look for.
     * @param status The {@link ClaimValidationRecord#getStatus() status} to
     * look for.
     * @return The number of matching {@link ClaimValidationRecord ClaimValidationRecords}.
     */
    public int countByBatchIdAndRunNumberAndStatus(final long batchId, final long runNumber, final Status status) {
        return jdbcTemplate.queryForObject(
                COUNT_CLAIM_VALIDATION_RECORD_BY_BATCH_AND_RUN_AND_STATUS_SQL,
                new Object[]{ batchId, runNumber, status.toString() },
                Integer.class);
    }
    
    /**
     * Updates the {@link ClaimValidationRecord#getStatus() status} of a
     * particular {@link ClaimValidationRecord}.
     * 
     * @param id The {@link ClaimValidationRecord#getId() id} of the
     * {@link ClaimValidationRecord} to update.
     * @param newStatus The new {@link ClaimValidationRecord#getStatus() status}
     * to set on the {@link ClaimValidationRecord}.
     * @return The number of records updated. (Generally will be 1,
     * but may be 0 if no records are updated).
     */
    public int updateStatus(final Long id, final Status newStatus) throws UniqueConstraintException, NotFoundException {
        return super.update(
                UPDATE_CLAIM_VALIDATION_RECORD_STATUS_SQL,
                true,
                newStatus.toString(),
                id
                );
    }
    
    /**
     * Updates {@link ClaimValidationRecord ClaimValidationRecords} in bulk.
     * 
     * @param batchId The {@link ClaimValidationRecord#getBatchId() batchId} of the
     * {@link ClaimValidationRecord ClaimValidationRecords} to update.
     * @param runNumber The {@link ClaimValidationRecord#getRunNumber() runNumber} of
     * the {@link ClaimValidationRecord ClaimValidationRecords} to update.
     * @param status The {@link ClaimValidationRecord#getStatus() status} of the
     * {@link ClaimValidationRecord ClaimValidationRecords} to update.
     * @param newRunNumber The {@link ClaimValidationRecord#getRunNumber() runNumber}
     * to set the {@link ClaimValidationRecord ClaimValidationRecords} to.
     * @param newStatus The {@link ClaimValidationRecord#getStatus() status} to set
     * the {@link ClaimValidationRecord ClaimValidationRecords} to.
     * @return The number of {@link ClaimValidationRecord ClaimValidationRecords} that
     * were updated.
     */
    public int updateStatusAndRunNumber(final long batchId, final long runNumber, final Status status,
            final long newRunNumber, final Status newStatus) 
            throws UniqueConstraintException, NotFoundException {
        return super.update(
                UPDATE_CLAIM_VALIDATION_RECORD_STATUS_AND_RUN_SQL,
                false,
                newRunNumber,
                newStatus.toString(),
                batchId,
                runNumber,
                status.toString());
    }
}
