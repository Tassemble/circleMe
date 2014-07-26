package com.game.base.commons.utils.collection;

/*
 * @author jinde.fangjd
 * @date 2012-4-12
 */
public class PaginationBaseQuery extends BaseQuery {

    /**
     * 
     */
    private static final long serialVersionUID = 700893089023514074L;
    private String            sortCriterial    = null;

    public PaginationBaseQuery(int pageSize, int pageIndex) {
        this(pageSize, pageIndex, null);
    }

    public PaginationBaseQuery(int pageSize, int pageIndex, String sortCriterial) {
        super(pageSize, pageIndex);

        setSortCriterial(sortCriterial);
    }

    public String getSortCriterial() {
        return sortCriterial;
    }

    public void setSortCriterial(String sortCriterial) {
        this.sortCriterial = sortCriterial;
    }

}
