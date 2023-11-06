package com.cong.wanwu.bi.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cong.wanwu.bi.model.dto.disease.*;
import com.cong.wanwu.bi.model.enums.UserRoleEnum;
import com.cong.wanwu.common.common.BaseResponse;
import com.cong.wanwu.common.common.DeleteRequest;
import com.cong.wanwu.common.common.ErrorCode;
import com.cong.wanwu.common.common.ResultUtils;
import com.cong.wanwu.common.constant.SystemConstants;
import com.cong.wanwu.common.exception.BusinessException;
import com.cong.wanwu.common.exception.ThrowUtils;
import com.cong.wanwu.common.model.entity.User;
import com.google.gson.Gson;
import com.cong.wanwu.bi.manager.AiManager;
import com.cong.wanwu.bi.manager.RedisLimiterManager;
import com.cong.wanwu.bi.model.entity.Disease;
import com.cong.wanwu.bi.model.vo.BiDiseaseResponse;
import com.cong.wanwu.bi.service.DiseaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 图表接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/disease")
@Slf4j
public class DiseaseController {

    @Resource
    private DiseaseService diseaseService;

    @Resource
    private AiManager aiManager;
    @Resource
    private RedisLimiterManager redisLimiterManager;

    private final static Gson GSON = new Gson();

    // region 增删改查

    /**
     * 创建
     *
     * @param diseaseAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addDisease(@RequestBody DiseaseAddRequest diseaseAddRequest, HttpServletRequest request) {
        if (diseaseAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Disease disease = new Disease();
        BeanUtils.copyProperties(diseaseAddRequest, disease);

        // 先判断是否已登录
        Object userObj = StpUtil.getTokenSession().get(SystemConstants.USER_LOGIN_STATE);
        User loginUser = (User) userObj;
        disease.setUserId(loginUser.getId());

        boolean csvData = diseaseService.save(disease);
        ThrowUtils.throwIf(!csvData, ErrorCode.OPERATION_ERROR);
        long newDiseaseId = disease.getId();
        return ResultUtils.success(newDiseaseId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteDisease(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 先判断是否已登录
        Object userObj = StpUtil.getTokenSession().get(SystemConstants.USER_LOGIN_STATE);
        User loginUser = (User) userObj;
        long id = deleteRequest.getId();
        // 判断是否存在
        Disease oldDisease = diseaseService.getById(id);
        ThrowUtils.throwIf(oldDisease == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldDisease.getUserId().equals(loginUser.getId()) && !loginUser.getUserRole().equals(UserRoleEnum.ADMIN.getValue())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = diseaseService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param diseaseUpdateRequest
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateDisease(@RequestBody DiseaseUpdateRequest diseaseUpdateRequest) {
        if (diseaseUpdateRequest == null || diseaseUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Disease disease = new Disease();
        BeanUtils.copyProperties(diseaseUpdateRequest, disease);

        long id = diseaseUpdateRequest.getId();
        // 判断是否存在
        Disease oldDisease = diseaseService.getById(id);
        ThrowUtils.throwIf(oldDisease == null, ErrorCode.NOT_FOUND_ERROR);
        boolean csvData = diseaseService.updateById(disease);
        return ResultUtils.success(csvData);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Disease> getDiseaseById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Disease disease = diseaseService.getById(id);
        if (disease == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(disease);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param diseaseQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<Disease>> listDiseaseByPage(@RequestBody DiseaseQueryRequest diseaseQueryRequest,
                                                         HttpServletRequest request) {
        long current = diseaseQueryRequest.getCurrent();
        long size = diseaseQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Disease> diseasePage = diseaseService.page(new Page<>(current, size),
                diseaseService.getQueryWrapper(diseaseQueryRequest));
        return ResultUtils.success(diseasePage);
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param diseaseQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page")
    public BaseResponse<Page<Disease>> listMyDiseaseByPage(@RequestBody DiseaseQueryRequest diseaseQueryRequest,
                                                           HttpServletRequest request) {
        if (diseaseQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 先判断是否已登录
        Object userObj = StpUtil.getTokenSession().get(SystemConstants.USER_LOGIN_STATE);
        User loginUser = (User) userObj;
        diseaseQueryRequest.setUserId(loginUser.getId());
        long current = diseaseQueryRequest.getCurrent();
        long size = diseaseQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Disease> diseasePage = diseaseService.page(new Page<>(current, size),
                diseaseService.getQueryWrapper(diseaseQueryRequest));
        return ResultUtils.success(diseasePage);
    }

    // endregion


    /**
     * 编辑（用户）
     *
     * @param diseaseEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editDisease(@RequestBody DiseaseEditRequest diseaseEditRequest, HttpServletRequest request) {
        if (diseaseEditRequest == null || diseaseEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Disease disease = new Disease();
        BeanUtils.copyProperties(diseaseEditRequest, disease);

        // 先判断是否已登录
        Object userObj = StpUtil.getTokenSession().get(SystemConstants.USER_LOGIN_STATE);
        User loginUser = (User) userObj;
        long id = diseaseEditRequest.getId();
        // 判断是否存在
        Disease oldDisease = diseaseService.getById(id);
        ThrowUtils.throwIf(oldDisease == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldDisease.getUserId().equals(loginUser.getId()) && !loginUser.getUserRole().equals(UserRoleEnum.ADMIN.getValue())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean csvData = diseaseService.updateById(disease);
        return ResultUtils.success(csvData);
    }

    /**
     * 智能分析病症
     * @param genDiseaseByAiRequest
     * @param request
     * @return
     */
    @PostMapping("/gen")
    public BaseResponse<BiDiseaseResponse> genDiseaseByAi(
            GenDiseaseByAiRequest genDiseaseByAiRequest, HttpServletRequest request) {
        //用户性别
        Integer sex = genDiseaseByAiRequest.getSex();
        //用户描述
        String userDesc = genDiseaseByAiRequest.getUserDesc();
        //用户所挂科
        String diseaseType = genDiseaseByAiRequest.getDiseaseType();

        //校验
        ThrowUtils.throwIf(StringUtils.isBlank(userDesc), ErrorCode.PARAMS_ERROR, "描述为空");
        // 先判断是否已登录
        Object userObj = StpUtil.getTokenSession().get(SystemConstants.USER_LOGIN_STATE);
        User loginUser = (User) userObj;

        //限流判断,每个用户一个限流器
        redisLimiterManager.doRateLimit("genDiseaseByAi_" + loginUser.getId());
        //预设(无需写，直接调用现有模型) https://www.yucongming.com/
        /*final String prompt = "你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容：\n" +
                "分析需求：\n" +
                "(数据分析的需求或者目标}\n" +
                "原始数据：\n" +
                "(csv格式的原始数据，用,作为分隔符}\n" +
                "请根据这两部分内容，按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）" +
                "【【【【【\n" +
                "{前端Ediseases V5的opt1on配置对象js代码，合理地将数据进行可视化，不要生成任何多余的内容，比如注释)\n" +
                "【【【【【\n" +
                "{明确的数据分析结论、越详细越好，不要生成多余的注释)";*/

        //医生Id
        long bIModelId = 1651470811573370881L;
        //用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("用户性别：").append("\n");

        //拼接用户性别
        userInput.append(sex==1?"男":"女").append("\n");
        userInput.append("挂科室为：").append("\n");
        userInput.append(userDesc).append("\n");
        String response = aiManager.doChat(bIModelId, userInput.toString());
        String[] split = response.split("\n\n");
        if (split.length<2){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"AI生成错误");
        }
        //病症名称
        String name = split[0].split("：")[1].trim();
        //病症解决方案
        String genResult = split[1].split("：")[1].trim();
        //插入到数据库
        Disease disease = new Disease();
        disease.setUserId(loginUser.getId());
        disease.setName(name);
        disease.setSex(sex);
        disease.setDiseaseType(diseaseType);
        disease.setUserDesc(userDesc);
        disease.setGenResult(genResult);
        diseaseService.save(disease);
        BiDiseaseResponse biResponse = new BiDiseaseResponse();
        biResponse.setName(name);
        biResponse.setGenResult(genResult);
        return ResultUtils.success(biResponse);
    }
}
