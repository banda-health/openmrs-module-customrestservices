package org.openmrs.module.patientlist.api.util;

import org.openmrs.Visit;
import org.openmrs.Patient;
import org.openmrs.VisitType;
import org.openmrs.VisitAttribute;
import org.openmrs.VisitAttributeType;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.PatientIdentifier;
import org.openmrs.Location;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Dummy patient used for live previewing.
 */
public class DummyPatient extends Patient {

	private static final int BIRTH_YEAR = 1990;
	private static int BIRTH_DATE = 1;
	private static final String IDENTIFIER = "4564";

	private DummyPatient() {}

	private static class Holder {
		private static final DummyPatient INSTANCE = new DummyPatient().loadData();
	}

	public static DummyPatient getInstance() {
		return Holder.INSTANCE;
	}

	private DummyPatient loadData() {
		DummyPatient dummyPatient = new DummyPatient();

		// set id
		dummyPatient.setId(1);

		// set address
		PersonAddress address = new PersonAddress();
		address.setId(1);
		address.setAddress1("Address 1");
		address.setAddress2("Address 2");
		address.setAddress3("Address 3");
		Set<PersonAddress> addresses = new HashSet<PersonAddress>();
		addresses.add(address);
		dummyPatient.setAddresses(addresses);

		// set name
		PersonName personName = new PersonName();
		personName.setId(1);
		personName.setFamilyName("Doe");
		personName.setFamilyName2("Moe");
		personName.setGivenName("Joe");
		personName.setMiddleName("Blow");
		Set<PersonName> names = new HashSet<PersonName>();
		names.add(personName);
		dummyPatient.setNames(names);

		// set person attributes
		PersonAttribute personAttribute = new PersonAttribute();
		personAttribute.setId(1);
		personAttribute.setValue("Married");

		PersonAttributeType type = new PersonAttributeType();
		type.setId(1);
		type.setName("Civil Status");
		personAttribute.setAttributeType(type);
		Set<PersonAttribute> attributes = new HashSet<PersonAttribute>();
		attributes.add(personAttribute);

		personAttribute = new PersonAttribute();
		personAttribute.setId(2);
		personAttribute.setValue("Cambodia");

		type = new PersonAttributeType();
		type.setId(2);
		type.setName("Citizenship");
		personAttribute.setAttributeType(type);
		attributes.add(personAttribute);

		dummyPatient.setAttributes(attributes);

		// set gender
		dummyPatient.setGender("M");

		//set birthdate
		Calendar cal = Calendar.getInstance();
		cal.set(BIRTH_YEAR, Calendar.JANUARY, BIRTH_DATE);
		dummyPatient.setBirthdate(cal.getTime());

		// set identifier
		PatientIdentifier patientIdentifier = new PatientIdentifier();
		patientIdentifier.setId(1);
		patientIdentifier.setIdentifier(IDENTIFIER);
		Set<PatientIdentifier> identifiers = new HashSet<PatientIdentifier>();
		identifiers.add(patientIdentifier);
		dummyPatient.setIdentifiers(identifiers);

		// set visit id
		Visit visit = new Visit();
		visit.setVisitId(1);

		// set visit type
		VisitType visitType = new VisitType();
		visitType.setId(1);
		visitType.setName("Outpatient");
		visit.setVisitType(visitType);

		// set patient
		visit.setPatient(dummyPatient);

		// set location
		Location location = new Location();
		location.setId(1);
		location.setName("OpenHMIS offices");
		visit.setLocation(location);

		// set startdatetime
		cal.setTime(new Date());
		//cal.add(Calendar.MONTH, -2);
		visit.setStartDatetime(cal.getTime());

		// set enddatetime -- if required

		// set visit attributes
		VisitAttribute visitAttribute = new VisitAttribute();
		visitAttribute.setVisitAttributeId(1);
		visitAttribute.setVisit(visit);

		VisitAttributeType visitAttributeType = new VisitAttributeType();
		visitAttributeType.setVisitAttributeTypeId(1);
		visitAttributeType.setName("ward");
		visitAttributeType.setDatatypeClassname("");
		visitAttribute.setAttributeType(visitAttributeType);

		return dummyPatient;
	}
}
