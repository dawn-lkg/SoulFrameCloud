package com.clm.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clm.common.core.constants.Constants;
import com.clm.common.redis.constants.RedisKeyConstants;
import com.clm.common.core.exception.BaseException;
import com.clm.common.redis.utils.RedisUtils;
import com.clm.modules.system.domain.entity.DictData;
import com.clm.modules.system.domain.param.DictDataQueryParam;
import com.clm.modules.system.mapper.DictDataMapper;
import com.clm.modules.system.service.DictDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 字典数据表 服务实现
 *
 * @author 陈黎明
 * @date 2025/3/5
 */
@Service
@RequiredArgsConstructor
public class DictDataServiceImpl extends ServiceImpl<DictDataMapper, DictData> implements DictDataService {

    private final DictDataMapper dictDataMapper;
    
    private final RedisUtils redisUtils;

    @Override
    public Page<DictData> selectDictDataPage(DictDataQueryParam param) {
        Page<DictData> page = Page.of(param.getPageNum(), param.getPageSize());
        return page(page, query(param));
    }

    @Override
    public List<DictData> selectDictDataList(DictDataQueryParam param) {
        return dictDataMapper.selectList(query(param));
    }

    private LambdaQueryWrapper<DictData> query(DictDataQueryParam param) {
        LambdaQueryWrapper<DictData> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(param.getDictType() != null, DictData::getDictType, param.getDictType());
        queryWrapper.eq(param.getDictLabel() != null, DictData::getDictLabel, param.getDictLabel());
        queryWrapper.orderByAsc(DictData::getDictSort);
        return queryWrapper;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictDataByIds(Long[] dictCodes) {
        if (dictCodes.length == 0) {
            return;
        }
        List<Long> dictCodeList = Arrays.stream(dictCodes).toList();
        List<DictData> dictDataList = listByIds(dictCodeList);
        List<Long> idList = dictDataList.stream().map(DictData::getDictCode).toList();
        if (!removeByIds(idList)) {
            throw new BaseException("删除失败");
        }
        //删除缓存
        dictDataList.forEach(dictData -> redisUtils.delete(RedisKeyConstants.System.DICT_PREFIX + dictData.getDictCode()));
    }

    @Override
    public void deleteDictDataByType(String dictType) {
        remove(new LambdaQueryWrapper<DictData>().eq(DictData::getDictType, dictType));
    }

    @Override
    public boolean updateDictTypeValue(String oldDictType, String newDictType) {
        return lambdaUpdate().eq(DictData::getDictType, oldDictType)
                .set(DictData::getDictType, newDictType)
                .update();
    }

    @Override
    public boolean updateDictStatus(String dictType, String status) {
        return lambdaUpdate().eq(DictData::getDictType, dictType)
                .set(DictData::getStatus, status).update();
    }

    @Override
    public void addDictData(DictData dictData) {
        redisUtils.delete(RedisKeyConstants.System.DICT_PREFIX + dictData.getDictType());
        if (!save(dictData)) {
            throw new BaseException("添加字典数据失败");
        }
    }

    @Override
    public void updateDictData(DictData dictData) {
        redisUtils.delete(RedisKeyConstants.System.DICT_PREFIX + dictData.getDictType());
        if (!updateById(dictData)) {
            throw new BaseException("修改字典数据失败");
        }
    }

    @Override
    public List<DictData> selectDictDataByType(String dictType) {
        return redisUtils.get(RedisKeyConstants.System.DICT_PREFIX, dictType, () -> selectDictDataList(dictType),
                RedisKeyConstants.System.DICT_TIMEOUT, TimeUnit.SECONDS);
    }

    @Override
    public List<DictData> selectDictDataList(String dictType) {
        return lambdaQuery().eq(DictData::getDictType, dictType)
                .eq(DictData::getStatus, Constants.DICT_NORMAL)
                .orderByAsc(DictData::getDictSort)
                .list();
    }
} 