package com.bdqn.controller;


import cn.itrip.common.*;
import cn.itrip.dao.itripUser.ItripUserMapper;
import cn.itrip.pojo.ItripUser;
import cn.itrip.pojo.ItripUserVO;
import com.alibaba.fastjson.JSONArray;
import cz.mallat.uasparser.UserAgentInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

@Controller
public class UserContoller {

    @Resource
    ItripUserMapper dao;


    @Resource
    JredisApi jredisApi;


    @Resource
    SMS_Sent sms_sent;


    @RequestMapping(value="/api/validatephone",method= RequestMethod.PUT,produces="application/json; charset=utf-8")

    public @ResponseBody Dto re(String user,String code ,HttpServletRequest request) throws Exception {
           //判断redis 中是否有数据
           String oldcode=jredisApi.getRedis(user);
           if(oldcode!=null&&oldcode.equals(code))
           {
               dao.jh(user);
               //如果有数据那么把刚才插入到数据库中的数据激活
               return  DtoUtil.returnSuccess("激活成功");
           }
           return DtoUtil.returnFail("激活失败","1000");


    }


        @RequestMapping(value="/api/registerbyphone",method= RequestMethod.POST,produces="application/json; charset=utf-8")

    public @ResponseBody Dto re(@RequestBody ItripUserVO vo, HttpServletRequest request) throws Exception {

        try {
            //第一步插入数据
            ItripUser itripUser=new ItripUser();
            itripUser.setUserCode(vo.getUserCode());
            itripUser.setUserPassword(MD5.getMd5(vo.getUserPassword(),32) );
            itripUser.setUserName(vo.getUserName());
            itripUser.setActivated(0);

            dao.insertItripUser(itripUser);



            //第二步发送验证码 把手机号和验证码存入到redis 中15210254693--1123
            Random random=new Random(4);
            int mess=random.nextInt(9999);
            jredisApi.SetRedis(vo.getUserCode(),""+mess,300);

            //给手机发送短信
            sms_sent.SentSms(vo.getUserCode(),""+mess);
            return DtoUtil.returnSuccess("注册成功");
        }
        catch (Exception ex)
        {
            return DtoUtil.returnFail("登录失败","10000");
        }







    }


    @RequestMapping(value="/api/dologin",method= RequestMethod.POST,produces="application/json; charset=utf-8")
    @ResponseBody
    public Dto Dologin(String name, String password, HttpServletRequest request) throws Exception {
          ItripUser user=dao.getlogin(name, MD5.getMd5(password,32));
          //存入redis 中key  value 过期时间
          if(user!=null)
          {
              String agent=request.getHeader("User-Agent");
               //token =md5加密 userID+userCode+时间错
              String token=generateToken(agent,user);

              jredisApi.SetRedis(token, JSONArray.toJSONString(user),7200);

              ItripTokenVO
                      tokenVO=new ItripTokenVO(token, Calendar.getInstance().getTimeInMillis()+7200,Calendar.getInstance().getTimeInMillis());
              return DtoUtil.returnDataSuccess(tokenVO);
          }
          return DtoUtil.returnFail("登录失败","10000");



    }

    /***
     *
     * @param agent 是浏览器上的agent
     * @param user
     * @return
     */
    public String generateToken(String agent, ItripUser user) {
                        // TODO Auto-generated method stub
                        try {
                            UserAgentInfo userAgentInfo = UserAgentUtil.getUasParser().parse(
                                    agent);
                            StringBuilder sb = new StringBuilder();
                            sb.append("token:");//统一前缀
                            if (userAgentInfo.getDeviceType().equals(UserAgentInfo.UNKNOWN)) {
                                if (UserAgentUtil.CheckAgent(agent)) {
                                    sb.append("MOBILE-");
                } else {
                    sb.append("PC-");
                }
            } else if (userAgentInfo.getDeviceType()
                    .equals("Personal computer")) {
                sb.append("PC-");
            } else
                sb.append("MOBILE-");
//			sb.append(user.getUserCode() + "-");
            sb.append(MD5.getMd5(user.getUserCode(),32) + "-");//加密用户名称
            sb.append(user.getId() + "-");
            sb.append(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
                    + "-");
            sb.append(MD5.getMd5(agent, 6));// 识别客户端的简化实现——6位MD5码

            return sb.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
