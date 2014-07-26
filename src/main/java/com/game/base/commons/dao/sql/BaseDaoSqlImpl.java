package com.game.base.commons.dao.sql;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.game.base.commons.dao.BaseDao;
import com.game.base.commons.dao.annotation.DomainMetadata;
import com.game.base.commons.dao.handler.MaxIdHandler;
import com.game.base.commons.utils.collection.LowcaseKeyHashMap;
import com.game.base.commons.utils.collection.OrderLimit;
import com.game.base.commons.utils.collection.PaginationBaseQuery;
import com.game.base.commons.utils.collection.PaginationResult;
import com.game.base.commons.utils.sql.SqlBuilder;
import com.game.base.commons.utils.text.NeteaseEduStringUtils;
import com.netease.framework.config.ObjectConfig;
import com.netease.framework.dao.ObjectBase;
import com.netease.framework.dao.sql.AddParamMap;
import com.netease.framework.dao.sql.DomainObjectDaoSqlBase;
import com.netease.framework.dao.sql.ListCondition;
import com.netease.framework.dao.sql.annotation.DataProperty;
import com.netease.framework.dao.sql.annotation.VirtualProperty;
import com.netease.framework.dbsupport.SqlManager;
import com.netease.framework.dbsupport.impl.DBResource;

/**
 * @author hzfjd@corp.netease.com
 */

public class BaseDaoSqlImpl<DomainObject> extends
		DomainObjectDaoSqlBase<DomainObject> implements BaseDao<DomainObject>,
		InitializingBean {

	private final Map<String, Map<String, Method>> columnSetMethodClassMap = new HashMap<String, Map<String, Method>>();
	private final Map<String, Map<String, Method>> columnGetMethodClassMap = new HashMap<String, Map<String, Method>>();
	private final Map<String, Set<String>> virtualPropertiesClassMap = new HashMap<String, Set<String>>();

	private static Log log = LogFactory.getLog(BaseDaoSqlImpl.class);

	@Autowired
	public void setSqlManager(SqlManager sqlManager) {
		super.setSqlManager(sqlManager);
	}

	@Override
	public List<DomainObject> getByDomainObjectSelective(
			DomainObject domainObject) {

		return getByDomainObjectSelective(domainObject, null);

	}

	@Override
	public List<DomainObject> getByCondition(String condition,
			Object... conditionParam) {
		return getOrderLimitByCondition(null, condition, conditionParam);
	}

	@Override
	public DomainObject getFirstOneByCondition(String condition,
			Object... conditionParam) {
		List<DomainObject> domainObjects = getByCondition(condition,
				conditionParam);
		if (CollectionUtils.isEmpty(domainObjects)) {
			return null;
		}
		return domainObjects.get(0);
	}

	@Override
	public List<DomainObject> getAllOrderLimit(OrderLimit orderLimit) {
		if (orderLimit == null) {
			return getAll();
		}
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("select * from ");
		selectSql.append(getObjectConfig().getTableName());

		if (StringUtils.isNotBlank(orderLimit.getSortCriterial())) {
			selectSql.append(" order by " + orderLimit.getSortCriterial());
		}
		if (orderLimit.getLimit() > 0) {
			selectSql.append(" limit " + orderLimit.getLimit());
			if (orderLimit.getOffset() > 0) {
				selectSql.append(" offset " + orderLimit.getOffset());
			}
		}
		return queryObjects(selectSql.toString(), Collections.emptyList());

	}

	@Override
	public List<DomainObject> getOrderLimitByCondition(OrderLimit orderLimit,
			String condition, Object... conditionParam) {
		if (StringUtils.isEmpty(condition)) {
			return getAllOrderLimit(orderLimit);
		}

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("select * from  ");
		selectSql.append(getObjectConfig().getTableName());

		selectSql.append(" where ");
		selectSql.append(condition);
		selectSql.append(" ");

		if (orderLimit != null) {
			if (StringUtils.isNotBlank(orderLimit.getSortCriterial())) {
				selectSql.append(" order by ").append(
						orderLimit.getSortCriterial());
			}

			if (orderLimit.getLimit() > 0) {
				selectSql.append(" limit " + orderLimit.getLimit());
				if (orderLimit.getOffset() > 0) {
					selectSql.append(" offset " + orderLimit.getOffset());
				}
			}
		}

		return queryObjects(selectSql.toString(), conditionParam);

	}

	@Override
	public PaginationResult<DomainObject> getPaginationByCondition(
			PaginationBaseQuery pagination, String condition,
			Object... conditionParam) {
		return getPaginationByCondition(condition,
				Arrays.asList(conditionParam), pagination);
	}

	@Override
	public long countByCondition(String condition, Object... param) {
		if (StringUtils.isEmpty(condition)) {
			throw new RuntimeException(
					"must pass condition before call, use '1=1' if needed");
		}
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("select count(*) from  ");
		selectSql.append(getObjectConfig().getTableName());

		selectSql.append(" where ");
		selectSql.append(condition);
		selectSql.append(" ");

		return getSqlManager().queryCount(selectSql.toString(), param);
	}

	@Override
	public List<DomainObject> getByDomainObjectSelective(
			DomainObject domainObject, ListCondition lc) {

		if (domainObject == null) {
			return getAll(lc);
		}

		String className = getObjectConfig().getClassName();
		Map<String, Method> columnMethodMap = getGetMethodMap(className);

		LowcaseKeyHashMap columnPropertyMap = getColumnPropertyMap4ReadStyle(
				columnMethodMap, domainObject);

		int changedColumnSize = columnPropertyMap.size();
		if (changedColumnSize <= 0) {
			log.warn(className
					+ "using getByDomainObjectSelective without assigning any property value is meaningless! please use getAll install.");
		}

		String[] columnNames = new String[changedColumnSize];
		Object[] columnValues = new Object[changedColumnSize];
		int index = 0;
		for (Map.Entry<String, Object> entry : columnPropertyMap.entrySet()) {
			columnNames[index] = entry.getKey();
			columnValues[index] = entry.getValue();
			index++;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("select * from  ");
		sb.append(getObjectConfig().getTableName());

		if (changedColumnSize > 0) {
			sb.append(" where ");
			for (int i = 0; i < columnNames.length; i++) {
				if (i != 0) {
					sb.append(" and ");
				}
				sb.append(columnNames[i]);
				sb.append("=?");
			}
		}

		if (lc != null) {
			if (StringUtils.isNotBlank(lc.getOrderColumn())) {
				sb.append(" order by " + lc.getOrderColumn());
				if (lc.isOrderAsc()) {
					sb.append(" asc");
				} else {
					sb.append(" desc");
				}
			}
			if (lc.getLimit() > 0) {
				sb.append(" limit " + lc.getLimit());
				if (lc.getOffset() > 0) {
					sb.append(" offset " + lc.getOffset());
				}
			}
		}

		List<Object> params = new ArrayList<Object>();
		for (int i = 0; i < columnNames.length; i++) {
			params.add(columnValues[i]);
		}
		return getSqlManager().queryList(sb.toString(), this, params);

	}

	@Override
	public PaginationResult<DomainObject> getPaginationByDomainObjectSelective(
			DomainObject domainObject, PaginationBaseQuery pagination) {

		if (domainObject == null) {
			return getAllPagination(pagination);
		}

		String className = getObjectConfig().getClassName();
		Map<String, Method> columnMethodMap = getGetMethodMap(className);

		LowcaseKeyHashMap columnPropertyMap = getColumnPropertyMap4ReadStyle(
				columnMethodMap, domainObject);

		int changedColumnSize = columnPropertyMap.size();
		if (changedColumnSize <= 0) {
			log.warn(className
					+ "using getByDomainObjectSelective without assigning any property value is meaningless! please use getAll install.");
		}
		String[] columnNames = new String[changedColumnSize];
		Object[] columnValues = new Object[changedColumnSize];
		int index = 0;
		for (Map.Entry<String, Object> entry : columnPropertyMap.entrySet()) {
			columnNames[index] = entry.getKey();
			columnValues[index] = entry.getValue();
			index++;
		}

		// with fields ready, begin to query
		List<Object> params = new ArrayList<Object>();
		for (int i = 0; i < columnNames.length; i++) {
			params.add(columnValues[i]);
		}

		// count first
		StringBuilder countSb = new StringBuilder();
		countSb.append("select count(*) from  ");
		countSb.append(getObjectConfig().getTableName());

		if (changedColumnSize > 0) {
			countSb.append(" where ");
			for (int i = 0; i < columnNames.length; i++) {
				if (i != 0) {
					countSb.append(" and ");
				}
				countSb.append(columnNames[i]);
				countSb.append("=?");
			}
		}

		long count = getSqlManager().queryCount(countSb.toString(), params);
		// list second
		pagination.setTotleCount(count);
		PaginationResult<DomainObject> paginationResult = new PaginationResult<DomainObject>();
		paginationResult.setQuery(pagination);

		if (count == 0) {
			paginationResult.setList(null);
			return paginationResult;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("select * from  ");
		sb.append(getObjectConfig().getTableName());

		if (changedColumnSize > 0) {
			sb.append(" where ");
			for (int i = 0; i < columnNames.length; i++) {
				if (i != 0) {
					sb.append(" and ");
				}
				sb.append(columnNames[i]);
				sb.append("=?");
			}
		}

		if (pagination != null) {
			if (StringUtils.isNotBlank(pagination.getSortCriterial())) {
				sb.append(" order by " + pagination.getSortCriterial());
			}
			if (pagination.getLimit() > 0) {
				sb.append(" limit " + pagination.getLimit());
				if (pagination.getOffset() > 0) {
					sb.append(" offset " + pagination.getOffset());
				}
			}
		}

		List<DomainObject> list = getSqlManager().queryList(sb.toString(),
				this, params);
		paginationResult.setList(list);
		return paginationResult;

	}

	@Override
	public PaginationResult<DomainObject> getAllPagination(
			PaginationBaseQuery pagination) {

		// count first
		StringBuilder countSb = new StringBuilder();
		countSb.append("select count(*) from  ");
		countSb.append(getObjectConfig().getTableName());

		long count = getSqlManager().queryCount(countSb.toString());
		// list second
		pagination.setTotleCount(count);
		PaginationResult<DomainObject> paginationResult = new PaginationResult<DomainObject>();
		paginationResult.setQuery(pagination);

		if (count == 0) {
			paginationResult.setList(null);
			return paginationResult;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("select * from  ");
		sb.append(getObjectConfig().getTableName());

		if (pagination != null) {
			if (StringUtils.isNotBlank(pagination.getSortCriterial())) {
				sb.append(" order by " + pagination.getSortCriterial());
			}
			if (pagination.getLimit() > 0) {
				sb.append(" limit " + pagination.getLimit());
				if (pagination.getOffset() > 0) {
					sb.append(" offset " + pagination.getOffset());
				}
			}
		}

		List<DomainObject> list = getSqlManager()
				.queryList(sb.toString(), this);
		paginationResult.setList(list);
		return paginationResult;
	}

	@Override
	public int updateSelectiveByIdAndVersion(DomainObject domainObject) {

		String className = getObjectConfig().getClassName();
		Map<String, Method> columnMethodMap = getGetMethodMap(className);

		LowcaseKeyHashMap columnPropertyMap = new LowcaseKeyHashMap();
		long idValue = getColumnPropertyMap4UpdateStyle(columnMethodMap,
				domainObject, columnPropertyMap);

		int changedColumnSize = columnPropertyMap.size();
		if (changedColumnSize <= 0) {
			throw new RuntimeException("update table "
					+ getObjectConfig().getTableName()
					+ " without assign any fields to change!");
		}

		String[] columnNames = new String[changedColumnSize];
		Object[] columnValues = new Object[changedColumnSize];

		int index = 0;
		boolean hasVersion = false;
		for (Map.Entry<String, Object> entry : columnPropertyMap.entrySet()) {
			String columnName = entry.getKey();
			Object columnValue = entry.getValue();

			if ("version".equalsIgnoreCase(columnName)) {
				if ((columnValue != null) && (columnValue instanceof Long)) {
					hasVersion = true;
				}
			}
			columnNames[index] = entry.getKey();
			columnValues[index] = entry.getValue();
			index++;
		}

		if (!hasVersion) {
			throw new RuntimeException(
					"can't updateSelectiveByIdAndVersion a table without version column, use updateSelectiveById instead.");
		}

		return updateChangedColumnValuesById(idValue, columnNames, columnValues);

	}

	@Override
	public int updateSelectiveById(DomainObject domainObject) {
		String className = getObjectConfig().getClassName();
		Map<String, Method> columnMethodMap = getGetMethodMap(className);

		LowcaseKeyHashMap columnPropertyMap = new LowcaseKeyHashMap();
		long idValue = getColumnPropertyMap4UpdateStyle(columnMethodMap,
				domainObject, columnPropertyMap);

		int changedColumnSize = columnPropertyMap.size();
		if (changedColumnSize <= 0) {
			throw new RuntimeException("update table "
					+ getObjectConfig().getTableName()
					+ " without assign any fields to change!");
		}

		String[] columnNames = new String[changedColumnSize];
		Object[] columnValues = new Object[changedColumnSize];

		int index = 0;
		for (Map.Entry<String, Object> entry : columnPropertyMap.entrySet()) {
			columnNames[index] = entry.getKey();
			columnValues[index] = entry.getValue();
			index++;
		}

		return updateChangedColumnValuesById(idValue, columnNames, columnValues);
	}

	@Override
	public int updateSelectiveByObject(DomainObject changedValue,
			DomainObject condition) {
		String className = getObjectConfig().getClassName();
		Map<String, Method> columnMethodMap = getGetMethodMap(className);

		LowcaseKeyHashMap changedColumnPropertyMap = new LowcaseKeyHashMap();

		this.getModifiableColumnPropertyMap(columnMethodMap, changedValue,
				changedColumnPropertyMap);
		LowcaseKeyHashMap conditionalColumnPropertyMap = this
				.getConditionalColumnPropertyMap(columnMethodMap, condition);

		int changedColumnSize = changedColumnPropertyMap.size();
		if (changedColumnSize <= 0) {
			throw new RuntimeException("update table "
					+ getObjectConfig().getTableName()
					+ " without assign any fields to change!");
		}
		int conditionalColumnSize = conditionalColumnPropertyMap.size();
		if (conditionalColumnSize <= 0) {
			throw new RuntimeException("update table "
					+ getObjectConfig().getTableName()
					+ " without any conditions!");
		}

		String[] columnNames = new String[changedColumnSize
				+ conditionalColumnSize];
		Object[] columnValues = new Object[changedColumnSize
				+ conditionalColumnSize];

		Set<String> conflictiveFields = new HashSet<String>();
		int index = 0;
		for (Map.Entry<String, Object> entry : changedColumnPropertyMap
				.entrySet()) {
			columnNames[index] = entry.getKey();
			conflictiveFields.add(columnNames[index]);
			columnValues[index] = entry.getValue();

			index++;
		}
		for (Map.Entry<String, Object> entry : conditionalColumnPropertyMap
				.entrySet()) {
			if (conflictiveFields.contains(entry.getKey())) {
				throw new RuntimeException("更新字段存在where条件语句中:" + entry.getKey()
						+ "，是不是修改下？");
			}
			columnNames[index] = entry.getKey();
			columnValues[index] = entry.getValue();
			index++;
		}

		return updateChangedColumnValuesByObject(columnNames, columnValues,
				index - conditionalColumnPropertyMap.size());
	}

	private int updateChangedColumnValuesByObject(String[] columnNames,
			Object[] values, int seperatorIndex) {
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE ");
		sb.append(getObjectConfig().getTableName());
		sb.append(" SET ");
		for (int i = 0; i < columnNames.length; i++) {
			if (i < seperatorIndex) {
				sb.append(columnNames[i]);
				sb.append("=?");
				if (i != seperatorIndex - 1) {
					sb.append(",");
				}
			} else {
				if (i == seperatorIndex)
					sb.append(" WHERE ");
				sb.append(columnNames[i]);
				sb.append("=?");
				if (i != columnNames.length - 1) {
					sb.append(" and ");
				}
			}
		}

		String sql = sb.toString();

		List<Object> params = new ArrayList<Object>();
		for (int i = 0; i < columnNames.length; i++) {
			params.add(values[i]);
		}

		return getSqlManager().updateRecords(sql, params);
	}

	private int updateChangedColumnValuesByCondition(String[] columnNames,
			Object[] values, String condition, Object... conditionParam) {
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE ");
		sb.append(getObjectConfig().getTableName());
		sb.append(" SET ");
		for (int i = 0; i < columnNames.length; i++) {

			sb.append(columnNames[i]);
			sb.append("=?");
			if (i != columnNames.length - 1) {
				sb.append(",");
			}

		}

		sb.append(" WHERE ");
		if (StringUtils.isBlank(condition)) {
			throw new RuntimeException("update table "
					+ getObjectConfig().getTableName()
					+ " without any conditions!");
		}
		sb.append(condition);

		List<Object> params = new ArrayList<Object>();
		for (int i = 0; i < columnNames.length; i++) {
			params.add(values[i]);
		}
		for (Object cp : conditionParam) {
			params.add(cp);
		}

		return getSqlManager().updateRecords(sb.toString(), params);
	}

	private LowcaseKeyHashMap getConditionalColumnPropertyMap(
			Map<String, Method> columnMethodMap, DomainObject domainObject) {
		return this.getColumnPropertyMap4ReadStyle(columnMethodMap,
				domainObject);
	}

	private int updateChangedColumnValuesById(long id, String[] columnNames,
			Object[] values) {
		if (columnNames.length == 0) {
			return 0;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE ");
		sb.append(getObjectConfig().getTableName());
		sb.append(" SET ");
		for (int i = 0; i < columnNames.length; i++) {
			sb.append(columnNames[i]);
			sb.append("=?");
			if (i != columnNames.length - 1) {
				sb.append(",");
			}
		}
		sb.append(" WHERE ");
		sb.append(getObjectConfig().getIdColumn());
		sb.append("=?");
		String sql = sb.toString();

		List<Object> params = new ArrayList<Object>();
		for (int i = 0; i < columnNames.length; i++) {
			params.add(values[i]);
		}
		params.add(id);

		return getSqlManager().updateRecords(sql, params);
	}

	@Override
	public Long getId() {
		return getSqlManager().allocateRecordId(
				getObjectConfig().getTableName());
	}

	@Override
	public String getTableName() {
		return getObjectConfig().getTableName();
	}

	@Override
	protected AddParamMap getAddParamMap(DomainObject object) {
		try {
			AddParamMap paramMap = new AddParamMap();
			String className = getObjectConfig().getClassName();
			Map<String, Method> methodMap = getGetMethodMap(className);

			String name;
			Object value;
			for (Iterator<String> i$ = methodMap.keySet().iterator(); i$
					.hasNext(); paramMap.put(name, value)) {
				name = i$.next();
				Method method = methodMap.get(name);
				value = method.invoke(object, new Object[0]);
				if (getObjectConfig().isIdColumn(name)
						&& ((value == null) || value.equals(Integer.valueOf(0))
								|| value.equals(Long.valueOf(0L)) || value
									.equals(Short.valueOf((short) 0)))) {
					setObjectId(object);
					value = method.invoke(object, new Object[0]);
				}

				if (!getObjectConfig().isIdColumn(name) && (value != null)
						&& java.util.Date.class.equals(value.getClass())) {
					value = ((java.util.Date) value).getTime();

				}
			}

			if (object instanceof ObjectBase) {
				long now = System.currentTimeMillis();
				paramMap.put("CreateTime", Long.valueOf(now));
				paramMap.put("ModifyTime", Long.valueOf(now));
			}
			return paramMap;
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public DomainObject getObjectFromRs(ResultSet rs) throws SQLException {
		try {
			String className = getObjectConfig().getClassName();
			Map<String, Method> methodMap = getSetMethodMap(className);
			Set<String> properties = getCustomProperties(className);
			Object object = newObject(className);
			ResultSetMetaData metaData = rs.getMetaData();
			int count = metaData.getColumnCount();
			for (int i = 1; i <= count; i++) {
				String columnName = metaData.getColumnName(i).toLowerCase();
				if (properties.contains(columnName))
					continue;
				if (methodMap.containsKey(columnName)) {
					Object value = rs.getObject(columnName);
					if (value == null)
						continue;
					Class<? extends Object> valueType = value.getClass();
					Method method = methodMap.get(columnName);
					Class<? extends Object> propertyType = method
							.getParameterTypes()[0];
					propertyType = ClassUtils.primitiveToWrapper(propertyType);
					boolean support = true;
					if (!valueType.equals(propertyType)) {
						if (valueType.equals(Long.class)) {
							if (propertyType.equals(Integer.class)) {
								value = Integer.valueOf(((Long) value)
										.intValue());
							} else if (propertyType.equals(Short.class)) {
								value = Short.valueOf(((Long) value)
										.shortValue());
							}

							else if (propertyType.equals(Byte.class)) {
								value = Byte
										.valueOf(((Long) value).byteValue());
							}

							else if (propertyType.equals(String.class)) {
								value = String.valueOf(value);
							} else if (propertyType
									.equals(java.util.Date.class)) {
								value = new java.util.Date((Long) value);
							} else {
								support = false;
							}

						} else if (valueType.equals(BigInteger.class)) {
							if (propertyType.equals(Long.class)) {
								value = ((BigInteger)value).longValue();
							} else {
								support = false;
							}
						}
						else if (valueType.equals(Integer.class)) {
							if (propertyType.equals(Long.class))
								value = Long.valueOf(((Integer) value)
										.longValue());
							else if (propertyType.equals(Short.class))
								value = Short.valueOf(((Integer) value)
										.shortValue());
							else if (propertyType.equals(Byte.class))
								value = Byte.valueOf(((Integer) value)
										.byteValue());
							else if (propertyType.equals(String.class))
								value = String.valueOf(value);
							else if (propertyType.equals(Boolean.class))
								value = Boolean.valueOf(((Integer) value)
										.intValue() != 0);
							else
								support = false;
						} else if (valueType.equals(String.class))
							try {
								if (propertyType.equals(Long.class)) {
									value = Long.valueOf(Long
											.parseLong((String) value));
								}

								else if (propertyType.equals(Integer.class)) {
									value = Integer.valueOf(Integer
											.parseInt((String) value));
								}

								else if (propertyType.equals(Short.class)) {
									value = Short.valueOf(Short
											.parseShort((String) value));
								}

								else if (propertyType.equals(Byte.class)) {
									value = Byte.valueOf(Byte
											.parseByte((String) value));
								}

								else {
									support = false;
								}

							} catch (NumberFormatException e) {
								log.warn(className
										+ "String\u8F6C\u5316\u9519\u8BEF", e);
								support = false;
							}
						else if (valueType.equals(java.sql.Timestamp.class)) {
							if (propertyType.equals(String.class))
								value = value.toString();
							if (propertyType.equals(Long.class)) {
								value = ((java.sql.Timestamp)value).getTime();
							}
							else
								support = false;
						} else if (valueType.equals(java.sql.Date.class)) {
							if (propertyType.equals(String.class))
								value = value.toString();
							else
								support = false;
						} else if (valueType.equals(java.sql.Time.class)) {
							if (propertyType.equals(String.class))
								value = value.toString();
							else
								support = false;
						} else {
							support = false;
						}
					}
					if (support) {
						method.invoke(object, new Object[] { value });
					}

					else {
						log.warn(
								(new StringBuilder())
										.append(className)
										.append("\u6682\u65F6\u4E0D\u652F\u6301")
										.append(valueType).append("\u5230")
										.append(propertyType)
										.append("\u7684\u8F6C\u5316")
										.toString(), new RuntimeException());
					}

				} else {
					log.warn((new StringBuilder())
							.append(className)
							.append(columnName)
							.append("\u6CA1\u6709\u627E\u5230\u5BF9\u5E94\u7684set\u65B9\u6CD5")
							.toString());
				}
			}

			return (DomainObject) object;
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		}

	}

	public static void main(String[] args) {
		System.out.println((new StringBuilder())
				.append("\u6682\u65F6\u4E0D\u652F\u6301").append("valueType")
				.append("\u5230").append("propertyType")
				.append("\u7684\u8F6C\u5316").toString());

		System.out
				.println((new StringBuilder())
						.append("columnName")
						.append("\u6CA1\u6709\u627E\u5230\u5BF9\u5E94\u7684set\u65B9\u6CD5")
						.toString());

	}

	private Map<String, Method> getSetMethodMap(String className) {
		Map<String, Method> methodMap = columnSetMethodClassMap.get(className);
		if (methodMap == null)
			synchronized (this) {
				methodMap = columnSetMethodClassMap.get(className);
				if (methodMap == null) {
					init(className);
					methodMap = columnSetMethodClassMap.get(className);
				}
			}
		return methodMap;
	}

	private Map<String, Method> getGetMethodMap(String className) {
		Map<String, Method> methodMap = columnGetMethodClassMap.get(className);
		if (methodMap == null)
			synchronized (this) {
				methodMap = columnGetMethodClassMap.get(className);
				if (methodMap == null) {
					init(className);
					methodMap = columnGetMethodClassMap.get(className);
				}
			}
		return methodMap;
	}

	private Set<String> getCustomProperties(String className) {
		Set<String> cp = virtualPropertiesClassMap.get(className);
		if (cp == null)
			synchronized (this) {
				cp = virtualPropertiesClassMap.get(className);
				if (cp == null) {
					init(className);
					cp = virtualPropertiesClassMap.get(className);
				}
			}
		return cp;
	}

	private void init(String className) {
		try {
			Class<? extends Object> clazz = Class.forName(className);
			Method methods[] = clazz.getMethods();
			Map<String, Method> setMethodMap = new HashMap<String, Method>();
			Map<String, Method> getMethodMap = new HashMap<String, Method>();
			Set<String> customProperty = new HashSet<String>();
			Method arr$[] = methods;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++) {
				Method method = arr$[i$];

				if (method.getName().equals("getClass")) {
					continue;
				}

				if (!method.getName().startsWith("get")
						&& !method.getName().startsWith("is")) {
					continue;
				}

				if (method.isAnnotationPresent(VirtualProperty.class)) {
					String propertyName = getPropertyName(method,
							VirtualProperty.class.getSimpleName());
					customProperty.add(propertyName.toLowerCase());
					continue;
				}

				String columnName = null;
				String propertyName = getPropertyName(method);
				if (method.isAnnotationPresent(DataProperty.class)) {
					DataProperty dp = method.getAnnotation(DataProperty.class);
					columnName = dp.column();

				} else {
					columnName = propertyName;
				}

				String setMethodName = (new StringBuilder()).append("set")
						.append(propertyName).toString();
				Method setMethod = clazz.getMethod(setMethodName,
						new Class[] { method.getReturnType() });
				setMethodMap.put(columnName.toLowerCase(), setMethod);
				getMethodMap.put(columnName.toLowerCase(), method);

			}

			columnGetMethodClassMap.put(className, getMethodMap);
			columnSetMethodClassMap.put(className, setMethodMap);
			virtualPropertiesClassMap.put(className, customProperty);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private String getPropertyName(Method method) {
		if (method.getName().startsWith("get")
				|| method.getName().startsWith("set"))
			return method.getName().substring(3);
		if (method.getName().startsWith("is"))
			return method.getName().substring(2);
		else
			throw new RuntimeException((new StringBuilder()).append(
					"\u6807\u8BC6\u6807\u9519\u4F4D\u7F6E\u4E86").toString());
	}

	private String getPropertyName(Method method, String annotationName) {
		if (method.getName().startsWith("get")
				|| method.getName().startsWith("set"))
			return method.getName().substring(3);
		if (method.getName().startsWith("is"))
			return method.getName().substring(2);
		else
			throw new RuntimeException((new StringBuilder())
					.append(annotationName)
					.append("\u6807\u8BC6\u6807\u9519\u4F4D\u7F6E\u4E86")
					.toString());
	}

	private Object newObject(String className) {
		try {
			Class<? extends Object> clazz = Class.forName(className);
			Object object = clazz.newInstance();
			return object;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<DomainObject> getByIdList(List<Long> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return null;
		}
		StringBuilder sql = new StringBuilder("select * from ");
		sql.append(getTableName()).append(" where ");
		sql.append(SqlBuilder.inSql(getObjectConfig().getIdColumn(), ids));

		return this.getSqlManager().queryList(sql.toString(), this);
	}

	@Override
	public List<DomainObject> getByPropertyList(
			List<? extends Object> propertyList, String columnName) {
		Assert.isTrue(StringUtils.isNotBlank(columnName));
		if (CollectionUtils.isEmpty(propertyList)) {
			return null;
		}
		StringBuilder sql = new StringBuilder("select * from ");
		sql.append(getTableName()).append(" where ")
				.append(SqlBuilder.inSql(columnName, propertyList));

		return this.getSqlManager().queryList(sql.toString(), this);
	}

	private LowcaseKeyHashMap getColumnPropertyMap4ReadStyle(
			Map<String, Method> columnMethodMap, DomainObject domainObject) {
		LowcaseKeyHashMap columnPropertyMap = new LowcaseKeyHashMap();
		for (Map.Entry<String, Method> columnMethodEntry : columnMethodMap
				.entrySet()) {

			String columenName = columnMethodEntry.getKey();
			Method propertyMethod = columnMethodEntry.getValue();
			Object propertyValue = null;
			try {
				propertyValue = propertyMethod.invoke(domainObject,
						new Object[0]);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}

			if (propertyValue != null) {

				if (java.util.Date.class.equals(propertyValue.getClass())) {
					propertyValue = ((java.util.Date) propertyValue).getTime();
				}
				columnPropertyMap.put(columenName, propertyValue);
			}

		}

		return columnPropertyMap;
	}

	private long getColumnPropertyMap4UpdateStyle(
			Map<String, Method> columnMethodMap, DomainObject domainObject,
			LowcaseKeyHashMap columnPropertyMap) {
		long idValue = 0;

		for (Map.Entry<String, Method> columnMethodEntry : columnMethodMap
				.entrySet()) {

			String columenName = columnMethodEntry.getKey();
			Method propertyMethod = columnMethodEntry.getValue();
			Object propertyValue = null;

			// 跳过非Id的均衡字段修改 （注意，均衡字段无法修改）
			String policyColumn = getObjectConfig().getPolicyIdColumn();
			if (StringUtils.isNotBlank(policyColumn)
					&& policyColumn.equals(columenName)
					&& !policyColumn.equals(getObjectConfig().getIdColumn())) {
				continue;
			}

			try {
				propertyValue = propertyMethod.invoke(domainObject,
						new Object[0]);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}

			if (columenName.equalsIgnoreCase(getObjectConfig().getIdColumn())) {
				if (propertyValue == null) {
					throw new RuntimeException("update table "
							+ getObjectConfig().getTableName()
							+ " without assign id!");
				}

				idValue = (Long) propertyValue;
			} else {
				if (propertyValue != null) {
					if (java.util.Date.class.equals(propertyValue.getClass())) {
						propertyValue = ((java.util.Date) propertyValue)
								.getTime();
					}
					columnPropertyMap.put(columenName, propertyValue);
				}

			}

		}
		return idValue;
	}

	private void getModifiableColumnPropertyMap(
			Map<String, Method> columnMethodMap, DomainObject domainObject,
			LowcaseKeyHashMap columnPropertyMap) {
		for (Map.Entry<String, Method> columnMethodEntry : columnMethodMap
				.entrySet()) {

			String columenName = columnMethodEntry.getKey();
			Method propertyMethod = columnMethodEntry.getValue();
			Object propertyValue = null;

			// 跳过非Id的均衡字段修改 （注意，均衡字段无法修改）
			String policyColumn = getObjectConfig().getPolicyIdColumn();
			if (StringUtils.isNotBlank(policyColumn)
					&& policyColumn.equals(columenName)
					&& !policyColumn.equals(getObjectConfig().getIdColumn())) {
				continue;
			}

			try {
				propertyValue = propertyMethod.invoke(domainObject,
						new Object[0]);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}

			if (columenName.equalsIgnoreCase(getObjectConfig().getIdColumn())) {
				// 如果是id则直接抛异常，因为id不可修改
				if (propertyValue != null) {
					throw new RuntimeException("can't update table "
							+ getObjectConfig().getTableName() + " id!");
				}
			} else {
				if (propertyValue != null) {
					if (java.util.Date.class.equals(propertyValue.getClass())) {
						propertyValue = ((java.util.Date) propertyValue)
								.getTime();
					}
					columnPropertyMap.put(columenName, propertyValue);
				}

			}

		}
	}

	@Override
	public long countByDomainObjectSelective(DomainObject domainObject) {
		if (domainObject == null) {
			return 0L;
		}

		String className = getObjectConfig().getClassName();
		Map<String, Method> columnMethodMap = getGetMethodMap(className);

		LowcaseKeyHashMap columnPropertyMap = getColumnPropertyMap4ReadStyle(
				columnMethodMap, domainObject);

		int changedColumnSize = columnPropertyMap.size();
		if (changedColumnSize <= 0) {
			log.warn(className
					+ "using countByDomainObjectSelective without assigning any property value is meaningless! please use countAll install.");
		}

		String[] columnNames = new String[changedColumnSize];
		Object[] columnValues = new Object[changedColumnSize];
		int index = 0;
		for (Map.Entry<String, Object> entry : columnPropertyMap.entrySet()) {
			columnNames[index] = entry.getKey();
			columnValues[index] = entry.getValue();
			index++;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("select count(*) from ");
		sb.append(getObjectConfig().getTableName());

		if (changedColumnSize > 0) {
			sb.append(" where ");
			for (int i = 0; i < columnNames.length; i++) {
				if (i != 0) {
					sb.append(" and ");
				}
				sb.append(columnNames[i]);
				sb.append("=?");
			}
		}

		List<Object> params = new ArrayList<Object>();
		for (int i = 0; i < columnNames.length; i++) {
			params.add(columnValues[i]);
		}
		return getSqlManager().queryCount(sb.toString(), params);
	}

	@Override
	public PaginationResult<DomainObject> getPaginationByCondition(
			String condition, List<Object> params,
			PaginationBaseQuery pagination) {

		if (StringUtils.isEmpty(condition)) {
			return getAllPagination(pagination);
		}

		if (params == null) {
			params = Collections.emptyList();
		}

		// count first
		StringBuilder countSb = new StringBuilder();
		countSb.append("select count(*) from  ");
		countSb.append(getObjectConfig().getTableName());

		countSb.append(" where ");
		countSb.append(condition);
		countSb.append(" ");

		long count = getSqlManager().queryCount(countSb.toString(), params);

		// list second
		pagination.setTotleCount(count);
		PaginationResult<DomainObject> paginationResult = new PaginationResult<DomainObject>();
		paginationResult.setQuery(pagination);

		if (count == 0) {
			paginationResult.setList(null);
			return paginationResult;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("select * from  ");
		sb.append(getObjectConfig().getTableName());

		sb.append(" where ");
		sb.append(condition);
		sb.append(" ");

		if (pagination != null) {
			if (StringUtils.isNotBlank(pagination.getSortCriterial())) {
				sb.append(" order by " + pagination.getSortCriterial());
			}
			if (pagination.getLimit() > 0) {
				sb.append(" limit " + pagination.getLimit());
				if (pagination.getOffset() > 0) {
					sb.append(" offset " + pagination.getOffset());
				}
			}
		}

		List<DomainObject> list = getSqlManager().queryList(sb.toString(),
				this, params);
		paginationResult.setList(list);
		return paginationResult;

	}

	@Override
	public int updateSelectiveByCondition(DomainObject changedValue,
			String condition, Object... conditionParam) {
		String className = getObjectConfig().getClassName();
		Map<String, Method> columnMethodMap = getGetMethodMap(className);

		LowcaseKeyHashMap changedColumnPropertyMap = new LowcaseKeyHashMap();

		this.getModifiableColumnPropertyMap(columnMethodMap, changedValue,
				changedColumnPropertyMap);

		int changedColumnSize = changedColumnPropertyMap.size();
		if (changedColumnSize <= 0) {
			throw new RuntimeException("update table "
					+ getObjectConfig().getTableName()
					+ " without assign any fields to change!");
		}
		if (StringUtils.isBlank(condition)) {
			throw new RuntimeException("update table "
					+ getObjectConfig().getTableName()
					+ " without any conditions!");
		}

		String[] columnNames = new String[changedColumnSize];
		Object[] columnValues = new Object[changedColumnSize];

		int index = 0;
		for (Map.Entry<String, Object> entry : changedColumnPropertyMap
				.entrySet()) {
			columnNames[index] = entry.getKey();
			columnValues[index] = entry.getValue();
			index++;
		}

		return updateChangedColumnValuesByCondition(columnNames, columnValues,
				condition, conditionParam);
	}

	@Override
	public int deleteByCondition(String condition, Object... conditionParam) {
		if (StringUtils.isBlank(condition)) {
			throw new RuntimeException("delete where assign any condition !");
		}

		StringBuilder sb = new StringBuilder();
		sb.append("delete from  ");
		sb.append(getObjectConfig().getTableName());

		sb.append(" where ");
		sb.append(condition);
		return getSqlManager().updateRecords(sb.toString(), conditionParam);
	}

	@Override
	public <P> DomainObject getByIdAndPolicyId(Long id, P policyId,
			Class<P> policyIdClass) {
		StringBuilder sb = new StringBuilder();
		sb.append("select * from ").append(getTableName()).append(" where ")
				.append(getObjectConfig().getIdColumn()).append(" =? and ")
				.append(getObjectConfig().getPolicyIdColumn()).append(" =? ");
		return queryObject(sb.toString(), id, policyId);
	}

	@Override
	public <P> int deleteByIdAndPolicyId(Long id, P policyId,
			Class<P> policyIdClass) {
		StringBuilder sb = new StringBuilder();
		sb.append("delete from ").append(getTableName()).append(" where ")
				.append(getObjectConfig().getIdColumn()).append(" =? and ")
				.append(getObjectConfig().getPolicyIdColumn()).append(" =? ");
		return getSqlManager().updateRecords(sb.toString(), id, policyId);
	}

	@Override
	public Long[] getIdsByCondition(String condition, Object... param) {
		StringBuilder sb = new StringBuilder();
		sb.append("select ").append(getObjectConfig().getIdColumn())
				.append(" from ").append(getTableName());
		if (StringUtils.isNotBlank(condition)) {
			sb.append(" where ").append(condition);
		}
		return getSqlManager().queryObjectIds(sb.toString(), param);
	}

	@Override
	public Long getMaxExistId() {
		StringBuilder sb = new StringBuilder();
		sb.append("select   max(id)  from ").append(getTableName());
		return getSqlManager().queryObject(sb.toString(),
				MaxIdHandler.getInstance());
	}

	@Override
	public List<DomainObject> getOrderLimitByConditionOpt(
			OrderLimit orderLimit, String condition, Object... conditionParam) {
		if (orderLimit == null) {
			return null;
		}

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("select ").append(getObjectConfig().getIdColumn())
				.append(" from ").append(getTableName());
		if (StringUtils.isNotBlank(condition)) {
			selectSql.append(" where ").append(condition);
		}

		if (StringUtils.isNotBlank(orderLimit.getSortCriterial())) {
			selectSql.append(" order by ")
					.append(orderLimit.getSortCriterial());
		}

		if (orderLimit.getLimit() > 0) {
			selectSql.append(" limit ").append(orderLimit.getLimit());
		} else {// limit to 1024 avoid DB error
			selectSql.append(" limit ").append(1024);
		}

		if (orderLimit.getOffset() > 0) {
			selectSql.append(" offset ").append(orderLimit.getOffset());
		}

		Long[] ids = this.getSqlManager().queryObjectIds(selectSql.toString(),
				conditionParam);

		if (ids == null || !(ids.length > 0)) {
			return null;
		}

		selectSql = new StringBuilder();
		selectSql.append("select * from ");
		selectSql.append(getObjectConfig().getTableName());
		selectSql.append(" where ").append(
				SqlBuilder.inSql(getObjectConfig().getIdColumn(),
						Arrays.asList(ids)));
		if (StringUtils.isNotBlank(orderLimit.getSortCriterial())) {
			selectSql.append(" order by ")
					.append(orderLimit.getSortCriterial());
		}

		return queryObjects(selectSql.toString());
	}

	@Override
	public List<DomainObject> getAllOrderLimitOpt(OrderLimit orderLimit) {
		return this.getOrderLimitByConditionOpt(orderLimit, null);
	}

	@Override
	public PaginationResult<DomainObject> getAllPaginationOpt(
			PaginationBaseQuery pagination) {
		return getPaginationByConditionOpt(pagination, null);
	}

	@Override
	public PaginationResult<DomainObject> getPaginationByDomainObjectSelectiveOpt(
			PaginationBaseQuery pagination, DomainObject domainObject) {
		if (pagination == null) {
			return null;
		}

		StringBuilder condition = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		setConditionAndParams(domainObject, condition, params);

		return getPaginationByConditionOpt(pagination, condition.toString(),
				params.toArray());
	}

	@Override
	public PaginationResult<DomainObject> getPaginationByConditionOpt(
			PaginationBaseQuery pagination, String condition,
			Object... conditionParam) {
		if (pagination == null) {
			return null;
		}

		// count first
		StringBuilder countSb = new StringBuilder();
		countSb.append("select count(*) from ");
		countSb.append(getObjectConfig().getTableName());
		if (StringUtils.isNotBlank(condition)) {
			countSb.append(" where ").append(condition);
		}
		long count = getSqlManager().queryCount(countSb.toString(),
				conditionParam);

		// list second
		pagination.setTotleCount(count);
		PaginationResult<DomainObject> paginationResult = new PaginationResult<DomainObject>();
		paginationResult.setQuery(pagination);

		if (count == 0) {
			paginationResult.setList(null);
			return paginationResult;
		}

		OrderLimit ol = new OrderLimit();
		if (StringUtils.isNotBlank(pagination.getSortCriterial())) {
			ol.setSortCriterial(pagination.getSortCriterial());
		}
		if (pagination.getLimit() > 0) {
			ol.setLimit(pagination.getLimit());
			if (pagination.getOffset() > 0) {
				ol.setOffset(pagination.getOffset());
			}
		}

		List<DomainObject> list = this.getOrderLimitByConditionOpt(ol,
				condition, conditionParam);
		paginationResult.setList(list);

		return paginationResult;
	}

	@Override
	public List<DomainObject> getByDomainObjectSelectiveOpt(
			DomainObject domainObject, ListCondition lc) {
		if (lc == null) {
			return null;
		}

		if (domainObject == null) {
			return getAll(lc);
		}

		StringBuilder condition = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		setConditionAndParams(domainObject, condition, params);

		OrderLimit ol = new OrderLimit();
		if (StringUtils.isNotBlank(lc.getOrderColumn())) {
			StringBuilder sort = new StringBuilder();
			sort.append(lc.getOrderColumn());
			if (lc.isOrderAsc()) {
				sort.append(" asc");
			} else {
				sort.append(" desc");
			}
			ol.setSortCriterial(sort.toString());
		}

		if (lc.getLimit() > 0) {
			ol.setLimit(lc.getLimit());
			if (lc.getOffset() > 0) {
				ol.setOffset(lc.getOffset());
			}
		}

		return this.getOrderLimitByConditionOpt(ol, condition.toString(),
				params.toArray());
	}

	private void setConditionAndParams(DomainObject domainObject,
			StringBuilder condition, List<Object> params) {
		String className = getObjectConfig().getClassName();
		Map<String, Method> columnMethodMap = getGetMethodMap(className);

		LowcaseKeyHashMap columnPropertyMap = getColumnPropertyMap4ReadStyle(
				columnMethodMap, domainObject);

		int changedColumnSize = columnPropertyMap.size();
		if (changedColumnSize <= 0) {
			log.warn(className
					+ "using getByDomainObjectSelective without assigning any property value is meaningless! please use getAll install.");
		}
		String[] columnNames = new String[changedColumnSize];
		Object[] columnValues = new Object[changedColumnSize];
		int index = 0;
		for (Map.Entry<String, Object> entry : columnPropertyMap.entrySet()) {
			columnNames[index] = entry.getKey();
			columnValues[index] = entry.getValue();
			index++;
		}

		// with fields ready, begin to query
		for (int i = 0; i < columnNames.length; i++) {
			params.add(columnValues[i]);
		}

		if (changedColumnSize > 0) {
			for (int i = 0; i < columnNames.length; i++) {
				if (i != 0) {
					condition.append(" and ");
				}
				condition.append(columnNames[i]);
				condition.append(" = ? ");
			}
		}
	}

	public void initBaseDao() {

		ObjectConfig oc = getObjectConfig();
		if (oc != null && StringUtils.isNotBlank(oc.getClassName())) {
			if (log.isDebugEnabled()) {
				log.debug("============base dao null afterPropertiesSet"
						+ getClass().getName()
						+ "ObjectConfig="
						+ String.format("%s,%s,%s,%s", oc.getClassName(),
								oc.getIdProperty(), oc.getPolicyIdProperty()));
			}
			return;
		}
		DomainMetadata domainMetadata = this.getClass().getAnnotation(
				DomainMetadata.class);
		if (domainMetadata == null) {
			throw new RuntimeException(
					"No objectConfig in xml or annotation ! for "
							+ this.getClass());
		}
		oc = new ObjectConfig();
		if (domainMetadata.domainClass() == null) {
			throw new RuntimeException(
					"No domainClass assigned in DomainMetadata annotation ! for "
							+ this.getClass());
		}

		oc.setClassName(domainMetadata.domainClass().getName());
		if (StringUtils.isBlank(domainMetadata.tableName())) {
			String simpleClassName = domainMetadata.domainClass()
					.getSimpleName();
			oc.setTableName(NeteaseEduStringUtils
					.convertCamel2UnderlineStyle(simpleClassName));
		}

		if (StringUtils.isNotBlank(domainMetadata.tableName())) {
			oc.setTableName(domainMetadata.tableName());
		}

		if (StringUtils.isNotBlank(domainMetadata.idProperty())) {
			oc.setIdProperty(domainMetadata.idProperty());
		}
		if (StringUtils.isNotBlank(domainMetadata.idColumn())) {
			oc.setIdColumn(domainMetadata.idColumn());
		}
		if (StringUtils.isNotBlank(domainMetadata.policyIdProperty())) {
			oc.setPolicyIdProperty(domainMetadata.policyIdProperty());
		}
		if (StringUtils.isNotBlank(domainMetadata.policyIdColumn())) {
			oc.setPolicyIdColumn(domainMetadata.policyIdColumn());
		}

		setObjectConfig(oc);

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		initBaseDao();
	}
}
