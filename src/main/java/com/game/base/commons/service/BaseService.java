package com.game.base.commons.service;

import java.util.List;

import com.game.base.commons.utils.collection.OrderLimit;
import com.game.base.commons.utils.collection.PaginationBaseQuery;
import com.game.base.commons.utils.collection.PaginationResult;
import com.netease.framework.dao.sql.ListCondition;

public interface BaseService<T> {

    public boolean delete(T domainObject);

    public int add(T object);

    public int add(List<T> objects);

    public boolean deleteById(long id);

    public List<T> getAllByIds(Long[] ids);

    public T getById(long id);

    public int updateSelectiveById(T domainObject);

    public List<T> getAll();

    public List<T> getAll(ListCondition listcondition);

    public List<T> getByDomainObjectSelective(T domainObject);

    public Long getId();

    public long getCount();

    /**
     * 动态查询，支持排序、限量功能
     * 
     * @param domainObject
     * @return
     */
    public List<T> getByDomainObjectSelective(T domainObject, ListCondition lc);

    /**
     * 动态分页查询
     * 
     * @param domainObject 参数设计你要查询的匹配条件
     * @param pagination 指定排序字段名称，升降序，以及页大小、页码
     * @return PaginationResult 里面的getQuery()包含了查询条件，页码信息，以及总数、总页数等；getList()对应当前页中domain对象列表。
     */
    public PaginationResult<T> getPaginationByDomainObjectSelective(T domainObject, PaginationBaseQuery pagination);

    /**
     * 不限定参数翻页
     * 
     * @param pagination
     * @return
     */
    public PaginationResult<T> getAllPagination(PaginationBaseQuery pagination);

    /**
     * 动态统计数据量
     * 
     * @param domainObject
     * @return
     */
    public long countByDomainObjectSelective(T domainObject);

    /***
     * 构建指定字段的in语句进行查询
     * 
     * @param propertyList
     * @param columnName
     * @return
     */
    public List<T> getByPropertyList(List<? extends Object> propertyList, String columnName);

    /**
     * 动态分页查询
     * 
     * @param condition where语句中的条件， 可以使用？表示参数
     * @param params where中使用的参数， 按顺序替代？； 可以为null
     * @param pagination 里面的getQuery()包含了查询条件，页码信息，以及总数、总页数等；getList()对应当前页中domain对象列表。
     * @return
     */
    public PaginationResult<T> getPaginationByCondition(String condition, List<Object> params,
                                                        PaginationBaseQuery pagination);

    /**
     * 根据多个条件进行字段更新
     * 
     * @param domainObject
     * @return
     */
    public int updateSelectiveByObject(T changedValue, T condition);

    /***
     * 根据id和version选择更新domian对象的非空字段，用于需要进行并发控制的情况，数据库表中必须存在version字段
     * 
     * @param domainObject
     * @return
     */
    public int updateSelectiveByIdAndVersion(T domainObject);

    /**
     * 根据idList查询
     * 
     * @param ids
     * @return
     */
    public List<T> getByIdList(List<Long> ids);

    /**
     * 根据condition sql 查询所有数据
     * 
     * @param condition
     * @param conditionParam
     * @return
     */
    public List<T> getByCondition(String condition, Object... conditionParam);

    /**
     * 分区或者limit查询相关记录
     * 
     * @param orderLimit
     * @return
     */
    public List<T> getAllOrderLimit(OrderLimit orderLimit);

    /**
     * 根据condition和limit查询记录
     * 
     * @param orderLimit
     * @param condition
     * @param conditionParam
     * @return
     */
    public List<T> getOrderLimitByCondition(OrderLimit orderLimit, String condition, Object... conditionParam);

    /**
     * 根据condition分页查询数据，使用可变参数传递参数
     * 
     * @param pagination
     * @param condition
     * @param conditionParam
     * @return
     */
    public PaginationResult<T> getPaginationByCondition(PaginationBaseQuery pagination, String condition,
                                                        Object... conditionParam);

    /**
     * 根据condition查询记录数
     * 
     * @param condition
     * @param param
     * @return
     */
    public long countByCondition(String condition, Object... param);

    /**
     * 根据Condition进行字段更新
     * 
     * @param domainObject
     * @return
     */
    public int updateSelectiveByCondition(T changedValue, String condition, Object... conditionParam);

    /**
     * 根据Condition进行记录删除
     * 
     * @param condition
     * @return
     */
    public int deleteByCondition(String condition, Object... conditionParam);

    /**
     * 根据id和policyID查询，policyID类型需要制定，并和实际类型一致
     * 
     * @param id
     * @param policyId
     * @param policyIdClass
     * @return
     */
    public <P> T getByIdAndPolicyId(Long id, P policyId, Class<P> policyIdClass);

    /**
     * 根据id和policyID删除，policyID类型需要制定，并和实际类型一致
     * 
     * @param id
     * @param policyId
     * @param policyIdClass
     * @return
     */
    public <P> int deleteByIdAndPolicyId(Long id, P policyId, Class<P> policyIdClass);

    /**
     * 根据condition查询所有的id
     * 
     * @param condition
     * @param param
     * @return
     */
    public Long[] getIdsByCondition(String condition, Object... param);
    
    /**
     * getAllOrderLimit的优化方法
     * 适用于表数据比较大的情况
     * 
     * @param orderLimit
     * @return
     */
    public List<T> getAllOrderLimitOpt(OrderLimit orderLimit);
    
    /**
     * getOrderLimitByCondition的优化方法
     * 适用于表数据比较大的情况
     * 
     * @param orderLimit
     * @return
     */
    public List<T> getOrderLimitByConditionOpt(OrderLimit orderLimit, String condition, Object... conditionParam);
    
    /**
     * 不限定参数翻页
     * 
     * @param pagination
     * @return
     */
    public PaginationResult<T> getAllPaginationOpt(PaginationBaseQuery pagination);
    
    /**
     * 指定条件动态分页查询
     * @param domainObject
     * @param pagination
     * @return
     */
    public PaginationResult<T> getPaginationByDomainObjectSelectiveOpt(PaginationBaseQuery pagination, T domainObject);
    /**
     * 指定条件动态分页查询
     * @param pagination
     * @param condition
     * @param conditionParam
     * @return
     */
    public PaginationResult<T> getPaginationByConditionOpt(PaginationBaseQuery pagination, String condition, Object... conditionParam);
    
    /**
     * 动态查询，支持排序、限量功能
     * 
     * @param domainObject
     * @return
     */
    public List<T> getByDomainObjectSelectiveOpt(T domainObject, ListCondition lc);

}
