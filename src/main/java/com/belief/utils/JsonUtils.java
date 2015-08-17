package com.belief.utils;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author bes
 * 
 */
public class JsonUtils {
    private static Logger log = LoggerFactory.getLogger(JsonUtils.class);

    /**
     * JSON转换为List对象
     * 
     * @throws IOException
     * @throws JsonMappingException
     */
    public static <T> T json2Object(String json, Class<T> clazz) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        // 反序列化字段，避免新增字段后无法解析
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        T obj = null;
        try {
            obj = (T) objectMapper.readValue(json, clazz);
        } catch (JsonParseException e) {
            log.error(e.getMessage(), e);
        } catch (JsonMappingException e) {
            log.error(e.getMessage(), e);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return obj;
    }

    /**
     * list 转换成json
     * 
     * @param
     * @return
     * @throws IOException
     */
    public static String object2Json(Object object) throws IOException {
        if (object == null) {
            return "";
        }
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

}
