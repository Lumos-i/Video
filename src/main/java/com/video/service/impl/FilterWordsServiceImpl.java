package com.video.service.impl;

import com.video.service.FilterWordsService;
import com.video.utils.BaseResponse;
import com.video.utils.DFAUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
@Slf4j
public class FilterWordsServiceImpl implements FilterWordsService {
    @Resource
    private DFAUtil dfaUtil;

    @Override
    public BaseResponse addFilterWords(String[] strings) {
        try {
            Set<String> set = new HashSet<>();
            String path= System.getProperty("user.dir") + "\\word.txt";
            BufferedWriter bw= new BufferedWriter
                    (new OutputStreamWriter(new FileOutputStream(path, true), StandardCharsets.UTF_8));
            for (String s : strings) {
                HashMap<Boolean, String> map = dfaUtil.getSensitiveWordByDFAMap(s, 2);
                if (map.containsKey(false)) {
                    bw.write(s);
                    bw.newLine();
                    set.add(s);
                }
            }
            bw.flush();
            bw.close();
            dfaUtil.createDFAHashMap(set);
            return BaseResponse.success("添加成功");
        }catch (Exception e){
            log.info("添加敏感词出现错误");
            return BaseResponse.error("添加失败");
        }
    }
}
