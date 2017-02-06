package com.springmvc.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.springmvc.dao.TestDao;

@Controller
@RequestMapping("/test.action")
public class TestAction {
	@Autowired
	private TestDao testDao;
	
	@RequestMapping(params = "operate=helloworld")
	public String helloworld(String name) throws IOException {
		this.testDao.dosomething();
		return "index";
	}
	
	@RequestMapping(params = "operate=ajax")
	public @ResponseBody Object ajax(String name) throws IOException {
		return name;
	}
	
<<<<<<< HEAD
	//这是新的测试asd
=======
>>>>>>> parent of 5b2970a... 新增的注释
	@RequestMapping(params = "operate=mtd1")
	public void mtd1(HttpServletResponse response,HttpServletRequest request,HttpSession session) throws IOException {
		String param = request.getParameter("param");
		session.setAttribute("param", param);
		response.getWriter().println(param);
	}
}
