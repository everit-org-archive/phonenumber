package org.everit.phonenumber.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(PhoneNumberAreaEntity.class)
public abstract class PhoneNumberAreaEntity_ {

	public static volatile SingularAttribute<PhoneNumberAreaEntity, Integer> subscriberNumberLength;
	public static volatile SingularAttribute<PhoneNumberAreaEntity, PhoneNumberCountryEntity> phoneNumberCountry;
	public static volatile SingularAttribute<PhoneNumberAreaEntity, String> areaName;
	public static volatile SingularAttribute<PhoneNumberAreaEntity, String> callNumber;
	public static volatile SingularAttribute<PhoneNumberAreaEntity, Boolean> active;
	public static volatile SingularAttribute<PhoneNumberAreaEntity, Long> phoneAreaId;

}

