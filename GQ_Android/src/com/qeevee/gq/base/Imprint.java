package com.qeevee.gq.base;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Element;

import com.qeevee.gq.R;
import com.qeevee.gq.R.string;
import com.qeevee.gq.xml.XMLUtilities;

public class Imprint {

	public static final String HIDDEN_FIELD = "HIDDEN";

	public static final String[] FIELD_KEYS = new String[] { "authorName",
			"legalForm", "streetAndNr", "postalCode", "city", "email", "phone",
			"fax", "registeredOffice", "registrationNumber", "vatIdNr" };
	private static final Integer[] FIELD_VALUE_DEFAULT_RES_IDs = new Integer[] {
			R.string.imprint_authorName, R.string.imprint_legalForm,
			R.string.imprint_streetAndNr, R.string.imprint_postalCode,
			R.string.imprint_city, R.string.imprint_email,
			R.string.imprint_phone, R.string.imprint_fax,
			R.string.imprint_registeredOffice,
			R.string.imprint_registrationNumber, R.string.imprint_vatIdNr };
	private static final Integer[] FIELD_LABEL_DEFAULT_RES_IDs = new Integer[] {
			R.string.imprint_authorNameLabel, R.string.imprint_legalFormLabel,
			R.string.imprint_streetAndNrLabel,
			R.string.imprint_postalCodeLabel, R.string.imprint_cityLabel,
			R.string.imprint_emailLabel, R.string.imprint_phoneLabel,
			R.string.imprint_faxLabel, R.string.imprint_registeredOfficeLabel,
			R.string.imprint_registrationNumberLabel,
			R.string.imprint_vatIdNrLabel };

	private Map<String, String> fieldNames = new HashMap<String, String>();
	private Map<String, String> fieldValues = new HashMap<String, String>();

	String completeText;

	public Imprint(Element imprintElement) {
		for (int i = 0; i < FIELD_KEYS.length; i++) {
			fieldNames.put(FIELD_KEYS[i], (String) XMLUtilities.getStringAttribute(
					FIELD_KEYS[i], FIELD_LABEL_DEFAULT_RES_IDs[i],
					imprintElement));
			fieldValues.put(FIELD_KEYS[i], (String) XMLUtilities.getStringAttribute(
					FIELD_KEYS[i], FIELD_VALUE_DEFAULT_RES_IDs[i],
					imprintElement));
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < FIELD_KEYS.length; i++) {
			if (!HIDDEN_FIELD.equals(fieldValues.get(FIELD_KEYS[i])))
				sb.append(fieldNames.get(FIELD_KEYS[i]) + ": \n\t"
						+ fieldValues.get(FIELD_KEYS[i]) + "\n");
		}
		completeText = sb.toString();
	}

	public String getCompleteText() {
		return completeText;
	}
}
