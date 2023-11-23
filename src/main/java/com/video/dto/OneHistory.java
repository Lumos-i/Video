package com.video.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

/**
 * @author zrq
 * @ClassName OneHistory
 * @date 2022/10/4 16:35
 * @Description 取出某一历史记录
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OneHistory implements Serializable {
    private Integer id;
    private String progress;
    private String address;
    private String collection;
    private String sidelightsName;
}
