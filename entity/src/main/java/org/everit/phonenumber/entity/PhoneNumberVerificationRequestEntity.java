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
import org.everit.verifiabledata.entity.VerificationRequestEntity;

/**
 * The entity of the phone number verification request.
 */
@Entity
@Table(name = "PHONENUMBER_VERIFICATION_REQUEST")
public class PhoneNumberVerificationRequestEntity {

    /**
     * The id of the phone number verification request.
     */
    @Id
    @GeneratedValue
    @Column(name = "PHONENUMBER_VERIFICATION_REQUEST_ID")
    private long phoneNumberVerificationRequestId;

    /**
     * The id of the verifiable phone.
     */
    @ManyToOne
    @JoinColumn(name = "VERIFIABLE_PHONE_ID", referencedColumnName = "VERIFIABLE_PHONE_ID")
    private PhoneNumberVerifiablePhoneEntity verifiablePhone;

    /**
     * The id of the verification request.
     */
    @ManyToOne
    @JoinColumn(name = "VERIFATION_REQUEST_ID", referencedColumnName = "VERIFICATION_REQUEST_ID")
    private VerificationRequestEntity verificationRequest;

    /**
     * The verification channel of the phone number verification request.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "VERIFICATION_CHANNEL")
    private VerificationType verificationChannel;

    /**
     * The default constructor.
     */
    public PhoneNumberVerificationRequestEntity() {
    }

    /**
     * The simple constructor.
     * 
     * @param phoneNumberVerificationRequestId
     *            the id of the phone number verification request.
     * @param verifiablePhone
     *            the {@link PhoneNumberVerifiablePhoneEntity} object.
     * @param verificationRequest
     *            the {@link VerificationRequestEntity} object.
     * @param verificationChannel
     *            the {@link VerificationType}.
     */
    public PhoneNumberVerificationRequestEntity(final long phoneNumberVerificationRequestId,
            final PhoneNumberVerifiablePhoneEntity verifiablePhone,
            final VerificationRequestEntity verificationRequest,
            final VerificationType verificationChannel) {
        this.phoneNumberVerificationRequestId = phoneNumberVerificationRequestId;
        this.verifiablePhone = verifiablePhone;
        this.verificationRequest = verificationRequest;
        this.verificationChannel = verificationChannel;
    }

    public long getPhoneNumberVerificationRequestId() {
        return phoneNumberVerificationRequestId;
    }

    public PhoneNumberVerifiablePhoneEntity getVerifiablePhone() {
        return verifiablePhone;
    }

    public VerificationType getVerificationChannel() {
        return verificationChannel;
    }

    public VerificationRequestEntity getVerificationRequest() {
        return verificationRequest;
    }

    public void setPhoneNumberVerificationRequestId(final long phoneNumberVerificationRequestId) {
        this.phoneNumberVerificationRequestId = phoneNumberVerificationRequestId;
    }

    public void setVerifiablePhone(final PhoneNumberVerifiablePhoneEntity verifiablePhone) {
        this.verifiablePhone = verifiablePhone;
    }

    public void setVerificationChannel(final VerificationType verificationChannel) {
        this.verificationChannel = verificationChannel;
    }

    public void setVerificationRequest(final VerificationRequestEntity verificationRequest) {
        this.verificationRequest = verificationRequest;
    }
}
