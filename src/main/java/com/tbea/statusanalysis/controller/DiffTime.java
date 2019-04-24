package com.tbea.statusanalysis.controller;

import com.tbea.statusanalysis.MqttReceiveConfig.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author hanchen
 * @create 2019-04-16 10:21
 */
public class DiffTime {

    public String diffTime(String st1, String st2) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");

        Date d1 = df.parse(st1);
        Date d2 = df.parse(st2);
        long diff = d2.getTime() - d1.getTime();//这样得到的差值是微秒级别
        long days = diff / (1000 * 60 * 60 * 24);

        long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
        //秒数的加入
        long seconds=(diff-days*(1000*60*60*24)-hours*(1000*60*60)-minutes*(1000*60))/1000;
        String result = "" + days + "天" + hours + "小时" + minutes + "分"+seconds+"秒";
        //System.out.println(""+days+"天"+hours+"小时"+minutes+"分");

        return result;
    }
}
