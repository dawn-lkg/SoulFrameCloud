package com.clm.modules.system.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clm.common.core.enums.HttpCodeEnum;
import com.clm.common.core.exception.BaseException;
import com.clm.common.core.utils.ServletUtils;
import com.clm.common.core.utils.UserAgentUtils;
import com.clm.common.log.enums.BusinessType;
import com.clm.common.log.enums.OperatorType;
import com.clm.modules.system.domain.entity.OperLog;
import com.clm.modules.system.domain.param.OperLogQueryParam;
import com.clm.modules.system.domain.param.VisitingStatisticParam;
import com.clm.modules.system.domain.vo.OperLogExportVO;
import com.clm.modules.system.domain.vo.OperLogVO;
import com.clm.modules.system.domain.vo.VisitingRangeDataVO;
import com.clm.modules.system.domain.vo.VisitingStatisticVO;
import com.clm.modules.system.mapper.OperLogMapper;
import com.clm.modules.system.service.OperLogService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 操作日志记录(OperLog)表服务实现类
 *
 * @author 陈黎明
 * @since 2025-03-08
 */
@Service("operLogService")
@RequiredArgsConstructor
public class OperLogServiceImpl extends ServiceImpl<OperLogMapper, OperLog> implements OperLogService {
    
    @Override
    public IPage<OperLogVO> pageOperLog(OperLogQueryParam param) {
        Page<OperLogVO> page = new Page<>(param.getPageNum(), param.getPageSize());
        IPage<OperLogVO> pageResult = baseMapper.selectOperLogPage(page, param);
        
        // 填充枚举描述
        for (OperLogVO vo : pageResult.getRecords()) {
            processOperLogVO(vo);
        }
        
        return pageResult;
    }

    @Override
    public List<OperLogVO> listRel(OperLogQueryParam param){
        return baseMapper.selectListRel(param);
    }

    @Override
    public boolean insertOperLog(OperLog operLog) {
        return save(operLog);
    }

    @Async
    @Override
    public void insertOperLogAsync(OperLog operLog) {
        save(operLog);
    }
    
    @Override
    public OperLog getOperLogById(Long operId) {
        OperLog operLog = getById(operId);
        if (operLog == null) {
            throw new BaseException("操作日志不存在", HttpCodeEnum.DATA_NOT_EXIST.getCode());
        }
        
        return operLog;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cleanOperLog() {
        baseMapper.cleanOperLog();
        return true;
    }
    
    @Override
    public void exportOperLog(OperLogQueryParam param) {
        List<OperLogVO> list = baseMapper.selectListRel(param);
        List<OperLogExportVO> exportVOList = list.stream().map(vo -> {
            OperLogExportVO operLogExportVO = new OperLogExportVO();
            processOperLogVO(vo);
            BeanUtils.copyProperties(vo, operLogExportVO);
            return operLogExportVO;
        }).toList();

        // 获取返回对象
        HttpServletResponse response = ServletUtils.getResponse();

        if(response == null){
            throw new BaseException("返回对象为空", HttpCodeEnum.ERROR.getCode());
        }

        response.setCharacterEncoding("utf-8");
        String fileName = "操作日志_" + System.currentTimeMillis() + ".xlsx";
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        try(OutputStream os = response.getOutputStream()){
            EasyExcel.write(os).sheet("操作日志").head(OperLogExportVO.class).doWrite(exportVOList);
        } catch (Exception e){
            throw new BaseException("导出操作日志失败", HttpCodeEnum.ERROR.getCode());
        }

    }
    
    /**
     * 创建查询条件
     */
    private LambdaQueryWrapper<OperLog> createQueryWrapper(OperLogQueryParam param) {
        LambdaQueryWrapper<OperLog> queryWrapper = new LambdaQueryWrapper<>();
        
        queryWrapper.like(param.getTitle() != null, OperLog::getTitle, param.getTitle())
                .like(param.getOperName() != null, OperLog::getOperName, param.getOperName())
                .eq(param.getBusinessType() != null, OperLog::getBusinessType, param.getBusinessType())
                .eq(param.getStatus() != null, OperLog::getStatus, param.getStatus());
        
        if (param.getBeginTime() != null && param.getEndTime() != null) {
            queryWrapper.between(OperLog::getOperTime, param.getBeginTime(), param.getEndTime());
        } else if (param.getBeginTime() != null) {
            queryWrapper.ge(OperLog::getOperTime, param.getBeginTime());
        } else if (param.getEndTime() != null) {
            queryWrapper.le(OperLog::getOperTime, param.getEndTime());
        }
        
        queryWrapper.orderByDesc(OperLog::getOperTime);
        
        return queryWrapper;
    }
    
    /**
     * 处理操作日志VO，填充枚举描述
     */
    private void processOperLogVO(OperLogVO vo) {
        // 填充业务类型描述
        if (vo.getBusinessType() != null) {
            for (BusinessType type : BusinessType.values()) {
                if (type.getCode().equals(vo.getBusinessType())) {
                    vo.setBusinessTypeDesc(type.name());
                    break;
                }
            }
        }
        
        // 填充操作类型描述
        if (vo.getOperatorType() != null) {
            for (OperatorType type : OperatorType.values()) {
                if (type.getCode().equals(vo.getOperatorType())) {
                    vo.setOperatorTypeDesc(type.name());
                    break;
                }
            }
        }
        
        // 填充操作状态描述
        if (vo.getStatus() != null) {
            vo.setStatusDesc(vo.getStatus() == 0 ? "成功" : "失败");
        }
        
        // 填充耗时描述
        if (vo.getCostTime() != null) {
            vo.setCostTimeDesc(UserAgentUtils.formatCostTime(vo.getCostTime()));
        }
    }

    @Override
    public VisitingStatisticVO getVisitingStatistic() {
        VisitingStatisticVO visitingStatisticVO = new VisitingStatisticVO();
        // 今日访问
        CompletableFuture<Long> todayCountFuture = CompletableFuture.supplyAsync(() -> baseMapper.getVisitingCount(new VisitingStatisticParam(1)));
        //昨日访问
        CompletableFuture<Long> yesterdayCountFuture = CompletableFuture.supplyAsync(() -> baseMapper.getVisitingCount(new VisitingStatisticParam(2)));
        // 本周访问
        CompletableFuture<Long> thisWeekCountFuture = CompletableFuture.supplyAsync(() -> baseMapper.getVisitingCount(new VisitingStatisticParam(3)));
        // 上周访问
        CompletableFuture<Long> lastWeekCountFuture = CompletableFuture.supplyAsync(() -> baseMapper.getVisitingCount(new VisitingStatisticParam(4)));
        // 本月访问
        CompletableFuture<Long> thisMonthCountFuture = CompletableFuture.supplyAsync(() -> baseMapper.getVisitingCount(new VisitingStatisticParam(5)));
        // 上月访问
        CompletableFuture<Long> lastMonthCountFuture = CompletableFuture.supplyAsync(() -> baseMapper.getVisitingCount(new VisitingStatisticParam(6)));
        // 访问图表
        CompletableFuture<List<VisitingRangeDataVO>> rangeDataFuture = CompletableFuture.supplyAsync(() -> {
            String endTime = DateUtil.date().offset(DateField.HOUR, 2).toString("yyyy-MM-dd HH");
            String startTime = DateUtil.date().offset(DateField.HOUR, -23).toString("yyyy-MM-dd HH");
            return baseMapper.getVisitingCountRange(new VisitingStatisticParam(startTime, endTime));
        });
        // 所有访问
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(todayCountFuture, yesterdayCountFuture, thisWeekCountFuture, lastWeekCountFuture, thisMonthCountFuture, lastMonthCountFuture);
        voidCompletableFuture.join();
        try {
            Long todayCount = todayCountFuture.get();
            Long yesterdayCount = yesterdayCountFuture.get();
            Long thisWeekCount = thisWeekCountFuture.get();
            Long lastWeekCount = lastWeekCountFuture.get();
            Long thisMonthCount = thisMonthCountFuture.get();
            Long lastMonthCount = lastMonthCountFuture.get();
            visitingStatisticVO.setList(rangeDataFuture.get());
            visitingStatisticVO.setVisitsTodayCount(Math.toIntExact(todayCount));
            visitingStatisticVO.setVisitsYesterdayCount(Math.toIntExact(yesterdayCount));
            visitingStatisticVO.setVisitsThisWeekCount(Math.toIntExact(thisWeekCount));
            visitingStatisticVO.setVisitsLastWeekCount(Math.toIntExact(lastWeekCount));
            visitingStatisticVO.setVisitsThisMonthCount(Math.toIntExact(thisMonthCount));
            visitingStatisticVO.setVisitsLastMonthCount(Math.toIntExact(lastMonthCount));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            log.error("获取访问统计数据失败");
        }
        return visitingStatisticVO;
    }
} 