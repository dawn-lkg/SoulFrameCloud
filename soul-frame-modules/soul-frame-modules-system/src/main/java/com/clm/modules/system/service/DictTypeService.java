package com.clm.modules.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clm.modules.system.domain.entity.DictType;
import com.clm.modules.system.domain.param.DictTypeQueryParam;

import java.util.List;

/**
 * 字典类型表 服务层
 *
 * @author 陈黎明
 * @date 2025/3/5
 */
public interface DictTypeService extends IService<DictType> {

    /**
     * 查询字典类型表列表
     *
     * @param param 字典类型表查询参数
     * @return 字典类型表列表
     */
    Page<DictType> selectDictTypePage(DictTypeQueryParam param);

    /**
     * 根据条件分页查询字典类型
     *
     * @param param 字典类型信息
     * @return 字典类型集合
     */
    List<DictType> selectDictTypeList(DictTypeQueryParam param);

    /**
     * 批量删除字典类型信息
     *
     * @param idList 需要删除的字典ID
     */
    void deleteDictTypeByIds(List<Long> idList);

    /**
     * 重置字典缓存数据
     */
    void resetDictCache();

    /**
     * 新增保存字典类型信息
     *
     * @param dictType 字典类型信息
     */
    void addDictType(DictType dictType);

    /**
     * 修改保存字典类型信息
     *
     * @param dictType 字典类型信息
     */
    void updateDictType(DictType dictType);

    /**
     * 校验字典类型称是否唯一
     *
     * @param dictType 字典类型
     * @return 结果
     */
    boolean checkDictTypeUnique(DictType dictType);
} 