package com.wq.demo.bean;

/**
 * Created by WQ on 2016/10/21.
 */

public class ChapterContentData extends BaseData {
	public ChapterContentBody showapi_res_body;

	public class ChapterContentBody {
		public int ret_code;
		public int bookId;
		public int cid;
		public String txt;
		public String cname;
	}
}
