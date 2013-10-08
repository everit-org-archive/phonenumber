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
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.everit.phonenumber.api.PhoneNumberService;
import org.everit.phonenumber.api.dto.Area;
import org.everit.phonenumber.api.dto.CallablePhoneNumber;
import org.everit.phonenumber.api.dto.Country;
import org.everit.phonenumber.api.enums.VerificationType;
import org.everit.phonenumber.api.expections.DuplicateSelectableAreaException;
import org.everit.phonenumber.api.expections.InvalidNumberExcption;
import org.everit.phonenumber.entity.PhoneNumberAreaEntity;
import org.everit.phonenumber.entity.PhoneNumberAreaEntity_;
import org.everit.phonenumber.entity.PhoneNumberCountryEntity;
import org.everit.phonenumber.entity.PhoneNumberCountryEntity_;
import org.everit.phonenumber.entity.PhoneNumberEntity;

/**
 * Implementation of the {@link PhoneNumberService}.
 */
public class PhoneNumberServiceImpl implements PhoneNumberService {

    private EntityManager em;

    /**
     * Convert {@link PhoneNumberAreaEntity} to {@link Area} object.
     * 
     * @param entity
     *            the {@link PhoneNumberAreaEntity} object.
     * @return the {@link Area} object if not null the parameter, otherwise return <code>null</code>.
     */
    private Area convertPhoneNumberAreaEntityToArea(final PhoneNumberAreaEntity entity) {
        if (entity != null) {
            Area area = new Area(entity.getPhoneNumberCountry().getCountryISO3166A2Code(), entity.getCallNumber(),
                    entity.getName(), entity.getSubscriberNumberLength(), entity.isActive());
            return area;
        }
        return null;
    }

    /**
     * Convert {@link PhoneNumberCountryEntity} to {@link Country} object.
     * 
     * @param entity
     *            the {@link PhoneNumberCountryEntity} object.
     * @return the {@link Country} object if not null the parameter, otherwise return <code>null</code>.
     */
    private Country convertPhoneNumberCountryEntityToCountry(final PhoneNumberCountryEntity entity) {
        if (entity != null) {
            Country country = new Country(entity.getCountryISO3166A2Code(), entity.getIddPrefix(),
                    entity.getNddPrefix(), entity.getCountryCallCode(), entity.isActive());
            return country;
        }
        return null;
    }

    /**
     * Convert {@link PhoneNumberEntity} to {@link CallablePhoneNumber} object.
     * 
     * @param entity
     *            the {@link PhoneNumberEntity} object.
     * @return the {@link CallablePhoneNumber} object which contains all relevant information if exist. If the parameter
     *         is <code>null</code> return <code>null</code>.
     */
    private CallablePhoneNumber convertPhoneNumberEntityToCallablePhoneNumber(final PhoneNumberEntity entity) {
        CallablePhoneNumber result = null;
        if (entity != null) {
            result = new CallablePhoneNumber(null, null, null, null, null, null);
            PhoneNumberAreaEntity phoneNumberArea = entity.getPhoneNumberArea();
            if (phoneNumberArea != null) {
                PhoneNumberCountryEntity phoneNumberCountry = phoneNumberArea.getPhoneNumberCountry();
                if (phoneNumberCountry != null) {
                    result.setCountryIDD(phoneNumberCountry.getIddPrefix());
                    result.setCountryNDD(phoneNumberCountry.getNddPrefix());
                    result.setCountryCallCode(phoneNumberCountry.getCountryCallCode());
                }
                result.setAreaCallNumber(phoneNumberArea.getCallNumber());
            }
            result.setSubscriberNumber(entity.getSubScriberNumber());
            result.setExtension(entity.getExtension());
        }
        return result;
    }

    @Override
    public void createVerificationRequest(final long phoneNumberId, final VerificationType verificationChannel) {
        // TODO Auto-generated method stub
    }

    /**
     * Finds the {@link PhoneNumberAreaEntity} based on callNumber.
     * 
     * @param countryISO3166A2Code
     *            the country code.
     * @param callNumber
     *            the call number.
     * @return the {@link PhoneNumberAreaEntity} if exist area. If not exist return <code>null</code>.
     */
    private PhoneNumberAreaEntity findActivePhoneNumberAreaEntityByCountryCodeAndCallNumber(
            final String countryISO3166A2Code,
            final String callNumber) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<PhoneNumberAreaEntity> criteriaQuery = cb.createQuery(PhoneNumberAreaEntity.class);
        Root<PhoneNumberAreaEntity> root = criteriaQuery
                .from(PhoneNumberAreaEntity.class);
        Predicate predicate = cb.equal(root.get(PhoneNumberAreaEntity_.callNumber),
                callNumber);
        Predicate and = cb.and(
                predicate,
                cb.equal(root.get(PhoneNumberAreaEntity_.phoneNumberCountry),
                        em.getReference(PhoneNumberCountryEntity.class, countryISO3166A2Code)));
        criteriaQuery.where(cb.and(and, cb.equal(root.get(PhoneNumberAreaEntity_.active), true)));
        List<PhoneNumberAreaEntity> resultList = em.createQuery(criteriaQuery).getResultList();
        if (resultList.size() == 1) {
            return resultList.get(0);
        }
        return null;
    }

    /**
     * Finds all {@link PhoneNumberAreaEntity}s based on countryISO3166A2Code (country code).
     * 
     * @param countryISO3166A2Code
     *            the country code.
     * @param startPosition
     *            the first index of the list or null if not defined.
     * @param maxResultCount
     *            the maximum number of elements or null if not defined.
     * @return the {@link PhoneNumberAreaEntity}s in list. If startPosition or maxResultCount negative or no one result
     *         return empty list.
     */
    private List<PhoneNumberAreaEntity> findPhoneNumberAreaEntitiesByCountryCode(final String countryISO3166A2Code,
            final Long startPosition, final Long maxResultCount) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<PhoneNumberAreaEntity> criteriaQuery = cb.createQuery(PhoneNumberAreaEntity.class);
        Root<PhoneNumberAreaEntity> root = criteriaQuery
                .from(PhoneNumberAreaEntity.class);
        Predicate predicate = cb.equal(root.get(PhoneNumberAreaEntity_.phoneNumberCountry),
                em.getReference(PhoneNumberCountryEntity.class, countryISO3166A2Code));
        Predicate and = cb.and(
                predicate,
                cb.equal(root.get(PhoneNumberAreaEntity_.active), true));
        criteriaQuery.where(and);
        TypedQuery<PhoneNumberAreaEntity> query = em.createQuery(criteriaQuery);
        List<PhoneNumberAreaEntity> resultList = new ArrayList<PhoneNumberAreaEntity>();
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
            resultList = new ArrayList<PhoneNumberAreaEntity>();
        }
        return resultList;
    }

    /**
     * Finds the {@link PhoneNumberAreaEntity} based on area id.
     * 
     * @param areaId
     *            the id of the {@link PhoneNumberAreaEntity}.
     * @return the {@link PhoneNumberAreaEntity} if exist, otherwise return <code>null</code>.
     */
    private PhoneNumberAreaEntity findPhoneNumberAreaEntityByAreaId(final long areaId) {
        return em.find(PhoneNumberAreaEntity.class, areaId);
    }

    /**
     * Finds all {@link PhoneNumberCountryEntity}s based on active.
     * 
     * @return the {@link PhoneNumberCountryEntity}s in list. If no one return empty list.
     */
    private List<PhoneNumberCountryEntity> findPhoneNumberCountryEntitiesByActive(final Long startPosition,
            final Long maxResultCount) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<PhoneNumberCountryEntity> criteriaQuery = cb.createQuery(PhoneNumberCountryEntity.class);
        Root<PhoneNumberCountryEntity> root = criteriaQuery
                .from(PhoneNumberCountryEntity.class);
        Predicate predicate = cb.equal(root.get(PhoneNumberCountryEntity_.active), true);
        criteriaQuery.where(predicate);
        TypedQuery<PhoneNumberCountryEntity> query = em.createQuery(criteriaQuery);
        List<PhoneNumberCountryEntity> resultList = new ArrayList<PhoneNumberCountryEntity>();
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
            resultList = new ArrayList<PhoneNumberCountryEntity>();
        }
        return resultList;
    }

    /**
     * Finds the {@link PhoneNumberCountryEntity} based on countryISO3166A2Code.
     * 
     * @param countryISO3166A2Code
     *            the country code.
     * @return the {@link PhoneNumberCountryEntity} object if exist. If not exist return <code>null</code>.
     */
    private PhoneNumberCountryEntity findPhoneNumberCountryEntityByCountryISO3166A2Code(
            final String countryISO3166A2Code) {
        return em.find(PhoneNumberCountryEntity.class, countryISO3166A2Code);
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

    @Override
    public Area getActiveAreaByCountryAndCallNumber(final String countryISO3166A2Code, final String areaCallNumber) {
        if ((countryISO3166A2Code == null) || (areaCallNumber == null)) {
            throw new IllegalArgumentException("The countryISO3166A2Code or areaCallNumber parameter is null.");
        }
        PhoneNumberAreaEntity pnAreaEntity = findActivePhoneNumberAreaEntityByCountryCodeAndCallNumber(
                countryISO3166A2Code,
                areaCallNumber);
        return convertPhoneNumberAreaEntityToArea(pnAreaEntity);
    }

    @Override
    public Area getAreaById(final long areaId) {
        return convertPhoneNumberAreaEntityToArea(findPhoneNumberAreaEntityByAreaId(areaId));
    }

    @Override
    public CallablePhoneNumber getCallablePhoneNumber(final long phoneNumberId) {
        return convertPhoneNumberEntityToCallablePhoneNumber(findPhoneNumberEntityByPhoneNumberId(phoneNumberId));
    }

    @Override
    public Country getCountry(final String countryISO3166A2Code) {
        if ((countryISO3166A2Code == null)) {
            throw new IllegalArgumentException("The countryISO3166A2Code parameter is null.");
        }
        PhoneNumberCountryEntity pnCountryEntity = findPhoneNumberCountryEntityByCountryISO3166A2Code(countryISO3166A2Code);
        return convertPhoneNumberCountryEntityToCountry(pnCountryEntity);
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
        List<Area> listAreaEntities = new ArrayList<Area>();
        List<PhoneNumberAreaEntity> listPhoneNumberAreaEntities = findPhoneNumberAreaEntitiesByCountryCode(
                countryISO3166A2Code, startPosition, maxResultCount);
        for (PhoneNumberAreaEntity pnae : listPhoneNumberAreaEntities) {
            listAreaEntities.add(convertPhoneNumberAreaEntityToArea(pnae));
        }
        return listAreaEntities;
    }

    @Override
    public List<Country> listActiveCountries(final Long startPosition, final Long maxResultCount) {
        List<Country> listCountryEntities = new ArrayList<Country>();
        List<PhoneNumberCountryEntity> listPhoneNumberCountryEntities = findPhoneNumberCountryEntitiesByActive(
                startPosition, maxResultCount);
        for (PhoneNumberCountryEntity pnce : listPhoneNumberCountryEntities) {
            listCountryEntities.add(convertPhoneNumberCountryEntityToCountry(pnce));
        }
        return listCountryEntities;
    }

    @Override
    public long saveArea(final String countryISO3166A2Code, final String callNumber, final String name,
            final int subscriberNumberLength) {
        if ((countryISO3166A2Code == null) || (callNumber == null)) {
            throw new IllegalArgumentException("The countryISO3166A2Code or callNumber parameter is null.");
        } else if (subscriberNumberLength < 1) {
            throw new IllegalArgumentException("The subscriberNumberLength is zero or negative.");
        } else {
            PhoneNumberAreaEntity pnAeraEntity = findActivePhoneNumberAreaEntityByCountryCodeAndCallNumber(
                    countryISO3166A2Code, callNumber);
            if (pnAeraEntity != null) {
                throw new DuplicateSelectableAreaException();
            }
        }
        PhoneNumberAreaEntity entity = new PhoneNumberAreaEntity();
        entity.setPhoneNumberCountry(em.getReference(PhoneNumberCountryEntity.class, countryISO3166A2Code));
        entity.setCallNumber(callNumber);
        entity.setName(name);
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
        } else {
            PhoneNumberCountryEntity pnCountryEntity = findPhoneNumberCountryEntityByCountryISO3166A2Code(countryISO3166A2Code);
            if (pnCountryEntity != null) {
                throw new IllegalArgumentException("DUPLICATE COUNTRY.");
            }
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
        PhoneNumberAreaEntity pnAreaEntity = findPhoneNumberAreaEntityByAreaId(areaId);
        if (pnAreaEntity == null) {
            throw new IllegalArgumentException("NOT EXIST AREA.");
        } else {
            if (subscriberNumber.length() != pnAreaEntity.getSubscriberNumberLength()) {
                throw new InvalidNumberExcption();
            }
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
            throw new IllegalArgumentException("NOT EXIST PHONE NUMBER");
        } else {
            PhoneNumberAreaEntity pnAreaEntity = findPhoneNumberAreaEntityByAreaId(areaId);
            if (pnAreaEntity == null) {
                throw new IllegalArgumentException("NOT EXIST AREA.");
            } else {
                if (pnAreaEntity.getSubscriberNumberLength() != subscriberNumber.length()) {
                    throw new InvalidNumberExcption();
                }
            }
        }
        pnEntity.setExtension(extension);
        pnEntity.setSubScriberNumber(subscriberNumber);
        pnEntity.setPhoneNumberArea(em.getReference(PhoneNumberAreaEntity.class, areaId));
        em.merge(pnEntity);
        em.flush();
    }
}
