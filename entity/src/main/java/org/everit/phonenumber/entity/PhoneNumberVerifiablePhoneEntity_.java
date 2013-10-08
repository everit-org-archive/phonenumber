package org.everit.phonenumber.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.everit.verifiabledata.entity.VerifiableDataEntity;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(PhoneNumberVerifiablePhoneEntity.class)
public abstract class PhoneNumberVerifiablePhoneEntity_ {

	public static volatile SingularAttribute<PhoneNumberVerifiablePhoneEntity, PhoneNumberEntity> phoneNumber;
	public static volatile SingularAttribute<PhoneNumberVerifiablePhoneEntity, Long> verifiablePhoneId;
	public static volatile SingularAttribute<PhoneNumberVerifiablePhoneEntity, VerifiableDataEntity> verifiableData;

}

