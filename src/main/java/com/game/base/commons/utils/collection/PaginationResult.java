package com.game.base.commons.utils.collection;

import java.io.Serializable;
import java.util.List;

/*
 * @author jinde.fangjd
 * @date 2012-4-12
 */
public class PaginationResult<T> implements Serializable {

    /**
     * 
     */
    private static final long   serialVersionUID = 336835384112586683L;
    private PaginationBaseQuery query;
    private List<T>             list;

    public PaginationBaseQuery getQuery() {
        return query;
    }

    public void setQuery(PaginationBaseQuery query) {
        this.query = query;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

}
