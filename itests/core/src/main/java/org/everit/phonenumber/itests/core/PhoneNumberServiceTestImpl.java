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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.persistence.PersistenceException;

import junit.framework.Assert;

import org.everit.phonenumber.api.PhoneNumberService;
import org.everit.phonenumber.api.PhoneVerificationResult;
import org.everit.phonenumber.api.dto.Area;
import org.everit.phonenumber.api.dto.CallablePhoneNumber;
import org.everit.phonenumber.api.dto.Country;
import org.everit.phonenumber.api.enums.PhoneNumberConfirmationResult;
import org.everit.phonenumber.api.exceptions.DuplicateCountryException;
import org.everit.phonenumber.api.exceptions.DuplicateSelectableAreaException;
import org.everit.phonenumber.api.exceptions.InvalidPhoneNumberException;
import org.everit.phonenumber.api.exceptions.NoSuchAreaException;
import org.everit.phonenumber.api.exceptions.NoSuchPhoneNumberException;
import org.everit.phonenumber.api.exceptions.NonPositiveSubscriberNumberLengthException;
import org.everit.smssender.dummy.core.api.DummySMSSender;
import org.everit.smssender.dummy.core.api.dto.DummySMS;
import org.everit.verifiabledata.api.enums.VerificationLengthBase;
import org.everit.verifiabledata.api.exceptions.NonPositiveVerificationLength;

/**
 * Implementation of the {@link PhoneNumberServiceTest}.
 */
public class PhoneNumberServiceTestImpl implements PhoneNumberServiceTest {

    /**
     * List of the test countries. http://www.findacrew.net/secure-server/eng/dialing.asp
     */
    private static final List<Country> COUNTRIES = Arrays.asList(new Country("HU", "00", "06", "36", true),
            new Country("DE", "00", "0", "49", true),
            new Country("HK", "001", "", "852", true),
            new Country("UZ", "8P10", "8", "998", true),
            new Country("EH", "", "", "212", true),
            new Country("VI", "011", "", "1-340", true),
            new Country("BO", "0010", "010", "591", true),
            new Country("TD", "15", "", "235", true),
            new Country("CU", "119", "0", "53", true),
            new Country("SI", "00", "0", "421", true));

    /**
     * List of the test areas.
     */
    private static final List<Area> AREAS = Arrays.asList(new Area("HU", "20", "Telenor", 7, true),
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

    /**
     * List of the phone numbers.
     */
    private static final List<String> PHONENUMBERS = Arrays.asList("12", "12345", "1234567", "12345678",
            "123456789012345");

    /**
     * The saved area ids.
     */
    private static List<Long> areaIdsList = new ArrayList<Long>();

    /**
     * The saved phone number ids.
     */
    private static List<Long> phoneNumberIdsList = new ArrayList<Long>();

    /**
     * The maximum value of the random.
     */
    private static final int MAX_RANDOM_VALUE = 1000000;

    /**
     * The {@link PhoneNumberService} instance.
     */
    private PhoneNumberService phoneNumberService;

    /**
     * The {@link DummySMSSender} instance.
     */
    private DummySMSSender dummySMSSender;

    private void areaTestGetActiveAreaErrors() {
        Area a = AREAS.get(0);
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
    }

    private void areaTestGetAreaSuccess() {
        for (Long id : areaIdsList) {
            Area area = phoneNumberService.getAreaById(id);
            Assert.assertNotNull(area);
            area = phoneNumberService.getAreaById(0L);
            Assert.assertNull(area);
        }
    }

    private void areaTestListActiveAreasErrors() {
        try {
            phoneNumberService.listActiveAreasBycountryISO3166A2Code("HU", -1L, null);
            Assert.fail("Expect IllegalArgumentException, but not throw the method.");
        } catch (IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }

        try {
            phoneNumberService.listActiveAreasBycountryISO3166A2Code("HU", -1L, -1L);
            Assert.fail("Expect IllegalArgumentException, but not throw the method.");
        } catch (IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }

        try {
            phoneNumberService.listActiveAreasBycountryISO3166A2Code("HU", null, -1L);
            Assert.fail("Expect IllegalArgumentException, but not throw the method.");
        } catch (IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }

        try {
            phoneNumberService.listActiveAreasBycountryISO3166A2Code("HU", -1L, 1L);
            Assert.fail("Expect IllegalArgumentException, but not throw the method.");
        } catch (IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }

        try {
            phoneNumberService.listActiveAreasBycountryISO3166A2Code("HU", 1L, -1L);
            Assert.fail("Expect IllegalArgumentException, but not throw the method.");
        } catch (IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }

        try {
            phoneNumberService.listActiveAreasBycountryISO3166A2Code(null, null, null);
            Assert.fail("Expect IllegalArgumentException, but the method not throws.");
        } catch (IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }
    }

    private void areaTestListActiveAreasSuccess() {
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

        areaList = phoneNumberService.listActiveAreasBycountryISO3166A2Code("HU", null, 1L);
        Assert.assertNotNull(areaList);
        Assert.assertFalse(areaList.isEmpty());
        Assert.assertEquals(1, areaList.size());
    }

    private void areaTestSaveAreaErrors() {
        Area a = AREAS.get(0);
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
    }

    private void areaTestSaveAreaSuccess() {
        for (Area a : AREAS) {
            long saveArea = phoneNumberService.saveArea(a.getCountryCode(), a.getCallNumber(), a.getName(),
                    a.getSubscriberNumberLength());
            Assert.assertTrue(saveArea > 0L);
            areaIdsList.add(saveArea);
            Area area = phoneNumberService.getActiveAreaByCountryAndCallNumber(a.getCountryCode(), a.getCallNumber());
            Assert.assertNotNull(area);
            Assert.assertTrue(area.isActive());
        }
    }

    private void countryTestGetCountryErrors() {
        try {
            phoneNumberService.getCountry(null);
            Assert.fail("Expect IllegalArgumentException, but the method not throws.");
        } catch (IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }
    }

    private void countryTestGetCountrySuccess() {
        Country country = phoneNumberService.getCountry("EN");
        Assert.assertNull(country);

        country = phoneNumberService.getCountry("");
        Assert.assertNull(country);

        country = phoneNumberService.getCountry("NOTCOUNTRYCODE");
        Assert.assertNull(country);
    }

    private void countryTestListActiveCountriesErrors() {
        try {
            phoneNumberService.listActiveCountries(null, -1L);
            Assert.fail("Expect IllegalArgumentException, but not throw the method.");
        } catch (IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }

        try {
            phoneNumberService.listActiveCountries(-1L, null);
            Assert.fail("Expect IllegalArgumentException, but not throw the method.");
        } catch (IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }

        try {
            phoneNumberService.listActiveCountries(-1L, -1L);
            Assert.fail("Expect IllegalArgumentException, but not throw the method.");
        } catch (IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }

        try {
            phoneNumberService.listActiveCountries(1L, -1L);
            Assert.fail("Expect IllegalArgumentException, but not throw the method.");
        } catch (IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }

        try {
            phoneNumberService.listActiveCountries(-1L, 1L);
            Assert.fail("Expect IllegalArgumentException, but not throw the method.");
        } catch (IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }
    }

    private void countryTestListActiveCountriesSuccess() {
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
    }

    private void countryTestSaveCountryErrors() {
        Country c = COUNTRIES.get(0);

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
    }

    private void countryTestSaveCountrySuccess() {
        for (Country c : COUNTRIES) {
            phoneNumberService.saveCountry(c.getCountryISO3166A2Code(), c.getIddPrefix(), c.getNddPrefix(),
                    c.getCountryCallCode());
            Country country = phoneNumberService.getCountry(c.getCountryISO3166A2Code());
            Assert.assertNotNull(country);
        }
    }

    private Date getExpiredTokenValidityEndDate() {
        Date actualDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(actualDate);
        c.add(Calendar.MILLISECOND, 150);
        return c.getTime();
    }

    private Date getNotExpiredTokenValidityEndDate() {
        Date actualDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(actualDate);
        c.add(Calendar.DATE, 1);
        return c.getTime();
    }

    /**
     * Select random {@link VerificationLengthBase}.
     * 
     * @return the random {@link VerificationLengthBase}.
     */
    private VerificationLengthBase getRandomVerificationLengthBase() {
        VerificationLengthBase result = null;
        Random random = new Random();
        int number = random.nextInt(2);
        if (number == 0) {
            result = VerificationLengthBase.REQUEST_CREATION;
        } else {
            result = VerificationLengthBase.VERIFICATION;
        }
        return result;
    }

    private void internalTestArea() {
        areaTestSaveAreaSuccess();

        areaTestSaveAreaErrors();

        areaTestGetAreaSuccess();

        areaTestGetActiveAreaErrors();

        areaTestListActiveAreasSuccess();

        areaTestListActiveAreasErrors();
    }

    private void internalTestCountry() {
        countryTestSaveCountrySuccess();

        countryTestSaveCountryErrors();

        countryTestGetCountrySuccess();

        countryTestGetCountryErrors();

        countryTestListActiveCountriesSuccess();

        countryTestListActiveCountriesErrors();
    }

    private void internalTestPhoneNumber() {
        phoneNumberTestSavePhoneNumberAndUpdatePhoneNumberSuccess();

        phoneNumberTestSavePhoneNumberErrors();

        phoneNumberTestUpdatePhoneNumberErrors();
    }

    private void internalTestVerifiablePhone() {
        int halfListNumber = phoneNumberIdsList.size() / 2;
        List<DummySMS> expiredVerifiablePhoneSMSs = new ArrayList<DummySMS>();
        for (int i = 0; i < halfListNumber; i++) {
            phoneNumberService.createVerificationRequestViaSMS(phoneNumberIdsList.get(i), "$acceptToken\n$rejectToken",
                    getExpiredTokenValidityEndDate(), 1L, getRandomVerificationLengthBase());
            DummySMS latestDummySMS = dummySMSSender.getLatestDummySMS();
            Assert.assertNotNull(latestDummySMS);
            expiredVerifiablePhoneSMSs.add(latestDummySMS);
        }

        verifiablePhoneTestCreateVerificationRequestAndVerifyPhoneNumberSuccess();

        verifiablePhoneTestCreateVerificationsRequestErrors();

        verifiablePhoneTestVerifyPhoneNumberErrors();

        boolean change = true;
        for (DummySMS sms : expiredVerifiablePhoneSMSs) {
            String message = sms.getMessage();
            String[] splitMessage = message.split("\n");
            PhoneVerificationResult verifyPhoneNumber = null;
            if (change) {
                verifyPhoneNumber = phoneNumberService.verifyPhoneNumber(splitMessage[0]);
            } else {
                verifyPhoneNumber = phoneNumberService.verifyPhoneNumber(splitMessage[1]);
            }
            Assert.assertNotNull(verifyPhoneNumber);
            Assert.assertEquals(PhoneNumberConfirmationResult.FAILED, verifyPhoneNumber.getResult());
            change = !change;
        }
    }

    private void phoneNumberTestSavePhoneNumberAndUpdatePhoneNumberSuccess() {
        for (String sn : PHONENUMBERS) {
            int index = 0;
            for (Area a : AREAS) {
                if (sn.length() == a.getSubscriberNumberLength()) {
                    long savePhoneNumber = phoneNumberService.savePhoneNumber(areaIdsList.get(index), sn, null);
                    Assert.assertTrue(savePhoneNumber > 0L);
                    phoneNumberIdsList.add(savePhoneNumber);
                    CallablePhoneNumber callablePhoneNumber = phoneNumberService
                            .getCallablePhoneNumber(savePhoneNumber);
                    Assert.assertNotNull(callablePhoneNumber);
                    Assert.assertEquals(sn, callablePhoneNumber.getSubscriberNumber());

                    String reverse = new StringBuilder(sn).reverse().toString();
                    phoneNumberService.updatePhoneNumber(savePhoneNumber, areaIdsList.get(index), reverse, null);
                    callablePhoneNumber = phoneNumberService
                            .getCallablePhoneNumber(savePhoneNumber);
                    Assert.assertNotNull(callablePhoneNumber);
                    Assert.assertEquals(reverse, callablePhoneNumber.getSubscriberNumber());
                    break;
                }
                index++;
            }
        }
    }

    private void phoneNumberTestSavePhoneNumberErrors() {
        try {
            phoneNumberService.savePhoneNumber(0L, PHONENUMBERS.get(0), null);
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
        } catch (InvalidPhoneNumberException e) {
            Assert.assertNotNull(e);
        }
    }

    private void phoneNumberTestUpdatePhoneNumberErrors() {
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
        } catch (InvalidPhoneNumberException e) {
            Assert.assertNotNull(e);
        }
    }

    public void setDummySMSSender(final DummySMSSender dummySMSSender) {
        this.dummySMSSender = dummySMSSender;
    }

    public void setPhoneNumberService(final PhoneNumberService phoneNumberService) {
        this.phoneNumberService = phoneNumberService;
    }

    @Override
    public void test() {
        internalTestCountry();
        internalTestArea();
        internalTestPhoneNumber();
        internalTestVerifiablePhone();
    }

    private void verifiablePhoneTestCreateVerificationRequestAndVerifyPhoneNumberSuccess() {
        Random random = new Random();
        int halfListNumber = phoneNumberIdsList.size() / 2;
        boolean change = true;
        for (int i = halfListNumber; i < phoneNumberIdsList.size(); i++) {
            phoneNumberService.createVerificationRequestViaSMS(phoneNumberIdsList.get(i), "$acceptToken\n$rejectToken",
                    getNotExpiredTokenValidityEndDate(), random.nextInt(MAX_RANDOM_VALUE),
                    getRandomVerificationLengthBase());
            DummySMS latestDummySMS = dummySMSSender.getLatestDummySMS();
            Assert.assertNotNull(latestDummySMS);
            String message = latestDummySMS.getMessage();
            String[] splitMessage = message.split("\n");
            PhoneVerificationResult verifyPhoneNumber = null;
            if (change) {
                verifyPhoneNumber = phoneNumberService.verifyPhoneNumber(splitMessage[0]);
                Assert.assertNotNull(verifyPhoneNumber);
                Assert.assertEquals(PhoneNumberConfirmationResult.SUCCESS, verifyPhoneNumber.getResult());
            } else {
                verifyPhoneNumber = phoneNumberService.verifyPhoneNumber(splitMessage[1]);
                Assert.assertNotNull(verifyPhoneNumber);
                Assert.assertEquals(PhoneNumberConfirmationResult.REJECTED, verifyPhoneNumber.getResult());
            }
            change = !change;
            CallablePhoneNumber callablePhone = phoneNumberService
                    .getCallablePhoneNumberByVerifiableId(verifyPhoneNumber.getVerifiablePhoneId());
            Assert.assertNotNull(callablePhone);
        }

        PhoneVerificationResult verifyPhoneNumber = phoneNumberService.verifyPhoneNumber("Test-UUID");
        Assert.assertNotNull(verifyPhoneNumber);
        Assert.assertEquals(PhoneNumberConfirmationResult.FAILED, verifyPhoneNumber.getResult());
        Assert.assertNull(verifyPhoneNumber.getVerifiablePhoneId());
    }

    private void verifiablePhoneTestCreateVerificationsRequestErrors() {
        try {
            phoneNumberService.createVerificationRequestViaSMS(1L, null, getNotExpiredTokenValidityEndDate(), 1L,
                    getRandomVerificationLengthBase());
            Assert.fail("Expect IllegalArgumentException, but the method not throws.");
        } catch (IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }

        try {
            phoneNumberService.createVerificationRequestViaSMS(1L, "", null, 1L, getRandomVerificationLengthBase());
            Assert.fail("Expect IllegalArgumentException, but the method not throws.");
        } catch (IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }

        try {
            phoneNumberService.createVerificationRequestViaSMS(1L, "", getNotExpiredTokenValidityEndDate(), 1L, null);
            Assert.fail("Expect IllegalArgumentException, but the method not throws.");
        } catch (IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }

        try {
            phoneNumberService.createVerificationRequestViaSMS(0L, "", getNotExpiredTokenValidityEndDate(), 1L,
                    getRandomVerificationLengthBase());
            Assert.fail("Expect NoSuchPhoneNumberException, but the method not throws.");
        } catch (NoSuchPhoneNumberException e) {
            Assert.assertNotNull(e);
        }

        try {
            phoneNumberService.createVerificationRequestViaSMS(1L, "", getNotExpiredTokenValidityEndDate(), 0L,
                    getRandomVerificationLengthBase());
            Assert.fail("Expect NonPositiveVerificationLength, but the method not throws.");
        } catch (NonPositiveVerificationLength e) {
            Assert.assertNotNull(e);
        }
    }

    private void verifiablePhoneTestVerifyPhoneNumberErrors() {
        try {
            phoneNumberService.verifyPhoneNumber(null);
            Assert.fail("Expect IllegalArgumentException, but the method not throws.");
        } catch (IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }
    }
}
