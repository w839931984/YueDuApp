package com.wq.demo.bean;

import java.util.ArrayList;

/**
 * Created by WQ on 2016/10/24.
 */

public class AreaById extends BaseData {
	public AreaBody showapi_res_body;

	public class AreaBody {
		public int ret_code;
		public boolean flag;
		public SuperAreaData data;
	}

	public class SuperAreaData{
		public ArrayList<AreaData> children;
	}

	public class AreaData {
		public int id;
		public String areaName;

		@Override
		public String toString() {
			return "id:" + id + ",areaName:" + areaName;
		}
	}
}
