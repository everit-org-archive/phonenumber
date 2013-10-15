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
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The entity of the phone number country.
 */
@Entity
@Table(name = "PHONENUMBER_COUNTRY")
public class PhoneNumberCountryEntity {

    /**
     * The country code in ISO 3166-alpha-2 format.
     */
    @Id
    @Column(name = "COUNTRY_ISO3166_A2_CODE", length = 2)
    private String countryISO3166A2Code;

    /**
     * The IDD code of the country.
     */
    @Column(name = "IDD_PREFIX")
    private String iddPrefix;

    /**
     * The NDD code of the country.
     */
    @Column(name = "NDD_PREFIX")
    private String nddPrefix;

    /**
     * The country call code.
     */
    @Column(name = "COUNTRY_CALL_CODE")
    private String countryCallCode;

    /**
     * The country is active. For example Hungary is active, but Yugoslavia is ceased so Yugoslavia is not active.
     */
    @Column(name = "ACTIVE")
    private boolean active;

    /**
     * The default constructor.
     */
    public PhoneNumberCountryEntity() {
    }

    /**
     * The simple constructor.
     * 
     * @param countryISO3166A2Code
     *            the country code.
     * @param iddPrefix
     *            the IDD code of the country.
     * @param nddPrefix
     *            the NDD code of the country.
     * @param countryCallCode
     *            the country call code.
     * @param active
     *            the country is active or not.
     */
    public PhoneNumberCountryEntity(final String countryISO3166A2Code, final String iddPrefix, final String nddPrefix,
            final String countryCallCode,
            final boolean active) {
        this.countryISO3166A2Code = countryISO3166A2Code;
        this.iddPrefix = iddPrefix;
        this.nddPrefix = nddPrefix;
        this.countryCallCode = countryCallCode;
        this.active = active;
    }

    public String getCountryCallCode() {
        return countryCallCode;
    }

    public String getCountryISO3166A2Code() {
        return countryISO3166A2Code;
    }

    public String getIddPrefix() {
        return iddPrefix;
    }

    public String getNddPrefix() {
        return nddPrefix;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public void setCountryCallCode(final String countryCallCode) {
        this.countryCallCode = countryCallCode;
    }

    public void setCountryISO3166A2Code(final String countryISO3166A2Code) {
        this.countryISO3166A2Code = countryISO3166A2Code;
    }

    public void setIddPrefix(final String iddPrefix) {
        this.iddPrefix = iddPrefix;
    }

    public void setNddPrefix(final String nddPrefix) {
        this.nddPrefix = nddPrefix;
    }

}
