package com.belief.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class FileTest {
    @Test
    public void test() throws IOException {
        String basePath = System.getProperty("user.dir");

        File file = new File(basePath + "/logback.txt");
        File log = new File(basePath + "/log.txt");
        String result = FileUtils.readFileToString(file);
        System.out.println(result);
        String[] array = result.split(",");
        List<String> lines = new ArrayList<String>();
        Map<String, String> map = new HashMap<String, String>();
        for (String str : array) {
            int idx = str.indexOf(":");
            String key = str.substring(0, idx);
            String value = str.substring(++idx);
            map.put(key, value);
            lines.add(value);
        }

        String content = map.get("\"html\"");
        content = content.substring(1, content.length() - 1);
        lines.add(content);

        content = content.replace("\\r\\n", "");
        lines.add(content);
        content = content.replace("\\t", "");
        lines.add(content);
        content = content.replace("\\", "");
        lines.add(content);
        FileUtils.writeLines(log, lines);



    }


}
