package edu.bonn.mobilegaming.geoquest;

import org.dom4j.Element;

import com.qeevee.gq.xml.XMLUtilities;

public class Imprint {

	String authorName;
	String legalForm;
	String streetAndNr;
	String postalCode;
	String city;
	String email;
	String phone;
	String fax;
	String registeredOffice;
	String registrationNumber;
	String vatIdNr; // TODO rename rechtsform engl.

	public Imprint(Element imprintElement) {
		authorName = (String) XMLUtilities.getAttribute("authorName",
				R.string.imprint_authorName, imprintElement);
		legalForm = (String) XMLUtilities.getAttribute("legalForm",
				R.string.imprint_legalForm, imprintElement);
		streetAndNr = (String) XMLUtilities.getAttribute("streetAndNr",
				R.string.imprint_streetAndNr, imprintElement);
		postalCode = (String) XMLUtilities.getAttribute("postalCode",
				R.string.imprint_postalCode, imprintElement);
		city = (String) XMLUtilities.getAttribute("city",
				R.string.imprint_city, imprintElement);
		email = (String) XMLUtilities.getAttribute("email",
				R.string.imprint_email, imprintElement);
		phone = (String) XMLUtilities.getAttribute("phone",
				R.string.imprint_phone, imprintElement);
		fax = (String) XMLUtilities.getAttribute("fax", R.string.imprint_fax,
				imprintElement);
		registeredOffice = (String) XMLUtilities.getAttribute(
				"registeredOffice", R.string.imprint_registeredOffice,
				imprintElement);
		registrationNumber = (String) XMLUtilities.getAttribute(
				"registrationNumber", R.string.imprint_registrationNumber,
				imprintElement);
		vatIdNr = (String) XMLUtilities.getAttribute("vatIdNr",
				R.string.imprint_vatIdNr, imprintElement);
	}

}
