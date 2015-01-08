package com.hfour.base.datamgr;
/**
 * 数据层的业务逻辑消息请全部定义在此处，避免冲突<br>
 * 定义模板<br>
 * MSG_MODE_XXXXX
 * @author Tony
 *
 */
public class DataMgrMessages {
	//登录MSG
	public static final int MSG_LOGIN_SUCCESS = 1;
	public static final int MSG_LOGIN_FAILED = 2;
	
	//公告消息 10
	public static final int MSG_NOTICE_UPDATE = 10;
	public static final int MSG_NOTICE_NONE = 11;
	//版本更新
	public static final int MSG_UPDATE_VERSION_INFO = 15;
	
	//拜访记录 20
	public static final int MSG_VISIT_RECORDS_UPDATE = 20;
	public static final int MSG_VISIT_RECORDS_NONE_DATA = 21;
	
	//拜访详情30
	public static final int MSG_VISIT_DETAIL_UPDATE = 10;
	public static final int MSG_VISIT_DETAIL_NONE = 11;
	public static final int MSG_MODIFY_VISIT_SUCCESS = 12;
	public static final int MSG_MODIFY_VISIT_FAILED = 13;
	
	
	//新的拜访30
	public static final int MSG_SUBMIT_NEW_NOTE_SUCCESS = 30;
	public static final int MSG_SUBMIT_NEW_NOTES_FAILED = 31;
	public static final int MSG_REFRESH_VISIT_TASK_SUCCESS = 32;
	public static final int MSG_REFRESH_VISIT_TASK_FAILED = 33;
	
	//随拍列表
	public static final int MSG_BLOGS_UPDATE = 40;
	public static final int MSG_BLOGS_FAILED = 41;
	
	//反馈列表
	public static final int MSG_FEEDBACKS_UPDATE = 50;
	public static final int MSG_FEEDBACKS_FAILED = 51;
	
	//修改密码
	public static final int MSG_MODIFY_PWD_SUCCESS = 60;
	public static final int MSG_MODIFY_PWD_FAILED = 61;
	
	//修改头像
	public static final int MSG_MODIFY_PORTRAIT_SUCCESS = 70;
	public static final int MSG_MODIFY_PORTRAIT_FAILED = 71;
	
	//问题反馈的回复
	public static final int MSG_FEEDBACK_REPLY_UPDATE = 80;
	public static final int MSG_FEEDBACK_REPLAY_NONE = 81;
	
	//获取我的拜访区域
	public static final int MSG_MYAREA_UPDATE = 90;
	public static final int MSG_MYAREA_NONE = 91;
	
	//新随拍
	public static final int MSG_NEW_BLOG_SUCCESS = 110;
	public static final int MSG_NEW_BLOG_FAILED = 111;
	
	//新问题反馈
	public static final int MSG_NEW_FEEDBACK_SUCCESS = 120;
	public static final int MSG_NEW_FEEDBACK_FAILED = 121;
	
	public static final int MSG_MYCLIENT_UPDATE = 131;
	public static final int MSG_MYCLIENT_FAILED = 132;
	
}
