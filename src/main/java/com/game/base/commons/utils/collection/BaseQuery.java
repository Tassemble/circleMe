package com.game.base.commons.utils.collection;

import java.io.Serializable;

/*
 * @author 
 * @date 2012-7-12
 */
public class BaseQuery implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID         = 7570580569451391302L;
    private final int         DEFAULT_PAGE_SIZE        = 10;
    private final int         DEFAULT_PAGE_INDEX       = 1;
    private final int         DEFAULT_TOTLE_PAGE_COUNT = 1;
    private final long        DEFAULT_TOTLE_COUNT      = 0;
    private final long        DEFAULT_OFFSET           = 0;

    private int               pageSize                 = DEFAULT_PAGE_SIZE;
    private int               pageIndex                = DEFAULT_PAGE_INDEX;
    private int               totlePageCount           = DEFAULT_TOTLE_PAGE_COUNT;
    private long              totleCount               = DEFAULT_TOTLE_COUNT;
    private long              offset                   = DEFAULT_OFFSET;

    private int               limit                    = DEFAULT_PAGE_SIZE;

    public BaseQuery(int pageSize, int pageIndex) {
        setPageIndex(pageIndex);
        setPageSize(pageSize);
        setOffset((this.pageIndex - 1) * this.pageSize);
        setLimit(pageSize);
    }

    /**
     * 页大小
     * 
     * @return
     */
    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        if (pageSize <= 0) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        this.pageSize = pageSize;
        setLimit(pageSize);
        setOffset((this.pageIndex - 1) * this.pageSize);
    }

    /**
     * 当前页码
     * 
     * @return
     */
    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        if (pageIndex <= 0) {
            pageIndex = DEFAULT_PAGE_INDEX;
        }

        this.pageIndex = pageIndex;
        setOffset((this.pageIndex - 1) * this.pageSize);
    }

    /**
     * 总记录条数
     * 
     * @return
     */
    public long getTotleCount() {
        return totleCount;
    }

    /**
     * 计算总页数
     * 
     * @param totleCount
     */
    public void setTotleCount(long totleCount) {
        if (totleCount < 0) {
            totleCount = DEFAULT_TOTLE_COUNT;
        }

        // 统计总页数
        if (totleCount == 0) {
            totlePageCount = 1;
        } else {
            totlePageCount = (int) ((totleCount - 1) / pageSize + 1);
        }

        // 防止上越界
        if (pageIndex <= 0) {
            this.setPageIndex(1);
        }
        // 防止下越界
        if (pageIndex > totlePageCount) {
            this.setPageIndex(totlePageCount);
        }

        this.totleCount = totleCount;
    }

    /**
     * @return
     */
    public long getOffset() {

        return offset;
    }

    public void setOffset(long offset) {
        if (offset < 0) {
            offset = DEFAULT_OFFSET;
        }
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public int getTotlePageCount() {
        return totlePageCount;
    }

    public void setTotlePageCount(int totlePageCount) {
        this.totlePageCount = totlePageCount;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

}
