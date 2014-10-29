package com.game.base.commons.dao;

import java.util.List;

import com.game.base.commons.utils.collection.OrderLimit;
import com.game.base.commons.utils.collection.PaginationBaseQuery;
import com.game.base.commons.utils.collection.PaginationResult;
import com.netease.framework.dao.DomainObjectDao;
import com.netease.framework.dao.sql.ListCondition;

/**
 * @author 
 * @param <DomainObject>
 */
public interface BaseDao<DomainObject> extends DomainObjectDao<DomainObject> {

    /**
     * 根据id选择更新domian对象的非空字段
     * 
     * @param domainObject
     * @return
     */
    public int updateSelectiveById(DomainObject domainObject);

    /**
     * 根据多个条件进行字段更新
     * 
     * @param domainObject
     * @return
     */
    public int updateSelectiveByObject(DomainObject changedValue, DomainObject condition);

    /***
     * 根据id和version选择更新domian对象的非空字段，用于需要进行并发控制的情况，数据库表中必须存在version字段
     * 
     * @param domainObject
     * @return
     */
    public int updateSelectiveByIdAndVersion(DomainObject domainObject);

    /**
     * 动态查询
     * 
     * @param domainObject
     * @return
     */
    public List<DomainObject> getByDomainObjectSelective(DomainObject domainObject);

    public Long getId();

    public String getTableName();

    /**
     * 根据idList查询
     * 
     * @param ids
     * @return
     */
    public List<DomainObject> getByIdList(List<Long> ids);

    /***
     * 根据属性集合查询 比如根据member的userNameList来查询
     * 
     * @param propertyList
     * @param columnName 查询依据的数据库字段名
     * @return
     */
    public List<DomainObject> getByPropertyList(List<? extends Object> propertyList, String columnName);

    /**
     * 动态查询，支持排序、限量功能
     * 
     * @param domainObject
     * @return
     */
    public List<DomainObject> getByDomainObjectSelective(DomainObject domainObject, ListCondition lc);

    /**
     * 动态分页查询
     * 
     * @param domainObject 参数设计你要查询的匹配条件
     * @param pagination 指定排序字段名称，升降序，以及页大小、页码
     * @return PaginationResult 里面的getQuery()包含了查询条件，页码信息，以及总数、总页数等；getList()对应当前页中domain对象列表。
     */
    public PaginationResult<DomainObject> getPaginationByDomainObjectSelective(DomainObject domainObject,
                                                                               PaginationBaseQuery pagination);

    /**
     * 不限定参数翻页
     * 
     * @param pagination
     * @return
     */
    public PaginationResult<DomainObject> getAllPagination(PaginationBaseQuery pagination);

    /**
     * 动态统计数据量
     * 
     * @param domainObject
     * @return
     */
    public long countByDomainObjectSelective(DomainObject domainObject);

    /**
     * 动态分页查询
     * 
     * @param condition where语句中的条件， 可以使用？表示参数
     * @param params where中使用的参数， 按顺序替代？； 可以为null
     * @param pagination 里面的getQuery()包含了查询条件，页码信息，以及总数、总页数等；getList()对应当前页中domain对象列表。
     * @return
     */
    PaginationResult<DomainObject> getPaginationByCondition(String condition, List<Object> params,
                                                            PaginationBaseQuery pagination);

    /**
     * 根据condition sql 查询所有数据
     * 
     * @param condition
     * @param conditionParam
     * @return
     */
    public List<DomainObject> getByCondition(String condition, Object... conditionParam);
    
    
    public DomainObject getFirstOneByCondition(String condition, Object... conditionParam);

    /**
     * 分区或者limit查询相关记录
     * 
     * @param orderLimit
     * @return
     */
    public List<DomainObject> getAllOrderLimit(OrderLimit orderLimit);

    /**
     * 根据condition和limit查询记录
     * 
     * @param orderLimit
     * @param condition
     * @param conditionParam
     * @return
     */
    public List<DomainObject> getOrderLimitByCondition(OrderLimit orderLimit, String condition,
                                                       Object... conditionParam);

    /**
     * 根据condition分页查询数据，使用可变参数传递参数
     * 
     * @param pagination
     * @param condition
     * @param conditionParam
     * @return
     */
    public PaginationResult<DomainObject> getPaginationByCondition(PaginationBaseQuery pagination, String condition,
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
    public int updateSelectiveByCondition(DomainObject changedValue, String condition, Object... conditionParam);

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
    public <P> DomainObject getByIdAndPolicyId(Long id, P policyId, Class<P> policyIdClass);

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
    
    
    public Long getMaxExistId();
    
    /**
     * getAllOrderLimit的优化方法
     * 适用于表数据比较大的情况
     * 
     * @param orderLimit
     * @return
     */
    public List<DomainObject> getAllOrderLimitOpt(OrderLimit orderLimit);
    
    /**
     * getOrderLimitByCondition的优化方法
     * 适用于表数据比较大的情况
     * 
     * @param orderLimit
     * @return
     */
    public List<DomainObject> getOrderLimitByConditionOpt(OrderLimit orderLimit, String condition, Object... conditionParam);
    
    /**
     * 不限定参数翻页
     * 
     * @param pagination
     * @return
     */
    public PaginationResult<DomainObject> getAllPaginationOpt(PaginationBaseQuery pagination);
    
    /**
     * 指定条件动态分页查询
     * @param domainObject
     * @param pagination
     * @return
     */
    public PaginationResult<DomainObject> getPaginationByDomainObjectSelectiveOpt(PaginationBaseQuery pagination, DomainObject domainObject);
    /**
     * 指定条件动态分页查询
     * @param pagination
     * @param condition
     * @param conditionParam
     * @return
     */
    public PaginationResult<DomainObject> getPaginationByConditionOpt(PaginationBaseQuery pagination, String condition, Object... conditionParam);
    
    /**
     * 动态查询，支持排序、限量功能
     * 
     * @param domainObject
     * @return
     */
    public List<DomainObject> getByDomainObjectSelectiveOpt(DomainObject domainObject, ListCondition lc);
}
