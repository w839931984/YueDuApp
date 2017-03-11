package com.wq.demo.bean;

import java.util.ArrayList;

/**
 * Created by WQ on 2016/10/12.
 */

public class NovelKindsData extends BaseData {
	public NovelKindsBody showapi_res_body;

	public class NovelKindsBody{
		public int ret_code;
		public ArrayList<NovelKind> typeList;
	}

	public class NovelKind{
		public String id;
		public String name;

		@Override
		public String toString() {
			return "id:"+id+";name:"+name;
		}
	}
}
