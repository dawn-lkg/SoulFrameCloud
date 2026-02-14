import {defineStore} from 'pinia'
import {getDictDataList} from '@/api/modules/dict'

export const useDictStore = defineStore('dict', {
    state: () => ({
        // 字典数据缓存
        dictCache: {},
        // 正在加载的字典类型
        loadingDict: new Set()
    }),
    actions: {
        // 获取字典数据
        async getDictData(dictType) {
            if (this.dictCache[dictType]) {
                return this.dictCache[dictType]
            }

            if (this.loadingDict.has(dictType)) {
                return new Promise((resolve) => {
                    const checkInterval = setInterval(() => {
                        if (this.dictCache[dictType]) {
                            clearInterval(checkInterval);
                            resolve(this.dictCache[dictType]);
                        }
                    }, 50);
                });
            }
            this.loadingDict.add(dictType)

            try {
                const data = await getDictDataList(dictType)
                const dictData = data.map(item => ({
                    label: item.dictLabel,
                    value: item.dictValue,
                    cssClass: item.cssClass,
                    listClass: item.listClass
                }))
                // 缓存数据
                this.dictCache[dictType] = dictData
                return dictData
            } catch (error) {
                console.error(`获取字典数据[${dictType}]失败`, error)
                return []
            }
        },

        // 设置字典数据
        setDictData(dictType, data) {
            this.dictCache[dictType] = data
        },

        // 重置字典数据
        resetDictData() {
            this.dictCache = {}
        },

        // 删除指定字典数据
        removeDictData(dictType) {
            delete this.dictCache[dictType]
        }
    },
}) 