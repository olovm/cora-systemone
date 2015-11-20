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

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.metadata.MetadataTypes;
import se.uu.ub.cora.spider.record.storage.RecordStorage;

public class MetadataForTexts {
	private static final String TEXT_PART = "textPart";
	private static final String TEXT_SYSTEM_ONE_GROUP = "textSystemOneGroup";
	private static final String ID_TEXT_TEXT_VAR = "idTextTextVar";
	private static final String TEXT_DEFAULT_ALTERNATIVE_GROUP = "textDefaultAlternativeGroup";
	private static final String RECORD_INFO_TEXT_GROUP = "recordInfoTextGroup";
	private static final String TEXT_PART_EN_GROUP = "textPartEnGroup";
	private static final String TEXT_PART_SV_GROUP = "textPartSvGroup";
	private static final String TEXT_PART_ALTERNATIVE_GROUP = "textPartAlternativeGroup";
	private static final String TEXT_PART_DEFAULT_GROUP = "textPartDefaultGroup";
	private static final String SYSTEM_LANGUAGE_COLLECTION_VAR = "systemLanguageCollectionVar";
	private static final String TEXT_PART_TYPE_COLLECTION_VAR = "textPartTypeCollectionVar";
	private static final String TEXT_PART_GROUP = "textPartGroup";
	private static final String TEXT_PART_TYPE_COLLECTION = "textPartTypeCollection";
	private static final String TEXT_TEXT_VAR = "textTextVar";
	private static final String COLLECTION_VAR = "CollectionVar";
	private static final String ALTERNATIVE = "alternative";
	private static final String SYSTEM_LANGUAGES_COLLECTION = "systemLanguagesCollection";
	private static final String COLLECTION = "Collection";
	private static final String DEFAULT = "default";
	private static final String REF_PARENT_ID = "refParentId";
	private static final String FINAL_VALUE = "finalValue";
	private static final String REF_COLLECTION_ID = "refCollectionId";
	private static final String COLLECTION_ITEM_REFERENCES = "collectionItemReferences";
	private static final String TYPE = "type";
	private static final String DEF_TEXT = "DefText";
	private static final String NAME_FOR_ABSTRACT = "abstract";
	private static final String PARENT_ID = "parentId";
	private static final String DEF_TEXT_ID = "defTextId";
	private static final String TEXT_ID = "textId";
	private static final String ATTRIBUTE_REFERENCES = "attributeReferences";
	private static final String NAME_IN_DATA = "nameInData";
	private static final String NAME_FOR_METADATA = "metadata";
	private static final String RECORD_TYPE = "recordType";
	private DataGroup emptyLinkList = DataGroup.withNameInData("collectedDataLinks");
	private RecordStorage recordStorage;
	private MetadataCreator metadataCreator;

	public MetadataForTexts(RecordStorage recordStorage, MetadataCreator metadataCreator) {
		this.recordStorage = recordStorage;
		this.metadataCreator = metadataCreator;
	}

	public void createMetadataForTexts() {
		createLanguageCollectionItems();
		createSystemLanguageItemCollection();
		createSystemLanguageVar();
		createSystemLanguageSvVar();
		createSystemLanguageEnVar();
		createTextVar();
		createTextPartTypeCollectionItems();
		createTextPartTypeItemCollection();
		createTextPartTypeVar();
		createTextPartGroup();
		createTextPartTypeDefaultVar();
		createTextPartTypeAlternativeVar();
		createTextPartDefaultGroup();
		createTextPartAlternativeGroup();
		createTextPartSvGroup();
		createTextPartEnGroup();
		createRecordInfoTextGroup();
		createTextGroup();
		createTextDefaultAlternativeGroup();
		createTextSystemOneGroup();
		addRecordTypeText();
		addRecordTypeTextSystemOne();
	}

	private void createLanguageCollectionItems() {
		metadataCreator.createCollectionItem("sv", "sv");
		metadataCreator.createCollectionItem("en", "en");
		metadataCreator.createCollectionItem("es", "es");
		metadataCreator.createCollectionItem("no", "no");
	}

	private void createSystemLanguageItemCollection() {
		String id = "systemLanguages";
		DataGroup dataGroup = createDataGroupForItemCollectionWithId(id);
		// note, only sv and en are currently used in the system
		addCollectionItemReferenceByCollectionItemId(dataGroup, "svItem");
		addCollectionItemReferenceByCollectionItemId(dataGroup, "enItem");
		recordStorage.create(MetadataTypes.ITEMCOLLECTION.type, id + COLLECTION, dataGroup,
				emptyLinkList);
	}

	private DataGroup createDataGroupForItemCollectionWithId(String id) {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue(TYPE, "itemCollection");
		dataGroup.addChild(metadataCreator.createRecordInfoWithRecordTypeAndRecordId(
				MetadataTypes.ITEMCOLLECTION.type, id + COLLECTION));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, id + COLLECTION));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, id + "CollectionTextId"));
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, id + "CollectionDefTextId"));

		DataGroup collectionItemReferences = DataGroup.withNameInData(COLLECTION_ITEM_REFERENCES);
		dataGroup.addChild(collectionItemReferences);
		return dataGroup;
	}

	private void addCollectionItemReferenceByCollectionItemId(DataGroup dataGroup,
			String collectionItemId) {
		DataGroup collectionItemReferences = dataGroup
				.getFirstGroupWithNameInData(COLLECTION_ITEM_REFERENCES);
		collectionItemReferences
				.addChild(DataAtomic.withNameInDataAndValue("ref", collectionItemId));
	}

	private void createSystemLanguageVar() {
		String collectionId = "systemLanguages";
		DataGroup dataGroup = createCollectionVarDataGroupWithIdAndRefCollectionIdAndNameInData(
				collectionId, SYSTEM_LANGUAGES_COLLECTION, "lang");
		recordStorage.create(MetadataTypes.COLLECTIONVARIABLE.type, collectionId + COLLECTION_VAR,
				dataGroup, emptyLinkList);
	}

	private DataGroup createCollectionVarDataGroupWithIdAndRefCollectionIdAndNameInData(
			String collectionId, String refCollectionId, String nameInData) {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue(TYPE, "collectionVariable");
		dataGroup.addChild(metadataCreator.createRecordInfoWithRecordTypeAndRecordId(
				MetadataTypes.COLLECTIONVARIABLE.type, collectionId + COLLECTION_VAR));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, nameInData));
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(TEXT_ID, collectionId + "CollectionVarTextId"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID,
				collectionId + "CollectionVarDefTextId"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(REF_COLLECTION_ID, refCollectionId));
		return dataGroup;
	}

	private void createSystemLanguageSvVar() {
		String collectionVarId = "systemLanguageSv";
		DataGroup dataGroup = createCollectionVarDataGroupWithIdAndRefCollectionIdAndNameInData(
				collectionVarId, SYSTEM_LANGUAGES_COLLECTION, "lang");

		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(REF_PARENT_ID, "systemLanguagesCollectionVar"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(FINAL_VALUE, "sv"));

		recordStorage.create(MetadataTypes.COLLECTIONVARIABLE.type,
				collectionVarId + COLLECTION_VAR, dataGroup, emptyLinkList);
	}

	private void createSystemLanguageEnVar() {
		String collectionVarId = "systemLanguageEn";
		DataGroup dataGroup = createCollectionVarDataGroupWithIdAndRefCollectionIdAndNameInData(
				collectionVarId, SYSTEM_LANGUAGES_COLLECTION, "lang");

		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(REF_PARENT_ID, "systemLanguagesCollectionVar"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(FINAL_VALUE, "en"));

		recordStorage.create(MetadataTypes.COLLECTIONVARIABLE.type,
				collectionVarId + COLLECTION_VAR, dataGroup, emptyLinkList);
	}

	private void createTextVar() {
		metadataCreator.addMetadataTextVariableWithIdAndNameInDataAndRegEx(TEXT_TEXT_VAR, "text",
				"(.*)");
	}

	private void createTextPartTypeCollectionItems() {
		metadataCreator.createCollectionItem(DEFAULT, DEFAULT);
		metadataCreator.createCollectionItem(ALTERNATIVE, ALTERNATIVE);
	}

	private void createTextPartTypeItemCollection() {
		String id = "textPartType";
		DataGroup dataGroup = createDataGroupForItemCollectionWithId(id);
		addCollectionItemReferenceByCollectionItemId(dataGroup, DEFAULT);
		addCollectionItemReferenceByCollectionItemId(dataGroup, ALTERNATIVE);
		recordStorage.create(MetadataTypes.ITEMCOLLECTION.type, id + COLLECTION, dataGroup,
				emptyLinkList);
	}

	private void createTextPartTypeVar() {
		String collectionId = "textPartType";
		DataGroup dataGroup = createCollectionVarDataGroupWithIdAndRefCollectionIdAndNameInData(
				collectionId, TEXT_PART_TYPE_COLLECTION, "type");
		recordStorage.create(MetadataTypes.COLLECTIONVARIABLE.type, collectionId + COLLECTION_VAR,
				dataGroup, emptyLinkList);
	}

	private void createTextPartGroup() {
		DataGroup dataGroup = metadataCreator
				.createDataGroupForMetadataWithRecordId(TEXT_PART_GROUP);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, TEXT_PART));
		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences
				.addChild(DataAtomic.withNameInDataAndValue("ref", TEXT_PART_TYPE_COLLECTION_VAR));
		attributeReferences
				.addChild(DataAtomic.withNameInDataAndValue("ref", SYSTEM_LANGUAGE_COLLECTION_VAR));

		metadataCreator.addChildReferenceWithRef1to1(dataGroup, TEXT_TEXT_VAR);

		recordStorage.create(MetadataTypes.GROUP.type, TEXT_PART_GROUP, dataGroup, emptyLinkList);
	}

	private void createTextPartTypeDefaultVar() {
		String collectionVarId = "textPartTypeDefault";
		DataGroup dataGroup = createCollectionVarDataGroupWithIdAndRefCollectionIdAndNameInData(
				collectionVarId, TEXT_PART_TYPE_COLLECTION, "type");

		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(REF_PARENT_ID, TEXT_PART_TYPE_COLLECTION_VAR));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(FINAL_VALUE, DEFAULT));

		recordStorage.create(MetadataTypes.COLLECTIONVARIABLE.type,
				collectionVarId + COLLECTION_VAR, dataGroup, emptyLinkList);
	}

	private void createTextPartTypeAlternativeVar() {
		String collectionVarId = "textPartTypeAlternative";
		DataGroup dataGroup = createCollectionVarDataGroupWithIdAndRefCollectionIdAndNameInData(
				collectionVarId, TEXT_PART_TYPE_COLLECTION, "type");

		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(REF_PARENT_ID, TEXT_PART_TYPE_COLLECTION_VAR));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(FINAL_VALUE, ALTERNATIVE));

		recordStorage.create(MetadataTypes.COLLECTIONVARIABLE.type,
				collectionVarId + COLLECTION_VAR, dataGroup, emptyLinkList);
	}

	private void createTextPartDefaultGroup() {
		DataGroup dataGroup = metadataCreator
				.createDataGroupForMetadataWithRecordId(TEXT_PART_DEFAULT_GROUP);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, TEXT_PART));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(REF_PARENT_ID, TEXT_PART_GROUP));

		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", "textPartTypeDefaultCollectionVar"));
		attributeReferences
				.addChild(DataAtomic.withNameInDataAndValue("ref", SYSTEM_LANGUAGE_COLLECTION_VAR));

		metadataCreator.addChildReferenceWithRef1to1(dataGroup, TEXT_TEXT_VAR);

		recordStorage.create(MetadataTypes.GROUP.type, TEXT_PART_DEFAULT_GROUP, dataGroup,
				emptyLinkList);
	}

	private void createTextPartAlternativeGroup() {
		DataGroup dataGroup = metadataCreator
				.createDataGroupForMetadataWithRecordId(TEXT_PART_ALTERNATIVE_GROUP);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, TEXT_PART));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(REF_PARENT_ID, TEXT_PART_GROUP));

		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", "textPartTypeAlternativeCollectionVar"));
		attributeReferences
				.addChild(DataAtomic.withNameInDataAndValue("ref", SYSTEM_LANGUAGE_COLLECTION_VAR));

		metadataCreator.addChildReferenceWithRef1to1(dataGroup, TEXT_TEXT_VAR);

		recordStorage.create(MetadataTypes.GROUP.type, TEXT_PART_ALTERNATIVE_GROUP, dataGroup,
				emptyLinkList);
	}

	private void createTextPartSvGroup() {
		DataGroup dataGroup = metadataCreator
				.createDataGroupForMetadataWithRecordId(TEXT_PART_SV_GROUP);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, TEXT_PART));
		dataGroup
				.addChild(DataAtomic.withNameInDataAndValue(REF_PARENT_ID, TEXT_PART_DEFAULT_GROUP));

		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", "textPartTypeDefaultCollectionVar"));
		attributeReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", "systemLanguageSvCollectionVar"));

		metadataCreator.addChildReferenceWithRef1to1(dataGroup, TEXT_TEXT_VAR);

		recordStorage.create(MetadataTypes.GROUP.type, TEXT_PART_SV_GROUP, dataGroup, emptyLinkList);
	}

	private void createTextPartEnGroup() {
		DataGroup dataGroup = metadataCreator
				.createDataGroupForMetadataWithRecordId(TEXT_PART_EN_GROUP);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, TEXT_PART));
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(REF_PARENT_ID, TEXT_PART_ALTERNATIVE_GROUP));

		DataGroup attributeReferences = DataGroup.withNameInData(ATTRIBUTE_REFERENCES);
		dataGroup.addChild(attributeReferences);
		attributeReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", "textPartTypeAlternativeCollectionVar"));
		attributeReferences.addChild(
				DataAtomic.withNameInDataAndValue("ref", "systemLanguageEnCollectionVar"));

		metadataCreator.addChildReferenceWithRef1to1(dataGroup, TEXT_TEXT_VAR);

		recordStorage.create(MetadataTypes.GROUP.type, TEXT_PART_EN_GROUP, dataGroup, emptyLinkList);
	}

	private void addMetadataTextVariableChildWithIdAndNameInDataAndRegExAndRefParentId(String id,
			String nameInData, String regEx, String refParentId) {
		DataGroup dataGroup = DataGroup.withNameInData(NAME_FOR_METADATA);
		dataGroup.addAttributeByIdWithValue(TYPE, "textVariable");
		dataGroup.addChild(metadataCreator
				.createRecordInfoWithRecordTypeAndRecordId(MetadataTypes.TEXTVARIABLE.type, id));

		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, nameInData));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(TEXT_ID, id + "Text"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(DEF_TEXT_ID, id + DEF_TEXT));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue("regEx", regEx));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(REF_PARENT_ID, refParentId));
		recordStorage.create(MetadataTypes.TEXTVARIABLE.type, id, dataGroup, emptyLinkList);
	}

	private void createRecordInfoTextGroup() {
		addMetadataTextVariableChildWithIdAndNameInDataAndRegExAndRefParentId(ID_TEXT_TEXT_VAR, "id",
				"(.*Text$)", "idTextVar");
		metadataCreator.addMetadataRecordInfoNewWithRecordInfoIdAndRefMetadataIdUsedAsId(
				"recordInfoNewTextGroup", ID_TEXT_TEXT_VAR);
		metadataCreator.addMetadataRecordInfoWithRecordInfoIdAndRefMetadataIdUsedAsId(
				RECORD_INFO_TEXT_GROUP, ID_TEXT_TEXT_VAR);
	}

	private void createTextGroup() {
		DataGroup dataGroup = metadataCreator.createDataGroupForMetadataWithRecordId("textGroup");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, "text"));

		metadataCreator.addChildReferenceWithRef1to1(dataGroup, RECORD_INFO_TEXT_GROUP);
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, TEXT_PART_GROUP, "1",
				"X");

		recordStorage.create(MetadataTypes.GROUP.type, "textGroup", dataGroup, emptyLinkList);

	}

	private void createTextDefaultAlternativeGroup() {
		DataGroup dataGroup = metadataCreator
				.createDataGroupForMetadataWithRecordId(TEXT_DEFAULT_ALTERNATIVE_GROUP);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, "text"));
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, RECORD_INFO_TEXT_GROUP);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(REF_PARENT_ID, "text"));

		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup,
				TEXT_PART_DEFAULT_GROUP, "1", "1");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup,
				TEXT_PART_ALTERNATIVE_GROUP, "0", "X");

		recordStorage.create(MetadataTypes.GROUP.type, TEXT_DEFAULT_ALTERNATIVE_GROUP, dataGroup,
				emptyLinkList);
	}

	private void createTextSystemOneGroup() {
		DataGroup dataGroup = metadataCreator
				.createDataGroupForMetadataWithRecordId(TEXT_SYSTEM_ONE_GROUP);
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, "text"));
		metadataCreator.addChildReferenceWithRef1to1(dataGroup, RECORD_INFO_TEXT_GROUP);
		dataGroup.addChild(
				DataAtomic.withNameInDataAndValue(REF_PARENT_ID, TEXT_DEFAULT_ALTERNATIVE_GROUP));

		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, TEXT_PART_SV_GROUP,
				"1", "1");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup, TEXT_PART_EN_GROUP,
				"0", "1");

		recordStorage.create(MetadataTypes.GROUP.type, TEXT_SYSTEM_ONE_GROUP, dataGroup,
				emptyLinkList);

		DataGroup dataGroup2 = metadataCreator
				.createDataGroupForMetadataWithRecordId("textSystemOneNewGroup");
		dataGroup2.addChild(DataAtomic.withNameInDataAndValue(NAME_IN_DATA, "text"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(REF_PARENT_ID, TEXT_SYSTEM_ONE_GROUP));

		metadataCreator.addChildReferenceWithRef1to1(dataGroup2, "recordInfoNewTextGroup");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup2, TEXT_PART_SV_GROUP,
				"1", "1");
		metadataCreator.addChildReferenceWithRefRepeatMinRepeatMax(dataGroup2, TEXT_PART_EN_GROUP,
				"0", "1");

		recordStorage.create(MetadataTypes.GROUP.type, "textSystemOneNewGroup", dataGroup2,
				emptyLinkList);
	}

	private void addRecordTypeText() {
		DataGroup dataGroup = metadataCreator.createRecordTypeWithId("text");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_FOR_ABSTRACT, "true"));
		recordStorage.create(RECORD_TYPE, "text", dataGroup, emptyLinkList);
	}

	private void addRecordTypeTextSystemOne() {
		DataGroup dataGroup = metadataCreator.createRecordTypeWithId("textSystemOne");
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(NAME_FOR_ABSTRACT, "false"));
		dataGroup.addChild(DataAtomic.withNameInDataAndValue(PARENT_ID, "text"));
		recordStorage.create(RECORD_TYPE, "textSystemOne", dataGroup, emptyLinkList);
	}
}
