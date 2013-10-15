package org.everit.phonenumber.api.dto;

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

/**
 * Information of the phone number. Contains all information to call the phone number.
 */
public class CallablePhoneNumber {

    /**
     * The IDD code of the country.
     */
    private String countryIDD;

    /**
     * The NDD code of the country.
     */
    private String countryNDD;

    /**
     * The country call code.
     */
    private String countryCallCode;

    /**
     * The country area call number.
     */
    private String areaCallNumber;

    /**
     * The number of the subscriber.
     */
    private String subscriberNumber;

    /**
     * The extension number. Optional.
     */
    private String extension;

    /**
     * The simple constructor.
     * 
     * @param countryIDD
     *            the IDD code of the country.
     * @param countryNDD
     *            the NDD code of the country.
     * @param countryCallCode
     *            the country call code.
     * @param areaCallNumber
     *            the country area call number.
     * @param subscriberNumber
     *            the number of the subscriber.
     * @param extension
     *            the extension number. Optional.
     */
    public CallablePhoneNumber(final String countryIDD, final String countryNDD, final String countryCallCode,
            final String areaCallNumber,
            final String subscriberNumber, final String extension) {
        this.countryIDD = countryIDD;
        this.countryNDD = countryNDD;
        this.countryCallCode = countryCallCode;
        this.areaCallNumber = areaCallNumber;
        this.subscriberNumber = subscriberNumber;
        this.extension = extension;
    }

    public String getAreaCallNumber() {
        return areaCallNumber;
    }

    public String getCountryCallCode() {
        return countryCallCode;
    }

    public String getCountryIDD() {
        return countryIDD;
    }

    public String getCountryNDD() {
        return countryNDD;
    }

    public String getExtension() {
        return extension;
    }

    public String getSubscriberNumber() {
        return subscriberNumber;
    }

    public void setAreaCallNumber(final String areaCallNumber) {
        this.areaCallNumber = areaCallNumber;
    }

    public void setCountryCallCode(final String countryCallCode) {
        this.countryCallCode = countryCallCode;
    }

    public void setCountryIDD(final String countryIDD) {
        this.countryIDD = countryIDD;
    }

    public void setCountryNDD(final String countryNDD) {
        this.countryNDD = countryNDD;
    }

    public void setExtension(final String extension) {
        this.extension = extension;
    }

    public void setSubscriberNumber(final String subscriberNumber) {
        this.subscriberNumber = subscriberNumber;
    }

}
