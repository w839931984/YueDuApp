package com.wq.demo.bean;

import java.util.ArrayList;

/**
 * Created by WQ on 2016/10/16.
 */

public class NovelBooksData extends BaseData {
	public NovelBooksBody showapi_res_body;

	public class NovelBooksBody {
		public int ret_code;
		public PageBean pagebean;
	}

	public class PageBean {
		public ArrayList<NovelBooks> contentlist;
		public String allpages;
		public String currentPage;
		public String allNum;
		public String maxResult;
	}

	public class NovelBooks{
		public String typeName;
		public int id;
		public String author;
		public String updateTime;
		public String name;
		public String type;
		public String newChapter;
		public String size;

		@Override
		public String toString() {
			return "name:"+name;
		}
	}
}
