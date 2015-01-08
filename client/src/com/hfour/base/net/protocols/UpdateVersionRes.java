package com.hfour.base.net.protocols;

import com.hfour.base.net.BaseResp;
import com.hfour.base.net.data.UpdateVersionBean;
/**
 * {"code":100,"err":"","result":{"upgradeCode":1,"serverVer":"1.0.01","serverVerInt":1001,"downloadUrl":"http://cdn.joloplay.cn/a.apk"}}
 * @author Tony
 *
 */
public class UpdateVersionRes extends BaseResp{
	private UpdateVersionBean result;

	public UpdateVersionBean getResult() {
		return result;
	}

	public void setResult(UpdateVersionBean result) {
		this.result = result;
	}
}
