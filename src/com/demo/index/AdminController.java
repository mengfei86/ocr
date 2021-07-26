package com.demo.index;

import org.apache.log4j.Logger;

import com.demo.common.model.Realface;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

public class AdminController extends Controller {
	private static final Logger logger = Logger.getLogger(AdminController.class);

	public void index() {
		render("login.html");
	}

	public void login() {
		String username = getPara("username");
		String password = getPara("password");
		if (("admin".equals(username)) && ("adminadmin321".equals(password))) {
			setSessionAttr("nickname", "admin");
			redirect("/admin/select");
		} else {
			setAttr("msg", "用户名或密码错误");
			render("login.html");
		}
	}

	@Before({ AdminInterceptor.class })
	public void select() {
		String urls = getRequest().getSession().getServletContext().getRealPath("/");
		urls.replace("face_demo", "");
		logger.info(urls);
		int pagecount = 10;

		String aa = "SELECT r.*,IF(EXISTS(SELECT f.faketype FROM fakeface f WHERE f.pid = r.`id` AND f.faketype <> 4),1,0) AS video, IF(EXISTS(SELECT f.faketype FROM fakeface f WHERE f.pid = r.`id`  AND f.faketype = 4),1,0) AS photo";

		String delsql = " FROM  realface r ORDER BY r.id DESC";
		Page page = Realface.dao.paginate(getParaToInt(0, Integer.valueOf(1)).intValue(), pagecount, aa, delsql);
		setAttr("page", page);
		render("index.html");
	}

	@Before({ AdminInterceptor.class })
	public void show() {
		int id = getParaToInt(0).intValue();
		Realface a = (Realface) Realface.dao.findById(Integer.valueOf(id));
		setAttr("url", a.getUrl());
		render("show.html");
	}
}
