package org.everit.phonenumber.entity;

/*
 * Copyright (c) 2011, Everit Kft.
 *
 * All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */

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

