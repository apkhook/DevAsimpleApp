package com.qtfreet.devasimpleapp.data.bean;

import java.util.List;

/**
 * Created by Bear on 2016/2/3.
 */
public class DataInfo {

    public boolean error;

    public List<ResultsEntity> results;

    public static class ResultsEntity {
        public String who;
        public String publishedAt;
        public String desc;
        public String type;
        public String url;
        public boolean used;
        public String objectId;
        public String createdAt;
        public String updatedAt;
    }
}
