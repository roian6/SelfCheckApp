package com.david0926.selfcheck.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SettingModel implements Serializable {

    private String title, message, version, link;
    private List<String> rspns;
    private Boolean enable, notice, cancelable, update;
    private Map<String, String> school;

    public SettingModel() {
    }

    public SettingModel(String title, String message, String version, String link, List<String> rspns, Boolean enable, Boolean notice, Boolean cancelable, Boolean update, Map<String, String> school) {
        this.title = title;
        this.message = message;
        this.version = version;
        this.link = link;
        this.rspns = rspns;
        this.enable = enable;
        this.notice = notice;
        this.cancelable = cancelable;
        this.update = update;
        this.school = school;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public List<String> getRspns() {
        return rspns;
    }

    public void setRspns(List<String> rspns) {
        this.rspns = rspns;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Boolean getNotice() {
        return notice;
    }

    public void setNotice(Boolean notice) {
        this.notice = notice;
    }

    public Boolean getCancelable() {
        return cancelable;
    }

    public void setCancelable(Boolean cancelable) {
        this.cancelable = cancelable;
    }

    public Boolean getUpdate() {
        return update;
    }

    public void setUpdate(Boolean update) {
        this.update = update;
    }

    public Map<String, String> getSchool() {
        return school;
    }

    public void setSchool(Map<String, String> school) {
        this.school = school;
    }
}
