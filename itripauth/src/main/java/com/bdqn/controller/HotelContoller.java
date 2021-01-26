package com.bdqn.controller;


import cn.itrip.dao.itripHotel.ItripHotelMapper;
import cn.itrip.pojo.ItripHotel;
import com.alibaba.fastjson.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;

@Controller
public class HotelContoller {

    @Resource
    ItripHotelMapper dao;

    @RequestMapping(value="/list",method= RequestMethod.GET,produces="application/json; charset=utf-8")
    @ResponseBody
    public  ItripHotel Resgiter() throws Exception {



        ItripHotel cn=dao.getItripHotelById(new Long(1));



        return cn;
    }
}
