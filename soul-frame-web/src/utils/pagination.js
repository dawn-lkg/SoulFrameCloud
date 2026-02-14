/**
 * 分页相关工具函数
 */

/**
 * 处理删除后的分页状态
 * @param {Array} currentList 当前页面的数据列表
 * @param {Array} deletedItems 被删除的数据ID数组
 * @param {Object} pageParams 分页参数对象，必须包含pageNum属性
 * @param {Function} getDataFn 获取数据的函数
 * @returns {Promise<void>}
 */
export const handleDeletePagination = (currentList, deletedItems, pageParams, getDataFn) => {
  // 检查是否删除了当前页的所有数据
  const isMassiveDeletion = deletedItems.length >= currentList?.length

  // 如果删除了大量数据且不是第一页，则返回上一页
  if (isMassiveDeletion && pageParams.pageNum > 1) {
    pageParams.pageNum -= 1
  }

  // 重新加载数据
  return getDataFn()
}

/**
 * 处理单个删除后的分页状态
 * @param {Array} currentList 当前页面的数据列表
 * @param {Object} pageParams 分页参数对象，必须包含pageNum属性
 * @param {Function} getDataFn 获取数据的函数
 * @returns {Promise<void>}
 */
export const handleSingleDeletePagination = (currentList, pageParams, getDataFn) => {
  // 检查是否删除的是当前页的最后一条数据
  if (currentList?.length === 1 && pageParams.pageNum > 1) {
    // 如果是最后一条且不是第一页，则返回上一页
    pageParams.pageNum -= 1
  }

  // 重新加载数据
  return getDataFn()
}
