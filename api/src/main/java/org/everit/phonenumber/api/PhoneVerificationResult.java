package org.everit.phonenumber.api;

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

import org.everit.phonenumber.api.enums.PhoneNumberConfirmationResult;

/**
 * Information of the phone verification result.
 */
public class PhoneVerificationResult {

    /**
     * The id of the verifiable phone.
     */
    private Long verifiablePhoneId;

    /**
     * The phone number confirmation result.
     */
    private PhoneNumberConfirmationResult result;

    /**
     * The simple constructor.
     * 
     * @param verifiablePhoneId
     *            the id if the verifiable phone.
     * @param result
     *            the {@link PhoneNumberConfirmationResult}.
     */
    public PhoneVerificationResult(final Long verifiablePhoneId, final PhoneNumberConfirmationResult result) {
        this.verifiablePhoneId = verifiablePhoneId;
        this.result = result;
    }

    public PhoneNumberConfirmationResult getResult() {
        return result;
    }

    public Long getVerifiablePhoneId() {
        return verifiablePhoneId;
    }

    public void setResult(final PhoneNumberConfirmationResult result) {
        this.result = result;
    }

    public void setVerifiablePhoneId(final Long verifiablePhoneId) {
        this.verifiablePhoneId = verifiablePhoneId;
    }

}
