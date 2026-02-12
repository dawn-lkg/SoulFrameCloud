package com.clm.modules.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clm.modules.system.domain.entity.DictData;
import com.clm.modules.system.domain.param.DictDataQueryParam;

import java.util.List;

/**
 * 字典数据表 服务层
 *
 * @author 陈黎明
 * @date 2025/3/5
 */
public interface DictDataService extends IService<DictData> {


    /**
     * 查询字典数据
     *
     * @param param 查询参数
     * @return 字典数据列表
     */
    Page<DictData> selectDictDataPage(DictDataQueryParam param);

    /**
     * 根据条件分页查询字典数据
     *
     * @param param 字典数据信息
     * @return 字典数据集合
     */
    List<DictData> selectDictDataList(DictDataQueryParam param);

    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据集合
     */
    List<DictData> selectDictDataByType(String dictType);

    /**
     * 根据字典类型和字典键值查询字典数据信息
     *
     * @param dictType 字典类型
     * @return 字典数据信息
     */
    List<DictData> selectDictDataList(String dictType);

    /**
     * 新增保存字典数据信息
     *
     * @param dictData 字典数据信息
     */
    void addDictData(DictData dictData);

    /**
     * 修改保存字典数据信息
     *
     * @param dictData 字典数据信息
     */
    void updateDictData(DictData dictData);

    /**
     * 批量删除字典数据信息
     *
     * @param dictCodes 需要删除的字典数据ID
     */
    void deleteDictDataByIds(Long[] dictCodes);

    /**
     * 根据字典类型删除字典数据信息
     *
     * @param dictType 字典类型
     */
    void deleteDictDataByType(String dictType);

    /**
     * 修改类型值
     *
     * @param oldDictType 字典类型
     * @param newDictType 新的字典类型
     * @return 操作结果
     */
    boolean updateDictTypeValue(String oldDictType, String newDictType);


    /**
     * 更新字典状态
     */
    boolean updateDictStatus(String dictType, String status);


}