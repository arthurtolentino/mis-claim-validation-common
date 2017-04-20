package com.doradosystems.mis.dao;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import com.doradosystems.data.dao.AbstractDao;
import com.doradosystems.exception.NotFoundException;
import com.doradosystems.exception.UniqueConstraintException;
import com.doradosystems.mis.domain.ClaimValidationBatch;
import com.doradosystems.mis.domain.ClaimValidationBatch.Status;

/**
 * DAO for performing operations on {@link ClaimValidationBatch} records.
 * 
 * @author <a href="mailto:neil.drummond@doradosystems.com">Neil Drummond</a>
 */
public class ClaimValidationBatchDao extends AbstractDao {

    /**
     * SQL for inserting a single {@link ClaimValidationBatch} record.
     */
    private static final String INSERT_SQL =
            "insert into claim_validation_batch" +
                    "(client_id, filename, status, run_number, global_control_number) " +
                    "value(?, ?, ?, ?, ?)";
    
    /**
     * SQL for selecting a {@link ClaimValidationBatch} record by it's
     * {@link ClaimValidationBatch#getId() id}.
     */
    private static final String SELECT_CLAIM_VALIDATION_BATCH_SQL =
            "select client_id, filename, status, run_number, global_control_number, create_date, updated_date " +
                    "from claim_validation_batch where id = ?";
    
    /**
     * SQL for selecting all {@link ClaimValidationBatch} records with
     * a particular {@link ClaimValidationBatch#getStatus() status}.
     */
    private static final String SELECT_CLAIM_VALIDATION_BATCH_BY_STATUS_SQL =
            "select id, client_id, filename, run_number, global_control_number, create_date, updated_date " +
                    "from claim_validation_batch where status = ?";
    
    /**
     * SQL for selecting a specific number of {@link ClaimValidationBatch}
     * records with a particular {@link ClaimValidationBatch#getStatus() status}.
     * <p>
     * This orders the records by {@link ClaimValidationBatch#getCreateDate() createDate}
     * ascending.
     */
    private static final String SELECT_CLAIM_VALIDATION_BATCH_BY_STATUS_WITH_LIMIT_ORDERED_BY_CREATE_DATE_SQL =
            "select id, client_id, filename, run_number, global_control_number, create_date, updated_date " +
                    "from claim_validation_batch where status = ? order by create_date ASC limit ?";
    
    /**
     * SQL for counting the number of {@link ClaimValidationBatch} records
     * in a particular {@link ClaimValidationBatch#getStatus() status}.
     */
    private static final String COUNT_CLAIM_VALIDATION_BATCH_BY_STATUS_SQL =
            "select count(0) as total " +
                    "from claim_validation_batch where status = ?";
    
    /**
     * SQL to update the {@link ClaimValidationBatch#getStatus() status} of
     * a {@link ClaimValidationBatch} record.
     */
    private static final String UPDATE_CLAIM_VALIDATION_BATCH_STATUS_SQL =
            "update claim_validation_batch set status = ? " +
                    "where id = ?";
    
    /**
     * SQL to update the {@link ClaimValidationBatch#getStatus() status} and
     * {@link ClaimValidationBatch#getRunNumber() runNumber} of a {@link ClaimValidationBatch}
     * record.
     */
    private static final String UPDATE_CLAIM_VALIDATION_BATCH_STATUS_AND_RUN_SQL =
            "update claim_validation_batch set status = ?, run_number = ? " +
                    "where id = ?";
    
    /**
     * Constructs a new {@link ClaimValidationBatchDao} using the provided
     * {@link DataSource} to initialize the {@link JdbcTemplate}.
     * 
     * @param dataSource The {@link DataSource} to use to initialize the
     * {@link JdbcTemplate}.
     */
    public ClaimValidationBatchDao(final DataSource dataSource) {
        super(dataSource);
    }
    
    /**
     * Inserts a {@link ClaimValidationBatch} record into the database.
     * <p>
     * Note: This ignores the {@link ClaimValidationBatch#getId() id},
     * {@link ClaimValidationBatch#getCreateDate() createDate}, and
     * {@link ClaimValidationBatch#getUpdatedDate() updatedDate} fields
     * of the provided record.
     * 
     * @param claimValidationBatch The {@link ClaimValidationBatch} record to insert.
     * @return The {@link ClaimValidationBatch#getId() id} that was assigned to the
     * record.
     */
    public long add(final ClaimValidationBatch claimValidationBatch) throws UniqueConstraintException {
        return super.add(
                connection -> {
                    final PreparedStatement preparedStatement =
                            connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
                    preparedStatement.setLong(1, claimValidationBatch.getClientId());
                    preparedStatement.setString(2, claimValidationBatch.getFilename());
                    preparedStatement.setString(3, claimValidationBatch.getStatus().toString());
                    preparedStatement.setLong(4, claimValidationBatch.getRunNumber());
                    preparedStatement.setString(5, claimValidationBatch.getGlobalControlNumber());
                    return preparedStatement;
                });
    }
    
    /**
     * Retrieves a {@link ClaimValidationBatch} record by it's
     * {@link ClaimValidationBatch#getId() id}.
     * 
     * @param id The {@link ClaimValidationBatch#getId() id} of the
     * record to retrieve.
     * @return The {@link ClaimValidationBatch} record.
     */
    public ClaimValidationBatch get(final Long id) throws NotFoundException{
        return super.get(
                SELECT_CLAIM_VALIDATION_BATCH_SQL, 
                (rs, count) -> {
                    return new ClaimValidationBatch(
                            id,
                            rs.getLong("client_id"),
                            rs.getString("filename"),
                            Status.valueOf(rs.getString("status")),
                            rs.getLong("run_number"),
                            rs.getString("global_control_number"),
                            rs.getTimestamp("create_date"),
                            rs.getTimestamp("updated_date")
                            );
                }, id);
    }
    
    /**
     * Retrieves all {@link ClaimValidationBatch} records with a
     * particular {@link ClaimValidationBatch#getStatus() status}.
     * 
     * @param status The {@link ClaimValidationBatch#getStatus() status}
     * to retrieve records for.
     * @return A {@link List} of all {@link ClaimValidationBatch} records
     * currently in the provided {@link ClaimValidationBatch#getStatus() status}.
     */
    public List<ClaimValidationBatch> getByStatus(final Status status) {
        return jdbcTemplate.query(
                SELECT_CLAIM_VALIDATION_BATCH_BY_STATUS_SQL,
                new Object[]{ status.toString() },
                (rs, count) -> {
                    return new ClaimValidationBatch(
                            rs.getLong("id"),
                            rs.getLong("client_id"),
                            rs.getString("filename"),
                            status,
                            rs.getLong("run_number"),
                            rs.getString("global_control_number"),
                            rs.getTimestamp("create_date"),
                            rs.getTimestamp("updated_date")
                            );
                });
    }
    
    /**
     * Retrieves all {@link ClaimValidationBatch} records with a
     * particular {@link ClaimValidationBatch#getStatus() status}.
     * <p>
     * This method will return values in ascending order of creation, such
     * that the oldest values are the first ones returned.
     * 
     * @param status The {@link ClaimValidationBatch#getStatus() status}
     * to retrieve records for.
     * @return A {@link List} of all {@link ClaimValidationBatch} records
     * currently in the provided {@link ClaimValidationBatch#getStatus() status}.
     */
    public List<ClaimValidationBatch> getByStatusWithCreateTimeAscending(final Status status, final int limit) {
        return jdbcTemplate.query(
                SELECT_CLAIM_VALIDATION_BATCH_BY_STATUS_WITH_LIMIT_ORDERED_BY_CREATE_DATE_SQL,
                new Object[]{ status.toString(), limit },
                (rs, count) -> {
                    return new ClaimValidationBatch(
                            rs.getLong("id"),
                            rs.getLong("client_id"),
                            rs.getString("filename"),
                            status,
                            rs.getLong("run_number"),
                            rs.getString("global_control_number"),
                            rs.getTimestamp("create_date"),
                            rs.getTimestamp("updated_date")
                            );
                });
    }
    
    /**
     * Counts the number of {@link ClaimValidationBatch} records in
     * a particular {@link ClaimValidationBatch#getStatus() status}.
     * 
     * @param status The {@link ClaimValidationBatch#getStatus() status}
     * to query for.
     * @return The number of records in the specified
     * {@link ClaimValidationBatch#getStatus() status}.
     */
    public int countByStatus(final Status status) {
        return jdbcTemplate.queryForObject(
                COUNT_CLAIM_VALIDATION_BATCH_BY_STATUS_SQL,
                new Object[]{ status.toString() },
                Integer.class);
    }
    
    /**
     * Updates the {@link ClaimValidationBatch#getStatus() status} of a
     * particular {@link ClaimValidationBatch} record.
     * 
     * @param id The {@link ClaimValidationBatch#getId() id} of the
     * {@link ClaimValidationBatch} record to update.
     * @param newStatus The new {@link ClaimValidationBatch#getStatus() status}
     * to set on the {@link ClaimValidationBatch} record.
     * @return The number of records updated. (Generally will be 1,
     * but may be 0 if no records are updated).
     * @throws NotFoundException 
     * @throws UniqueConstraintException 
     */
    public int updateStatus(final Long id, final Status newStatus) throws UniqueConstraintException, NotFoundException {
        return super.update(
                UPDATE_CLAIM_VALIDATION_BATCH_STATUS_SQL,
                true,
                newStatus.toString(),
                id
                );
    }
    
    /**
     * Updates the {@link ClaimValidationBatch#getStatus() status} and
     * {@link ClaimValidationBatch#getRunNumber() runNumber} of a specific
     * {@link ClaimValidationBatch} record.
     * 
     * @param id The {@link ClaimValidationBatch#getId() id} of the
     * {@link ClaimValidationBatch} to update.
     * @param newStatus The new {@link ClaimValidationBatch#getStatus() status}.
     * @param newRunNumber The new {@link ClaimValidationBatch#getRunNumber() runNumber}.
     * @return The number of records modified.
     * @throws NotFoundException 
     * @throws UniqueConstraintException 
     */
    public int updateStatusAndRunNumber(final long id, final Status newStatus, final long newRunNumber) throws UniqueConstraintException, NotFoundException {
        return super.update(
                UPDATE_CLAIM_VALIDATION_BATCH_STATUS_AND_RUN_SQL,
                true,
                newStatus.toString(),
                newRunNumber,
                id);
    }
}
