package com.clm.modules.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clm.common.redis.constants.RedisKeyConstants;
import com.clm.common.core.enums.HttpCodeEnum;
import com.clm.common.core.exception.BaseException;
import com.clm.common.redis.utils.RedisUtils;
import com.clm.modules.system.domain.dto.ConfigDTO;
import com.clm.modules.system.domain.entity.Config;
import com.clm.modules.system.domain.param.ConfigParam;
import com.clm.modules.system.domain.vo.ConfigVO;
import com.clm.modules.system.mapper.ConfigMapper;
import com.clm.modules.system.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 系统配置表 服务实现
 *
 * @author 陈黎明
 * @date 2025-07-12 19:37:02
 */
@Service
@RequiredArgsConstructor
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements ConfigService {

    private final RedisUtils redisUtils;

    @Override
    public Page<ConfigVO> selectConfigPage(ConfigParam param) {
        Page<ConfigVO> page = new Page<>(param.getPageNum(), param.getPageSize());
        return page.setRecords(baseMapper.selectConfigPage(page, param));
    }
    
    @Override
    public List<ConfigVO> selectConfigList(ConfigParam param) {
        return baseMapper.selectConfigList(param);
    }

    @Override
    public ConfigVO getByIdRel(Long id) {
        ConfigParam param = new ConfigParam();
        param.setId(id);
        return baseMapper.selectConfigList(param).stream().findFirst().orElse(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveConfig(ConfigDTO dto) {
        Config entity = new Config();
        BeanUtils.copyProperties(dto, entity);
        if(!save(entity)){
            throw new BaseException(HttpCodeEnum.FAILED_ADD);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateConfig(ConfigDTO dto) {
        Config entity = new Config();
        BeanUtil.copyProperties(dto, entity);
        if(!updateById(entity)){
            throw new BaseException(HttpCodeEnum.FAILED_UPDATE);
        }
    }
    
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteConfig(Long id){
        if(!removeById(id)){
            throw new BaseException(HttpCodeEnum.FAILED_DELETE);
        }
    }

    @Override
    public List<ConfigVO> getConfigList(String groupName) {
        ConfigParam configParam = new ConfigParam();
        configParam.setConfigGroup(groupName);
        return redisUtils.get(RedisKeyConstants.System.CONFIG_PREFIX, groupName, () -> baseMapper.selectConfigList(configParam),
                RedisKeyConstants.System.SYSTEM_CONFIG_TIMEOUT, TimeUnit.SECONDS);
    }

    @Override
    public ConfigVO getByKey(String key) {
        ConfigParam param = new ConfigParam();
        param.setConfigKey(key);
        return redisUtils.get(RedisKeyConstants.System.CONFIG_PREFIX, key,
                () -> baseMapper.selectConfigList(param).stream().findFirst().orElse(null),
                RedisKeyConstants.System.SYSTEM_CONFIG_TIMEOUT, TimeUnit.SECONDS);
    }

    @Override
    public <T> T getValueByKey(String key, Class<T> clazz) {
        Config config = redisUtils.get(RedisKeyConstants.System.CONFIG_PREFIX, key, () ->
                lambdaQuery().eq(Config::getConfigKey, key).last("limit 1").one(), RedisKeyConstants.System.SYSTEM_CONFIG_TIMEOUT, TimeUnit.SECONDS);
        if (config == null) {
            return null;
        }
        String val = config.getConfigValue();
        return clazz.cast(val);
    }


    @Override
    public void refreshAllCache() {
        redisUtils.deletePattern(RedisKeyConstants.System.CONFIG_PREFIX + "*");
    }

    @Override
    public void refreshCache(String key) {
        Config one = lambdaQuery().eq(Config::getConfigKey, key).last("limit 1").one();
        if (one == null) {
            return;
        }
        String configGroup = one.getConfigGroup();
        redisUtils.deletePattern(RedisKeyConstants.System.CONFIG_PREFIX + configGroup);
        redisUtils.delete(RedisKeyConstants.System.CONFIG_PREFIX + key);
    }
}
