package com.tbea.statusanalysis.service;

import com.tbea.statusanalysis.controller.DiffTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author hanchen
 * @create 2019-04-19 10:37
 */
@Service
public class LogAnalysisServiceImp implements LogAnalysisService {

    @Value("${spring.mqttLog.path}")
    private String path;

    @Override
    public List<Map<String, Object>> getDifftimeByIdAndTime(Map<String, Object> map) {
        List<Map<String, Object>> result = new ArrayList<>();

        FileReader fileReader = null;
        BufferedReader br = null;
        try {
            fileReader = new FileReader(path + map.get("productId").toString() + ".txt");
            br = new BufferedReader(fileReader);
            String currentLine = null;
            String tempString = null;
            String[] tempStringList = null;
            String[] curStringList = null;

            DiffTime di = new DiffTime();
            Map<String, Object> itemMap = null;

            String startTime = map.get("startTime").toString();
            String endTime = map.get("endTime").toString();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
            while ((currentLine = br.readLine()) != null) {
                if (null == tempString) {
                    tempString = currentLine;
                    continue;
                }
                curStringList = currentLine.split("\\s");
                tempStringList = tempString.split("\\s");

                itemMap = new HashMap<>();

                Date compareTime = sdf2.parse(curStringList[1] + curStringList[2]);

                if (startTime != "") {
                    if (!(compareTime).after(sdf.parse(startTime))) {
                        continue;
                    }
                }
                if (endTime != "") {
                    if (!(compareTime).before(sdf.parse(endTime))) {
                        continue;
                    }
                }

                if (!tempStringList[3].equals(curStringList[3])) {

                    itemMap.put("difftime", di.diffTime(tempStringList[1] + tempStringList[2], curStringList[1] + curStringList[2]));
                    itemMap.put("productId", curStringList[0]);
                    itemMap.put("time", curStringList[1] + " " + curStringList[2]);
                    itemMap.put("status", tempStringList[3] + "-" + curStringList[3]);
                    result.add(itemMap);
                }
                tempString = currentLine;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != br) {
                    br.close();
                }
                if (null != fileReader) {
                    fileReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getContentByIdAndTime(Map<String, Object> map) {

        /**
         * 1.遍历取得所有相应Id的文本每一行;
         * 2.切分出时间YY-MM-DDHH：mm：ss,转化为Date类型，记作compareTime[];
         * 3.对比前台发送的startTime，endTime，同时转化为Date类型，对比compareTime，if（startTime<compareTime[i]）
         * if(endTime>comparetime[j])，记录当前index([i],[j])，遍历for(i~j)，上传
         */
        List<Map<String, Object>> result = new ArrayList<>();

        FileReader fileReader = null;
        BufferedReader br = null;
        try {
            fileReader = new FileReader(path + map.get("productId").toString() + ".txt");

            br = new BufferedReader(fileReader);


            String currentLine = null;

            String[] curStringList = null;

            Map<String, Object> itemMap = null;

            String startTime = map.get("startTime").toString();
            String endTime = map.get("endTime").toString();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
            while ((currentLine = br.readLine()) != null) {

                curStringList = currentLine.split("\\s");

                itemMap = new HashMap<>();

                Date compareTime = sdf2.parse(curStringList[1] + curStringList[2]);
                if (startTime != "") {
                    if (!(compareTime).after(sdf.parse(startTime))) {
                        continue;
                    }
                }
                if (endTime != "") {
                    if (!(compareTime).before(sdf.parse(endTime))) {
                        continue;
                    }
                }
                itemMap.put("productId", curStringList[0]);
                itemMap.put("time", curStringList[1] + " " + curStringList[2]);
                itemMap.put("status", curStringList[3]);
                result.add(itemMap);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != br) {
                    br.close();
                }
                if (null != fileReader) {
                    fileReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public List<String> getAllTxtFileName() {
        List<String> result = new ArrayList<>();
        File file = new File(path);
        String[] fileName = file.list();
        String pattern = ".*txt.*";

        for (int i = 0; i < fileName.length; i++) {

            int length = fileName[i].length();

            if (Pattern.matches(pattern, fileName[i])) {
                result.add(fileName[i].substring(0, length - 4));
            }
        }
        return result;
    }
/**
 *
 * getInfoList
* */
    public List<Map<String, Object>> getInfoList(Map<String, Object> parameters,boolean calcDiff){
        List<Map<String, Object>> result = new ArrayList<>();

        FileReader fileReader = null;
        BufferedReader br = null;
        try {
            fileReader = new FileReader(path + parameters.get("productId").toString() + ".txt");
            br = new BufferedReader(fileReader);
            String currentLine = null;
            String tempString = null;
            String[] tempStringList = null;
            String[] curStringList = null;

            DiffTime di = new DiffTime();
            Map<String, Object> itemMap = null;

            String startTime = parameters.get("startTime").toString();
            String endTime = parameters.get("endTime").toString();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
            while ((currentLine = br.readLine()) != null) {
                if (null == tempString) {
                    tempString = currentLine;
                    continue;
                }
                curStringList = currentLine.split("\\s");
                tempStringList = tempString.split("\\s");

                itemMap = new HashMap<>();

                Date compareTime = sdf2.parse(curStringList[1] + curStringList[2]);

                if (startTime != "") {
                    if (!(compareTime).after(sdf.parse(startTime))) {
                        continue;
                    }
                }
                if (endTime != "") {
                    if (!(compareTime).before(sdf.parse(endTime))) {
                        continue;
                    }
                }
                if (calcDiff && !tempStringList[3].equals(curStringList[3])) {

                    itemMap.put("difftime", di.diffTime(tempStringList[1] + tempStringList[2], curStringList[1] + curStringList[2]));
                    itemMap.put("productId", tempStringList[0]);
                    itemMap.put("time", tempStringList[1] + " " + tempStringList[2]);
                    itemMap.put("status", tempStringList[3] + "-" + curStringList[3]);
                    result.add(itemMap);
                }else{
                    itemMap.put("productId", tempStringList[0]);
                    itemMap.put("time", tempStringList[1] + " " + tempStringList[2]);
                    itemMap.put("status", tempStringList[3] + "-" + curStringList[3]);
                    result.add(itemMap);
                }
                tempString = currentLine;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != br) {
                    br.close();
                }
                if (null != fileReader) {
                    fileReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
