package com.clm.modules.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clm.modules.system.domain.dto.ConfigDTO;
import com.clm.modules.system.domain.entity.Config;
import com.clm.modules.system.domain.param.ConfigParam;
import com.clm.modules.system.domain.vo.ConfigVO;

import java.util.List;

/**
 * 系统配置表 服务层
 *
 * @author 陈黎明
 * @date 2025-07-12 19:37:02
 */
public interface ConfigService extends IService<Config> {

    /**
     * 分页查询系统配置表列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    Page<ConfigVO> selectConfigPage(ConfigParam param);
    
    /**
     * 查询系统配置表列表
     *
     * @param param 查询参数
     * @return 系统配置表集合
     */
    List<ConfigVO> selectConfigList(ConfigParam param);

    /**
     * 根据ID查询信息
     *
     * @param id id
     * @return 系统配置表
     */
    ConfigVO getByIdRel(Long id);

    /**
     * 根据key查询信息
     *
     * @param key key
     * @return 系统配置表
     */
    ConfigVO getByKey(String key);


    <T> T getValueByKey(String key, Class<T> clazz);



    /**
     * 新增保存系统配置表信息
     *
     * @param dto 系统配置表信息
     */
    void saveConfig(ConfigDTO dto);

    /**
     * 修改保存系统配置表信息
     *
     * @param dto 系统配置表信息
     */
    void updateConfig(ConfigDTO dto);
    
    /**
     * 删除系统配置表信息
     * @param id id
     */
    void deleteConfig(Long id);

    /**
     * 查询系统配置表列表
     *
     * @param groupName 分组名称
     * @return 系统配置表列表
     */
    List<ConfigVO> getConfigList(String groupName);

    /**
     * 刷新配置全部缓存
     */
    void refreshAllCache();

    /**
     * 刷新缓存
     *
     * @param key 缓存key
     */
    void refreshCache(String key);
}
