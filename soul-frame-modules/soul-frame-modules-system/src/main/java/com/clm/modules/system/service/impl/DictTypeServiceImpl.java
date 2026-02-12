package com.clm.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clm.common.redis.constants.RedisKeyConstants;
import com.clm.common.core.exception.BaseException;
import com.clm.common.redis.utils.RedisUtils;
import com.clm.modules.system.domain.entity.DictType;
import com.clm.modules.system.domain.param.DictTypeQueryParam;
import com.clm.modules.system.mapper.DictTypeMapper;
import com.clm.modules.system.service.DictDataService;
import com.clm.modules.system.service.DictTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 字典类型表 服务实现
 *
 * @author 陈黎明
 * @date 2025/3/5
 */
@Service
@RequiredArgsConstructor
public class DictTypeServiceImpl extends ServiceImpl<DictTypeMapper, DictType> implements DictTypeService {

    private final DictTypeMapper dictTypeMapper;

    private final DictDataService dictDataService;
    
    private final RedisUtils redisUtils;

    @Override
    public Page<DictType> selectDictTypePage(DictTypeQueryParam param) {
        Page<DictType> dictTypePage = new Page<>(param.getPageNum(), param.getPageSize());
        return page(dictTypePage, query(param));
    }
    @Override
    public List<DictType> selectDictTypeList(DictTypeQueryParam param) {
        return dictTypeMapper.selectList(query(param));
    }


    private LambdaQueryWrapper<DictType> query(DictTypeQueryParam param) {
        LambdaQueryWrapper<DictType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(param.getDictName() != null, DictType::getDictName, param.getDictName());
        queryWrapper.like(param.getDictType() != null, DictType::getDictType, param.getDictType());
        queryWrapper.eq(param.getStatus() != null, DictType::getStatus, param.getStatus());
        queryWrapper.orderByDesc(DictType::getCreateTime);
        return queryWrapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictTypeByIds(List<Long> dictIdList) {
        if (dictIdList.isEmpty()) {
            return;
        }

        // 删除字典数据
        List<DictType> dictTypes = listByIds(dictIdList);
        dictTypes.forEach(dictType -> {
            redisUtils.delete(RedisKeyConstants.System.DICT_PREFIX + dictType.getDictType());
            dictDataService.deleteDictDataByType(dictType.getDictType());
        });

        // 删除字典类型
        if (!removeByIds(dictIdList)) {
            throw new BaseException("删除字典类型失败");
        }
    }

    @Override
    public void resetDictCache() {
        // 删除所有字典缓存
        redisUtils.deleteByPattern(RedisKeyConstants.System.DICT_PREFIX + "*");
    }

    @Override
    public void addDictType(DictType dictType) {
        // 检查是否唯一
        if (checkDictTypeUnique(dictType)) {
            throw new BaseException("新增字典'" + dictType.getDictName() + "'失败，字典类型已存在");
        }

        if (!save(dictType)) {
            throw new BaseException("新增字典'" + dictType.getDictName() + "'失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDictType(DictType dictType) {
        // 检查是否唯一
        if (checkDictTypeUnique(dictType)) {
            throw new BaseException("修改字典'" + dictType.getDictName() + "'失败，字典类型已存在");
        }
        // 获取旧值
        DictType oldDict = getById(dictType.getDictId());
        if (Objects.isNull(oldDict)) {
            throw new BaseException("修改字典'" + dictType.getDictName() + "'失败，字典不存在");
        }
        // 判断字典类型是否被引用
        if (!oldDict.getStatus().equals(dictType.getStatus())) {
            dictDataService.updateDictStatus(oldDict.getDictType(), dictType.getStatus());
        }
        // 更新字典
        if (!updateById(dictType)) {
            throw new BaseException("修改字典'" + dictType.getDictName() + "'失败");
        }

        // 删除缓存
        redisUtils.delete(RedisKeyConstants.System.DICT_PREFIX + oldDict.getDictType());
    }

    @Override
    public boolean checkDictTypeUnique(DictType dictType) {
        return lambdaQuery().eq(DictType::getDictType, dictType.getDictType())
                .ne(Objects.nonNull(dictType.getDictId()), DictType::getDictId, dictType.getDictId())
                .exists();
    }
} 