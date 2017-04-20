package com.doradosystems.mis.kafka.event;

import java.io.Serializable;

import com.doradosystems.mis.domain.ClaimValidationRecord;

/**
 * Wraps a ClaimValidationRecord and clientId object for Kafka
 * serialization/deserialization.
 * 
 * @author Arthur Tolentino
 *
 */
public class ProcessClaimValidationRecordEvent implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long clientId;
	private ClaimValidationRecord record;
	
	public ProcessClaimValidationRecordEvent(Long clientId, ClaimValidationRecord record) {
	
		this.clientId = clientId;
		this.record = record;
	}

	public Long getClientId() {
		return clientId;
	}

	public ClaimValidationRecord getClaimValidationRecord() {
		return record;
	}

	@Override
	public String toString() {
		return "ProcessClaimValidationRecordEvent [clientId=" + clientId + ", claimValidationRecord=" + record + "]";
	}

}
