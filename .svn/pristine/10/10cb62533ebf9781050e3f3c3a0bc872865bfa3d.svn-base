package com.merge.config;

import java.util.List;

@SuppressWarnings("rawtypes")
public class Page<E extends java.io.Serializable> {
	//*********************************分页***********************************//
	private int startNum; //开始数
	private int pageNum;  //页数
	private List rows;    //行数
	private int total;    //总数
	private int cid;
	
    public int getStartNum() {
		return startNum;
	}
	public void setStartNum(int startNum) {
		this.startNum = startNum;
	}
	public int getPageNum() {
		return pageNum;
	}
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}
	public List getRows() {
		return rows;
	}
	public void setRows(List rows) {
		this.rows = rows;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
    public int getCid() {
        return cid;
    }
    public void setCid(int cid) {
        this.cid = cid;
    }

    //*********************************分页***********************************//
    //*********************************查询***********************************//
	private String Query ;
	/**
	 * get() 查询内容
	 * @return
	 */
	public String getQuery() {
		return Query;
	}
	/**
	 * set() 查询内容
	 * @return
	 */
	public void setQuery(String query) {
		Query = query;
	}
	
	//*********************************查询***********************************//
	
	
	//*********************************排序***********************************//.
	private String Order;
	private String sortName;
	/**
	 * get() 降序或升序  desc/asc
	 * @return
	 */
	public String getOrder() {
		return Order;
	}
	/**
	 * set() 降序或升序  desc/asc
	 * @return
	 */
	public void setOrder(String Order) {
		this.Order = Order;
	}
	/**
	 * get() 降序表名或升序表名  desc/asc
	 * @return
	 */
	public String getSortName() {
		return sortName;
	}
	/**
	 * set() 降序表名或升序表名  desc/asc
	 * @return
	 */
	public void setSortName(String sortName) {
		this.sortName = sortName;
	}
	//*********************************排序***********************************//
	
	//用于channel表的查询
	private int type;
	private String language;
	private int status;
    
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }	
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }
	
}
