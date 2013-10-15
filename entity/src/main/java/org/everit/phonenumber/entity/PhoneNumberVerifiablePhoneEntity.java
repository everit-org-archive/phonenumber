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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.everit.verifiabledata.entity.VerifiableDataEntity;

/**
 * The entity of the verifiable phone.
 */
@Entity
@Table(name = "PHONENUMBER_VERIFIABLE_PHONE")
public class PhoneNumberVerifiablePhoneEntity {

    /**
     * The id of the verifiable phone.
     */
    @Id
    @GeneratedValue
    @Column(name = "VERIFIABLE_PHONE_ID")
    private long verifiablePhoneId;

    /**
     * The id of the verifiable phone. The {@link PhoneNumberEntity} object.
     */
    @ManyToOne
    @JoinColumn(name = "PHONE_NUMBER_ID", referencedColumnName = "PHONE_NUMBER_ID")
    private PhoneNumberEntity phoneNumber;

    /**
     * The id of the verifiable data. The {@link VerifiableDataEntity} object.
     */
    @ManyToOne
    @JoinColumn(name = "VERIFIABLE_DATA_ID", referencedColumnName = "VERIFIABLE_DATA_ID")
    private VerifiableDataEntity verifiableData;

    /**
     * The default constructor.
     */
    public PhoneNumberVerifiablePhoneEntity() {
    }

    /**
     * The simple constructor.
     * 
     * @param verifiablePhoneId
     *            the id of the verifiable phone.
     * @param phoneNumber
     *            the {@link PhoneNumberEntity} object.
     * @param verifiableData
     *            the {@link VerifiableDataEntity} object.
     */
    public PhoneNumberVerifiablePhoneEntity(final long verifiablePhoneId, final PhoneNumberEntity phoneNumber,
            final VerifiableDataEntity verifiableData) {
        this.verifiablePhoneId = verifiablePhoneId;
        this.phoneNumber = phoneNumber;
        this.verifiableData = verifiableData;
    }

    public PhoneNumberEntity getPhoneNumber() {
        return phoneNumber;
    }

    public VerifiableDataEntity getVerifiableData() {
        return verifiableData;
    }

    public long getVerifiablePhoneId() {
        return verifiablePhoneId;
    }

    public void setPhoneNumber(final PhoneNumberEntity phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setVerifiableData(final VerifiableDataEntity verifiableData) {
        this.verifiableData = verifiableData;
    }

    public void setVerifiablePhoneId(final long verifiablePhoneId) {
        this.verifiablePhoneId = verifiablePhoneId;
    }

}
