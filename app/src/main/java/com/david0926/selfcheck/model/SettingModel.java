package com.david0926.selfcheck.model;

public class SettingModel {

    private String title, message, version, link;
    private Boolean enable, notice, cancelable, update;

    public SettingModel() {
    }

    public SettingModel(String title, String message, String version, String link, Boolean enable, Boolean notice, Boolean cancelable, Boolean update) {
        this.title = title;
        this.message = message;
        this.version = version;
        this.link = link;
        this.enable = enable;
        this.notice = notice;
        this.cancelable = cancelable;
        this.update = update;
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
}
