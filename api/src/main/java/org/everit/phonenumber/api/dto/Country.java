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
 * Information of the country.
 */
public class Country {

    /**
     * The country code.
     */
    private String countryISO3166A2Code;

    /**
     * The IDD code of the country.
     */
    private String iddPrefix;

    /**
     * The NDD code of the country.
     */
    private String nddPrefix;

    /**
     * The country call code.
     */
    private String countryCallCode;

    /**
     * The country is selectable or not.
     */
    private boolean selectable;

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
     * @param selectable
     *            the country is selectable or not.
     */
    public Country(final String countryISO3166A2Code, final String iddPrefix, final String nddPrefix,
            final String countryCallCode, final boolean selectable) {
        this.countryISO3166A2Code = countryISO3166A2Code;
        this.iddPrefix = iddPrefix;
        this.nddPrefix = nddPrefix;
        this.countryCallCode = countryCallCode;
        this.selectable = selectable;
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

    public boolean isSelectable() {
        return selectable;
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

    public void setSelectable(final boolean selectable) {
        this.selectable = selectable;
    }

}
