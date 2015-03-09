package epc.systemone.record;

import epc.metadataformat.data.DataGroup;
import epc.spider.record.RecordInputBoundary;
import epc.systemone.SystemHolder;

public class SystemOneRecordHandler implements SystemOneRecordInputBoundary {

	@Override
	public DataGroup createRecord(String userId, String type, DataGroup record) {

		RecordInputBoundary recordInputBoundary = SystemHolder
				.getRecordInputBoundary();
		DataGroup recordOut = recordInputBoundary.createAndStoreRecord(userId,
				type, record);

		return recordOut;

	}

	@Override
	public DataGroup readRecord(String userId, String type, String id) {
		RecordInputBoundary recordInputBoundary = SystemHolder
				.getRecordInputBoundary();
		DataGroup recordOut = recordInputBoundary.readRecord(userId, type, id);

		return recordOut;
	}

}
