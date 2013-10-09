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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import org.everit.phonenumber.api.exceptions.DuplicateCountryException;
import org.everit.phonenumber.api.exceptions.DuplicateSelectableAreaException;
import org.everit.phonenumber.api.exceptions.InvalidNumberException;
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
import org.everit.verifiabledata.api.enums.VerificationLengthBase;

/**
 * Implementation of the {@link PhoneNumberService}.
 */
public class PhoneNumberServiceImpl implements PhoneNumberService {

    /**
     * EntityManager set by blueprint.
     */
    private EntityManager em;

    @Override
    public void createVerificationRequestViaSMS(final long phoneNumberId, final String messagetemplate,
            final Date tokenValidityEndDate,
            final long verificationLength, final VerificationLengthBase verificationLengthBase) {
        // TODO Auto-generated method stub

    }

    private boolean existAreaByCountryCodeAndCallNumber(final String countryISO3166A2Code,
            final String callNumber) {
        Long areaId = getActiveAreaIdByCountryCodeAndCallNumber(countryISO3166A2Code, callNumber);
        if (areaId != null) {
            return true;
        }
        return false;
    }

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
     * Finds the {@link PhoneNumberEntity} based on phone number id.
     * 
     * @param phoneNumberId
     *            the id of the phone number.
     * @return the {@link PhoneNumberEntity} object if exist, otherwise return <code>null</code>.
     */
    private PhoneNumberEntity findPhoneNumberEntityByPhoneNumberId(final long phoneNumberId) {
        return em.find(PhoneNumberEntity.class, phoneNumberId);
    }

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
        List<Area> resultList = new ArrayList<Area>();
        if ((startPosition == null) && (maxResultCount == null)) {
            resultList = query.getResultList();
        } else if ((startPosition == null) && (maxResultCount >= 0L)) {
            int maxResult = Integer.parseInt(Long.toString(maxResultCount));
            resultList = query.setMaxResults(maxResult).getResultList();
        } else if ((startPosition != null) && (startPosition >= 0L) && (maxResultCount == null)) {
            int position = Integer.parseInt(Long.toString(startPosition));
            resultList = query.setFirstResult(position).getResultList();
        } else if ((startPosition != null) && (startPosition >= 0L) && (maxResultCount >= 0L)) {
            int position = Integer.parseInt(Long.toString(startPosition));
            int maxResult = Integer.parseInt(Long.toString(maxResultCount));
            resultList = query.setFirstResult(position).setMaxResults(maxResult).getResultList();
        } else {
            resultList = new ArrayList<Area>();
        }
        return resultList;
    }

    @Override
    public Area getAreaById(final long areaId) {
        // return convertPhoneNumberAreaEntityToArea(findPhoneNumberAreaEntityByAreaId(areaId));
        return getActiveAreaByAreaId(areaId);
    }

    @Override
    public CallablePhoneNumber getCallablePhoneNumber(final long phoneNumberId) {
        return getCallablePhoneNumberByPhoneNumberId(phoneNumberId);
    }

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

    // TODO testing getCallablePhoneNumberByVerifiablePhoneId
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
        List<Country> resultList = new ArrayList<Country>();
        if ((startPosition == null) && (maxResultCount == null)) {
            resultList = query.getResultList();
        } else if ((startPosition == null) && (maxResultCount >= 0L)) {
            int maxResult = Integer.parseInt(Long.toString(maxResultCount));
            resultList = query.setMaxResults(maxResult).getResultList();
        } else if ((startPosition != null) && (startPosition >= 0L) && (maxResultCount == null)) {
            int position = Integer.parseInt(Long.toString(startPosition));
            resultList = query.setFirstResult(position).getResultList();
        } else if ((startPosition != null) && (startPosition >= 0L) && (maxResultCount >= 0L)) {
            int position = Integer.parseInt(Long.toString(startPosition));
            int maxResult = Integer.parseInt(Long.toString(maxResultCount));
            resultList = query.setFirstResult(position).setMaxResults(maxResult).getResultList();
        } else {
            resultList = new ArrayList<Country>();
        }
        return resultList;
    }

    @Override
    public Country getCountry(final String countryISO3166A2Code) {
        if ((countryISO3166A2Code == null)) {
            throw new IllegalArgumentException("The countryISO3166A2Code parameter is null.");
        }
        return getCountryByCountryCode(countryISO3166A2Code);
    }

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

    @Override
    public void inactiveArea(final long areaId) {
        // TODO Auto-generated method stub
    }

    @Override
    public void inactiveCountry(final String countryISO3166A2Code) {
        // TODO Auto-generated method stub

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

        if (existAreaByCountryCodeAndCallNumber(countryISO3166A2Code, callNumber)) {
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
            throw new IllegalArgumentException("The countryISO3166A2Code, idd, ndd, countryCallCode parameter is null.");
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
            throw new InvalidNumberException();
        }
        PhoneNumberEntity entity = new PhoneNumberEntity();
        entity.setSubScriberNumber(subscriberNumber);
        entity.setPhoneNumberArea(em.getReference(PhoneNumberAreaEntity.class, areaId));
        entity.setExtension(extension);
        em.persist(entity);
        em.flush();
        return entity.getPhoneNumberId();
    }

    public void setEm(final EntityManager em) {
        this.em = em;
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
            throw new InvalidNumberException();
        }

        pnEntity.setExtension(extension);
        pnEntity.setSubScriberNumber(subscriberNumber);
        pnEntity.setPhoneNumberArea(em.getReference(PhoneNumberAreaEntity.class, areaId));
        em.merge(pnEntity);
        em.flush();
    }

    @Override
    public PhoneVerificationResult verifyPhoneNumber(final String tokenUUID) {
        // TODO Auto-generated method stub
        return null;
    }
}
