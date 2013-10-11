package org.everit.phonenumber.core;

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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.everit.phonenumber.api.PhoneNumberService;
import org.everit.phonenumber.api.PhoneVerificationResult;
import org.everit.phonenumber.api.dto.Area;
import org.everit.phonenumber.api.dto.CallablePhoneNumber;
import org.everit.phonenumber.api.dto.Country;
import org.everit.phonenumber.api.enums.PhoneNumberConfirmationResult;
import org.everit.phonenumber.api.enums.VerificationChannel;
import org.everit.phonenumber.api.exceptions.DuplicateCountryException;
import org.everit.phonenumber.api.exceptions.DuplicateSelectableAreaException;
import org.everit.phonenumber.api.exceptions.InvalidPhoneNumberException;
import org.everit.phonenumber.api.exceptions.NoSuchAreaException;
import org.everit.phonenumber.api.exceptions.NoSuchPhoneNumberException;
import org.everit.phonenumber.api.exceptions.NonPositiveSubscriberNumberLengthException;
import org.everit.phonenumber.entity.PhoneNumberAreaEntity;
import org.everit.phonenumber.entity.PhoneNumberAreaEntity_;
import org.everit.phonenumber.entity.PhoneNumberCountryEntity;
import org.everit.phonenumber.entity.PhoneNumberCountryEntity_;
import org.everit.phonenumber.entity.PhoneNumberEntity;
import org.everit.phonenumber.entity.PhoneNumberEntity_;
import org.everit.phonenumber.entity.PhoneNumberVerifiablePhoneEntity;
import org.everit.phonenumber.entity.PhoneNumberVerifiablePhoneEntity_;
import org.everit.phonenumber.entity.PhoneNumberVerificationRequestEntity;
import org.everit.smssender.api.MessageFormat;
import org.everit.smssender.api.SMSSender;
import org.everit.util.core.velocity.VelocityUtil;
import org.everit.verifiabledata.api.VerifyService;
import org.everit.verifiabledata.api.dto.VerifiableDataCreation;
import org.everit.verifiabledata.api.dto.VerificationResult;
import org.everit.verifiabledata.api.enums.TokenUsageResult;
import org.everit.verifiabledata.api.enums.VerificationLengthBase;
import org.everit.verifiabledata.api.exceptions.NoSuchVerifiableDataException;
import org.everit.verifiabledata.api.exceptions.NoSuchVerificationRequestException;
import org.everit.verifiabledata.api.exceptions.NonPositiveVerificationLength;
import org.everit.verifiabledata.entity.VerifiableDataEntity;
import org.everit.verifiabledata.entity.VerifiableDataEntity_;
import org.everit.verifiabledata.entity.VerificationRequestEntity;
import org.everit.verifiabledata.entity.VerificationRequestEntity_;

/**
 * Implementation of the {@link PhoneNumberService}.
 */
public class PhoneNumberServiceImpl implements PhoneNumberService {

    /**
     * Setting the firstResult and maxResult attribute the query.
     * 
     * @param query
     *            the query what to setting.
     * @param startPosition
     *            the first index of the list or null if not defined.
     * @param maxResultCount
     *            the maximum number of elements or null if not defined.
     */
    private static void applyRangeToQuery(final TypedQuery<?> query, final Long startPosition,
            final Long maxResultCount) {
        if (startPosition != null) {
            query.setFirstResult(startPosition.intValue());
        }

        if (maxResultCount != null) {
            query.setMaxResults(maxResultCount.intValue());
        }
    }

    /**
     * EntityManager set by blueprint.
     */
    private EntityManager em;

    /**
     * The {@link VerifyService} instance.
     */
    private VerifyService verifyService;

    /**
     * The {@link SMSSender} instance.
     */
    private SMSSender smsSender;

    @Override
    public void createVerificationRequestViaSMS(final long phoneNumberId, final String messagetemplate,
            final Date tokenValidityEndDate,
            final long verificationLength, final VerificationLengthBase verificationLengthBase) {
        if ((messagetemplate == null) || (tokenValidityEndDate == null) || (verificationLengthBase == null)) {
            throw new IllegalArgumentException("The messagetemplate or tokenValidityEndData or"
                    + " verificationLengthBase is null. Cannot be null.");
        }

        if (!existPhoneNumber(phoneNumberId)) {
            throw new NoSuchPhoneNumberException();
        }

        if (verificationLength <= 0.0) {
            throw new NonPositiveVerificationLength();
        }

        VerifiableDataCreation verifiableDataCreation = verifyService.createVerifiableData(tokenValidityEndDate,
                verificationLength, verificationLengthBase);
        if (verifiableDataCreation != null) {
            saveVerifiablePhone(phoneNumberId, verifiableDataCreation.getVerifiableDataId(),
                    verifiableDataCreation.getVerificationRequest().getVerificationRequestId(),
                    VerificationChannel.SMS);

            em.flush();
            Map<String, Object> variables = new HashMap<String, Object>();
            variables.put("acceptToken", verifiableDataCreation.getVerificationRequest().getVerifyTokenUUID());
            variables.put("rejectToken", verifiableDataCreation.getVerificationRequest().getRejectTokenUUID());
            String messageBody = VelocityUtil.processVelocityTemplateFromString(messagetemplate, "ERROR",
                    variables);

            CallablePhoneNumber callablePhoneNumber = getCallablePhoneNumberByPhoneNumberId(phoneNumberId);

            smsSender.sendMessage(callablePhoneNumber.getCountryCallCode(), callablePhoneNumber.getAreaCallNumber(),
                    callablePhoneNumber.getSubscriberNumber(), callablePhoneNumber.getExtension(), messageBody,
                    MessageFormat.UNICODE, true);
        }

    }

    /**
     * /** Determine the phone number confirmation result.
     * 
     * @param tokenUsageResult
     *            the {@link TokenUsageResult} object.
     * @return the {@link PhoneNumberConfirmationResult} object.
     */
    private PhoneNumberConfirmationResult determinePhoneNumberConfirmationResult(
            final TokenUsageResult tokenUsageResult) {
        PhoneNumberConfirmationResult result = null;
        if (tokenUsageResult.equals(TokenUsageResult.VERIFIED)) {
            result = PhoneNumberConfirmationResult.SUCCESS;
        } else if (tokenUsageResult.equals(TokenUsageResult.REJECTED)) {
            result = PhoneNumberConfirmationResult.REJECTED;
        } else {
            result = PhoneNumberConfirmationResult.FAILED;
        }
        return result;
    }

    /**
     * Checks the exist the area based on country code (ISO3166-alpha-2) and call number.
     * 
     * @param countryISO3166A2Code
     *            the country code (ISO3166-alpha-2).
     * @param callNumber
     *            the area call number.
     * @return <code>true</code> if exist area, otherwise return <code>false</code>.
     */
    private boolean existActiveAreaByCountryCodeAndCallNumber(final String countryISO3166A2Code,
            final String callNumber) {
        Long areaId = getActiveAreaIdByCountryCodeAndCallNumber(countryISO3166A2Code, callNumber);
        if (areaId != null) {
            return true;
        }
        return false;
    }

    /**
     * Checks the country exist or not.
     * 
     * @param countryISO3166A2Code
     *            the country code (ISO3166-alpha-2).
     * @return <code>true</code> if exist country, otherwise return <code>false</code>.
     */
    private boolean existCountry(final String countryISO3166A2Code) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> criteriaQuery = cb.createQuery(String.class);

        Root<PhoneNumberCountryEntity> root =
                criteriaQuery.from(PhoneNumberCountryEntity.class);

        criteriaQuery.select(root.get(PhoneNumberCountryEntity_.iddPrefix));

        Predicate predicate = cb.equal(root.get(PhoneNumberCountryEntity_.countryISO3166A2Code), countryISO3166A2Code);

        criteriaQuery.where(predicate);
        List<String> resultList = em.createQuery(criteriaQuery).getResultList();
        if (resultList.size() == 1) {
            return true;
        }
        return false;
    }

    /**
     * Checks the phone number exist or not.
     * 
     * @param phoneNumberId
     *            the id of the phone number record.
     * @return <code>true</code> if exist phone number, otherwise return <code>false</code>.
     */
    private boolean existPhoneNumber(final long phoneNumberId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);

        Root<PhoneNumberEntity> root =
                criteriaQuery.from(PhoneNumberEntity.class);

        criteriaQuery.select(root.get(PhoneNumberEntity_.phoneNumberId));

        Predicate predicate = cb.equal(root.get(PhoneNumberEntity_.phoneNumberId), phoneNumberId);

        criteriaQuery.where(predicate);
        List<Long> resultList = em.createQuery(criteriaQuery).getResultList();
        if (resultList.size() == 1) {
            return true;
        }
        return false;
    }

    /**
     * Checks the verifiable data is exist or not.
     * 
     * @param verifiableDataId
     *            the id of the verifiable data.
     * @return <code>true</code> if exist verifiable data, otherwise return <code>false</code>.
     */
    private boolean existVerifiableData(final long verifiableDataId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);

        Root<VerifiableDataEntity> root =
                criteriaQuery.from(VerifiableDataEntity.class);

        criteriaQuery.select(root.get(VerifiableDataEntity_.verifiableDataId));

        Predicate predicate = cb.equal(root.get(VerifiableDataEntity_.verifiableDataId), verifiableDataId);

        criteriaQuery.where(predicate);
        List<Long> resultList = em.createQuery(criteriaQuery).getResultList();
        if (resultList.size() == 1) {
            return true;
        }
        return false;
    }

    /**
     * Checks the verification request exist or not.
     * 
     * @param verificationRequestId
     *            the id of the verification request.
     * @return <code>true</code> if exist verification request, otherwise return <code>false</code>.
     */
    private boolean existVerificationRequest(final long verificationRequestId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);

        Root<VerificationRequestEntity> root =
                criteriaQuery.from(VerificationRequestEntity.class);

        criteriaQuery.select(root.get(VerificationRequestEntity_.verificationRequestId));

        Predicate predicate = cb.equal(root.get(VerificationRequestEntity_.verificationRequestId),
                verificationRequestId);

        criteriaQuery.where(predicate);
        List<Long> resultList = em.createQuery(criteriaQuery).getResultList();
        if (resultList.size() == 1) {
            return true;
        }
        return false;
    }

    /**
     * Finds the {@link PhoneNumberEntity} based on phone number id.
     * 
     * @param phoneNumberId
     *            the id of the phone number.
     * @return the {@link PhoneNumberEntity} object if exist, otherwise return <code>null</code>.
     */
    private PhoneNumberEntity findPhoneNumberEntityByPhoneNumberId(final long phoneNumberId) {
        return em.find(PhoneNumberEntity.class, phoneNumberId);
    }

    /**
     * Get the active area based on area id.
     * 
     * @param areaId
     *            the id of the area.
     * @return the Area object if exist, otherwise return <code>null</code>.
     */
    private Area getActiveAreaByAreaId(final long areaId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Area> criteriaQuery = cb.createQuery(Area.class);

        Root<PhoneNumberAreaEntity> root =
                criteriaQuery.from(PhoneNumberAreaEntity.class);
        Join<PhoneNumberAreaEntity, PhoneNumberCountryEntity> pnc = root
                .join(PhoneNumberAreaEntity_.phoneNumberCountry);
        criteriaQuery.multiselect(pnc.get(PhoneNumberCountryEntity_.countryISO3166A2Code),
                root.get(PhoneNumberAreaEntity_.callNumber), root.get(PhoneNumberAreaEntity_.areaName),
                root.get(PhoneNumberAreaEntity_.subscriberNumberLength), root.get(PhoneNumberAreaEntity_.active));
        Predicate predicate = cb.equal(root.get(PhoneNumberAreaEntity_.phoneAreaId), areaId);

        criteriaQuery.where(predicate);
        List<Area> resultList = em.createQuery(criteriaQuery).getResultList();
        if (resultList.size() == 1) {
            return resultList.get(0);
        }
        return null;
    }

    @Override
    public Area getActiveAreaByCountryAndCallNumber(final String countryISO3166A2Code, final String areaCallNumber) {
        if ((countryISO3166A2Code == null) || (areaCallNumber == null)) {
            throw new IllegalArgumentException("The countryISO3166A2Code or areaCallNumber parameter is null.");
        }
        return getActiveAreaByAreaId(getActiveAreaIdByCountryCodeAndCallNumber(countryISO3166A2Code, areaCallNumber));
    }

    /**
     * Get the active area id based on country code (ISO3166-alpha-2) and area call code.
     * 
     * @param countryISO3166A2Code
     *            the country code (ISO3166-alpha-2).
     * @param callNumber
     *            the area call code.
     * @return the active area id if exist, otherwise return <code>null</code>.
     */
    private Long getActiveAreaIdByCountryCodeAndCallNumber(final String countryISO3166A2Code,
            final String callNumber) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);

        Root<PhoneNumberAreaEntity> root =
                criteriaQuery.from(PhoneNumberAreaEntity.class);

        criteriaQuery.select(root.get(PhoneNumberAreaEntity_.phoneAreaId));

        Predicate predicate = cb.equal(root.get(PhoneNumberAreaEntity_.callNumber),
                callNumber);
        Predicate and = cb.and(
                predicate,
                cb.equal(root.get(PhoneNumberAreaEntity_.phoneNumberCountry),
                        em.getReference(PhoneNumberCountryEntity.class, countryISO3166A2Code)));

        criteriaQuery.where(cb.and(and, cb.equal(root.get(PhoneNumberAreaEntity_.active), true)));
        List<Long> resultList = em.createQuery(criteriaQuery).getResultList();
        if (resultList.size() == 1) {
            return resultList.get(0);
        }
        return null;
    }

    /**
     * Get active areas based on country code (ISO3166-alpha-2).
     * 
     * @param countryISO3166A2Code
     *            the country code (ISO3166-alpha-2).
     * @param startPosition
     *            the first index of the list or null if not defined.
     * @param maxResultCount
     *            the maximum number of elements or null if not defined.
     * @return the active Areas in list. If no one return empty list.
     */
    private List<Area> getActiveAreasByCountryCode(final String countryISO3166A2Code,
            final Long startPosition, final Long maxResultCount) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Area> criteriaQuery = cb.createQuery(Area.class);
        Root<PhoneNumberAreaEntity> root = criteriaQuery
                .from(PhoneNumberAreaEntity.class);
        Join<PhoneNumberAreaEntity, PhoneNumberCountryEntity> pnc = root
                .join(PhoneNumberAreaEntity_.phoneNumberCountry);
        criteriaQuery.multiselect(pnc.get(PhoneNumberCountryEntity_.countryISO3166A2Code),
                root.get(PhoneNumberAreaEntity_.callNumber), root.get(PhoneNumberAreaEntity_.areaName),
                root.get(PhoneNumberAreaEntity_.subscriberNumberLength), root.get(PhoneNumberAreaEntity_.active));
        Predicate predicate = cb.equal(root.get(PhoneNumberAreaEntity_.phoneNumberCountry),
                em.getReference(PhoneNumberCountryEntity.class, countryISO3166A2Code));
        Predicate and = cb.and(
                predicate,
                cb.equal(root.get(PhoneNumberAreaEntity_.active), true));
        criteriaQuery.where(and);
        TypedQuery<Area> query = em.createQuery(criteriaQuery);
        PhoneNumberServiceImpl.applyRangeToQuery(query, startPosition, maxResultCount);
        List<Area> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public Area getAreaById(final long areaId) {
        return getActiveAreaByAreaId(areaId);
    }

    @Override
    public CallablePhoneNumber getCallablePhoneNumber(final long phoneNumberId) {
        return getCallablePhoneNumberByPhoneNumberId(phoneNumberId);
    }

    /**
     * Get the callable phone number based on phone number id.
     * 
     * @param phoneNumberId
     *            the id of the phone number
     * @return the CallablePhoneNumber object if exist, otherwise return <code>null</code>.
     */
    private CallablePhoneNumber getCallablePhoneNumberByPhoneNumberId(final long phoneNumberId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CallablePhoneNumber> criteriaQuery = cb.createQuery(CallablePhoneNumber.class);

        Root<PhoneNumberEntity> root =
                criteriaQuery.from(PhoneNumberEntity.class);
        Join<PhoneNumberEntity, PhoneNumberAreaEntity> pna = root.join(PhoneNumberEntity_.phoneNumberArea);
        Join<PhoneNumberAreaEntity, PhoneNumberCountryEntity> pnc = pna.join(PhoneNumberAreaEntity_.phoneNumberCountry);
        criteriaQuery.multiselect(pnc.get(PhoneNumberCountryEntity_.iddPrefix),
                pnc.get(PhoneNumberCountryEntity_.nddPrefix), pnc.get(PhoneNumberCountryEntity_.countryCallCode),
                pna.get(PhoneNumberAreaEntity_.callNumber), root.get(PhoneNumberEntity_.subScriberNumber),
                root.get(PhoneNumberEntity_.extension));

        Predicate predicate = cb.equal(root.get(PhoneNumberEntity_.phoneNumberId), phoneNumberId);

        criteriaQuery.where(predicate);
        List<CallablePhoneNumber> resultList = em.createQuery(criteriaQuery).getResultList();
        if (resultList.size() == 1) {
            return resultList.get(0);
        }
        return null;
    }

    @Override
    public CallablePhoneNumber getCallablePhoneNumberByVerifiableId(final long verifiablePhoneId) {
        return getCallablePhoneNumberByVerifiablePhoneId(verifiablePhoneId);
    }

    /**
     * Get the callable phone number based on the verifiable phone id.
     * 
     * @param verifiablePhoneId
     *            the id of the verifiable phone.
     * @return the CallablePhoneNumber object if exist, otherwise return <code>null</code>.
     */
    private CallablePhoneNumber getCallablePhoneNumberByVerifiablePhoneId(final long verifiablePhoneId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CallablePhoneNumber> criteriaQuery = cb.createQuery(CallablePhoneNumber.class);

        Root<PhoneNumberVerifiablePhoneEntity> root =
                criteriaQuery.from(PhoneNumberVerifiablePhoneEntity.class);
        Join<PhoneNumberVerifiablePhoneEntity, PhoneNumberEntity> pn = root
                .join(PhoneNumberVerifiablePhoneEntity_.phoneNumber);
        Join<PhoneNumberEntity, PhoneNumberAreaEntity> pna = pn.join(PhoneNumberEntity_.phoneNumberArea);
        Join<PhoneNumberAreaEntity, PhoneNumberCountryEntity> pnc = pna.join(PhoneNumberAreaEntity_.phoneNumberCountry);

        criteriaQuery.multiselect(pnc.get(PhoneNumberCountryEntity_.iddPrefix),
                pnc.get(PhoneNumberCountryEntity_.nddPrefix), pnc.get(PhoneNumberCountryEntity_.countryCallCode),
                pna.get(PhoneNumberAreaEntity_.callNumber), pn.get(PhoneNumberEntity_.subScriberNumber),
                pn.get(PhoneNumberEntity_.extension));

        Predicate predicate = cb
                .equal(root.get(PhoneNumberVerifiablePhoneEntity_.verifiablePhoneId), verifiablePhoneId);

        criteriaQuery.where(predicate);
        List<CallablePhoneNumber> resultList = em.createQuery(criteriaQuery).getResultList();
        if (resultList.size() == 1) {
            return resultList.get(0);
        }
        return null;
    }

    /**
     * Get the active countries.
     * 
     * @param startPosition
     *            the first index of the list or null if not defined.
     * @param maxResultCount
     *            the maximum number of elements or null if not defined.
     * @return the active Countries in list. If no one return empty list.
     */
    private List<Country> getCountriesByActive(final Long startPosition,
            final Long maxResultCount) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Country> criteriaQuery = cb.createQuery(Country.class);
        Root<PhoneNumberCountryEntity> root = criteriaQuery
                .from(PhoneNumberCountryEntity.class);
        criteriaQuery.multiselect(root.get(PhoneNumberCountryEntity_.countryISO3166A2Code),
                root.get(PhoneNumberCountryEntity_.iddPrefix), root.get(PhoneNumberCountryEntity_.nddPrefix),
                root.get(PhoneNumberCountryEntity_.countryCallCode), root.get(PhoneNumberCountryEntity_.active));
        Predicate predicate = cb.equal(root.get(PhoneNumberCountryEntity_.active), true);
        criteriaQuery.where(predicate);
        TypedQuery<Country> query = em.createQuery(criteriaQuery);
        PhoneNumberServiceImpl.applyRangeToQuery(query, startPosition, maxResultCount);
        List<Country> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public Country getCountry(final String countryISO3166A2Code) {
        if ((countryISO3166A2Code == null)) {
            throw new IllegalArgumentException("The countryISO3166A2Code parameter is null.");
        }
        return getCountryByCountryCode(countryISO3166A2Code);
    }

    /**
     * Get the country based on country code (ISO3166-alpha-2).
     * 
     * @param countryISO3166A2Code
     *            the country code (ISO3166-alpha-2).
     * @return the Country if exist, otherwise <code>null</code>.
     */
    private Country getCountryByCountryCode(final String countryISO3166A2Code) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Country> criteriaQuery = cb.createQuery(Country.class);

        Root<PhoneNumberCountryEntity> root =
                criteriaQuery.from(PhoneNumberCountryEntity.class);

        criteriaQuery.multiselect(root.get(PhoneNumberCountryEntity_.countryISO3166A2Code),
                root.get(PhoneNumberCountryEntity_.iddPrefix), root.get(PhoneNumberCountryEntity_.nddPrefix),
                root.get(PhoneNumberCountryEntity_.countryCallCode), root.get(PhoneNumberCountryEntity_.active));

        Predicate predicate = cb.equal(root.get(PhoneNumberCountryEntity_.countryISO3166A2Code), countryISO3166A2Code);

        criteriaQuery.where(predicate);
        List<Country> resultList = em.createQuery(criteriaQuery).getResultList();
        if (resultList.size() == 1) {
            return resultList.get(0);
        }
        return null;
    }

    /**
     * Get the subscriber number length based on area id.
     * 
     * @param areaId
     *            the id of the area.
     * @return the subscriber number length. If not find return <code>null</code>.
     */
    private Integer getSubscriberNumberLengthByAreaId(final long areaId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);

        Root<PhoneNumberAreaEntity> root =
                criteriaQuery.from(PhoneNumberAreaEntity.class);

        criteriaQuery.select(root.get(PhoneNumberAreaEntity_.subscriberNumberLength));

        Predicate predicate = cb.equal(root.get(PhoneNumberAreaEntity_.phoneAreaId),
                areaId);
        criteriaQuery.where(cb.and(predicate, cb.equal(root.get(PhoneNumberAreaEntity_.active), true)));
        List<Integer> resultList = em.createQuery(criteriaQuery).getResultList();
        if (resultList.size() == 1) {
            return resultList.get(0);
        }
        return null;
    }

    /**
     * Get the verifiable phone id based on verifiable data id.
     * 
     * @param verifiableDataId
     *            the id of the verifiable data.
     * @return the verifiable phone id. If not finds return <code>null</code>.
     */
    private Long getVerifiablePhoneIdByVerifiableDataId(final long verifiableDataId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);

        Root<PhoneNumberVerifiablePhoneEntity> root =
                criteriaQuery.from(PhoneNumberVerifiablePhoneEntity.class);

        criteriaQuery.select(root.get(PhoneNumberVerifiablePhoneEntity_.verifiablePhoneId));

        Predicate predicate = cb.equal(root.get(PhoneNumberVerifiablePhoneEntity_.verifiableData),
                em.getReference(VerifiableDataEntity.class, verifiableDataId));

        criteriaQuery.where(predicate);
        List<Long> resultList = em.createQuery(criteriaQuery).getResultList();
        if (resultList.size() == 1) {
            return resultList.get(0);
        }
        return null;
    }

    @Override
    public void inactiveArea(final long areaId) {
        throw new UnsupportedOperationException();
        // TODO Write the method.
    }

    @Override
    public void inactiveCountry(final String countryISO3166A2Code) {
        throw new UnsupportedOperationException();
        // TODO Write the method.
    }

    @Override
    public List<Area> listActiveAreasBycountryISO3166A2Code(final String countryISO3166A2Code,
            final Long startPosition,
            final Long maxResultCount) {
        if (countryISO3166A2Code == null) {
            throw new IllegalArgumentException("The countryISO3166A2Code parameter is null.");
        }
        return getActiveAreasByCountryCode(countryISO3166A2Code, startPosition, maxResultCount);
    }

    @Override
    public List<Country> listActiveCountries(final Long startPosition, final Long maxResultCount) {
        return getCountriesByActive(startPosition, maxResultCount);
    }

    @Override
    public long saveArea(final String countryISO3166A2Code, final String callNumber, final String name,
            final int subscriberNumberLength) {
        if ((countryISO3166A2Code == null) || (callNumber == null)) {
            throw new IllegalArgumentException("The countryISO3166A2Code or callNumber parameter is null.");
        }

        if (subscriberNumberLength < 1) {
            throw new NonPositiveSubscriberNumberLengthException();
        }

        if (existActiveAreaByCountryCodeAndCallNumber(countryISO3166A2Code, callNumber)) {
            throw new DuplicateSelectableAreaException();
        }

        PhoneNumberAreaEntity entity = new PhoneNumberAreaEntity();
        entity.setPhoneNumberCountry(em.getReference(PhoneNumberCountryEntity.class, countryISO3166A2Code));
        entity.setCallNumber(callNumber);
        entity.setAreaName(name);
        entity.setSubscriberNumberLength(subscriberNumberLength);
        entity.setActive(true);
        em.persist(entity);
        em.flush();
        return entity.getPhoneAreaId();
    }

    @Override
    public void saveCountry(final String countryISO3166A2Code, final String idd, final String ndd,
            final String countryCallCode) {
        if ((countryISO3166A2Code == null) || (idd == null) || (ndd == null) || (countryCallCode == null)) {
            throw new IllegalArgumentException("The countryISO3166A2Code, idd, ndd, countryCallCode parameter "
                    + "is null.");
        }

        if (existCountry(countryISO3166A2Code)) {
            throw new DuplicateCountryException();
        }

        PhoneNumberCountryEntity entity = new PhoneNumberCountryEntity();
        entity.setCountryISO3166A2Code(countryISO3166A2Code);
        entity.setIddPrefix(idd);
        entity.setNddPrefix(ndd);
        entity.setCountryCallCode(countryCallCode);
        entity.setActive(true);
        em.persist(entity);
        em.flush();
    }

    @Override
    public long savePhoneNumber(final long areaId, final String subscriberNumber, final String extension) {
        Integer areaSubscriberNumberLength = getSubscriberNumberLengthByAreaId(areaId);
        if (subscriberNumber == null) {
            throw new IllegalArgumentException("The subscriberNumber parameter is null.");
        }

        if (areaSubscriberNumberLength == null) {
            throw new NoSuchAreaException();
        }

        if (subscriberNumber.length() != areaSubscriberNumberLength) {
            throw new InvalidPhoneNumberException();
        }
        PhoneNumberEntity entity = new PhoneNumberEntity();
        entity.setSubScriberNumber(subscriberNumber);
        entity.setPhoneNumberArea(em.getReference(PhoneNumberAreaEntity.class, areaId));
        entity.setExtension(extension);
        em.persist(entity);
        em.flush();
        return entity.getPhoneNumberId();
    }

    /**
     * Save and creating verifiable phone and verification request (in the beginning the phonenumber_ table).
     * 
     * @param phoneNumberId
     *            the id of the phone number.
     * @param verifiableDataId
     *            the id of the verifiable data.
     * @param verificationRequestId
     *            the id of the verification request.
     * @param verificationChannel
     *            the {@link VerificationChannel}.
     */
    private void saveVerifiablePhone(final long phoneNumberId, final long verifiableDataId,
            final long verificationRequestId, final VerificationChannel verificationChannel) {
        if (verificationChannel == null) {
            throw new IllegalArgumentException("The verificationChannel is null. Cannot be null.");
        }

        if (!existPhoneNumber(phoneNumberId)) {
            throw new NoSuchPhoneNumberException();
        }

        if (!existVerifiableData(verifiableDataId)) {
            throw new NoSuchVerifiableDataException();
        }

        if (!existVerificationRequest(verificationRequestId)) {
            throw new NoSuchVerificationRequestException();
        }

        PhoneNumberVerifiablePhoneEntity verifiablePhoneEntity = new PhoneNumberVerifiablePhoneEntity();
        verifiablePhoneEntity.setPhoneNumber(em.getReference(PhoneNumberEntity.class, phoneNumberId));
        verifiablePhoneEntity.setVerifiableData(em.getReference(VerifiableDataEntity.class, verifiableDataId));
        em.persist(verifiablePhoneEntity);

        PhoneNumberVerificationRequestEntity verificationRequestEntity = new PhoneNumberVerificationRequestEntity();
        verificationRequestEntity.setVerificationChannel(verificationChannel);
        verificationRequestEntity.setVerificationRequest(em.getReference(VerificationRequestEntity.class,
                verificationRequestId));
        verificationRequestEntity.setVerifiablePhone(em.getReference(PhoneNumberVerifiablePhoneEntity.class,
                verifiablePhoneEntity.getVerifiablePhoneId()));
        em.persist(verificationRequestEntity);
        em.flush();
    }

    public void setEm(final EntityManager em) {
        this.em = em;
    }

    public void setSmsSender(final SMSSender smsSender) {
        this.smsSender = smsSender;
    }

    public void setVerifyService(final VerifyService verifyService) {
        this.verifyService = verifyService;
    }

    @Override
    public void updatePhoneNumber(final long phoneNumberId, final long areaId, final String subscriberNumber,
            final String extension) {
        if (subscriberNumber == null) {
            throw new IllegalArgumentException("The subscriberNumber parameter is null.");
        }
        PhoneNumberEntity pnEntity = findPhoneNumberEntityByPhoneNumberId(phoneNumberId);
        if (pnEntity == null) {
            throw new NoSuchPhoneNumberException();
        }

        Integer areaSubscriberNumberLength = getSubscriberNumberLengthByAreaId(areaId);
        if (areaSubscriberNumberLength == null) {
            throw new NoSuchAreaException();
        }

        if (areaSubscriberNumberLength != subscriberNumber.length()) {
            throw new InvalidPhoneNumberException();
        }

        pnEntity.setExtension(extension);
        pnEntity.setSubScriberNumber(subscriberNumber);
        pnEntity.setPhoneNumberArea(em.getReference(PhoneNumberAreaEntity.class, areaId));
        em.merge(pnEntity);
        em.flush();
    }

    @Override
    public PhoneVerificationResult verifyPhoneNumber(final String tokenUUID) {
        if (tokenUUID == null) {
            throw new IllegalArgumentException("The tokenUUID is null. Cannot be null.");
        }
        PhoneVerificationResult result = null;
        VerificationResult verifyData = verifyService.verifyData(tokenUUID);
        if (verifyData != null) {
            Long verifiablePhoneId = getVerifiablePhoneIdByVerifiableDataId(verifyData.getVerifiableDataId());
            if (verifiablePhoneId != null) {
                result = new PhoneVerificationResult(verifiablePhoneId,
                        determinePhoneNumberConfirmationResult(verifyData.getTokenUsageResult()));
            } else {
                result = new PhoneVerificationResult(null, PhoneNumberConfirmationResult.FAILED);
            }
        } else {
            result = new PhoneVerificationResult(null, PhoneNumberConfirmationResult.FAILED);
        }
        return result;
    }
}
