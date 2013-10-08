package org.everit.phonenumber.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(PhoneNumberEntity.class)
public abstract class PhoneNumberEntity_ {

	public static volatile SingularAttribute<PhoneNumberEntity, String> extension;
	public static volatile SingularAttribute<PhoneNumberEntity, Long> phoneNumberId;
	public static volatile SingularAttribute<PhoneNumberEntity, PhoneNumberAreaEntity> phoneNumberArea;
	public static volatile SingularAttribute<PhoneNumberEntity, String> subScriberNumber;

}

