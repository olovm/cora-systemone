package se.uu.ub.cora.systemone.storage;

import java.util.ArrayList;
import java.util.Collection;

import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.spider.record.storage.RecordStorage;

public class RecordStorageSystemTwoSpy implements RecordStorage {

	public String type;
	public String id;
	public DataGroup record;
	public DataGroup linkList;
	public String dataDivider;

	private DataGroup someRecord = DataGroup.withNameInData("someNameInData");
	private Collection<DataGroup> someRecordList = new ArrayList<>();
	private DataGroup someLinkList = DataGroup.withNameInData("someLinkList");
	private Collection<DataGroup> someLinkPointingList = new ArrayList<>();

	@Override
	public DataGroup read(String type, String id) {
		this.type = type;
		this.id = id;
		return someRecord;
	}

	@Override
	public void create(String type, String id, DataGroup record, DataGroup collectedTerms,
			DataGroup linkList, String dataDivider) {
		this.type = type;
		this.id = id;
		this.record = record;
		this.linkList = linkList;
		this.dataDivider = dataDivider;
	}

	@Override
	public void deleteByTypeAndId(String type, String id) {
		this.type = type;
		this.id = id;

	}

	@Override
	public boolean linksExistForRecord(String type, String id) {
		this.type = type;
		this.id = id;

		return true;
	}

	@Override
	public void update(String type, String id, DataGroup record, DataGroup collectedTerms,
			DataGroup linkList, String dataDivider) {
		this.type = type;
		this.id = id;
		this.record = record;
		this.linkList = linkList;
		this.dataDivider = dataDivider;
	}

	@Override
	public Collection<DataGroup> readList(String type, DataGroup filter) {
		this.type = type;
		return someRecordList;
	}

	@Override
	public Collection<DataGroup> readAbstractList(String type, DataGroup filter) {
		this.type = type;
		return someRecordList;
	}

	@Override
	public DataGroup readLinkList(String type, String id) {
		this.type = type;
		this.id = id;
		return someLinkList;
	}

	@Override
	public Collection<DataGroup> generateLinkCollectionPointingToRecord(String type, String id) {
		this.type = type;
		this.id = id;
		return someLinkPointingList;
	}

	@Override
	public boolean recordsExistForRecordType(String type) {
		this.type = type;
		return true;
	}

	@Override
	public boolean recordExistsForAbstractOrImplementingRecordTypeAndRecordId(String type,
			String id) {
		this.type = type;
		this.id = id;

		return true;
	}

}
