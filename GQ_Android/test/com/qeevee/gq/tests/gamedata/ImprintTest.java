package com.qeevee.gq.tests.gamedata;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.qeevee.gq.base.Imprint;
import com.qeevee.gq.tests.robolectric.GQTestRunner;
import com.qeevee.gq.tests.util.TestUtils;


@RunWith(GQTestRunner.class)
public class ImprintTest {

	private Integer[] fieldValuesDefaultResIDs;
	private Integer[] fieldLabelsDefaultResIDs;
	private Imprint defaultImprint = new Imprint(null);
	private HashMap<String, String> fieldNames;
	private HashMap<String, String> fieldValues;

	// === TESTS FOLLOW =============================================

	@Test
	public void checkStatiFieldDefinitions() {
		// GIVEN:
		// nothing

		// WHEN:
		// nothing

		// THEN:
		assertEquals(Imprint.FIELD_KEYS.length, fieldValuesDefaultResIDs.length);
		assertEquals(Imprint.FIELD_KEYS.length, fieldLabelsDefaultResIDs.length);
		assertEquals(Imprint.FIELD_KEYS.length, fieldNames.size());
		assertEquals(Imprint.FIELD_KEYS.length, fieldValues.size());
	}

	// === HELPERS FOLLOW =============================================

	@SuppressWarnings("unchecked")
	@Before
	public void getPrivateFields() {
		this.fieldValuesDefaultResIDs = (Integer[]) TestUtils
				.getStaticFieldValue(Imprint.class,
						"FIELD_VALUE_DEFAULT_RES_IDs");
		this.fieldLabelsDefaultResIDs = (Integer[]) TestUtils
				.getStaticFieldValue(Imprint.class,
						"FIELD_LABEL_DEFAULT_RES_IDs");
		this.fieldNames = (HashMap<String, String>) TestUtils.getFieldValue(
				this.defaultImprint, "fieldNames");
		this.fieldValues = (HashMap<String, String>) TestUtils.getFieldValue(
				this.defaultImprint, "fieldValues");
	}
}
