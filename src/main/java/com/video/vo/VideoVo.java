package com.video.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author zrq
 * @ClassName VideoVo
 * @date 2022/11/3 22:05
 * @Description TODO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoVo {
    private Integer videoId;
    private List<String> collections;
    private MultipartFile[] files;
    private List<String> videoTypes;
    private List<String> sidelightName;
    private List<Integer> vips;
}
