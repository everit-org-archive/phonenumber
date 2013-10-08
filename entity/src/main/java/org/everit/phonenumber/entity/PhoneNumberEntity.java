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
 * The entity of the phone number.
 */
@Entity
@Table(name = "PHONENUMBER_NUMBER")
public class PhoneNumberEntity {

    /**
     * The id of the phone number.
     */
    @Id
    @GeneratedValue
    @Column(name = "PHONE_NUMBER_ID")
    private long phoneNumberId;

    /**
     * The number of subscribers, which comes after the region code.
     */
    @Column(name = "SUBSCRIBER_NUMBER")
    private String subScriberNumber;

    /**
     * The extension number. Optional.
     */
    @Column(name = "EXTENSION")
    private String extension;

    /**
     * The {@link PhoneNumberAreaEntity} object. The id of the phone number area.
     */
    @ManyToOne
    @JoinColumn(name = "AREA_ID", referencedColumnName = "PHONE_AREA_ID")
    private PhoneNumberAreaEntity phoneNumberArea;

    /**
     * The default constructor.
     */
    public PhoneNumberEntity() {
    }

    /**
     * The simple constructor.
     * 
     * @param phoneNumberId
     *            the id of the phone number.
     * @param subScriberNumber
     *            the number of the subscriber.
     * @param extension
     *            the extension number.
     * @param phoneNumberArea
     *            the id of the phone number area.
     */
    public PhoneNumberEntity(final long phoneNumberId, final String subScriberNumber, final String extension,
            final PhoneNumberAreaEntity phoneNumberArea) {
        this.phoneNumberId = phoneNumberId;
        this.subScriberNumber = subScriberNumber;
        this.extension = extension;
        this.phoneNumberArea = phoneNumberArea;
    }

    public String getExtension() {
        return extension;
    }

    public PhoneNumberAreaEntity getPhoneNumberArea() {
        return phoneNumberArea;
    }

    public long getPhoneNumberId() {
        return phoneNumberId;
    }

    public String getSubScriberNumber() {
        return subScriberNumber;
    }

    public void setExtension(final String extension) {
        this.extension = extension;
    }

    public void setPhoneNumberArea(final PhoneNumberAreaEntity phoneNumberArea) {
        this.phoneNumberArea = phoneNumberArea;
    }

    public void setPhoneNumberId(final long phoneNumberId) {
        this.phoneNumberId = phoneNumberId;
    }

    public void setSubScriberNumber(final String subScriberNumber) {
        this.subScriberNumber = subScriberNumber;
    }

}
