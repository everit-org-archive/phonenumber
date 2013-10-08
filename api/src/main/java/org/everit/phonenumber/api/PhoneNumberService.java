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

import java.util.List;

import org.everit.phonenumber.api.dto.Area;
import org.everit.phonenumber.api.dto.CallablePhoneNumber;
import org.everit.phonenumber.api.dto.Country;
import org.everit.phonenumber.api.enums.VerificationType;

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
 * Service for managing the phone number function.
 */
public interface PhoneNumberService {

    /**
     * Creating the verification.
     * 
     * @param phoneNumberId
     *            the id of the phone number.
     * @param verificationChannel
     *            what kind of the verification. Using the {@link VerificationType} enum class.
     */
    void createVerificationRequest(final long phoneNumberId, final VerificationType verificationChannel);

    /**
     * Finds and getting the area based on country code and area call number.
     * 
     * @param countryISO3166A2Code
     *            the country code. Cannot be <code>null</code>.
     * @param areaCallNumber
     *            the area call number. Cannot be <code>null</code>.
     * @return the {@link Area} object if exist, otherwise <code>null</code>.
     * @throws IllegalArgumentException
     *             if one parameter is <code>null</code>.
     */
    Area getActiveAreaByCountryAndCallNumber(final String countryISO3166A2Code, final String areaCallNumber);

    /**
     * Finds and getting the area based on the area id.
     * 
     * @param areaId
     *            the id of the area.
     * @return the {@link Area} object if exist the area. If not exitst return <code>null</code>.
     */
    Area getAreaById(final long areaId);

    /**
     * Getting the callable phone number based on the phone number id.
     * 
     * @param phoneNumberId
     *            the id of the phone number.
     * @return the {@link CallablePhoneNumber} object if exist the phone number id, otherwise return <code>null</code>.
     */
    CallablePhoneNumber getCallablePhoneNumber(final long phoneNumberId);

    /**
     * Getting the country based on countryISO3166A2Code.
     * 
     * @param countryISO3166A2Code
     *            the countryISO3166A2Code. Cannot be <code>null</code>.
     * @return the {@link Country} object if exist in the database. If not exist in the database return
     *         <code>null</code>.
     * @throws IllegalArgumentException
     *             if the countryISO3166A2Code is null.
     */
    Country getCountry(final String countryISO3166A2Code);

    /**
     * Deactivating the area. Cancel all number which belongs to area.
     * 
     * @param areaId
     *            the id of the area.
     */
    void inactiveArea(final long areaId);

    /**
     * Deactivating the country. Cancel all number which belongs to country.
     * 
     * @param countryISO3166A2Code
     *            the country code.
     */
    void inactiveCountry(final String countryISO3166A2Code);

    /**
     * Listing active areas based on the countryISO3166A2Code.
     * 
     * @param countryISO3166A2Code
     *            the country code.
     * @param startPosition
     *            the first index of the list or null if not defined.
     * @param maxResultCount
     *            the maximum number of elements or null if not defined.
     * @return the {@link Area}s in list. If no one return empty list.
     */
    List<Area> listActiveAreasBycountryISO3166A2Code(final String countryISO3166A2Code, final Long startPosition,
            final Long maxResultCount);

    /**
     * Listing the available active country.
     * 
     * @param startPosition
     *            the first index of the list or null if not defined.
     * @param maxResultCount
     *            the maximum number of elements or null if not defined.
     * @return the Country list. If no one return empty list.
     */
    List<Country> listActiveCountries(final Long startPosition, final Long maxResultCount);

    /**
     * Create and saving the area.
     * 
     * @param countryISO3166A2Code
     *            the country code. Cannot be <code>null</code>.
     * @param callNumber
     *            the calling number of the country region. Cannot be <code>null</code>.
     * @param name
     *            the descriptive name for the region.
     * @param subscriberNumberLength
     *            the expected length of the phone number in the local (country) area code. Must be positive.
     * @return the id of the area.
     * @throws DuplicateSelectableAreaException
     */
    long saveArea(final String countryISO3166A2Code, final String callNumber, final String name,
            final int subscriberNumberLength);

    /**
     * Saving the country in the database.
     * 
     * @param countryISO3166A2Code
     *            the country code. Cannot be <code>null</code>.
     * @param idd
     *            the IDD code of the country. Cannot be <code>null</code>.
     * @param ndd
     *            the NDD code of the country. Cannot be <code>null</code>.
     * @param countryCallCode
     *            the country call code. Cannot be <code>null</code>.
     * @throws IllegalArgumentException
     *             if one parameter is <code>null</code>.
     */
    void saveCountry(final String countryISO3166A2Code, final String idd, final String ndd, final String countryCallCode);

    /**
     * Saving the phone number in the database.
     * 
     * @param areaId
     *            the id of the area.
     * @param subscriberNumber
     *            the number of subscriber.
     * @param extension
     *            the extension number.
     * @return the id of the phone number.
     * @throws InvalidNumberExcption
     *             if the number length is not correct.
     */
    long savePhoneNumber(final long areaId, final String subscriberNumber, final String extension);

    /**
     * Updating the phone number. All verification request which belong to the phone number and the old phone number to
     * be invalid.
     * 
     * @param phoneNumberId
     *            the id of the phone number.
     * @param areaId
     *            the id of the area.
     * @param subscriberNumber
     *            the number of the subscribers. Cannot be <code>null</code>.
     * @param extension
     *            the extension number.
     * @throws InvalidNumberExcption
     *             if the number length is not correct.
     */
    void updatePhoneNumber(final long phoneNumberId, final long areaId, final String subscriberNumber,
            final String extension);
}
