/*
 * Copyright 2015 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.uu.ub.cora.systemone;

import java.util.HashMap;
import java.util.Map;

import se.uu.ub.cora.beefeater.Authorizator;
import se.uu.ub.cora.beefeater.AuthorizatorImp;
import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.linkcollector.DataRecordLinkCollector;
import se.uu.ub.cora.bookkeeper.linkcollector.DataRecordLinkCollectorImp;
import se.uu.ub.cora.bookkeeper.metadata.MetadataTypes;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorage;
import se.uu.ub.cora.bookkeeper.validator.DataValidator;
import se.uu.ub.cora.bookkeeper.validator.DataValidatorImp;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.record.PermissionKeyCalculator;
import se.uu.ub.cora.spider.record.RecordPermissionKeyCalculator;
import se.uu.ub.cora.spider.record.storage.RecordIdGenerator;
import se.uu.ub.cora.spider.record.storage.RecordStorage;
import se.uu.ub.cora.spider.record.storage.RecordStorageInMemory;
import se.uu.ub.cora.spider.record.storage.TimeStampIdGenerator;

/**
 * SystemOneDependencyProvider wires up the system for use in "production", as
 * this is in SystemOne production currently means using all in memory storage,
 * so do NOT use this class in production as it is written today. :)
 *
 * @author <a href="mailto:olov.mckie@ub.uu.se">Olov McKie</a>
 * @since 0.1
 */
public class SystemOneDependencyProvider implements SpiderDependencyProvider {
	private static final String REF_PARENT_ID = "refParentId";
	private static final String FINAL_VALUE = "finalValue";
	private static final String DUMMY = "dummy";
	private static final String REF_COLLECTION_ID = "refCollectionId";
	private static final String METADATA_GROUP = "metadataGroup";
	private static final String COLLECTION_ITEM_REFERENCES = "collectionItemReferences";
	private static final String COLLECTION_ITEM_REFERENCE = "collectionItemReference";
	private static final String TYPE = "type";
	private static final String ATTRIBUTE = "attribute";
	private static final String ATTRIBUTES = "attributes";
	private static final String COLLECTION_ITEM = "collectionItem";
	private static final String DEF_TEXT = "DefText";
	private static final String LINKED_PATH = "linkedPath";
	private static final String LINKED_RECORD_TYPE = "linkedRecordType";
	private static final String METADATA = "metadata";
	private static final String NAME_FOR_ABSTRACT = "abstract";
	private static final String PARENT_ID = "parentId";
	private static final String A_Z = "(^[A-Z\\_]{2,50}$)";
	private static final String TRUE_OR_FALSE = "^true$|^false$";
	private static final String SELF_PRESENTATION_VIEW_ID = "selfPresentationViewId";
	private static final String PERMISSION_KEY = "permissionKey";
	private static final String USER_SUPPLIED_ID = "userSuppliedId";
	private static final String SEARCH_PRESENTATION_FORM_ID = "searchPresentationFormId";
	private static final String SEARCH_METADATA_ID = "searchMetadataId";
	private static final String LIST_PRESENTATION_VIEW_ID = "listPresentationViewId";
	private static final String NEW_PRESENTATION_FORM_ID = "newPresentationFormId";
	private static final String PRESENTATION_FORM_ID = "presentationFormId";
	private static final String PRESENTATION_VIEW_ID = "presentationViewId";
	private static final String METADATA_ID = "metadataId";
	private static final String REPEAT_MAX = "repeatMax";
	private static final String REPEAT_MIN = "repeatMin";
	private static final String DEF_TEXT_ID = "defTextId";
	private static final String TEXT_ID = "textId";
	private static final String RECORD_TYPE_NEW = "recordTypeNew";
	private static final String ATTRIBUTE_REFERENCES = "attributeReferences";
	private static final String NEW_METADATA_ID = "newMetadataId";
	private static final String CHILD_REFERENCE = "childReference";
	private static final String GROUP = "group";
	private static final String RECORD_TYPE_TYPE_COLLECTION = "recordTypeTypeCollection";
	private static final String RECORD_TYPE_TYPE_COLLECTION_VAR = "recordTypeTypeCollectionVar";
	private static final String ID = "id";
	private static final String NAME_IN_DATA = "nameInData";
	private static final String CHILD_REFERENCES = "childReferences";
	private static final String RECORD_INFO_NEW = "recordInfoNew";
	private static final String NAME_FOR_METADATA = "metadata";
	private static final String CREATED_BY = "createdBy";
	private static final String RECORD_INFO = "recordInfo";
	private static final String RECORD_TYPE = "recordType";
	private static final String EVERYTHING_REG_EXP = "everythingRegExp";
	private static final String METADATA_TEXT_VARIABLE = "metadataTextVariable";
	private static final String METADATA_TEXT_VARIABLE_NEW = "metadataTextVariableNew";

	private RecordStorage recordStorage;
	private MetadataStorage metadataStorage;
	private Authorizator authorizator;
	private RecordIdGenerator idGenerator;
	private PermissionKeyCalculator keyCalculator;

	private DataGroup emptyLinkList = DataGroup.withNameInData("collectedDataLinks");

	public SystemOneDependencyProvider() {
		Map<String, Map<String, DataGroup>> records = new HashMap<>();

		recordStorage = new RecordStorageInMemory(records);
		metadataStorage = (MetadataStorage) recordStorage;
		authorizator = new AuthorizatorImp();
		idGenerator = new TimeStampIdGenerator();
		keyCalculator = new RecordPermissionKeyCalculator();

		bootstrapSystemMetadata();

		createDummyRecordLink();
		createDummyMetadataCollectionVariableChild();
	}

	private void createDummyRecordLink() {
		DataGroup dummyDTDL = DataGroup.withNameInData(NAME_FOR_METADATA);
		dummyDTDL.addAttributeByIdWithValue(TYPE, "recordLink");
		String id = "dummyRecordLink";
		dummyDTDL.addChild(
				createRecordInfoWithRecordTypeAndRecordId(MetadataTypes.RECORDLINK.type, id));

		dummyDTDL.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, "dummyLink"));
		dummyDTDL.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, id + "Text"));
		dummyDTDL.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, id + DEF_TEXT));
		dummyDTDL.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "someRecordType"));
		recordStorage.create(MetadataTypes.RECORDLINK.type, id, dummyDTDL, emptyLinkList);
	}

	private void createDummyMetadataCollectionVariableChild() {
		DataGroup dummyMCVC = DataGroup.withNameInData(NAME_FOR_METADATA);
		dummyMCVC.addAttributeByIdWithValue(TYPE, "collectionVariableChild");
		String id = "dummyCollectionVariableChild";
		dummyMCVC.addChild(createRecordInfoWithRecordTypeAndRecordId(
				MetadataTypes.COLLECTIONVARIABLECHILD.type, id));

		dummyMCVC.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, METADATA));
		dummyMCVC.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, id + "Text"));
		dummyMCVC.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, id + DEF_TEXT));
		dummyMCVC.addChild(DataAtomic.withNameInDataAndValue("refCollectionId", DUMMY));
		dummyMCVC.addChild(DataAtomic.withNameInDataAndValue(REF_PARENT_ID, DUMMY));
		dummyMCVC.addChild(DataAtomic.withNameInDataAndValue(FINAL_VALUE, DUMMY));
		recordStorage.create(MetadataTypes.COLLECTIONVARIABLECHILD.type, id, dummyMCVC,
				emptyLinkList);

	}

	/**
	 * This metadata and record types needs to be in place when the system
	 * starts
	 */
	private void bootstrapSystemMetadata() {
		addMetadataTextVariableWithId(ID);
		addMetadataTextVariableWithId(CREATED_BY);
		addMetadataTextVariableWithId(METADATA_ID);
		addMetadataTextVariableWithId(PARENT_ID);
		addMetadataTextVariableWithIdAndRegEx(NAME_FOR_ABSTRACT, TRUE_OR_FALSE);
		addMetadataTextVariableWithId(PRESENTATION_VIEW_ID);
		addMetadataTextVariableWithId(PRESENTATION_FORM_ID);
		addMetadataTextVariableWithId(NEW_METADATA_ID);
		addMetadataTextVariableWithId(NEW_PRESENTATION_FORM_ID);
		addMetadataTextVariableWithId(LIST_PRESENTATION_VIEW_ID);
		addMetadataTextVariableWithId(SEARCH_METADATA_ID);
		addMetadataTextVariableWithId(SEARCH_PRESENTATION_FORM_ID);
		addMetadataTextVariableWithIdAndRegEx(USER_SUPPLIED_ID, TRUE_OR_FALSE);
		addMetadataTextVariableWithIdAndRegEx(PERMISSION_KEY, A_Z);
		addMetadataTextVariableWithId(SELF_PRESENTATION_VIEW_ID);
		addMetadataTextVariableWithIdAndRegEx(EVERYTHING_REG_EXP, "regEx", ".+");
		addMetadataTextVariableWithId(LINKED_RECORD_TYPE);
		addMetadataTextVariable();
		addMetadataTextVariableNew();

		addMetadataMetadataCollectionVariableNew();
		addMetadataTextVariableWithId(REF_PARENT_ID);
		addMetadataTextVariableWithId(FINAL_VALUE);
		addMetadataMetadataCollectionVariableChildNew();
		addMetadataMetadataItemCollectionNew();
		addMetadataTextVariableWithId(REF_COLLECTION_ID);
		addMetadataCollectionItemReferences();
		addMetadataCollectionItemReference();
		addMetadataMetadataCollectionItemNew();

		addMetadataRecordLink();
		addMetadataRecordLinkNew();
		addMetadataLinkedPath();
		addMetadataAttributes();
		addMetadataAttribute();
		addMetadataTextVariableWithId("attributeName");
		addMetadataTextVariableWithId("attributeValue");

		addMetadataRecordInfoNew();
		addMetadataRecordInfo();
		addMetadataRecordTypeNew();
		addMetadataRecordType();

		addMetadataTextVariableWithId(NAME_IN_DATA);
		addMetadataTextVariableWithId(TEXT_ID);
		addMetadataTextVariableWithId(DEF_TEXT_ID);
		addMetadataTextVariableWithId("ref");
		addMetadataAttributeReferences();

		addMetadataTextVariableWithIdAndRegEx(REPEAT_MIN, "(^[0-9\\_]{1,3}$)");
		addMetadataTextVariableWithIdAndRegEx("repeatMinKey", A_Z);
		addMetadataTextVariableWithIdAndRegEx(REPEAT_MAX, "(^[0-9|X\\_]{1,3}$)");
		addMetadataTextVariableWithIdAndRegEx("secret", TRUE_OR_FALSE);
		addMetadataTextVariableWithIdAndRegEx("secretKey", A_Z);
		addMetadataTextVariableWithIdAndRegEx("readOnly", TRUE_OR_FALSE);
		addMetadataTextVariableWithIdAndRegEx("readOnlyKey", A_Z);
		addMetadataChildReference();
		addMetadataChildReferences();
		addMetadataCollectionVariableMetadataType();
		addMetadataTextVariableWithId(TYPE);
		addMetadataMetadataGroupNew();

		addRecordTypeRecordType();
		addRecordTypeMetadata();
		addRecordTypeForAllMetadataGroups();
	}

	@Override
	public Authorizator getAuthorizator() {
		return authorizator;
	}

	@Override
	public RecordStorage getRecordStorage() {
		return recordStorage;
	}

	@Override
	public RecordIdGenerator getIdGenerator() {
		return idGenerator;
	}

	@Override
	public PermissionKeyCalculator getPermissionKeyCalculator() {
		return keyCalculator;
	}

	@Override
	public DataValidator getDataValidator() {
		return new DataValidatorImp(metadataStorage);
	}

	@Override
	public DataRecordLinkCollector getDataRecordLinkCollector() {
		return new DataRecordLinkCollectorImp(metadataStorage);
	}

	private DataGroup createRecordInfoWithRecordTypeAndRecordId(String recordType, String id) {
		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", id));
		recordInfo.addChild(DataAtomic.withNameInDataAndValue(TYPE, recordType));
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("createdBy", "userId"));
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("updatedBy", "userId"));
		return recordInfo;
	}

	private void addMetadataTextVariableWithId(String id) {
		addMetadataTextVariableWithIdAndRegEx(id, "(^[0-9A-Za-z:-_]{2,50}$)");
	}

	private void addMetadataTextVariableWithIdAndRegEx(String id, String regEx) {
		addMetadataTextVariableWithIdAndRegEx(id, id, regEx);
	}

	private void addMetadataTextVariableWithIdAndRegEx(String id, String nameInData, String regEx) {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue(TYPE, "textVariable");
		dataGroup.addChild(
				createRecordInfoWithRecordTypeAndRecordId(MetadataTypes.TEXTVARIABLE.type, id));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, nameInData));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, id + "Text"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, id + DEF_TEXT));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("regEx", regEx));
		recordStorage.create(MetadataTypes.TEXTVARIABLE.type, id, dataGroup, emptyLinkList);
	}

	private void addMetadataTextVariable() {
		DataGroup dataGroup = createDataGroupOfTypeGroup(METADATA_TEXT_VARIABLE);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, NAME_FOR_METADATA));

		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", RECORD_TYPE_TYPE_COLLECTION_VAR));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);
		addChildReferenceWithRef1to1(childReferences, RECORD_INFO);
		addChildReferenceWithRef1to1(childReferences, NAME_IN_DATA);
		addChildReferenceWithRef1to1(childReferences, TEXT_ID);
		addChildReferenceWithRef1to1(childReferences, DEF_TEXT_ID);
		addChildReferenceWithRef1to1(childReferences, EVERYTHING_REG_EXP);

		recordStorage.create(MetadataTypes.GROUP.type, METADATA_TEXT_VARIABLE, dataGroup,
				emptyLinkList);
	}

	private void addMetadataTextVariableNew() {
		DataGroup dataGroup = createDataGroupOfTypeGroup(METADATA_TEXT_VARIABLE_NEW);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, NAME_FOR_METADATA));

		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", RECORD_TYPE_TYPE_COLLECTION_VAR));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);
		addChildReferenceWithRef1to1(childReferences, RECORD_INFO_NEW);
		addChildReferenceWithRef1to1(childReferences, NAME_IN_DATA);
		addChildReferenceWithRef1to1(childReferences, TEXT_ID);
		addChildReferenceWithRef1to1(childReferences, DEF_TEXT_ID);
		addChildReferenceWithRef1to1(childReferences, EVERYTHING_REG_EXP);

		recordStorage.create(MetadataTypes.GROUP.type, METADATA_TEXT_VARIABLE_NEW, dataGroup,
				emptyLinkList);
	}

	private void addMetadataMetadataCollectionVariableNew() {
		DataGroup dataGroup = createDataGroupOfTypeGroup("metadataCollectionVariableNew");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, NAME_FOR_METADATA));

		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", RECORD_TYPE_TYPE_COLLECTION_VAR));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		// what does a metadata textVariable contain
		addChildReferenceWithRef1to1(childReferences, RECORD_INFO_NEW);
		addChildReferenceWithRef1to1(childReferences, NAME_IN_DATA);
		addChildReferenceWithRef1to1(childReferences, TEXT_ID);
		addChildReferenceWithRef1to1(childReferences, DEF_TEXT_ID);
		addChildReferenceWithRef1to1(childReferences, REF_COLLECTION_ID);

		recordStorage.create(METADATA_GROUP, "metadataCollectionVariableNew", dataGroup,
				emptyLinkList);
	}

	private void addMetadataMetadataCollectionVariableChildNew() {
		DataGroup dataGroup = createDataGroupOfTypeGroup("metadataCollectionVariableChildNew");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, NAME_FOR_METADATA));

		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", RECORD_TYPE_TYPE_COLLECTION_VAR));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		// what does a metadataCollectionVariableChild contain
		addChildReferenceWithRef1to1(childReferences, RECORD_INFO_NEW);
		addChildReferenceWithRef1to1(childReferences, NAME_IN_DATA);
		addChildReferenceWithRef1to1(childReferences, TEXT_ID);
		addChildReferenceWithRef1to1(childReferences, DEF_TEXT_ID);

		addChildReferenceWithRef1to1(childReferences, REF_COLLECTION_ID);
		addChildReferenceWithRef1to1(childReferences, REF_PARENT_ID);
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, FINAL_VALUE, "0", "1");

		recordStorage.create(METADATA_GROUP, "metadataCollectionVariableChildNew", dataGroup,
				emptyLinkList);
	}

	private void addMetadataMetadataItemCollectionNew() {
		DataGroup dataGroup = createDataGroupOfTypeGroup("metadataItemCollectionNew");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, NAME_FOR_METADATA));

		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", RECORD_TYPE_TYPE_COLLECTION_VAR));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		// what does a metadata textVariable contain
		addChildReferenceWithRef1to1(childReferences, RECORD_INFO_NEW);
		addChildReferenceWithRef1to1(childReferences, NAME_IN_DATA);
		addChildReferenceWithRef1to1(childReferences, TEXT_ID);
		addChildReferenceWithRef1to1(childReferences, DEF_TEXT_ID);
		addChildReferenceWithRef1to1(childReferences, COLLECTION_ITEM_REFERENCES);

		recordStorage.create(METADATA_GROUP, "metadataItemCollectionNew", dataGroup, emptyLinkList);
	}

	private void addMetadataCollectionItemReferences() {
		DataGroup dataGroup = createDataGroupOfTypeGroup(COLLECTION_ITEM_REFERENCES);
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(NAME_IN_DATA, COLLECTION_ITEM_REFERENCES));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, "ref", "1", "X");
		recordStorage.create(METADATA_GROUP, COLLECTION_ITEM_REFERENCES, dataGroup, emptyLinkList);

	}

	private void addMetadataCollectionItemReference() {
		DataGroup dataGroup = createDataGroupOfTypeGroup(COLLECTION_ITEM_REFERENCE);
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(NAME_IN_DATA, COLLECTION_ITEM_REFERENCE));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		addChildReferenceWithRef1to1(childReferences, "ref");
		recordStorage.create(METADATA_GROUP, COLLECTION_ITEM_REFERENCE, dataGroup, emptyLinkList);
	}

	private void addMetadataMetadataCollectionItemNew() {
		DataGroup dataGroup = createDataGroupOfTypeGroup("metadataCollectionItemNew");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, NAME_FOR_METADATA));

		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", RECORD_TYPE_TYPE_COLLECTION_VAR));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		// what does a metadata textVariable contain
		addChildReferenceWithRef1to1(childReferences, RECORD_INFO_NEW);
		addChildReferenceWithRef1to1(childReferences, NAME_IN_DATA);
		addChildReferenceWithRef1to1(childReferences, TEXT_ID);
		addChildReferenceWithRef1to1(childReferences, DEF_TEXT_ID);

		recordStorage.create(METADATA_GROUP, "metadataCollectionItemNew", dataGroup, emptyLinkList);
	}

	private void addMetadataRecordLinkNew() {
		DataGroup dataGroup = createDataGroupOfTypeGroup("metadataRecordLinkNew");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, NAME_FOR_METADATA));

		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", RECORD_TYPE_TYPE_COLLECTION_VAR));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);
		addChildReferenceWithRef1to1(childReferences, RECORD_INFO_NEW);
		addChildReferenceWithRef1to1(childReferences, NAME_IN_DATA);
		addChildReferenceWithRef1to1(childReferences, TEXT_ID);
		addChildReferenceWithRef1to1(childReferences, DEF_TEXT_ID);
		addChildReferenceWithRef1to1(childReferences, LINKED_RECORD_TYPE);
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, LINKED_PATH, "0", "1");

		recordStorage.create(MetadataTypes.GROUP.type, "metadataRecordLinkNew", dataGroup,
				emptyLinkList);
	}

	private void addMetadataRecordLink() {
		DataGroup dataGroup = createDataGroupOfTypeGroup("metadataRecordLink");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, NAME_FOR_METADATA));

		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", RECORD_TYPE_TYPE_COLLECTION_VAR));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);
		addChildReferenceWithRef1to1(childReferences, RECORD_INFO);
		addChildReferenceWithRef1to1(childReferences, NAME_IN_DATA);
		addChildReferenceWithRef1to1(childReferences, TEXT_ID);
		addChildReferenceWithRef1to1(childReferences, DEF_TEXT_ID);
		addChildReferenceWithRef1to1(childReferences, LINKED_RECORD_TYPE);
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, LINKED_PATH, "0", "1");

		recordStorage.create(MetadataTypes.GROUP.type, "metadataRecordLink", dataGroup,
				emptyLinkList);
	}

	private void addMetadataLinkedPath() {
		DataGroup dataGroup = createDataGroupOfTypeGroup(LINKED_PATH);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, LINKED_PATH));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);
		addChildReferenceWithRef1to1(childReferences, NAME_IN_DATA);
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, ATTRIBUTES, "0", "1");
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, LINKED_PATH, "0", "1");

		recordStorage.create(MetadataTypes.GROUP.type, LINKED_PATH, dataGroup, emptyLinkList);
	}

	private void addMetadataAttributes() {
		DataGroup dataGroup = createDataGroupOfTypeGroup(ATTRIBUTES);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, ATTRIBUTES));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, ATTRIBUTE, "1", "X");

		recordStorage.create(MetadataTypes.GROUP.type, ATTRIBUTES, dataGroup, emptyLinkList);
	}

	private void addMetadataAttribute() {
		DataGroup dataGroup = createDataGroupOfTypeGroup(ATTRIBUTE);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, ATTRIBUTE));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);
		addChildReferenceWithRef1to1(childReferences, "attributeName");
		addChildReferenceWithRef1to1(childReferences, "attributeValue");

		recordStorage.create(MetadataTypes.GROUP.type, ATTRIBUTE, dataGroup, emptyLinkList);
	}

	private DataGroup createDataGroupOfTypeGroup(final String name) {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue(TYPE, GROUP);

		dataGroup.addChild(
				createRecordInfoWithRecordTypeAndRecordId(MetadataTypes.GROUP.type, name));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, name + "Text"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, name + DEF_TEXT));
		return dataGroup;
	}

	private void addMetadataCollectionVariableMetadataType() {
		addMetadataTypeCollectionVariable();
		addMetadataTypeItemCollection();
		addMetadataTypeCollectionItems();
	}

	private void addMetadataTypeCollectionVariable() {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue(TYPE, "collectionVariable");
		dataGroup.addChild(createRecordInfoWithRecordTypeAndRecordId(
				MetadataTypes.COLLECTIONVARIABLE.type, RECORD_TYPE_TYPE_COLLECTION_VAR));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, TYPE));
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(TEXT_ID, "recordTypeTypeCollectionVarTextId"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID,
				"recordTypeTypeCollectionVarDeffTextId"));
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(REF_COLLECTION_ID, RECORD_TYPE_TYPE_COLLECTION));

		recordStorage.create(MetadataTypes.COLLECTIONVARIABLE.type, RECORD_TYPE_TYPE_COLLECTION_VAR,
				dataGroup, emptyLinkList);
	}

	private void addMetadataTypeItemCollection() {
		// collection
		DataGroup dataGroup2 = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup2.addAttributeByIdWithValue(TYPE, "itemCollection");
		dataGroup2.addChild(createRecordInfoWithRecordTypeAndRecordId(
				MetadataTypes.ITEMCOLLECTION.type, RECORD_TYPE_TYPE_COLLECTION));

		dataGroup2.addChild(
				DataAtomic.withNameInDataAndValue(NAME_IN_DATA, RECORD_TYPE_TYPE_COLLECTION));
		dataGroup2.addChild(
				DataAtomic.withNameInDataAndValue(TEXT_ID, "recordTypeTypeCollectionTextId"));
		dataGroup2.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID,
				"recordTypeTypeCollectionDeffTextId"));

		DataGroup collectionItemReferences = DataGroup.withNameInData(COLLECTION_ITEM_REFERENCES);
		dataGroup2.addChild(collectionItemReferences);
		collectionItemReferences
				.addChild(DataAtomic.withNameInDataAndValue("ref", "metadataTypeGroupItem"));
		collectionItemReferences
				.addChild(DataAtomic.withNameInDataAndValue("ref", "metadataTypeTextVariableItem"));
		collectionItemReferences
				.addChild(DataAtomic.withNameInDataAndValue("ref", "metadataTypeRecordLinkItem"));
		collectionItemReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", "metadataTypeItemCollectionItem"));
		collectionItemReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", "metadataTypeCollectionItemItem"));
		collectionItemReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", "metadataTypeCollectionVariableItem"));
		collectionItemReferences.addChild(DataAtomic.withNameInDataAndValue("ref",
				"metadataTypeCollectionVariableChildItem"));

		recordStorage.create(MetadataTypes.ITEMCOLLECTION.type, RECORD_TYPE_TYPE_COLLECTION,
				dataGroup2, emptyLinkList);
	}

	private void addMetadataTypeCollectionItems() {
		createMetadataTypeCollectionItem("group");
		createMetadataTypeCollectionItem("textVariable");
		createMetadataTypeCollectionItem("recordLink");
		createMetadataTypeCollectionItem("itemCollection");
		createMetadataTypeCollectionItem("collectionItem");
		createMetadataTypeCollectionItem("collectionVariable");
		createMetadataTypeCollectionItem("collectionVariableChild");
	}

	private void createMetadataTypeCollectionItem(String id) {
		String idWithCapitalFirst = id.substring(0, 1).toUpperCase() + id.substring(1);
		DataGroup dataGroup3 = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup3.addAttributeByIdWithValue(TYPE, COLLECTION_ITEM);
		dataGroup3.addChild(createRecordInfoWithRecordTypeAndRecordId(
				MetadataTypes.COLLECTIONITEM.type, "metadataType" + idWithCapitalFirst + "Item"));

		dataGroup3.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, id));
		dataGroup3.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID,
				"recordTypeType" + idWithCapitalFirst + "TextId"));
		dataGroup3.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID,
				"recordTypeType" + idWithCapitalFirst + "DefTextId"));
		recordStorage.create(MetadataTypes.COLLECTIONITEM.type,
				"metadataType" + idWithCapitalFirst + "Item", dataGroup3, emptyLinkList);
	}

	private void addMetadataRecordInfoNew() {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue(TYPE, GROUP);
		dataGroup.addChild(createRecordInfoWithRecordTypeAndRecordId(MetadataTypes.GROUP.type,
				RECORD_INFO_NEW));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, RECORD_INFO));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, "recordInfoText"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, "recordInfoDefText"));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		addChildReferenceWithRef1to1(childReferences, ID);

		recordStorage.create(MetadataTypes.GROUP.type, RECORD_INFO_NEW, dataGroup, emptyLinkList);
	}

	private void addChildReferenceWithRef1to1(DataGroup childReferences, String ref) {
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, ref, "1", "1");
	}

	private void addChildReferenceWithRefRepeatMinRepeatMax(DataGroup childReferences, String ref,
			String repeatMin, String repeatMax) {
		DataGroup childReference = DataGroup.withNameInData(CHILD_REFERENCE);
		childReference.setRepeatId(String.valueOf(childReferences.getChildren().size() + 1));
		childReference.addChild(DataAtomic.withNameInDataAndValue("ref", ref));
		childReference.addChild(DataAtomic.withNameInDataAndValue(REPEAT_MIN, repeatMin));
		childReference.addChild(DataAtomic.withNameInDataAndValue(REPEAT_MAX, repeatMax));
		childReferences.addChild(childReference);
	}

	private void addMetadataRecordInfo() {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue(TYPE, GROUP);
		dataGroup.addChild(
				createRecordInfoWithRecordTypeAndRecordId(MetadataTypes.GROUP.type, RECORD_INFO));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, RECORD_INFO));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, "recordInfoText"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, "recordInfoDefText"));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		addChildReferenceWithRef1to1(childReferences, ID);
		addChildReferenceWithRef1to1(childReferences, TYPE);
		addChildReferenceWithRef1to1(childReferences, CREATED_BY);

		recordStorage.create(MetadataTypes.GROUP.type, RECORD_INFO, dataGroup, emptyLinkList);
	}

	private void addMetadataRecordTypeNew() {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue(TYPE, GROUP);
		dataGroup.addChild(createRecordInfoWithRecordTypeAndRecordId(MetadataTypes.GROUP.type,
				RECORD_TYPE_NEW));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, RECORD_TYPE));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, "recordTypeText"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, "recordTypeDefText"));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		addChildReferenceWithRef1to1(childReferences, METADATA_ID);
		addChildReferenceWithRef1to1(childReferences, NAME_FOR_ABSTRACT);
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, PARENT_ID, "0", "1");
		addChildReferenceWithRef1to1(childReferences, RECORD_INFO_NEW);
		addChildReferenceWithRef1to1(childReferences, PRESENTATION_VIEW_ID);
		addChildReferenceWithRef1to1(childReferences, PRESENTATION_FORM_ID);
		addChildReferenceWithRef1to1(childReferences, NEW_METADATA_ID);
		addChildReferenceWithRef1to1(childReferences, NEW_PRESENTATION_FORM_ID);
		addChildReferenceWithRef1to1(childReferences, LIST_PRESENTATION_VIEW_ID);
		addChildReferenceWithRef1to1(childReferences, SEARCH_METADATA_ID);
		addChildReferenceWithRef1to1(childReferences, SEARCH_PRESENTATION_FORM_ID);
		addChildReferenceWithRef1to1(childReferences, USER_SUPPLIED_ID);
		addChildReferenceWithRef1to1(childReferences, PERMISSION_KEY);
		addChildReferenceWithRef1to1(childReferences, SELF_PRESENTATION_VIEW_ID);

		recordStorage.create(MetadataTypes.GROUP.type, RECORD_TYPE_NEW, dataGroup, emptyLinkList);
	}

	private void addMetadataRecordType() {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue(TYPE, GROUP);
		dataGroup.addChild(
				createRecordInfoWithRecordTypeAndRecordId(MetadataTypes.GROUP.type, RECORD_TYPE));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, RECORD_TYPE));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, "recordTypeText"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, "recordTypeDefText"));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		addChildReferenceWithRef1to1(childReferences, METADATA_ID);
		addChildReferenceWithRef1to1(childReferences, NAME_FOR_ABSTRACT);
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, PARENT_ID, "0", "1");
		addChildReferenceWithRef1to1(childReferences, RECORD_INFO);
		addChildReferenceWithRef1to1(childReferences, PRESENTATION_VIEW_ID);
		addChildReferenceWithRef1to1(childReferences, PRESENTATION_FORM_ID);
		addChildReferenceWithRef1to1(childReferences, NEW_METADATA_ID);
		addChildReferenceWithRef1to1(childReferences, NEW_PRESENTATION_FORM_ID);
		addChildReferenceWithRef1to1(childReferences, LIST_PRESENTATION_VIEW_ID);
		addChildReferenceWithRef1to1(childReferences, SEARCH_METADATA_ID);
		addChildReferenceWithRef1to1(childReferences, SEARCH_PRESENTATION_FORM_ID);
		addChildReferenceWithRef1to1(childReferences, USER_SUPPLIED_ID);
		addChildReferenceWithRef1to1(childReferences, PERMISSION_KEY);
		addChildReferenceWithRef1to1(childReferences, SELF_PRESENTATION_VIEW_ID);

		recordStorage.create(MetadataTypes.GROUP.type, RECORD_TYPE, dataGroup, emptyLinkList);
	}

	private void addMetadataAttributeReferences() {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue(TYPE, GROUP);
		dataGroup.addChild(createRecordInfoWithRecordTypeAndRecordId(MetadataTypes.GROUP.type,
				ATTRIBUTE_REFERENCES));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, ATTRIBUTE_REFERENCES));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, "attributeReferencesText"));
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, "attributeReferencesDefText"));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, "ref", "1", "X");

		recordStorage.create(MetadataTypes.GROUP.type, ATTRIBUTE_REFERENCES, dataGroup,
				emptyLinkList);
	}

	private void addMetadataChildReferences() {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue(TYPE, GROUP);
		dataGroup.addChild(createRecordInfoWithRecordTypeAndRecordId(MetadataTypes.GROUP.type,
				CHILD_REFERENCES));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, CHILD_REFERENCES));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, "childReferencesText"));
		dataGroup
				.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, "childReferencesDefText"));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, CHILD_REFERENCE, "1", "X");

		recordStorage.create(MetadataTypes.GROUP.type, CHILD_REFERENCES, dataGroup, emptyLinkList);
	}

	private void addMetadataChildReference() {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue(TYPE, GROUP);
		dataGroup.addChild(createRecordInfoWithRecordTypeAndRecordId(MetadataTypes.GROUP.type,
				CHILD_REFERENCE));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, CHILD_REFERENCE));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, "childReferenceText"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, "childReferenceDefText"));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		addChildReferenceWithRef1to1(childReferences, "ref");
		addChildReferenceWithRef1to1(childReferences, REPEAT_MIN);
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, "repeatMinKey", "0", "1");
		addChildReferenceWithRef1to1(childReferences, REPEAT_MAX);
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, "secret", "0", "1");
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, "secretKey", "0", "1");
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, "readOnly", "0", "1");
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, "readOnlyKey", "0", "1");

		recordStorage.create(MetadataTypes.GROUP.type, CHILD_REFERENCE, dataGroup, emptyLinkList);
	}

	private void addMetadataMetadataGroupNew() {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue(TYPE, GROUP);
		dataGroup.addChild(createRecordInfoWithRecordTypeAndRecordId(MetadataTypes.GROUP.type,
				"metadataGroupNew"));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, NAME_FOR_METADATA));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, "metadataText"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, "metadataDefText"));

		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", RECORD_TYPE_TYPE_COLLECTION_VAR));

		DataGroup childReferences = DataGroup.withNameInData(CHILD_REFERENCES);
		dataGroup.addChild(childReferences);

		// what does a metadata group contain
		addChildReferenceWithRef1to1(childReferences, RECORD_INFO_NEW);
		addChildReferenceWithRef1to1(childReferences, NAME_IN_DATA);
		addChildReferenceWithRef1to1(childReferences, TEXT_ID);
		addChildReferenceWithRef1to1(childReferences, DEF_TEXT_ID);
		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, REF_PARENT_ID, "0", "1");

		addChildReferenceWithRefRepeatMinRepeatMax(childReferences, ATTRIBUTE_REFERENCES, "0", "1");
		// childReferences
		addChildReferenceWithRef1to1(childReferences, CHILD_REFERENCES);

		recordStorage.create(MetadataTypes.GROUP.type, "metadataGroupNew", dataGroup,
				emptyLinkList);
	}

	private DataGroup createRecordTypeWithId(String id) {
		String idWithCapitalFirst = id.substring(0, 1).toUpperCase() + id.substring(1);

		DataGroup dataGroup = DataGroup.withNameInData(RECORD_TYPE);
		dataGroup.addChild(createRecordInfoWithRecordTypeAndRecordId(RECORD_TYPE, id));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(METADATA_ID, id));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(PRESENTATION_VIEW_ID,
				"pg" + idWithCapitalFirst + "View"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(PRESENTATION_FORM_ID,
				"pg" + idWithCapitalFirst + "Form"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NEW_METADATA_ID, id + "New"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NEW_PRESENTATION_FORM_ID,
				"pg" + idWithCapitalFirst + "FormNew"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(LIST_PRESENTATION_VIEW_ID,
				"pg" + idWithCapitalFirst + "List"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(SEARCH_METADATA_ID, id + "Search"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(SEARCH_PRESENTATION_FORM_ID,
				"pg" + idWithCapitalFirst + "SearchForm"));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(USER_SUPPLIED_ID, "true"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(PERMISSION_KEY,
				"RECORDTYPE_" + id.toUpperCase()));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(SELF_PRESENTATION_VIEW_ID,
				"pg" + idWithCapitalFirst + "Self"));
		return dataGroup;
	}

	private void addRecordTypeRecordType() {
		DataGroup dataGroup = createRecordTypeWithId(RECORD_TYPE);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_FOR_ABSTRACT, "false"));
		recordStorage.create(RECORD_TYPE, RECORD_TYPE, dataGroup, emptyLinkList);
	}

	private void addRecordTypeMetadata() {
		DataGroup dataGroup = createRecordTypeWithId(NAME_FOR_METADATA);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_FOR_ABSTRACT, "true"));
		recordStorage.create(RECORD_TYPE, METADATA, dataGroup, emptyLinkList);
	}

	private void addRecordTypeForAllMetadataGroups() {
		for (MetadataTypes metadataType : MetadataTypes.values()) {
			String type = metadataType.type;
			DataGroup dataGroup = createRecordTypeWithId(type);
			dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_FOR_ABSTRACT, "false"));
			dataGroup.addChild(DataAtomic.withNameInDataAndValue(PARENT_ID, METADATA));
			recordStorage.create(RECORD_TYPE, type, dataGroup, emptyLinkList);
		}
	}
}