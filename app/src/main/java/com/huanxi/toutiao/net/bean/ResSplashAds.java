package com.huanxi.toutiao.net.bean;

import com.huanxi.toutiao.ui.adapter.AdBean;
import com.huanxi.toutiao.ui.adapter.FloatAdBean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Dinosa on 2018/2/9.
 */

public class ResSplashAds implements Serializable{
    /**
     * newfubiao : []
     * videofubiao : []
     * splash : {}
     * shipin_content : {}
     * my : []
     * newdetail : []
     * videodetail : []
     * signin : []
     * redbag : []
     * tasklist : []
     * exchange : []
     * friend : []
     */

    private SplashBean splash;
    private VideoDetailAds shipin_content;
    private ArrayList<FloatAdBean> newfubiao;
    private ArrayList<FloatAdBean> videofubiao;
    private ArrayList<AdBean> my;
    private ArrayList<AdBean> newdetail;
    private ArrayList<AdBean> videodetail;
    private ArrayList<AdBean> signin;
    private ArrayList<AdBean> redbag;
    private ArrayList<AdBean> tasklist;
    private ArrayList<AdBean> exchange;
    private ArrayList<AdBean> friend;

    public SplashBean getSplash() {
        return splash;
    }

    public void setSplash(SplashBean splash) {
        this.splash = splash;
    }

    public VideoDetailAds getShipin_content() {
        return shipin_content;
    }

    public void setShipin_content(VideoDetailAds shipin_content) {
        this.shipin_content = shipin_content;
    }

    public ArrayList<FloatAdBean> getNewfubiao() {
        return newfubiao;
    }

    public void setNewfubiao(ArrayList<FloatAdBean> newfubiao) {
        this.newfubiao = newfubiao;
    }

    public ArrayList<FloatAdBean> getVideofubiao() {
        return videofubiao;
    }

    public void setVideofubiao(ArrayList<FloatAdBean> videofubiao) {
        this.videofubiao = videofubiao;
    }

    public ArrayList<AdBean> getMy() {
        return my;
    }

    public void setMy(ArrayList<AdBean> my) {
        this.my = my;
    }

    public ArrayList<AdBean> getNewdetail() {
        return newdetail;
    }

    public void setNewdetail(ArrayList<AdBean> newdetail) {
        this.newdetail = newdetail;
    }

    public ArrayList<AdBean> getVideodetail() {
        return videodetail;
    }

    public void setVideodetail(ArrayList<AdBean> videodetail) {
        this.videodetail = videodetail;
    }

    public ArrayList<AdBean> getSignin() {
        return signin;
    }

    public void setSignin(ArrayList<AdBean> signin) {
        this.signin = signin;
    }

    public ArrayList<AdBean> getRedbag() {
        return redbag;
    }

    public void setRedbag(ArrayList<AdBean> redbag) {
        this.redbag = redbag;
    }

    public ArrayList<AdBean> getTasklist() {
        return tasklist;
    }

    public void setTasklist(ArrayList<AdBean> tasklist) {
        this.tasklist = tasklist;
    }

    public ArrayList<AdBean> getExchange() {
        return exchange;
    }

    public void setExchange(ArrayList<AdBean> exchange) {
        this.exchange = exchange;
    }

    public ArrayList<AdBean> getFriend() {
        return friend;
    }

    public void setFriend(ArrayList<AdBean> friend) {
        this.friend = friend;
    }

    public static class SplashBean implements Serializable{

        public static final String TYPE_GDT="gdt";
        public static final String TYPE_TA="TA";
        public static final String TYPE_CUSTOM="qmtt_tl";

        private String type;
        private String id;
        private String imgurl;
        private String url;

        public String getImgurl() {
            return imgurl;
        }

        public void setImgurl(String imgurl) {
            this.imgurl = imgurl;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class VideoDetailAds implements Serializable{


        private String type;
        private String id;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }






/*    private SplashBean splash;
    private VideoDetailAds shipin_content;
    private ArrayList<AdBean> my;

    private ArrayList<AdBean> tasklist;
    private ArrayList<AdBean> videodetail;
    private ArrayList<AdBean> redbag;
    private ArrayList<AdBean> friend;
    private ArrayList<AdBean> signin;
    private ArrayList<AdBean> exchange;
    private ArrayList<AdBean> newdetail;

    private ArrayList<FloatAdBean> newfubiao;
    private ArrayList<FloatAdBean> videofubiao;


    public ArrayList<FloatAdBean> getNewfubiao() {
        return newfubiao;
    }

    public void setNewfubiao(ArrayList<FloatAdBean> newfubiao) {
        this.newfubiao = newfubiao;
    }

    public ArrayList<FloatAdBean> getVideofubiao() {
        return videofubiao;
    }

    public void setVideofubiao(ArrayList<FloatAdBean> videofubiao) {
        this.videofubiao = videofubiao;
    }

    public List<AdBean> getNewdetail() {
        return newdetail;
    }



    public List<AdBean> getExchange() {
        if (exchange == null) {
            return new ArrayList<>();
        }
        return exchange;
    }


    public List<AdBean> getFriend() {
        if(friend == null){
            return new ArrayList<>();
        }
        return friend;
    }

    public List<AdBean> getSignin() {
        return signin;
    }



    public List<AdBean> getTasklist() {
        if(tasklist==null){
            return new ArrayList<>();
        }
        return tasklist;
    }


    public List<AdBean> getVideodetail() {
        if(videodetail == null){
            return new ArrayList<>();
        }
        return videodetail;
    }


    public List<AdBean> getRedbag() {
        if (redbag == null) {
            return new ArrayList<>();
        }
        return redbag;
    }

    public SplashBean getSplash() {
        return splash;
    }

    public void setSplash(SplashBean splash) {
        this.splash = splash;
    }

    public VideoDetailAds getShipin_content() {
        return shipin_content;
    }

    public void setShipin_content(VideoDetailAds shipin_content) {
        this.shipin_content = shipin_content;
    }


    public static class SplashBean implements Serializable{

        public static final String TYPE_GDT="gdt";
        *//**
         * type : gdt
         * id : 9030838014299060
         *//*

        private String type;
        private String id;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class VideoDetailAds implements Serializable{
        *//**
         * type : gdt
         * id : 7090434044396100
         *//*

        private String type;
        private String id;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public List<AdBean> getMy() {
        if(my==null){
            return new ArrayList<>();
        }
        return my;
    }*/

}
