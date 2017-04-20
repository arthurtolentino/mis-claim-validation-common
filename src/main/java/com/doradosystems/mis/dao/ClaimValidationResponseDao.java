package com.doradosystems.mis.dao;

import java.sql.PreparedStatement;
import java.util.List;

import javax.sql.DataSource;

import com.doradosystems.data.dao.AbstractDao;
import com.doradosystems.exception.NotFoundException;
import com.doradosystems.exception.UniqueConstraintException;
import com.doradosystems.mis.domain.ClaimValidationResponse;
import com.doradosystems.mis.domain.ClaimValidationResponse.Status;
import com.doradosystems.mis.domain.ClaimValidationResponseIdentifier;
import com.mysql.jdbc.Statement;

/**
 * @author kev
 *
 *  Data access object implementing crud operations for {@link ClaimValidationResponse}
 */
public class ClaimValidationResponseDao extends AbstractDao {

    private final static String SELECT_BY_BATCH_AND_RUN_SQL =
            "SELECT id, batch_id, run_number, status, claim_number, response, " +
            "create_date, updated_date, claim_validation_record_id " +
            "FROM claim_validation_response " +
            "where batch_id = ? " +
            "and run_number = ?";
    
    /**
     * SQL to select all the
     * {@link ClaimValidationResponseIdentifier ClaimValidationResponseIdentifiers}
     * that have {@link ClaimValidationResponse.Status#PENDING PENDING}
     * {@link ClaimValidationResponse ClaimValidationResponses}.
     */
    private static final String SELECT_PENDING_RESPONSE_IDENTIFIERS_SQL =
            "SELECT DISTINCT batch_id, run_number " +
                    "FROM claim_validation_response " +
                    "WHERE status = 'PENDING'";
    
    private final static String INSERT_SQL = 
            "INSERT INTO claim_validation_response(batch_id, run_number, status, " +
            "claim_number, response, claim_validation_record_id) VALUES(?, ?, ?, ?, ?, ?)";
    
    /**
     * SQL to update the status of all {@link ClaimValidationResponse}
     * records in a single batch and run.
     */
    private static final String UPDATE_STATUS_SQL =
            "UPDATE claim_validation_response " +
                    "SET status = ? " +
                    "WHERE batch_id = ? and run_number = ?";
    
    public ClaimValidationResponseDao(DataSource dataSource) {
        super (dataSource);
    }
    
    /**
     * Persist a {@link ClaimValdiationResponse} object to the database.
     * @param response a {@link ClaimValdiationResponse} object.
     * @return the generated PK
     */
    public long add(ClaimValidationResponse response) throws UniqueConstraintException {
        return super.add (
                connection -> {
                    final PreparedStatement preparedStatement =
                            connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
                    preparedStatement.setLong(1, response.getBatchId());
                    preparedStatement.setLong(2, response.getRunNumber());
                    preparedStatement.setString(3, response.getStatus().toString());
                    preparedStatement.setString(4, response.getClaimNumber());
                    preparedStatement.setString(5, response.getResponse());
                    preparedStatement.setLong(6, response.getClaimValidationRecordId());
                    return preparedStatement;
                });     
    }
    
    /**
     * Get all {@link ClaimValdiationResponse} objects for the given batch and run number.
     * @param batchId the batch id of the file.
     * @param runNumber the run number of the file
     * @return a list of {@link ClaimValdiationResponse} objects.
     */
    public List<ClaimValidationResponse> get(Long batchId, Long runNumber) {
        return jdbcTemplate.query(SELECT_BY_BATCH_AND_RUN_SQL, new Object[] { batchId, runNumber },
                (rs, count) -> {
                    return new ClaimValidationResponse(rs.getLong("id"), 
                            rs.getLong("batch_id"),
                            rs.getLong("run_number"),
                            Status.valueOf(rs.getString("status")),
                            rs.getString("claim_number"),
                            rs.getString("response"),
                            rs.getTimestamp("create_date"),
                            rs.getTimestamp("updated_date"),
                            rs.getLong("claim_validation_record_id"));
                });
    }
    
    /**
     * Gets all the
     * {@link ClaimValidationResponseIdentifier ClaimValidationResponseIdentifiers}
     * that have {@link ClaimValidationResponse.Status#PENDING PENDING}
     * {@link ClaimValidationResponse ClaimValidationResponses}.
     * 
     * @return A {@link List} of {@link ClaimValidationResponseIdentifier ClaimValidationResponseIdentifiers}.
     */
    public List<ClaimValidationResponseIdentifier> getPendingResponseIdentifiers() {
        return jdbcTemplate.query(
                SELECT_PENDING_RESPONSE_IDENTIFIERS_SQL,
                (rs, count) -> {
                    return new ClaimValidationResponseIdentifier(
                            rs.getLong("batch_id"),
                            rs.getLong("run_number")
                            );
                });
    }
    
    /**
     * Updates the {@link ClaimValidationResponse#getStatus() status} of
     * all {@link ClaimValidationResponse} records in a given batch and
     * run.
     * 
     * @param batchId The {@link ClaimValidationResponse#getBatchId() batchId} of
     * the batch to be updated.
     * @param runNumber The {@link ClaimValidationResponse#getRunNumber() runNumber}
     * of the run to be updated.
     * @param newStatus The new {@link ClaimValidationResponse#getStatus() status} to
     * set the records to.
     * @return The number of {@link ClaimValidationResponse} records modified.
     */
    public int updateStatus(final Long batchId, final Long runNumber, final Status newStatus) 
            throws NotFoundException, UniqueConstraintException {
        return super.update(
                UPDATE_STATUS_SQL,
                true,
                newStatus.toString(),
                batchId,
                runNumber
                );
    }
}
