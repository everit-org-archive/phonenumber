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

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(PhoneNumberAreaEntity.class)
public abstract class PhoneNumberAreaEntity_ {

	public static volatile SingularAttribute<PhoneNumberAreaEntity, Integer> subscriberNumberLength;
	public static volatile SingularAttribute<PhoneNumberAreaEntity, PhoneNumberCountryEntity> phoneNumberCountry;
	public static volatile SingularAttribute<PhoneNumberAreaEntity, String> callNumber;
	public static volatile SingularAttribute<PhoneNumberAreaEntity, String> name;
	public static volatile SingularAttribute<PhoneNumberAreaEntity, Boolean> active;
	public static volatile SingularAttribute<PhoneNumberAreaEntity, Long> phoneAreaId;

}

