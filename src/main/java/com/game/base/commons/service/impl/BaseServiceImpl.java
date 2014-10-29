package com.game.base.commons.service.impl;

import java.util.List;

import com.game.base.commons.dao.BaseDao;
import com.game.base.commons.service.BaseService;
import com.game.base.commons.utils.collection.OrderLimit;
import com.game.base.commons.utils.collection.PaginationBaseQuery;
import com.game.base.commons.utils.collection.PaginationResult;
import com.netease.framework.dao.sql.ListCondition;

/**
 * @author hzfjd@
 * @param <D>
 * @param <T>
 */
public class BaseServiceImpl<D extends BaseDao<T>, T> implements BaseService<T> {

    private BaseDao<T> baseDao;

    @Override
    public int add(T object) {
        return baseDao.add(object);
    }

    @Override
    public int add(List<T> objects) {
        return baseDao.add(objects);
    }

    @Override
    public boolean deleteById(long id) {
        return baseDao.deleteById(id);
    }

    @Override
    public List<T> getAllByIds(Long[] ids) {
        return baseDao.getAllByIds(ids);
    }

    @Override
    public T getById(long id) {
        return baseDao.getById(id);
    }

    @Override
    public int updateSelectiveById(T domainObject) {
        return baseDao.updateSelectiveById(domainObject);
    }

    @Override
    public boolean delete(T domainObject) {
        return baseDao.delete(domainObject);
    }

    @Override
    public List<T> getAll() {
        return baseDao.getAll();
    }

    public BaseDao<T> getBaseDao() {
        return baseDao;
    }

    public void setBaseDao(BaseDao<T> baseDao) {

        this.baseDao = baseDao;
    }

    @Override
    public List<T> getByDomainObjectSelective(T domainObject) {
        return baseDao.getByDomainObjectSelective(domainObject);
    }

    @Override
    public Long getId() {
        return baseDao.getId();
    }

    /**
     * @inheritDoc
     */
    @Override
    public long getCount() {
        return baseDao.getCount();
    }

    @Override
    public List<T> getAll(ListCondition listcondition) {
        return baseDao.getAll(listcondition);
    }

    @Override
    public List<T> getByDomainObjectSelective(T domainObject, ListCondition lc) {
        return baseDao.getByDomainObjectSelective(domainObject, lc);
    }

    @Override
    public PaginationResult<T> getPaginationByDomainObjectSelective(T domainObject, PaginationBaseQuery pagination) {
        return baseDao.getPaginationByDomainObjectSelective(domainObject, pagination);
    }

    @Override
    public PaginationResult<T> getAllPagination(PaginationBaseQuery pagination) {
        return baseDao.getAllPagination(pagination);
    }

    @Override
    public long countByDomainObjectSelective(T domainObject) {
        return baseDao.countByDomainObjectSelective(domainObject);
    }

    @Override
    public List<T> getByPropertyList(List<? extends Object> propertyList, String columnName) {
        return baseDao.getByPropertyList(propertyList, columnName);
    }

    @Override
    public PaginationResult<T> getPaginationByCondition(String condition, List<Object> params,
                                                        PaginationBaseQuery pagination) {
        return baseDao.getPaginationByCondition(condition, params, pagination);
    }

    @Override
    public int updateSelectiveByObject(T changedValue, T condition) {
        return baseDao.updateSelectiveByObject(changedValue, condition);
    }

    @Override
    public int updateSelectiveByIdAndVersion(T domainObject) {
        return baseDao.updateSelectiveByIdAndVersion(domainObject);
    }

    @Override
    public List<T> getByIdList(List<Long> ids) {
        return baseDao.getByIdList(ids);
    }

    @Override
    public List<T> getByCondition(String condition, Object... conditionParam) {
        return baseDao.getByCondition(condition, conditionParam);
    }

    @Override
    public List<T> getAllOrderLimit(OrderLimit orderLimit) {
        return baseDao.getAllOrderLimit(orderLimit);
    }

    @Override
    public List<T> getOrderLimitByCondition(OrderLimit orderLimit, String condition, Object... conditionParam) {
        return baseDao.getOrderLimitByCondition(orderLimit, condition, conditionParam);
    }

    @Override
    public PaginationResult<T> getPaginationByCondition(PaginationBaseQuery pagination, String condition,
                                                        Object... conditionParam) {
        return baseDao.getPaginationByCondition(pagination, condition, conditionParam);
    }

    @Override
    public long countByCondition(String condition, Object... param) {
        return baseDao.countByCondition(condition, param);
    }

    @Override
    public int updateSelectiveByCondition(T changedValue, String condition, Object... conditionParam) {
        return baseDao.updateSelectiveByCondition(changedValue, condition, conditionParam);
    }

    @Override
    public int deleteByCondition(String condition, Object... conditionParam) {
        return baseDao.deleteByCondition(condition, conditionParam);
    }

    @Override
    public <P> T getByIdAndPolicyId(Long id, P policyId, Class<P> policyIdClass) {
        return baseDao.getByIdAndPolicyId(id, policyId, policyIdClass);
    }

    @Override
    public <P> int deleteByIdAndPolicyId(Long id, P policyId, Class<P> policyIdClass) {
        return baseDao.deleteByIdAndPolicyId(id, policyId, policyIdClass);
    }

    @Override
    public Long[] getIdsByCondition(String condition, Object... param) {
        return baseDao.getIdsByCondition(condition, param);
    }

    @Override
    public List<T> getAllOrderLimitOpt(OrderLimit orderLimit) {
        return baseDao.getAllOrderLimitOpt(orderLimit);
    }

    @Override
    public List<T> getOrderLimitByConditionOpt(OrderLimit orderLimit, String condition, Object... conditionParam) {
        return baseDao.getOrderLimitByConditionOpt(orderLimit, condition, conditionParam);
    }

    @Override
    public PaginationResult<T> getAllPaginationOpt(PaginationBaseQuery pagination) {
        return baseDao.getAllPaginationOpt(pagination);
    }

    @Override
    public PaginationResult<T> getPaginationByDomainObjectSelectiveOpt(PaginationBaseQuery pagination, T domainObject) {
        return baseDao.getPaginationByDomainObjectSelectiveOpt(pagination, domainObject);
    }

    @Override
    public PaginationResult<T> getPaginationByConditionOpt(PaginationBaseQuery pagination, String condition,
                                                           Object... conditionParam) {
        return baseDao.getPaginationByConditionOpt(pagination, condition, conditionParam);
    }

    @Override
    public List<T> getByDomainObjectSelectiveOpt(T domainObject, ListCondition lc) {
        return baseDao.getByDomainObjectSelectiveOpt(domainObject, lc);
    }

}
