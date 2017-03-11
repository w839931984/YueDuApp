package com.wq.demo.bean;

import java.util.ArrayList;

/**
 * Created by WQ on 2016/10/21.
 */

public class NovelChapterData extends BaseData {
	public NovelChapterBody showapi_res_body;

	public class NovelChapterBody {
		public int ret_code;
		public String time;
		public BookData book;
	}

	public class BookData {
		public String typeName;
		public int id;
		public String author;
		public String updateTime;
		public String name;
		public ArrayList<Chapter> chapterList;

	}

	public class Chapter {
		public String name;
		public int bookId;
		public int cid;
	}

}
