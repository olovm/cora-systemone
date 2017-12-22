/*
  ~ Copyright 2017 Olov McKie
  ~
  ~ This file is part of Cora.
  ~
  ~     Cora is free software: you can redistribute it and/or modify
  ~     it under the terms of the GNU General Public License as published by
  ~     the Free Software Foundation, either version 3 of the License, or
  ~     (at your option) any later version.
  ~
  ~     Cora is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU General Public License for more details.
  ~
  ~     You should have received a copy of the GNU General Public License
  ~     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
*/
package se.uu.ub.cora.systemone.storage;

import java.util.Collection;

import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.spider.record.storage.RecordStorage;

public class RecordStorageInMix implements RecordStorage {

	private RecordStorage recordStorageInDatabase;
	private RecordStorage recordStorageOnDisk;

	public static RecordStorageInMix usingDatabaseAndDiskStorage(
			RecordStorage recordStorageInDatabase, RecordStorage recordStorageOnDisk) {
		return new RecordStorageInMix(recordStorageInDatabase, recordStorageOnDisk);
	}

	private RecordStorageInMix(RecordStorage recordStorageInDatabase,
			RecordStorage recordStorageOnDisk) {
		this.recordStorageInDatabase = recordStorageInDatabase;
		this.recordStorageOnDisk = recordStorageOnDisk;
	}

	@Override
	public DataGroup read(String type, String id) {
		if (type.equals("book")) {
			recordStorageInDatabase.read(type, id);
		}
		return recordStorageOnDisk.read(type, id);
	}

	@Override
	public void create(String type, String id, DataGroup record, DataGroup linkList,
			String dataDivider) {
		recordStorageOnDisk.create(type, id, record, linkList, dataDivider);
	}

	@Override
	public void deleteByTypeAndId(String type, String id) {
		recordStorageOnDisk.deleteByTypeAndId(type, id);
	}

	@Override
	public boolean linksExistForRecord(String type, String id) {
		return recordStorageOnDisk.linksExistForRecord(type, id);
	}

	@Override
	public void update(String type, String id, DataGroup record, DataGroup linkList,
			String dataDivider) {
		recordStorageOnDisk.update(type, id, record, linkList, dataDivider);
		recordStorageInDatabase.update(type, id, record, linkList, dataDivider);
	}

	@Override
	public Collection<DataGroup> readList(String type) {
		return recordStorageOnDisk.readList(type);
	}

	@Override
	public Collection<DataGroup> readAbstractList(String type) {
		return recordStorageOnDisk.readAbstractList(type);
	}

	@Override
	public DataGroup readLinkList(String type, String id) {
		return recordStorageOnDisk.readLinkList(type, id);
	}

	@Override
	public Collection<DataGroup> generateLinkCollectionPointingToRecord(String type, String id) {
		return recordStorageOnDisk.generateLinkCollectionPointingToRecord(type, id);
	}

	@Override
	public boolean recordsExistForRecordType(String type) {
		return recordStorageOnDisk.recordsExistForRecordType(type);
	}

	@Override
	public boolean recordExistsForAbstractOrImplementingRecordTypeAndRecordId(String type,
			String id) {
		return recordStorageOnDisk.recordExistsForAbstractOrImplementingRecordTypeAndRecordId(type,
				id);
	}

	public RecordStorage getRecordStorageInDatabase() {
		return recordStorageInDatabase;
	}

	public RecordStorage getRecordStorageOnDisk() {
		return recordStorageOnDisk;
	}

}
