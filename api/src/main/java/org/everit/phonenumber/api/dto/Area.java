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
 * Information of the area.
 */
public class Area {

    /**
     * The country code.
     */
    private String countryCode;

    /**
     * The calling number of the country region.
     */
    private String callNumber;

    /**
     * The descriptive name for the region.
     */
    private String name;

    /**
     * The expected length of the phone number in the local (country) area code.
     */
    private int subscriberNumberLength;

    /**
     * The area is active or not.
     */
    private boolean active;

    /**
     * The simple constructor.
     * 
     * @param countryCode
     *            the country code.
     * @param callNumber
     *            the calling number of the country region.
     * @param name
     *            the descriptive name for the region.
     * @param subscriberNumberLength
     *            the expected length of the phone number in the local (country) area code.
     * @param active
     *            the area is active or not.
     */
    public Area(final String countryCode, final String callNumber, final String name, final int subscriberNumberLength,
            final boolean active) {
        this.countryCode = countryCode;
        this.callNumber = callNumber;
        this.name = name;
        this.subscriberNumberLength = subscriberNumberLength;
        this.active = active;
    }

    public String getCallNumber() {
        return callNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getName() {
        return name;
    }

    public int getSubscriberNumberLength() {
        return subscriberNumberLength;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public void setCallNumber(final String callNumber) {
        this.callNumber = callNumber;
    }

    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setSubscriberNumberLength(final int subscriberNumberLength) {
        this.subscriberNumberLength = subscriberNumberLength;
    }

}
