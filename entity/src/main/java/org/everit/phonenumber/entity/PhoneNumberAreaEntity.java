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

/**
 * The entity of the phone number area.
 */
@Entity
@Table(name = "PHONENUMBER_AREA")
public class PhoneNumberAreaEntity {

    /**
     * The id of the phone number area.
     */
    @Id
    @GeneratedValue
    @Column(name = "PHONE_AREA_ID")
    private long phoneAreaId;

    /**
     * The calling number of the country region.
     */
    @Column(name = "CALL_NUMBER")
    private String callNumber;

    /**
     * The descriptive name for the region.
     */
    @Column(name = "NAME")
    private String name;

    /**
     * The expected length of the phone number in the local (country) area code.
     */
    @Column(name = "SUBSCIBER_NUMBER_LENTH")
    private int subscriberNumberLength;

    /**
     * It is possible that a region of a country ceases (for example Westel). If ceased the member is false.
     */
    @Column(name = "ACTIVE")
    private boolean active;

    /**
     * The {@link PhoneNumberCountryEntity} object.
     */
    @ManyToOne
    @JoinColumn(name = "COUNTRY_CODE", referencedColumnName = "COUNTRY_ISO3166_A2_CODE")
    private PhoneNumberCountryEntity phoneNumberCountry;

    /**
     * The default constructor.
     */
    public PhoneNumberAreaEntity() {
    }

    /**
     * The simple constructor.
     * 
     * @param phoneAreaId
     *            the id of the phone number area.
     * @param callNumber
     *            the calling number of the country region.
     * @param name
     *            the descriptive name of the region.
     * @param subscriberNumberLength
     *            the excepted length of the phone number in the local area.
     * @param active
     *            it is possible that a region of a country ceases (for example Westel). If ceased the member is false.
     * @param phoneNumberCountry
     *            the {@link PhoneNumberCountryEntity} object.
     */
    public PhoneNumberAreaEntity(final long phoneAreaId, final String callNumber, final String name,
            final int subscriberNumberLength,
            final boolean active, final PhoneNumberCountryEntity phoneNumberCountry) {
        this.phoneAreaId = phoneAreaId;
        this.callNumber = callNumber;
        this.name = name;
        this.subscriberNumberLength = subscriberNumberLength;
        this.active = active;
        this.phoneNumberCountry = phoneNumberCountry;
    }

    public String getCallNumber() {
        return callNumber;
    }

    public String getName() {
        return name;
    }

    public long getPhoneAreaId() {
        return phoneAreaId;
    }

    public PhoneNumberCountryEntity getPhoneNumberCountry() {
        return phoneNumberCountry;
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

    public void setName(final String name) {
        this.name = name;
    }

    public void setPhoneAreaId(final long phoneAreaId) {
        this.phoneAreaId = phoneAreaId;
    }

    public void setPhoneNumberCountry(final PhoneNumberCountryEntity phoneNumberCountry) {
        this.phoneNumberCountry = phoneNumberCountry;
    }

    public void setSubscriberNumberLength(final int subscriberNumberLength) {
        this.subscriberNumberLength = subscriberNumberLength;
    }

}
