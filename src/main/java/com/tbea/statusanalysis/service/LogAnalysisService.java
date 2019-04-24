package com.tbea.statusanalysis.service;

import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * @author hanchen
 * @create 2019-04-19 10:35
 */
public interface LogAnalysisService {

   List<Map<String, Object>> getDifftimeByIdAndTime(Map<String, Object> map) throws Exception;

   List<Map<String, Object>> getContentByIdAndTime(Map<String, Object> map) throws Exception;

   List<String> getAllTxtFileName();
}
