package com.game.base.commons.utils.collection;

/*
 * @author hzfjd@corp.netease.com
 * @date 2012-12-15
 */
public class OrderLimit {

    private String sortCriterial = null;
    private int    limit         = -1;
    private long   offset        = -1;

    public String getSortCriterial() {
        return sortCriterial;
    }

    public void setSortCriterial(String sortCriterial) {
        this.sortCriterial = sortCriterial;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

}
