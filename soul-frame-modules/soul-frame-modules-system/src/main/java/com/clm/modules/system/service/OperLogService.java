package com.clm.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clm.modules.system.domain.entity.OperLog;
import com.clm.modules.system.domain.param.OperLogQueryParam;
import com.clm.modules.system.domain.vo.OperLogVO;
import com.clm.modules.system.domain.vo.VisitingStatisticVO;

import java.util.List;

/**
 * 操作日志记录(OperLog)表服务接口
 *
 * @author 陈黎明
 * @since 2025-03-08
 */
public interface OperLogService extends IService<OperLog> {
    
    /**
     * 分页查询操作日志
     *
     * @param param 查询参数
     * @return 分页结果
     */
    IPage<OperLogVO> pageOperLog(OperLogQueryParam param);

    /**
     * 列表关联查询
     *
     * @param param 查询参数
     * @return 结果
     */
    List<OperLogVO> listRel(OperLogQueryParam param);

    /**
     * 新增操作日志
     *
     * @param operLog 操作日志信息
     * @return 结果
     */
    boolean insertOperLog(OperLog operLog);

    /**
     * 新增操作日志（异步）
     */
    void insertOperLogAsync(OperLog operLog);
    
    /**
     * 查询操作日志详细
     *
     * @param operId 操作ID
     * @return 操作日志对象
     */
    OperLog getOperLogById(Long operId);
    
    /**
     * 清空操作日志
     *
     * @return 结果
     */
    boolean cleanOperLog();
    
    /**
     * 导出操作日志
     * @param param 查询条件
     */
    void exportOperLog(OperLogQueryParam param);

    /**
     * 获取访问统计
     *
     * @return 访问统计结果
     */
    VisitingStatisticVO getVisitingStatistic();
}