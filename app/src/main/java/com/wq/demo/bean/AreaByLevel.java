package com.wq.demo.bean;

import java.util.ArrayList;

/**
 * Created by WQ on 2016/10/24.
 */

public class AreaByLevel extends BaseData {
	public AreaBody showapi_res_body;

	public class AreaBody {
		public int ret_code;
		public boolean flag;
		public ArrayList<AreaData> data;
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
