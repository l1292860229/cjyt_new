package com.coolwin.XYT.apientity;

import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.Entity.UserMenu;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yf on 2017/3/27.
 */

public class UserInfo implements Serializable {
    private String uid;                      //用户id
    private String nickname;                 //用户昵称
    private String ka6Id;                    //ka6id,唯一
    private String token;                    //身份识别值,用户登录时会变更
    private String headSmall;                //小头像
    private String userGrade;                //用户当前等级
    private String companyId;                //用户的企业id,例如本企业id=585
    private String tid;                      //用户推荐id,用于发展下级用户
    private String username;                 //用户登录名
    private String password;                 //用户密码
    private String companywebsite;           //公司主页
    private String industry;                 //行业
    private String company;                  //所在公司
    private String companyaddress;           //公司地址
    private String job;                      //职位
    private String provide;                  //提供
    private String demand;                   //需求
    private String telephone;                //电话,手机号
    private ArrayList<UserMenu> userMenu;    //用户的自定义菜单

    public UserInfo(){};
    public UserInfo(Login login,String username){
        uid = login.uid;
        nickname = login.nickname;
        ka6Id = login.kai6Id;
        token = login.token;
        headSmall = login.headsmall;
        userGrade = login.userDj;
        companyId = login.ypId;
        tid = login.tid;
        this.username = username;
        password = login.password;
        companywebsite = login.companywebsite;
        industry = login.industry;
        company = login.company;
        companyaddress = login.companyaddress;
        job = login.job;
        provide = login.provide;
        demand = login.demand;
        telephone = login.telephone;
        userMenu  = login.mUserMenu;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getKa6Id() {
        return ka6Id;
    }

    public void setKa6Id(String ka6Id) {
        this.ka6Id = ka6Id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getHeadSmall() {
        return headSmall;
    }

    public void setHeadSmall(String headSmall) {
        this.headSmall = headSmall;
    }

    public String getUserGrade() {
        return userGrade;
    }

    public void setUserGrade(String userGrade) {
        this.userGrade = userGrade;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCompanywebsite() {
        return companywebsite;
    }

    public void setCompanywebsite(String companywebsite) {
        this.companywebsite = companywebsite;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompanyaddress() {
        return companyaddress;
    }

    public void setCompanyaddress(String companyaddress) {
        this.companyaddress = companyaddress;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getProvide() {
        return provide;
    }

    public void setProvide(String provide) {
        this.provide = provide;
    }

    public String getDemand() {
        return demand;
    }

    public void setDemand(String demand) {
        this.demand = demand;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public ArrayList<UserMenu> getUserMenu() {
        return userMenu;
    }

    public void setUserMenu(ArrayList<UserMenu> userMenu) {
        this.userMenu = userMenu;
    }
}
