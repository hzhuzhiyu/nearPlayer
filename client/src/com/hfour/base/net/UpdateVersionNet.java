package com.hfour.base.net;

import com.hfour.base.jsonutils.JsonUtil;
import com.hfour.base.net.protocols.UpdateVersionReq;
import com.hfour.base.net.protocols.UpdateVersionRes;

public class UpdateVersionNet extends AbstractNetSource<UpdateVersionReq, UpdateVersionRes>{
	public UpdateVersionNet(
			OnNetResponseListener<UpdateVersionRes> dataListener) {
		super(dataListener, NetConstants.METHOD_POST);
	}

	@Override
	public String getUrl() {
		return NetConstants.AWIFI_APP_SERVER+"upgrade";
//		return "http://192.168.1.17:8080/iwifiservice/upgrade";
	}


	
	@Override
	public UpdateVersionRes parsResp(String jsonString) {
		String tmpStr = jsonString.replace("\\\\", "\\"); //此处特殊处理下 会出现 \n 转成 \\n
		UpdateVersionRes resp = JsonUtil.parserObj(tmpStr, UpdateVersionRes.class);
		return resp;
	}

	@Override
	public UpdateVersionReq getRequest() {
		UpdateVersionReq request = new UpdateVersionReq();
		
		return request;
	}

}
