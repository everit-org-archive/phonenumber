package org.everit.phonenumber.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.everit.phonenumber.api.enums.VerificationType;
import org.everit.verifiabledata.entity.VerificationRequestEntity;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(PhoneNumberVerificationRequestEntity.class)
public abstract class PhoneNumberVerificationRequestEntity_ {

	public static volatile SingularAttribute<PhoneNumberVerificationRequestEntity, PhoneNumberVerifiablePhoneEntity> verifiablePhone;
	public static volatile SingularAttribute<PhoneNumberVerificationRequestEntity, VerificationRequestEntity> verificationRequest;
	public static volatile SingularAttribute<PhoneNumberVerificationRequestEntity, Long> phoneNumberVerificationRequestId;
	public static volatile SingularAttribute<PhoneNumberVerificationRequestEntity, VerificationType> verificationChannel;

}

