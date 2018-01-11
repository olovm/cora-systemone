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

import static org.testng.Assert.assertEquals;

import java.util.Collection;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.spider.record.storage.RecordStorage;

public class RecordStorageInMixTest {
	private RecordStorageSystemTwoSpy recordStorageInDatabase = new RecordStorageSystemTwoSpy();
	private RecordStorageSystemTwoSpy recordStorageOnDisk = new RecordStorageSystemTwoSpy();
	private RecordStorageInMix recordStorageMix;

	@BeforeMethod
	public void beforeMethod() {
		recordStorageInDatabase = new RecordStorageSystemTwoSpy();
		recordStorageOnDisk = new RecordStorageSystemTwoSpy();
		recordStorageMix = RecordStorageInMix.usingDatabaseAndDiskStorage(recordStorageInDatabase,
				recordStorageOnDisk);
	}

	@Test
	public void testImplementsRecordStorage() {
		RecordStorageInMix r = RecordStorageInMix
				.usingDatabaseAndDiskStorage(recordStorageInDatabase, recordStorageOnDisk);
		assertEquals(r instanceof RecordStorage, true);
	}

	@Test
	public void testGetStorages() {
		assertEquals(recordStorageMix.getRecordStorageInDatabase(), recordStorageInDatabase);
		assertEquals(recordStorageMix.getRecordStorageOnDisk(), recordStorageOnDisk);
	}

	@Test
	public void testReadIsPassedOnToOnDiskStorage() throws Exception {
		DataGroup readRecord = recordStorageMix.read("someType", "someId");
		assertEquals(recordStorageOnDisk.type, "someType");
		assertEquals(recordStorageOnDisk.id, "someId");
		assertEquals(readRecord, recordStorageOnDisk.read("someType", "someId"));
	}

	@Test
	public void testCreateIsPassedOnToOnDiskStorage() throws Exception {
		DataGroup someDataGroup = DataGroup.withNameInData("someNameInData");
		DataGroup someLinkList = DataGroup.withNameInData("someLinkList");

		DataGroup collectedData = DataCreator.createCollectedDataWithTypeAndId("place",
				"place:0001");
		DataGroup collectStorageTerm = DataGroup.withNameInData("storage");
		collectedData.addChild(collectStorageTerm);

		// DataGroup collectedDataTerm = DataCreator
		// .createStorageTermWithRepeatIdAndTermIdAndTermValueAndStorageKey("1",
		// "placeNameStorageTerm", "Uppsala", "placeName");
		// collectStorageTerm.addChild(collectedDataTerm);

		recordStorageMix.create("someType", "someId", someDataGroup, collectedData, someLinkList,
				"someDataDivider");
		assertEquals(recordStorageOnDisk.type, "someType");
		assertEquals(recordStorageOnDisk.id, "someId");
		assertEquals(recordStorageOnDisk.record, someDataGroup);
		assertEquals(recordStorageOnDisk.linkList, someLinkList);
		assertEquals(recordStorageOnDisk.dataDivider, "someDataDivider");
	}

	@Test
	public void testDeleteByTypeAndIdIsPassedOnToOnDiskStorage() throws Exception {
		recordStorageMix.deleteByTypeAndId("someType", "someId");
		assertEquals(recordStorageOnDisk.type, "someType");
		assertEquals(recordStorageOnDisk.id, "someId");
	}

	@Test
	public void testLinksExistForRecordIsPassedOnToOnDiskStorage() throws Exception {
		boolean linksExistForRecord = recordStorageMix.linksExistForRecord("someType", "someId");
		assertEquals(recordStorageOnDisk.type, "someType");
		assertEquals(recordStorageOnDisk.id, "someId");
		assertEquals(linksExistForRecord,
				recordStorageOnDisk.linksExistForRecord("someType", "someId"));
	}

	@Test
	public void testUpdateIsPassedOnToOnDiskStorage() throws Exception {
		DataGroup someDataGroup = DataGroup.withNameInData("someNameInData");
		DataGroup someLinkList = DataGroup.withNameInData("someLinkList");

		DataGroup collectedData = DataCreator.createCollectedDataWithTypeAndId("place",
				"place:0001");
		DataGroup collectStorageTerm = DataGroup.withNameInData("storage");
		collectedData.addChild(collectStorageTerm);

		// DataGroup collectedDataTerm = DataCreator
		// .createStorageTermWithRepeatIdAndTermIdAndTermValueAndStorageKey("1",
		// "placeNameStorageTerm", "Uppsala", "placeName");
		// collectStorageTerm.addChild(collectedDataTerm);

		recordStorageMix.update("someType", "someId", someDataGroup, collectedData, someLinkList,
				"someDataDivider");
		assertEquals(recordStorageOnDisk.type, "someType");
		assertEquals(recordStorageOnDisk.id, "someId");
		assertEquals(recordStorageOnDisk.record, someDataGroup);
		assertEquals(recordStorageOnDisk.linkList, someLinkList);
		assertEquals(recordStorageOnDisk.dataDivider, "someDataDivider");
	}

	@Test
	public void testReadListIsPassedOnToOnDiskStorage() throws Exception {
		DataGroup filter = DataCreator.createEmptyFilter();
		// DataGroup part = DataCreator.createFilterPartWithRepeatIdAndKeyAndValue("0",
		// "placeName",
		// "Uppsala");
		// filter.addChild(part);
		Collection<DataGroup> readList = recordStorageMix.readList("someType", filter);
		assertEquals(recordStorageOnDisk.type, "someType");
		assertEquals(readList, recordStorageOnDisk.readList("someType", filter));
	}

	@Test
	public void testReadAbstractListIsPassedOnToOnDiskStorage() throws Exception {
		DataGroup filter = DataCreator.createEmptyFilter();
		// DataGroup part = DataCreator.createFilterPartWithRepeatIdAndKeyAndValue("0",
		// "placeName",
		// "Uppsala");
		// filter.addChild(part);
		Collection<DataGroup> readList = recordStorageMix.readAbstractList("someType", filter);
		assertEquals(recordStorageOnDisk.type, "someType");
		assertEquals(readList, recordStorageOnDisk.readAbstractList("someType", filter));
	}

	@Test
	public void testReadLinkListIsPassedOnToOnDiskStorage() throws Exception {
		DataGroup readLinkList = recordStorageMix.readLinkList("someType", "someId");
		assertEquals(recordStorageOnDisk.type, "someType");
		assertEquals(recordStorageOnDisk.id, "someId");
		assertEquals(readLinkList, recordStorageOnDisk.readLinkList("someType", "someId"));
	}

	@Test
	public void testGenerateLinkCollectionPointingToRecordIsPassedOnToOnDiskStorage()
			throws Exception {
		Collection<DataGroup> generatedLinks = recordStorageMix
				.generateLinkCollectionPointingToRecord("someType", "someId");
		assertEquals(recordStorageOnDisk.type, "someType");
		assertEquals(recordStorageOnDisk.id, "someId");
		assertEquals(generatedLinks,
				recordStorageOnDisk.generateLinkCollectionPointingToRecord("someType", "someId"));
	}

	@Test
	public void testRecordsExistForRecordTypeIsPassedOnToOnDiskStorage() throws Exception {
		boolean recordsExist = recordStorageMix.recordsExistForRecordType("someType");
		assertEquals(recordStorageOnDisk.type, "someType");
		assertEquals(recordsExist, recordStorageOnDisk.recordsExistForRecordType("someType"));
	}

	@Test
	public void testRecordsExistForAbstractOrImplementingRecordTypeIsPassedOnToOnDiskStorage()
			throws Exception {
		boolean recordsExist = recordStorageMix
				.recordExistsForAbstractOrImplementingRecordTypeAndRecordId("someType", "someId");
		assertEquals(recordStorageOnDisk.type, "someType");
		assertEquals(recordStorageOnDisk.id, "someId");
		assertEquals(recordsExist, recordStorageOnDisk
				.recordExistsForAbstractOrImplementingRecordTypeAndRecordId("someType", "someId"));
	}
}
