package com.cong.wanwu.bi.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cong.wanwu.bi.model.dto.chart.*;
import com.cong.wanwu.bi.bizmq.BiMessageProducer;
import com.cong.wanwu.common.common.BaseResponse;
import com.cong.wanwu.common.common.DeleteRequest;
import com.cong.wanwu.common.common.ErrorCode;
import com.cong.wanwu.bi.constant.CommonConstant;
import com.cong.wanwu.bi.manager.AiManager;
import com.cong.wanwu.bi.manager.RedisLimiterManager;
import com.cong.wanwu.bi.model.entity.Chart;
import com.cong.wanwu.bi.model.enums.UserRoleEnum;
import com.cong.wanwu.bi.model.vo.BiResponse;
import com.cong.wanwu.bi.service.ChartService;
import com.cong.wanwu.bi.utils.ExcelUtils;
import com.cong.wanwu.common.common.ResultUtils;
import com.cong.wanwu.common.constant.SystemConstants;
import com.cong.wanwu.common.exception.BusinessException;
import com.cong.wanwu.common.exception.ThrowUtils;
import com.cong.wanwu.common.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 图表接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/chart")
@Slf4j
public class ChartController {

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Resource
    private ChartService chartService;
    @Resource
    private AiManager aiManager;
    @Resource
    private RedisLimiterManager redisLimiterManager;
    @Resource
    private BiMessageProducer biMessageProducer;


    // region 增删改查

    /**
     * 创建
     *
     * @param chartAddRequest
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addChart(@RequestBody ChartAddRequest chartAddRequest) {
        if (chartAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartAddRequest, chart);

        // 先判断是否已登录
        Object userObj = StpUtil.getTokenSession().get(SystemConstants.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        ThrowUtils.throwIf(currentUser ==null, ErrorCode.NOT_LOGIN_ERROR);
        chart.setUserId(currentUser.getId());

        boolean csvData = chartService.save(chart);
        ThrowUtils.throwIf(!csvData, ErrorCode.OPERATION_ERROR);
        long newChartId = chart.getId();
        return ResultUtils.success(newChartId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteChart(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 先判断是否已登录
        Object userObj = StpUtil.getTokenSession().get(SystemConstants.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        ThrowUtils.throwIf(currentUser ==null, ErrorCode.NOT_LOGIN_ERROR);
        long id = deleteRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldChart.getUserId().equals(currentUser.getId()) && !currentUser.getUserRole().equals(UserRoleEnum.ADMIN.getValue())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = chartService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param chartUpdateRequest
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateChart(@RequestBody ChartUpdateRequest chartUpdateRequest) {
        if (chartUpdateRequest == null || chartUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartUpdateRequest, chart);

        long id = chartUpdateRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        boolean csvData = chartService.updateById(chart);
        return ResultUtils.success(csvData);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Chart> getChartById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = chartService.getById(id);
        if (chart == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(chart);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<Chart>> listChartByPage(@RequestBody ChartQueryRequest chartQueryRequest,
                                                     HttpServletRequest request) {
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> chartPage = chartService.page(new Page<>(current, size),
                chartService.getQueryWrapper(chartQueryRequest));
        return ResultUtils.success(chartPage);
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param chartQueryRequest
     * @return
     */
    @PostMapping("/my/list/page")
    public BaseResponse<Page<Chart>> listMyChartByPage(@RequestBody ChartQueryRequest chartQueryRequest) {
        if (chartQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 先判断是否已登录
        Object userObj = StpUtil.getTokenSession().get(SystemConstants.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        chartQueryRequest.setUserId(currentUser.getId());
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> chartPage = chartService.page(new Page<>(current, size),
                chartService.getQueryWrapper(chartQueryRequest));
        return ResultUtils.success(chartPage);
    }

    // endregion


    /**
     * 编辑（用户）
     *
     * @param chartEditRequest
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editChart(@RequestBody ChartEditRequest chartEditRequest) {
        if (chartEditRequest == null || chartEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartEditRequest, chart);

        // 先判断是否已登录
        Object userObj = StpUtil.getTokenSession().get(SystemConstants.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        long id = chartEditRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldChart.getUserId().equals(currentUser.getId()) && !currentUser.getUserRole().equals(UserRoleEnum.ADMIN.getValue())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean csvData = chartService.updateById(chart);
        return ResultUtils.success(csvData);
    }
    /**
     * 智能分析（同步）
     *
     * @param multipartFile
     * @param genChartByAiRequest
     * @param request
     * @return
     */
    @PostMapping("/gen")
    public BaseResponse<BiResponse> genChartByAi(@RequestPart("file") MultipartFile multipartFile,
                                                 GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {

        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();

        //校验
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
        ThrowUtils.throwIf(StringUtils.isBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");
        // 先判断是否已登录
        Object userObj = StpUtil.getTokenSession().get(SystemConstants.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        //校验文件大小
        long size = multipartFile.getSize();
        final long ONE_MB = 1024 * 1024L;
        ThrowUtils.throwIf(size>ONE_MB, ErrorCode.PARAMS_ERROR, "文件大小超过1M");
        //校验文件后缀
        String originalFilename = multipartFile.getOriginalFilename();
        String suffix = FileUtil.getSuffix(originalFilename);
        final List<String> validFileSuffix = Arrays.asList("png","jpg","svg","webp","jpeg","xlsx");
        //限流判断,每个用户一个限流器
        redisLimiterManager.doRateLimit("genChartByAi_"+ currentUser.getId());
        //预设(无需写，直接调用现有模型) https://www.yucongming.com/
        /*final String prompt = "你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容：\n" +
                "分析需求：\n" +
                "(数据分析的需求或者目标}\n" +
                "原始数据：\n" +
                "(csv格式的原始数据，用,作为分隔符}\n" +
                "请根据这两部分内容，按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）" +
                "【【【【【\n" +
                "{前端Echarts V5的opt1on配置对象js代码，合理地将数据进行可视化，不要生成任何多余的内容，比如注释)\n" +
                "【【【【【\n" +
                "{明确的数据分析结论、越详细越好，不要生成多余的注释)";*/
        long bIModelId = 1659171950288818178L;
        //用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：");

        //拼接分析目标
        String userGoal = goal;
        if(StringUtils.isNotBlank(chartType)){
            userGoal +=",请使用"+chartType;
        }
        userInput.append(userGoal);
        userInput.append("原始数据");
        //压缩后的数据
        String csvData = ExcelUtils.excelToCsv(multipartFile);
        userInput.append(csvData);
//        String response = aiManager.doChat(bIModelId, userInput.toString());
        String response = aiManager.doChat(userInput.toString());
//        String[] split = response.split("【【【【【");
        String[] split = response.split("```json");
//        if (split.length<3){
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"AI生成错误");
//        }
        split = split[1].split("```");

        String genChart = split[0].trim();
        String genResult = "AI处理繁忙";
        if (split.length>=2){
            genResult= split[1].trim();
        }
        //插入到数据库
        Chart chart = new Chart();
        chart.setUserId(currentUser.getId());
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
        chart.setGenChart(genChart);
        chart.setStatus("succeed");
        chart.setGenResult(genResult);
        boolean result = chartService.save(chart);
        ThrowUtils.throwIf(!result,ErrorCode.SYSTEM_ERROR,"图表保存失败");
        //封装返回类
        BiResponse biResponse = new BiResponse();
        biResponse.setGenChart(genChart);
        biResponse.setGenResult(genResult);
        biResponse.setChartId(chart.getId());

        return ResultUtils.success(biResponse);
    }
    /**
     * 智能分析（异步）
     *
     * @param multipartFile
     * @param genChartByAiRequest
     * @param request
     * @return
     */
    @PostMapping("/gen/async")
    public BaseResponse<BiResponse> genChartByAiAsync(@RequestPart("file") MultipartFile multipartFile,
                                                 GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {

        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();

        //校验
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
        ThrowUtils.throwIf(StringUtils.isBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");
        // 先判断是否已登录
        Object userObj = StpUtil.getTokenSession().get(SystemConstants.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        //校验文件大小
        long size = multipartFile.getSize();
        final long ONE_MB = 1024 * 1024L;
        ThrowUtils.throwIf(size>ONE_MB, ErrorCode.PARAMS_ERROR, "文件大小超过1M");
        //校验文件后缀
        String originalFilename = multipartFile.getOriginalFilename();
        String suffix = FileUtil.getSuffix(originalFilename);
        final List<String> validFileSuffix = Arrays.asList("png","jpg","svg","webp","jpeg","xlsx");
        //限流判断,每个用户一个限流器
        redisLimiterManager.doRateLimit("genChartByAi_"+ currentUser.getId());
        //预设(无需写，直接调用现有模型) https://www.yucongming.com/
        /*final String prompt = "你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容：\n" +
                "分析需求：\n" +
                "(数据分析的需求或者目标}\n" +
                "原始数据：\n" +
                "(csv格式的原始数据，用,作为分隔符}\n" +
                "请根据这两部分内容，按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）" +
                "【【【【【\n" +
                "{前端Echarts V5的opt1on配置对象js代码，合理地将数据进行可视化，不要生成任何多余的内容，比如注释)\n" +
                "【【【【【\n" +
                "{明确的数据分析结论、越详细越好，不要生成多余的注释)";*/
        long bIModelId = 1659171950288818178L;
        //用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");

        //拼接分析目标
        String userGoal = goal;
        if(StringUtils.isNotBlank(chartType)){
            userGoal +=",请使用"+chartType;
        }
        userInput.append(userGoal).append("\n");
        userInput.append("原始数据").append("\n");
        //压缩后的数据
        String csvData = ExcelUtils.excelToCsv(multipartFile);
        userInput.append(csvData).append("\n");
        //插入到数据库
        Chart chart = new Chart();
        setChart(name, goal, chartType, currentUser, csvData, chart);
        boolean result = chartService.save(chart);
        ThrowUtils.throwIf(!result,ErrorCode.SYSTEM_ERROR,"图表保存失败");
        CompletableFuture.runAsync(()->{
            //先将任务修改为执行中，防止重复执行
            Chart updateChart = new Chart();
            updateChart.setId(chart.getId());
            updateChart.setStatus("running");
            boolean b = chartService.updateById(updateChart);
            if(!b){
                chartService.handleChartUpdateError(chart.getId(), "更新图表成功状态失败");
                return;
            }
            String response = aiManager.doChat(bIModelId, userInput.toString());
            String[] split = response.split("【【【【【");
            if (split.length<3){
                chartService.handleChartUpdateError(chart.getId(), "AI生成错误");
                return;
            }
            String genChart = split[1].trim();
            String genResult = split[2].trim();
            Chart updateChartResult = new Chart();
            updateChartResult.setId(chart.getId());
            //todo 定义状态枚举
            updateChartResult.setStatus("succeed");
            updateChartResult.setGenChart(genChart);
            updateChartResult.setGenResult(genResult);
            boolean updateResult = chartService.updateById(updateChartResult);
            if(!updateResult){
                chartService.handleChartUpdateError(chart.getId(), "更新图表成功状态失败");
            }

        },threadPoolExecutor);

        //封装返回类
        BiResponse biResponse = new BiResponse();
        biResponse.setChartId(chart.getId());

        return ResultUtils.success(biResponse);
    }

    /**
     * 智能分析（异步消息队列）
     *
     * @param multipartFile
     * @param genChartByAiRequest
     * @param request
     * @return
     */
    @PostMapping("/gen/async/mq")
    public BaseResponse<BiResponse> genChartByAiAsyncMq(@RequestPart("file") MultipartFile multipartFile,
                                                      GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {

        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();

        //校验
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
        ThrowUtils.throwIf(StringUtils.isBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");
        // 先判断是否已登录
        Object userObj = StpUtil.getTokenSession().get(SystemConstants.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        //校验文件大小
        long size = multipartFile.getSize();
        final long ONE_MB = 1024 * 1024L;
        ThrowUtils.throwIf(size>ONE_MB, ErrorCode.PARAMS_ERROR, "文件大小超过1M");
        //校验文件后缀
        String originalFilename = multipartFile.getOriginalFilename();
        String suffix = FileUtil.getSuffix(originalFilename);
        final List<String> validFileSuffix = Arrays.asList("png","jpg","svg","webp","jpeg","xlsx");
        //限流判断,每个用户一个限流器
        redisLimiterManager.doRateLimit("genChartByAi_"+ currentUser.getId());
        //预设(无需写，直接调用现有模型) https://www.yucongming.com/
        /*final String prompt = "你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容：\n" +
                "分析需求：\n" +
                "(数据分析的需求或者目标}\n" +
                "原始数据：\n" +
                "(csv格式的原始数据，用,作为分隔符}\n" +
                "请根据这两部分内容，按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）" +
                "【【【【【\n" +
                "{前端Echarts V5的opt1on配置对象js代码，合理地将数据进行可视化，不要生成任何多余的内容，比如注释)\n" +
                "【【【【【\n" +
                "{明确的数据分析结论、越详细越好，不要生成多余的注释)";*/
        long bIModelId = CommonConstant.BI_MODEL_ID;
        String csvData = ExcelUtils.excelToCsv(multipartFile);
        //插入到数据库
        Chart chart = new Chart();
        setChart(name, goal, chartType, currentUser, csvData, chart);
        boolean result = chartService.save(chart);
        Long newChatId = chart.getId();
        ThrowUtils.throwIf(!result,ErrorCode.SYSTEM_ERROR,"图表保存失败");
        biMessageProducer.sendMessage(String.valueOf(newChatId));

        //封装返回类
        BiResponse biResponse = new BiResponse();
        biResponse.setChartId(chart.getId());

        return ResultUtils.success(biResponse);
    }



    private static void setChart(String name, String goal, String chartType, User loginUser, String csvData,  Chart chart) {
        chart.setUserId(loginUser.getId());
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
    }
}
