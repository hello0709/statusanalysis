package com.tbea.statusanalysis.controller;

import com.tbea.statusanalysis.service.LogAnalysisService;
import com.tbea.statusanalysis.service.LogAnalysisServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;


/**
 * @author hanchen
 * @create 2019-04-16 17:27
 */
@Controller
@RequestMapping("/mqtt")
public class LogAnalysisController {

    @Autowired
    private LogAnalysisService logAnalysisServiceImp;
    /**
     * 通过id，startTime和endTime抽取在startTime~endTime之间的List,同时计算时间差
     * */
    @RequestMapping(value = "/getDifftimeByIdAndTime", method = RequestMethod.POST)
    @ResponseBody
    public List<Map<String, Object>> getDifftimeByIdAndTime(@RequestBody Map<String, Object> map) throws Exception {
        List<Map<String, Object>> result = new ArrayList<>();
        if(map.containsKey("productId") && !map.get("productId").toString().isEmpty()){
            result.addAll(logAnalysisServiceImp.getDifftimeByIdAndTime(map));
        }
        return result;
    }
/**
 * 通过id，startTime和endTime抽取在startTime~endTime之间的ListContent
 * */
    @RequestMapping(value = "/getContentByIdAndTime", method = RequestMethod.POST)
    @ResponseBody
    public List<Map<String, Object>> getContentByIdAndTime(@RequestBody Map<String, Object> map) throws Exception{
        List<Map<String, Object>> result = new ArrayList<>();
        if(map.containsKey("productId") && !map.get("productId").toString().isEmpty()){
            result.addAll(logAnalysisServiceImp.getContentByIdAndTime(map));
        }
        return result;
    }
    /**
     * 返回target/class/下的所有txt后缀的文件名
     * */
    @RequestMapping(value = "/getTxtFileName", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getAllTxtFileName(){
        return logAnalysisServiceImp.getAllTxtFileName();
    }
}
