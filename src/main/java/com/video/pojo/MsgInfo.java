package com.video.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Calendar;

/**
 * @author zrq
 * @ClassName MsgInfo
 * @date 2022/10/4 9:56
 * @Description TODO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MsgInfo implements Serializable {
    private String creator;
    private String msgBody;
    private Calendar time;
}
