package com.david0926.selfcheck.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CheckBody {

    @SerializedName("qstnCrtfcNoEncpt")
    private String qstnCrtfcNoEncpt;
    @SerializedName("rspns01")
    private String rspns01;
    @SerializedName("rspns02")
    private String rspns02;
    @SerializedName("rspns07")
    private String rspns07;
    @SerializedName("rspns08")
    private String rspns08;
    @SerializedName("rspns09")
    private String rspns09;
    @SerializedName("rtnRsltCode")
    private String rtnRsltCode;
    @SerializedName("schulNm")
    private String schulNm;
    @SerializedName("stdntName")
    private String stdntName;

    public CheckBody() {
    }

    public CheckBody(String qstnCrtfcNoEncpt, String rspns01, String rspns02, String rspns07, String rspns08, String rspns09, String rtnRsltCode, String schulNm, String stdntName) {
        this.qstnCrtfcNoEncpt = qstnCrtfcNoEncpt;
        this.rspns01 = rspns01;
        this.rspns02 = rspns02;
        this.rspns07 = rspns07;
        this.rspns08 = rspns08;
        this.rspns09 = rspns09;
        this.rtnRsltCode = rtnRsltCode;
        this.schulNm = schulNm;
        this.stdntName = stdntName;
    }

    public String getQstnCrtfcNoEncpt() {
        return qstnCrtfcNoEncpt;
    }

    public void setQstnCrtfcNoEncpt(String qstnCrtfcNoEncpt) {
        this.qstnCrtfcNoEncpt = qstnCrtfcNoEncpt;
    }

    public String getRspns01() {
        return rspns01;
    }

    public void setRspns01(String rspns01) {
        this.rspns01 = rspns01;
    }

    public String getRspns02() {
        return rspns02;
    }

    public void setRspns02(String rspns02) {
        this.rspns02 = rspns02;
    }

    public String getRspns07() {
        return rspns07;
    }

    public void setRspns07(String rspns07) {
        this.rspns07 = rspns07;
    }

    public String getRspns08() {
        return rspns08;
    }

    public void setRspns08(String rspns08) {
        this.rspns08 = rspns08;
    }

    public String getRspns09() {
        return rspns09;
    }

    public void setRspns09(String rspns09) {
        this.rspns09 = rspns09;
    }

    public String getRtnRsltCode() {
        return rtnRsltCode;
    }

    public void setRtnRsltCode(String rtnRsltCode) {
        this.rtnRsltCode = rtnRsltCode;
    }

    public String getSchulNm() {
        return schulNm;
    }

    public void setSchulNm(String schulNm) {
        this.schulNm = schulNm;
    }

    public String getStdntName() {
        return stdntName;
    }

    public void setStdntName(String stdntName) {
        this.stdntName = stdntName;
    }

}