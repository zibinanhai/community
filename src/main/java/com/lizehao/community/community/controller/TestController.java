package com.lizehao.community.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/test")
public class TestController {

    @RequestMapping("/hello")
    @ResponseBody
    public String Hello() {
        return "Hello World";
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response) {
        //不需要返回值，因为Response对象可以向浏览器输入数据
        //获取请求对象，响应对象


        //获取请求数据
        //请求行
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        //请求消息头部
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + ":" + value);
        }
        //访问方法的时候传入参数的话，用下行代码获取参数,比如name=lll
        System.out.println(request.getParameter("code"));

        //返回响应数据
        response.setContentType("text/html; charset=UTF-8");
        try (
                PrintWriter writer = response.getWriter();
        ) {
            writer.write("<h1> 标题 <h1>");
            //底层就是用这种方式一步一步写网页
            //实际开发不这样做
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //怎么处理带参数的GET请求
    // /students?current=1&limit=20
    // 请求学生数据，分页显示，当前在第一页，一页最多20个

    //指定路径和强制只有GET方法才能访问
    @RequestMapping(path = "/students", method = RequestMethod.GET)
    @ResponseBody
    public String getStudents (
            //获取参数，false指这个参数没有也可已，默认是1
            @RequestParam (name = "current",required = false,defaultValue = "1") int current,
            @RequestParam (name = "limit",required = false,defaultValue = "10") int limit){
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    //  /student/123
    @RequestMapping(path = "/student/(id)",method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id){
        System.out.println(id);
        return "a student";
    }

    //post请求
    //post请求是要上传数据，所以要在static里面创建表单来接收数据
    //不用get传数据是因为用get传路径就会变得很长，而且长度有限制
    //用户访问post请求的路径时，会进入一个静态的表单，填入数据后，传入这个程序，并return
    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name,int age){
    //参数和表单一致，就会把数据传过来
        System.out.println(name);
        System.out.println(age);
        return "success";
    }


    //如何向浏览器响应html
    @RequestMapping(path = "/teacher",method = RequestMethod.GET)
    public ModelAndView getTeacher(){
        //所有组件都是DispatcherServlet调度的，Controller把Model传给它
        //它再把model传给模板引擎，得到一个动态网页，返回给浏览器
        ModelAndView mav= new ModelAndView();
        mav.addObject("name","张三");
        mav.addObject("age",30);
        //路径名和文件名给model
        mav.setViewName("/demo/view");
        return mav;

    }

    //另一种响应html方式，更简洁
    @RequestMapping(path = "/school",method = RequestMethod.GET)
    public String getSchool(Model model){
        model.addAttribute("name","北京大学");
        model.addAttribute("age",80);
        return "/demo/view";
    }

    //响应JSON数据（异步请求——当前页面不刷新，访问了数据库，
    // 例如注册用户名enter以后就自动查找服务器是否有相同名字）
    //JAVA对象 -> JSON字符串 -> JS对象
    //返回JSON要加上@ResponseBody
    @RequestMapping(path = "/emp",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getEmp(){
        Map<String,Object> emp = new HashMap<>();

        emp.put("name","张三");
        emp.put("age",23);
        emp.put("salary",20000);
        return emp;
        //输出为{"name":"张三","salary":20000,"age":23}，这个是JSON通用格式
    }

    //查询所有员工
    @RequestMapping(path = "/emps",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getEmps(){
        List<Map<String,Object>> list = new ArrayList<>();

        Map<String,Object> emp = new HashMap<>();
        emp.put("name","张三");
        emp.put("age",23);
        emp.put("salary",30000);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name","李四");
        emp.put("age",24);
        emp.put("salary",40000);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name","王五");
        emp.put("age",25);
        emp.put("salary",50000);
        list.add(emp);

        return list;
        //输出为[{"name":"张三","salary":30000,"age":23},
        // {"name":"李四","salary":40000,"age":24},
        // {"name":"王五","salary":50000,"age":25}]

    }

}
