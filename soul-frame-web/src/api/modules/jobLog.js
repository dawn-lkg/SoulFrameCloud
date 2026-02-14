import request from '@/utils/request'

// 分页获取调度日志
export async function getJobLogPage(params) {
  return request.get('/monitor/jobLog/page', { params })
}

// 根据jobId清空调度日志
export async function clearJobLogByJobLog(jobId) {
  return request.delete("/monitor/jobLog/clean",{params:{
    jobId
  }})
}