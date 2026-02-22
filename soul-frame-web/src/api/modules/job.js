import request from '@/utils/request'

// 分页查询定时任务列表
export function getJobPage(query) {
  return request.get('/system/monitor/job/page', {
    params: query
  })
}

// 查询定时任务列表
export function listJob(query) {
  return request.get('/system/monitor/job/list', {
    params: query
  })
}

// 查询定时任务详细
export function getJob(jobId) {
  return request.get('/system/monitor/job/' + jobId)
}

// 新增定时任务
export function addJob(data) {
  return request.post('/system/monitor/job', data)
}

// 修改定时任务
export function updateJob(data) {
  return request.put('/system/monitor/job', data)
}

// 删除定时任务
export function delJob(jobId) {
  return request.delete('/system/monitor/job/' + jobId)
}

// 任务状态修改
export function changeJobStatus(job) {
  return request.put('/system/monitor/job/changeStatus', job)
}

// 定时任务立即执行一次
export function runJob(jobId) {
  return request.put('/system/monitor/job/run', {jobId})
} 