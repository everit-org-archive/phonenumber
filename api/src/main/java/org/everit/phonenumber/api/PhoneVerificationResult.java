package org.everit.phonenumber.api;

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
