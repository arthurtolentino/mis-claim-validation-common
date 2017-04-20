package com.doradosystems.mis.kafka.serialization;

import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import com.doradosystems.mis.domain.ClaimValidationRecord;
import com.doradosystems.mis.kafka.event.ProcessClaimValidationRecordEvent;
import com.doradosystems.mis.kafka.serialization.ProcessClaimValidationRecordEventDeserializer;
import com.doradosystems.mis.kafka.serialization.ProcessClaimValidationRecordEventSerializer;

public class ProcessClaimValidationRecordEventDeserializerTest {
	
	private ProcessClaimValidationRecordEventSerializer serializer = new ProcessClaimValidationRecordEventSerializer();
	private ProcessClaimValidationRecordEventDeserializer deserializer = new ProcessClaimValidationRecordEventDeserializer();
	
	@Test
	public void deserializeProcessClaimValidationRecordEvent(){
		ProcessClaimValidationRecordEvent event = new ProcessClaimValidationRecordEvent(new Long(3),
				new ClaimValidationRecord(new Long(1), new Long(1), new Long(1), ClaimValidationRecord.Status.PENDING, "1",
						"bar", new Date(), new Date()));
		byte[] data= serializer.serialize(null, event);
		
		ProcessClaimValidationRecordEvent result = deserializer.deserialize(null, data);
		assertEquals(event.getClientId(), result.getClientId());
		assertThat(event.getClaimValidationRecord(), samePropertyValuesAs(result.getClaimValidationRecord()));
	}
	
	@Test
	public void deserializeProcessClaimValidationRecordEventWithNullClientId() {
		ProcessClaimValidationRecordEvent event = new ProcessClaimValidationRecordEvent(null,
				new ClaimValidationRecord(new Long(1), new Long(1), new Long(1), ClaimValidationRecord.Status.PENDING, "1",
						"bar", new Date(), new Date()));
		byte[] data= serializer.serialize(null, event);
		
		ProcessClaimValidationRecordEvent result = deserializer.deserialize(null, data);
		assertThat(result.getClientId(), is(nullValue()));
		assertThat(event.getClaimValidationRecord(), samePropertyValuesAs(result.getClaimValidationRecord()));
	}
	
	@Test
	public void deserializeProcessClaimValidationRecordEventWithNullClaimValidationRecord() {
		ProcessClaimValidationRecordEvent event = new ProcessClaimValidationRecordEvent(new Long(3), null);
		byte[] data= serializer.serialize(null, event);
		
		ProcessClaimValidationRecordEvent result = deserializer.deserialize(null, data);
		assertThat(result.getClaimValidationRecord(), is(nullValue()));
		assertEquals(event.getClientId(), result.getClientId());
	}
	
	@Test
	public void deserializeProcessClaimValidationRecordEventWithNullProperties() {
		ProcessClaimValidationRecordEvent event = new ProcessClaimValidationRecordEvent(null, null);
		byte[] data= serializer.serialize(null, event);
		
		ProcessClaimValidationRecordEvent result = deserializer.deserialize(null, data);
		assertThat(result.getClientId(), is(nullValue()));
		assertThat(result.getClaimValidationRecord(), is(nullValue()));
	}
	
	@Test
	public void deserializeNullObject() {
		ProcessClaimValidationRecordEvent result = deserializer.deserialize(null, null);
		assertThat(result, is(nullValue()));
	}

}
