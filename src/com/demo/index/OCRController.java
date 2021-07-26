package com.demo.index;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.demo.utils.Base64Util;
import com.demo.utils.ocr.Main;
import com.jfinal.core.Controller;
import com.jfinal.kit.HttpKit;

public class OCRController extends Controller {
	private static final Logger logger = Logger.getLogger(OCRController.class);

	public void index() throws Exception {
		String osurl = getRequest().getSession().getServletContext().getRealPath("/");
		osurl = osurl.replace("face_demo/", "");

		String json1 = HttpKit.readData(getRequest());
		JSONObject obj = JSONObject.parseObject(json1);
		JSONObject body = obj.getJSONObject("body");
		JSONObject head = obj.getJSONObject("head");

		String file = body.getString("file");
		String hz = body.getString("hz");
		String url = System.currentTimeMillis() + "." + hz;

		JSONObject re = new JSONObject();
		JSONObject _head = new JSONObject();
		JSONObject _body = new JSONObject();
		if (!Base64Util.GenerateImage(file, osurl + url)) {
			_head.put("CODE", "9999");
			_head.put("text", "上传失败");
			re.put("head", _head);
			renderJson(re);
			throw new Exception();
		} else {
			re.put("CODE", "1000");
			re.put("INFO", "成功");
			re.put("body", Main.test_sfz(osurl + url));
			renderJson(re);
		}
	}

}
