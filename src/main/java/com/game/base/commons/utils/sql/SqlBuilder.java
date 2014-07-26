package com.game.base.commons.utils.sql;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.netease.framework.dao.sql.ListCondition;

public class SqlBuilder {

    public static String inSql(String propertyName, List<? extends Object> valueList) {
        return builtInCondition(propertyName, valueList, true);
    }
    
    public static String notInSql(String propertyName, List<? extends Object> valueList) {
        return builtInCondition(propertyName, valueList, false);
    }
    
    private static String builtInCondition(String propertyName, List<? extends Object> valueList, boolean isIn) {
        if (StringUtils.isBlank(propertyName) || valueList == null || valueList.isEmpty()) {
            throw new IllegalArgumentException();
        }

        StringBuilder sb = new StringBuilder(" ");
        sb.append(propertyName);
        if (!isIn) {
            sb.append(" not ");
        }
        sb.append(" in (");
        int i = 1;
        int size = valueList.size();
        for (Object value : valueList) {
            if (value instanceof String) {
                sb.append("'").append(value);
                if (i == size) {
                    sb.append("'");
                } else {
                    sb.append("',");
                }
            } else {
                if (i == size) {
                    sb.append(value);
                } else {
                    sb.append(value).append(",");
                }
            }
            i++;
        }
        sb.append(")");

        return sb.toString();
    }

    /**
     * 转化ListCondition为sql
     * 
     * @param listCondition
     * @return
     */
    public static String listConditionSql(ListCondition listCondition) {
        if (listCondition == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder(" order by ");
        if (StringUtils.isNotBlank(listCondition.getOrderColumn())) {
            sb.append(listCondition.getOrderColumn());
            if (listCondition.isOrderAsc()) {
                sb.append(" asc ");
            } else {
                sb.append(" desc ");
            }
        }

        if (listCondition.getLimit() > 0) {
            sb.append(" limit " + listCondition.getLimit());
            if (listCondition.getOffset() > 0) {
                sb.append(" offset " + listCondition.getOffset());
            }
        }
        return sb.toString();

    }

    public static String commaSqlInUpdate(Map<String, Object> toSetKVs) {
        if (toSetKVs == null || toSetKVs.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int i = 1;
        for (Map.Entry<String, Object> kv : toSetKVs.entrySet()) {
            sb.append(" ");
            sb.append(kv.getKey());
            sb.append("=");
            sb.append(getValueForAndSqlInUpdate(kv.getValue()));
            if (i != toSetKVs.size()) {
                sb.append(" ,");
            }
            i++;
        }

        return sb.toString();

    }

    private static String getValueForAndSqlInUpdate(Object v) {

        if (v == null) {
            return null;
        }
        if (v instanceof String) {
            return "'" + v + "'";
        }

        if (v instanceof Number) {
            return v.toString();
        }

        throw new IllegalArgumentException();

    }
}
