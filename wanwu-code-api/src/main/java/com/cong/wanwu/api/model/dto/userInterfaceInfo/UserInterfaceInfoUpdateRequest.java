package com.cong.wanwu.api.model.dto.userInterfaceInfo;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新请求
 *
 * @author lhcong
 * @TableName InterfaceInfo
 */
@Data
public class UserInterfaceInfoUpdateRequest implements Serializable {

    /**
     * 主键
     */
    private Long id;


    /**
     * 总调用次数
     */
    private Integer totalNum;

    /**
     * 剩余调用次数
     */
    private Integer leftNum;

    /**
     * 接口状态（0-正常  1-禁用）
     */
    private Integer status;

}