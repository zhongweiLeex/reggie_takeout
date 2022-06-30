package com.itheima.reggie.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itheima.reggie.dto.DishDto;

import java.io.IOException;
import java.util.List;

/**
 * description 实现不同数据与JSON数据的相互转换
 *
 * @author Administrator
 * @date 2022/6/29-22:04
 */
public class JsonAnalysis {
    static ObjectMapper objectMapper = new ObjectMapper();
    public static List<DishDto> analysis(String jsonStr)
            throws IOException {
        return objectMapper.readValue(jsonStr, new TypeReference<List<DishDto>>() {});
    }

}
