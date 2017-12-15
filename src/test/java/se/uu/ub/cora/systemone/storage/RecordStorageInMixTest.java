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

import org.testng.annotations.Test;

import se.uu.ub.cora.spider.record.storage.RecordStorage;

public class RecordStorageInMixTest {
	// private RecordStorageInDatabase recordStorageInDatabase = new
	// RecordStorageInDatabaseSpy();
	// private RecordStorageOnDisk recordStorageOnDisk = new
	// RecordStorageOnDiskSpy();
	private RecordStorage recordStorageInDatabase = new RecordStorageSystemTwoSpy();
	private RecordStorage recordStorageOnDisk = new RecordStorageSystemTwoSpy();

	@Test
	public void testImplementsRecordStorage() {
		RecordStorageInMix r = RecordStorageInMix
				.usingDatabaseAndDiskStorage(recordStorageInDatabase, recordStorageOnDisk);
		assertEquals(r instanceof RecordStorage, true);
	}

	@Test
	public void testGetStorages() {
		RecordStorageInMix r = RecordStorageInMix
				.usingDatabaseAndDiskStorage(recordStorageInDatabase, recordStorageOnDisk);
		assertEquals(r.getRecordStorageInDatabase(), recordStorageInDatabase);
		assertEquals(r.getRecordStorageOnDisk(), recordStorageOnDisk);
	}
}
