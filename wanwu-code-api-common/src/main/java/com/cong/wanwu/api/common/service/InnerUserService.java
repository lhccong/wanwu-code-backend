package com.cong.wanwu.api.common.service;

import com.cong.wanwu.common.model.entity.User;


/**
 * 用户服务
 *
 * @author cong
 */
public interface InnerUserService  {

    /**
     * 数据库中查是否已分配给用户秘钥(accessKey)
     * @param accessKey
     * @return
     */
    User getInvokerUser(String accessKey);

}
