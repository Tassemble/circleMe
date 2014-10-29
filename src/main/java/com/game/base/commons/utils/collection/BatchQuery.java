package com.game.base.commons.utils.collection;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;


public class BatchQuery{

    /**
     * 对一个长数组queryParams进行分组查询，结果集再汇总到List<T>,其中BatchQueryerDataProvider为具体的数据查询服务
     * 
     * @param queryParams
     * @param dataProvider
     * @return
     * @author hzfjd@
     * @date 2013-3-29
     */
    public static <T, D> List<T> queryInBatch(List<D> queryParams, BatchQueryerDataProvider<T, D> dataProvider) {
        return queryInBatch(queryParams, dataProvider, 500);
    }

    public static <T, D> List<T> queryInBatch(List<D> queryParams, BatchQueryerDataProvider<T, D> dataProvider, int max) {
        List<T> all = new ArrayList<T>();
        int fromIndex = 0;
        int leftSize = queryParams.size();
        int toIndex = leftSize > max ? max : leftSize;
        int length = (toIndex - fromIndex);
        leftSize -= length;
        while (fromIndex < toIndex) {
            List<D> list = queryParams.subList(fromIndex, toIndex);
            List<T> refs = dataProvider.getByQueryParams(list);
            if (CollectionUtils.isEmpty(refs)) {
                break;
            }
            all.addAll(refs);
            fromIndex = toIndex;
            toIndex += leftSize > max ? max : leftSize;
            length = (toIndex - fromIndex);
            leftSize -= length;
        }

        return all;
    }
}
