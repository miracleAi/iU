package com.android.biubiu.bean;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import android.R.integer;

public class BiuDetialBean {
	@SerializedName("nickname")
	private String nickname;
	@SerializedName("token")
	private String token;
	/**
	 * 头像url
	 */
	@SerializedName("icon_thumbnailUrl")
	private String icon_thumbnailUrl;
	@SerializedName("icon_url")
	private String iconOrigin;
	/**
	 * 用户的code
	 */
	@SerializedName("user_code")
	private String user_code;
	/**
	 * 距离 单位m
	 */
	@SerializedName("distance")
	private long distance;
	
	@SerializedName("chat_tags")
	private String chatTags;
	@SerializedName("time")
	private long time;
	/**
	 * 匹配度
	 */
	@SerializedName("matching_score")
	private int matching_score;
	@SerializedName("sex")
	private String sex;
	@SerializedName("age")
	private int age;
	/**
	 * 星座
	 */
	@SerializedName("starsign")
	private String starsign;
	@SerializedName("superman")
	private int superMan;
	/**
	 * 学校
	 */
	@SerializedName("school")
	private String school;
	
	/**
	 * 命中个性标签个数
	 */
	@SerializedName("hit_tags_num")
	private int  hit_tags_num;
	
	@SerializedName("hit_tags")
	private List<PersonalTagBean> hit_tags;
	/**
	 * 共同兴趣个数
	 */
	@SerializedName("interested_tags_num")
	private int interested_tags_num;
	@SerializedName("interested_tags")
	private ArrayList<InterestByCateBean> interested_tags;
	@SerializedName("message")
	private String biuState;

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getIcon_thumbnailUrl() {
		return icon_thumbnailUrl;
	}

	public void setIcon_thumbnailUrl(String icon_thumbnailUrl) {
		this.icon_thumbnailUrl = icon_thumbnailUrl;
	}

	public String getIconOrigin() {
		return iconOrigin;
	}

	public void setIconOrigin(String iconOrigin) {
		this.iconOrigin = iconOrigin;
	}

	public String getUser_code() {
		return user_code;
	}

	public void setUser_code(String user_code) {
		this.user_code = user_code;
	}

	public long getDistance() {
		return distance;
	}

	public void setDistance(long distance) {
		this.distance = distance;
	}

	public String getChatTags() {
		return chatTags;
	}

	public void setChatTags(String chatTags) {
		this.chatTags = chatTags;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getMatching_score() {
		return matching_score;
	}

	public void setMatching_score(int matching_score) {
		this.matching_score = matching_score;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getStarsign() {
		return starsign;
	}

	public void setStarsign(String starsign) {
		this.starsign = starsign;
	}

	public int getSuperMan() {
		return superMan;
	}

	public void setSuperMan(int superMan) {
		this.superMan = superMan;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public int getHit_tags_num() {
		return hit_tags_num;
	}

	public void setHit_tags_num(int hit_tags_num) {
		this.hit_tags_num = hit_tags_num;
	}

	public List<PersonalTagBean> getHit_tags() {
		return hit_tags;
	}

	public void setHit_tags(List<PersonalTagBean> hit_tags) {
		this.hit_tags = hit_tags;
	}

	public int getInterested_tags_num() {
		return interested_tags_num;
	}

	public void setInterested_tags_num(int interested_tags_num) {
		this.interested_tags_num = interested_tags_num;
	}

	public ArrayList<InterestByCateBean> getInterested_tags() {
		return interested_tags;
	}

	public void setInterested_tags(ArrayList<InterestByCateBean> interested_tags) {
		this.interested_tags = interested_tags;
	}

	public String getBiuState() {
		return biuState;
	}

	public void setBiuState(String biuState) {
		this.biuState = biuState;
	}
}
