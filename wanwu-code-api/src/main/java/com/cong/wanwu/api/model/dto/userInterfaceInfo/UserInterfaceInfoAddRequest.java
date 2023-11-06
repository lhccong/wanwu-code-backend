package com.cong.wanwu.api.model.dto.userInterfaceInfo;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建请求

 * @author lhcong
 * @TableName InterfaceInfo
 */
@Data
public class UserInterfaceInfoAddRequest implements Serializable {



    /**
     * 调用用户
     */
    private Long userId;

    /**
     * 接口id
     */
    private Long interfaceInfoId;

    /**
     * 总调用次数
     */
    private Integer totalNum;

    /**
     * 剩余调用次数
     */
    private Integer leftNum;

}