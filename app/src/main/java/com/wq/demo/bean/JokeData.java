package com.wq.demo.bean;

import java.util.ArrayList;

/**
 * Created by WQ on 2017/3/16.
 */

public class JokeData extends BaseData{
    public JokeBody showapi_res_body;

    public class JokeBody {
        public int allPages;
        public int currentPage;
        public int allNum;
        public int maxResule;
        public ArrayList<Joke> contentlist;
    }

    public class Joke {
        public String id;
        public String title;
        public String img;
        public int type;
        public String text;
    }
}
