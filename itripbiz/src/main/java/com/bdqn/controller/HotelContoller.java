package com.bdqn.controller;

import cn.itrip.common.Dto;
import cn.itrip.common.DtoUtil;
import cn.itrip.dao.itripAreaDic.ItripAreaDicMapper;
import cn.itrip.dao.itripLabelDic.ItripLabelDicMapper;
import cn.itrip.pojo.ItripAreaDic;
import cn.itrip.pojo.ItripLabelDic;
import cn.itrip.pojo.ItripUserVO;
import com.sun.org.apache.xalan.internal.xsltc.trax.DOM2TO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class HotelContoller {

    @Resource
    ItripAreaDicMapper DAO;

    @Resource
    ItripLabelDicMapper da1;

    @RequestMapping(value="/api/hotel/queryhotcity/{id}",method= RequestMethod.GET,produces="application/json; charset=utf-8")
    public @ResponseBody Dto re(@PathVariable("id")int id) throws Exception {
        List<ItripAreaDic> list=DAO.ishot(id);
        return DtoUtil.returnDataSuccess(list);
    }
    @RequestMapping(value="api/hotel/queryhotelfeature",method= RequestMethod.GET,produces="application/json; charset=utf-8")
    public @ResponseBody Dto re1() throws Exception {
        List<ItripLabelDic> list=da1.list();
        return DtoUtil.returnDataSuccess(list);
    }

}
