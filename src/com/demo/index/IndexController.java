package com.demo.index;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.demo.common.model.Fakeface;
import com.demo.common.model.Realface;
import com.demo.utils.Base64Util;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.HttpKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;

public class IndexController extends Controller {
	private static final Logger logger = Logger.getLogger(IndexController.class);

	public void index() {
		render("index.html");
	}

	public void delete() {
		String osurl = getRequest().getSession().getServletContext().getRealPath("/");
		osurl = osurl.replace("face_demo/", "");
		String json1 = HttpKit.readData(getRequest());
		JSONObject obj = JSONObject.parseObject(json1);
		JSONObject body = obj.getJSONObject("body");
		JSONObject head = obj.getJSONObject("head");
		Integer userid = body.getInteger("userid");
		Realface.dao.deleteById(userid);
		String delsql = "DELETE FROM fakeface WHERE pid=?";

		String url = osurl + "/face/" + userid;
		JSONObject re = new JSONObject();
		JSONObject _head = new JSONObject();
		if (deleteDir(new File(url))) {
			Db.update(delsql, new Object[] { userid });
			_head.put("code", "0000");
			_head.put("text", "删除成功");
		} else {
			_head.put("code", "9999");
			_head.put("text", "删除失败");
		}
		re.put("head", _head);
		renderJson(re);
	}

	public void deletefake() {
		String osurl = getRequest().getSession().getServletContext().getRealPath("/");
		osurl = osurl.replace("face_demo/", "");
		String json1 = HttpKit.readData(getRequest());
		JSONObject obj = JSONObject.parseObject(json1);
		JSONObject body = obj.getJSONObject("body");
		Integer id = body.getInteger("id");
		Fakeface f = (Fakeface) Fakeface.dao.findById(id);
		String url = osurl + f.getUrl();
		JSONObject re = new JSONObject();
		JSONObject _head = new JSONObject();
		if (deleteDir(new File(url))) {
			f.delete();
			_head.put("code", "0000");
			_head.put("text", "删除成功");
		} else {
			_head.put("code", "9999");
			_head.put("text", "删除失败");
		}

		re.put("head", _head);
		renderJson(re);
	}

	private static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();

			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		return dir.delete();
	}

	public void select() {
		String json1 = HttpKit.readData(getRequest());
		JSONObject obj = JSONObject.parseObject(json1);
		JSONObject body = obj.getJSONObject("body");
		int pageno = body.getInteger("pageno").intValue();
		int pagecount = 10;

		String aa = "SELECT * ";
		String delsql = " FROM  realface ORDER BY id DESC";
		Page page = Realface.dao.paginate(pageno, pagecount, aa, delsql);
		List<Realface> list = page.getList();
		JSONArray _list = new JSONArray();
		for (Realface realface : list) {
			JSONObject jb = (JSONObject) JSON.toJSON(realface);
			List flist = Db.find("SELECT * from fakeface where pid=?", new Object[] { realface.getId() });
			jb.put("flist", flist);
			_list.add(jb);
		}
		JSONObject re = new JSONObject();
		JSONObject _body = new JSONObject();
		_body.put("list", _list);
		_body.put("pagenumber", Integer.valueOf(page.getPageNumber()));
		_body.put("pagesize", Integer.valueOf(page.getPageSize()));
		re.put("body", _body);
		JSONObject _head = new JSONObject();
		_head.put("code", "0000");
		_head.put("text", "ok");
		re.put("head", _head);
		renderJson(re);
	}

	@Before({ Tx.class })
	public void upload() throws Exception {
		String osurl = getRequest().getSession().getServletContext().getRealPath("/");
		osurl = osurl.replace("face_demo/", "");
		String json1 = HttpKit.readData(getRequest());
		JSONObject obj = JSONObject.parseObject(json1);
		JSONObject body = obj.getJSONObject("body");
		JSONObject head = obj.getJSONObject("head");

		String file = body.getString("file");
		String hz = body.getString("hz");
		String mobiletype = head.getString("mobiletype");
		int isindoor = body.getInteger("isindoor").intValue();
		Integer userid = body.getInteger("userid");
		Integer faketype = body.getInteger("faketype");

		logger.info("\n--hz==" + hz + "\n--mobiletype==" + mobiletype + "\n--isindoor==" + isindoor + "\n--userid==" + userid + "\n--faketype=="
				+ faketype);
		String url = "";
		if (userid == null) {
			Realface realface = new Realface();
			realface.setIsindoor(Integer.valueOf(isindoor));
			realface.setCreattime(new Date());
			realface.setMoblietype(mobiletype);
			realface.save();
			logger.info("--realface.getId()==" + realface.getId());
			url = getImgFilePath(realface.getId().intValue(), isindoor, mobiletype, faketype, 1, "", hz);
			realface.setUrl(url);
			realface.update();
		} else {
			Fakeface fakeface = new Fakeface();
			fakeface.setPid(userid);
			fakeface.setIsindoor(Integer.valueOf(isindoor));
			fakeface.setMobiletype(mobiletype);
			fakeface.setFaketype(faketype);
			fakeface.setCreatetime(new Date());
			fakeface.save();
			url = getImgFilePath(userid.intValue(), isindoor, mobiletype, faketype, 0, "_" + fakeface.getId(), hz);
			fakeface.setUrl(url);
			fakeface.update();
		}

		JSONObject re = new JSONObject();
		JSONObject _head = new JSONObject();
		if (!Base64Util.GenerateImage(file, osurl + url)) {
			_head.put("code", "9999");
			_head.put("text", "上传失败");
			re.put("head", _head);
			renderJson(re);
			throw new Exception();
		}
		_head.put("code", "0000");
		_head.put("text", "上传成功");
		re.put("head", _head);
		renderJson(re);
	}

	public String getImgFilePath(int userid, int isindoor, String mobiletype, Integer faketype, int isreal, String id, String hz) {
		String url = "/face/" + userid + "/";

		if ((isreal == 0) && ((faketype.intValue() == 6) || (faketype.intValue() == 7))) {
			isreal = 1;
		}
		if (isreal == 0) {
			url = url + "fake";
			url = url + "_" + mobiletype;
			switch (faketype.intValue()) {
			case 1:
				url = url + "_computer";
				break;
			case 2:
				url = url + "_pad";
				break;
			case 3:
				url = url + "_phone";
				break;
			case 4:
				url = url + "_photo";
				break;
			case 5:
				url = url + "_idcard";
				break;
			default:
				break;
			}
		} else {
			url = url + "real";
			url = url + "_" + mobiletype;
			if (faketype == null)
				url = url + "_large";
			else {
				switch (faketype.intValue()) {
				case 6:
					url = url + "_middle";
					break;
				case 7:
					url = url + "_small";
					break;
				}
			}

		}

		if (isindoor == 1)
			url = url + "_indoor";
		else {
			url = url + "_outdoor";
		}
		url = url + "_" + userid + id;
		url = url + "." + hz;
		return url;
	}

	public static void main(String[] args) {
		String a = "/home/java/tomcat7/webapps/face_demo/";
		System.err.println(a.replace("face_demo/", ""));
	}
}
