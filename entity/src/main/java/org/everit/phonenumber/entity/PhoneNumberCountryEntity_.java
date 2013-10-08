package org.everit.phonenumber.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(PhoneNumberCountryEntity.class)
public abstract class PhoneNumberCountryEntity_ {

	public static volatile SingularAttribute<PhoneNumberCountryEntity, String> nddPrefix;
	public static volatile SingularAttribute<PhoneNumberCountryEntity, Boolean> active;
	public static volatile SingularAttribute<PhoneNumberCountryEntity, String> iddPrefix;
	public static volatile SingularAttribute<PhoneNumberCountryEntity, String> countryCallCode;
	public static volatile SingularAttribute<PhoneNumberCountryEntity, String> countryISO3166A2Code;

}

