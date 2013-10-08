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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.everit.phonenumber.api.enums.VerificationType;
import org.everit.verifiabledata.entity.VerifiableDataEntity;

/**
 * The entity of the phone number verification.
 */
@Entity
@Table(name = "PHONENUMBER_VERIFICATION")
public class PhoneNumberVerificationEntity {

    /**
     * The id of the phone number verification.
     */
    @Id
    @GeneratedValue
    @Column(name = "PHONENUMBER_VERIFICIATION_ID")
    private long phoneNumberVerificationId;

    /**
     * The type of the verification.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "PHONE_VERIFICTION_TYPE")
    private VerificationType verificationType;

    /**
     * The id of the phone number. The {@link PhoneNumberEntity} object.
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
    public PhoneNumberVerificationEntity() {
    }

    /**
     * The simple constructor.
     * 
     * @param phoneNumberVerificationId
     *            the id of the phone number verification.
     * @param verificationType
     *            the type of the verification. Using the {@link VerificationType} enum class.
     * @param phoneNumber
     *            the {@link PhoneNumberEntity} object.
     * @param verifiableData
     *            the {@link VerifiableDataEntity} object.
     */
    public PhoneNumberVerificationEntity(final long phoneNumberVerificationId, final VerificationType verificationType,
            final PhoneNumberEntity phoneNumber, final VerifiableDataEntity verifiableData) {
        this.phoneNumberVerificationId = phoneNumberVerificationId;
        this.verificationType = verificationType;
        this.phoneNumber = phoneNumber;
        this.verifiableData = verifiableData;
    }

    public PhoneNumberEntity getPhoneNumber() {
        return phoneNumber;
    }

    public long getPhoneNumberVerificationId() {
        return phoneNumberVerificationId;
    }

    public VerifiableDataEntity getVerifiableData() {
        return verifiableData;
    }

    public VerificationType getVerificationType() {
        return verificationType;
    }

    public void setPhoneNumber(final PhoneNumberEntity phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPhoneNumberVerificationId(final long phoneNumberVerificationId) {
        this.phoneNumberVerificationId = phoneNumberVerificationId;
    }

    public void setVerifiableData(final VerifiableDataEntity verifiableData) {
        this.verifiableData = verifiableData;
    }

    public void setVerificationType(final VerificationType verificationType) {
        this.verificationType = verificationType;
    }

}
