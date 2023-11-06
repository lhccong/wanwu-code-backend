package com.cong.wanwu.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cong.wanwu.api.common.model.entity.UserInterfaceInfo;
import com.cong.wanwu.api.model.vo.InterfaceInfoVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 86188
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Mapper
* @createDate 2022-11-21 21:34:18
* @Entity com.cong.wanwu.api.model.entity.UserInterfaceInfo
*/
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {

    /**
     * 列表调用接口信息
     *
     * @param limit 限制
     * @return {@link List}<{@link InterfaceInfoVO}>
     */
    List<InterfaceInfoVO> listTopInvokeInterfaceInfo(@Param("limit") int limit);
}




