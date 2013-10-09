package org.everit.phonenumber.itests.core;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.PersistenceException;

import junit.framework.Assert;

import org.everit.phonenumber.api.PhoneNumberService;
import org.everit.phonenumber.api.dto.Area;
import org.everit.phonenumber.api.dto.CallablePhoneNumber;
import org.everit.phonenumber.api.dto.Country;
import org.everit.phonenumber.api.exceptions.DuplicateCountryException;
import org.everit.phonenumber.api.exceptions.DuplicateSelectableAreaException;
import org.everit.phonenumber.api.exceptions.InvalidNumberException;
import org.everit.phonenumber.api.exceptions.NoSuchAreaException;
import org.everit.phonenumber.api.exceptions.NoSuchPhoneNumberException;
import org.everit.phonenumber.api.exceptions.NonPositiveSubscriberNumberLengthException;

public class PhoneNumberServiceTestImpl implements PhoneNumberServiceTest {

    PhoneNumberService phoneNumberService;

    /**
     * http://www.findacrew.net/secure-server/eng/dialing.asp
     */
    private static final List<Country> countris = Arrays.asList(new Country("HU", "00", "06", "36", true),

            new Country("DE", "00", "0", "49", true),
            new Country("HK", "001", "", "852", true),
            new Country("UZ", "8P10", "8", "998", true),
            new Country("EH", "", "", "212", true),
            new Country("VI", "011", "", "1-340", true),
            new Country("BO", "0010", "010", "591", true),
            new Country("TD", "15", "", "235", true),
            new Country("CU", "119", "0", "53", true),
            new Country("SI", "00", "0", "421", true));

    private static final List<Area> areas = Arrays.asList(new Area("HU", "20", "Telenor", 7, true),
            new Area("HU", "30", "T-mobile", 7, true),
            new Area("HU", "31", "Tesco Mobile", 7, true),
            new Area("SI", "30", "Si.Mobile", 7, true),
            new Area("SI", "31", "Mobiltel", 7, true),
            new Area("SI", "70", "Tušmobil", 7, true),
            new Area("SI", "71", "Mobitel", 7, true),
            new Area("DE", "170", "T-Mobile GSM/UMTS", 7, true),
            new Area("DE", "700", "", 8, true),
            new Area("DE", "176", "O2 Germany", 8, true),
            new Area("DE", "175", "T-Mobile", 7, true),
            new Area("BO", "6", "Half fiction", 5, true),
            new Area("BO", "7", "Half fiction", 15, true),
            new Area("CU", "6", "fiction", 5, true),
            new Area("CU", "7", "fiction", 8, true),
            new Area("CU", "1", "fiction", 2, true));

    private static final List<String> phoneNumbers = Arrays.asList("12", "12345", "1234567", "12345678",
            "123456789012345");

    private static List<Long> areaIds = new ArrayList<Long>();

    public void setPhoneNumberService(final PhoneNumberService phoneNumberService) {
        this.phoneNumberService = phoneNumberService;
    }

    @Override
    public void test() {
        testCountry();
        testArea();
        testPhoneNumber();
    }

    void testArea() {
        for (Area a : areas) {
            long saveArea = phoneNumberService.saveArea(a.getCountryCode(), a.getCallNumber(), a.getName(),
                    a.getSubscriberNumberLength());
            Assert.assertTrue(saveArea > 0L);
            areaIds.add(saveArea);
            Area area = phoneNumberService.getActiveAreaByCountryAndCallNumber(a.getCountryCode(), a.getCallNumber());
            Assert.assertNotNull(area);
            Assert.assertTrue(area.isActive());
        }
        Area a = areas.get(0);

        try {
            phoneNumberService.saveArea(a.getCountryCode(), a.getCallNumber(), a.getName(),
                    a.getSubscriberNumberLength());
            Assert.fail("Expect DuplicateSelectableAreaException, but the method not throws.");
        } catch (DuplicateSelectableAreaException e) {
            Assert.assertNotNull(e);
        }

        try {
            phoneNumberService.saveArea(null, a.getCallNumber(), a.getName(),
                    a.getSubscriberNumberLength());
            Assert.fail("Expect IllegalArgumentException, but the method not throws.");
        } catch (IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }

        try {
            phoneNumberService.saveArea(a.getCountryCode(), a.getCallNumber(), null,
                    a.getSubscriberNumberLength());
            Assert.fail("Expect DuplicateSelectableAreaException, but the method not throws.");
        } catch (DuplicateSelectableAreaException e) {
            Assert.assertNotNull(e);
        }

        try {
            phoneNumberService.saveArea(a.getCountryCode(), null, a.getName(),
                    a.getSubscriberNumberLength());
            Assert.fail("Expect IllegalArgumentException, but the method not throws.");
        } catch (IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }

        try {
            phoneNumberService.saveArea(a.getCountryCode(), a.getCallNumber(), a.getName(),
                    0);
            Assert.fail("Expect NonPositiveSubscriberNumberLengthException, but the method not throws.");
        } catch (NonPositiveSubscriberNumberLengthException e) {
            Assert.assertNotNull(e);
        }

        try {
            phoneNumberService.getActiveAreaByCountryAndCallNumber(null, a.getCallNumber());
            Assert.fail("Expect IllegalArgumentException, but the method not throws.");
        } catch (IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }

        try {
            phoneNumberService.getActiveAreaByCountryAndCallNumber(a.getCountryCode(), null);
            Assert.fail("Expect IllegalArgumentException, but the method not throws.");
        } catch (IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }

        for (Long id : areaIds) {
            Area area = phoneNumberService.getAreaById(id);
            Assert.assertNotNull(area);
            area = phoneNumberService.getAreaById(0L);
            Assert.assertNull(area);
        }

        List<Area> areaList = phoneNumberService.listActiveAreasBycountryISO3166A2Code("HU", null, null);
        Assert.assertNotNull(areaList);
        Assert.assertFalse(areaList.isEmpty());
        int areaHUCount = 0;
        for (Area area : areaList) {
            if (!area.getCountryCode().equals("HU")) {
                Assert.fail("Not exist \"HU\" country code, but expect.");
            } else {
                areaHUCount++;
                Assert.assertTrue(true);
            }
        }
        areaList = phoneNumberService.listActiveAreasBycountryISO3166A2Code("HU", null, 1L);
        Assert.assertNotNull(areaList);
        Assert.assertFalse(areaList.isEmpty());
        Assert.assertEquals(1, areaList.size());

        areaList = phoneNumberService.listActiveAreasBycountryISO3166A2Code("HU", 1L, null);
        Assert.assertNotNull(areaList);
        Assert.assertFalse(areaList.isEmpty());
        Assert.assertEquals(areaHUCount - 1, areaList.size());

        areaList = phoneNumberService.listActiveAreasBycountryISO3166A2Code("HU", 1L, 1L);
        Assert.assertNotNull(areaList);
        Assert.assertFalse(areaList.isEmpty());
        Assert.assertEquals(1, areaList.size());

        areaList = phoneNumberService.listActiveAreasBycountryISO3166A2Code("HU", -1L, null);
        Assert.assertNotNull(areaList);
        Assert.assertTrue(areaList.isEmpty());

        areaList = phoneNumberService.listActiveAreasBycountryISO3166A2Code("HU", -1L, -1L);
        Assert.assertNotNull(areaList);
        Assert.assertTrue(areaList.isEmpty());

        areaList = phoneNumberService.listActiveAreasBycountryISO3166A2Code("HU", null, -1L);
        Assert.assertNotNull(areaList);
        Assert.assertTrue(areaList.isEmpty());

        areaList = phoneNumberService.listActiveAreasBycountryISO3166A2Code("HU", -1L, 10L);
        Assert.assertNotNull(areaList);
        Assert.assertTrue(areaList.isEmpty());

        areaList = phoneNumberService.listActiveAreasBycountryISO3166A2Code("HU", 1L, -10L);
        Assert.assertNotNull(areaList);
        Assert.assertTrue(areaList.isEmpty());

        areaList = phoneNumberService.listActiveAreasBycountryISO3166A2Code("HU", 5L, null);
        Assert.assertNotNull(areaList);
        Assert.assertTrue(areaList.isEmpty());

        areaList = phoneNumberService.listActiveAreasBycountryISO3166A2Code("HU", 5L, 5L);
        Assert.assertNotNull(areaList);
        Assert.assertTrue(areaList.isEmpty());

        areaList = phoneNumberService.listActiveAreasBycountryISO3166A2Code("HU", null, 5L);
        Assert.assertNotNull(areaList);
        Assert.assertFalse(areaList.isEmpty());
        Assert.assertEquals(areaHUCount, areaList.size());

        try {
            phoneNumberService.listActiveAreasBycountryISO3166A2Code(null, null, null);
            Assert.fail("Expect IllegalArgumentException, but the method not throws.");
        } catch (IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }
    }

    void testCountry() {
        for (Country c : countris) {
            phoneNumberService.saveCountry(c.getCountryISO3166A2Code(), c.getIddPrefix(), c.getNddPrefix(),
                    c.getCountryCallCode());
            Country country = phoneNumberService.getCountry(c.getCountryISO3166A2Code());
            Assert.assertNotNull(country);
        }
        Country c = countris.get(0);

        try {
            phoneNumberService.saveCountry(c.getCountryISO3166A2Code(), c.getIddPrefix(), c.getNddPrefix(),
                    c.getCountryCallCode());
            Assert.fail("Expect DuplicateCountryException, but the method not throws.");
        } catch (DuplicateCountryException e) {
            Assert.assertNotNull(e);
        }

        try {
            phoneNumberService.saveCountry("asdfasdfasdfasdf", c.getIddPrefix(), c.getNddPrefix(),
                    c.getCountryCallCode());
            Assert.fail("Expect PersistenceException, but the method not throws.");
        } catch (PersistenceException e) {
            Assert.assertNotNull(e);
        }

        try {
            phoneNumberService.saveCountry(null, c.getIddPrefix(), c.getNddPrefix(),
                    c.getCountryCallCode());
            Assert.fail("Expect IllegalArgumentException, but the method not throws.");
        } catch (IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }

        try {
            phoneNumberService.saveCountry(c.getCountryISO3166A2Code(), null, c.getNddPrefix(),
                    c.getCountryCallCode());
            Assert.fail("Expect IllegalArgumentException, but the method not throws.");
        } catch (IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }

        try {
            phoneNumberService.saveCountry(c.getCountryISO3166A2Code(), c.getIddPrefix(), null,
                    c.getCountryCallCode());
            Assert.fail("Expect IllegalArgumentException, but the method not throws.");
        } catch (IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }

        try {
            phoneNumberService.saveCountry(c.getCountryISO3166A2Code(), c.getIddPrefix(), c.getNddPrefix(),
                    null);
            Assert.fail("Expect IllegalArgumentException, but the method not throws.");
        } catch (IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }

        try {
            phoneNumberService.getCountry(null);
            Assert.fail("Expect IllegalArgumentException, but the method not throws.");
        } catch (IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }

        Country country = phoneNumberService.getCountry("EN");
        Assert.assertNull(country);

        country = phoneNumberService.getCountry("");
        Assert.assertNull(country);

        country = phoneNumberService.getCountry("NOTCOUNTRYCODE");
        Assert.assertNull(country);

        List<Country> countriesList = phoneNumberService.listActiveCountries(null, null);
        Assert.assertNotNull(countriesList);
        int countriresSize = countriesList.size();

        countriesList = phoneNumberService.listActiveCountries(1L, null);
        Assert.assertNotNull(countriesList);
        Assert.assertEquals(countriresSize - 1, countriesList.size());

        countriesList = phoneNumberService.listActiveCountries(null, 1L);
        Assert.assertNotNull(countriesList);
        Assert.assertEquals(1, countriesList.size());

        countriesList = phoneNumberService.listActiveCountries(1L, 1L);
        Assert.assertNotNull(countriesList);
        Assert.assertEquals(1, countriesList.size());

        countriesList = phoneNumberService.listActiveCountries(null, -1L);
        Assert.assertNotNull(countriesList);
        Assert.assertTrue(countriesList.isEmpty());

        countriesList = phoneNumberService.listActiveCountries(-1L, null);
        Assert.assertNotNull(countriesList);
        Assert.assertTrue(countriesList.isEmpty());

        countriesList = phoneNumberService.listActiveCountries(-1L, -1L);
        Assert.assertNotNull(countriesList);
        Assert.assertTrue(countriesList.isEmpty());

        countriesList = phoneNumberService.listActiveCountries(1L, -1L);
        Assert.assertNotNull(countriesList);
        Assert.assertTrue(countriesList.isEmpty());

        countriesList = phoneNumberService.listActiveCountries(-1L, 1L);
        Assert.assertNotNull(countriesList);
        Assert.assertTrue(countriesList.isEmpty());

    }

    void testPhoneNumber() {
        List<Long> phoneNumberIdsList = new ArrayList<Long>();
        for (String sn : phoneNumbers) {
            int index = 0;
            for (Area a : areas) {
                if (sn.length() == a.getSubscriberNumberLength()) {
                    long savePhoneNumber = phoneNumberService.savePhoneNumber(areaIds.get(index), sn, null);
                    Assert.assertTrue(savePhoneNumber > 0L);
                    phoneNumberIdsList.add(savePhoneNumber);
                    CallablePhoneNumber callablePhoneNumber = phoneNumberService
                            .getCallablePhoneNumber(savePhoneNumber);
                    Assert.assertNotNull(callablePhoneNumber);
                    Assert.assertEquals(sn, callablePhoneNumber.getSubscriberNumber());

                    String reverse = new StringBuilder(sn).reverse().toString();
                    phoneNumberService.updatePhoneNumber(savePhoneNumber, areaIds.get(index), reverse, null);
                    callablePhoneNumber = phoneNumberService
                            .getCallablePhoneNumber(savePhoneNumber);
                    Assert.assertNotNull(callablePhoneNumber);
                    Assert.assertEquals(reverse, callablePhoneNumber.getSubscriberNumber());
                    break;
                }
                index++;
            }
        }
        try {
            phoneNumberService.savePhoneNumber(0L, phoneNumbers.get(0), null);
            Assert.fail("Expect NoSuchAreaException, but the method not throws.");
        } catch (NoSuchAreaException e) {
            Assert.assertNotNull(e);
        }

        try {
            phoneNumberService.savePhoneNumber(1L, null, "");
            Assert.fail("Expect IllegalArgumentException, but the method not throws.");
        } catch (IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }

        try {
            phoneNumberService.savePhoneNumber(1L, "", null);
            Assert.fail("Expect InvalidNumberException, but the method not throws.");
        } catch (InvalidNumberException e) {
            Assert.assertNotNull(e);
        }

        try {
            phoneNumberService.updatePhoneNumber(0L, 1L, "", "");
            Assert.fail("Expect NoSuchPhoneNumberException, but the method not throws.");
        } catch (NoSuchPhoneNumberException e) {
            Assert.assertNotNull(e);
        }

        try {
            phoneNumberService.updatePhoneNumber(1L, 1L, null, "");
            Assert.fail("Expect IllegalArgumentException, but the method not throws.");
        } catch (IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }

        try {
            phoneNumberService.updatePhoneNumber(1L, 0L, "", "");
            Assert.fail("Expect NoSuchAreaException, but the method not throws.");
        } catch (NoSuchAreaException e) {
            Assert.assertNotNull(e);
        }

        try {
            phoneNumberService.updatePhoneNumber(1L, 1L, "", "");
            Assert.fail("Expect InvalidNumberException, but the method not throws.");
        } catch (InvalidNumberException e) {
            Assert.assertNotNull(e);
        }
    }
}
