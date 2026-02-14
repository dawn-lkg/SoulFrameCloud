import request from '@/utils/request'

// 分页查询定时任务列表
export function getJobPage(query) {
  return request.get('/monitor/job/page', {
    params: query
  })
}

// 查询定时任务列表
export function listJob(query) {
  return request.get('/monitor/job/list', {
    params: query
  })
}

// 查询定时任务详细
export function getJob(jobId) {
  return request.get('/monitor/job/' + jobId)
}

// 新增定时任务
export function addJob(data) {
  return request.post('/monitor/job', data)
}

// 修改定时任务
export function updateJob(data) {
  return request.put('/monitor/job', data)
}

// 删除定时任务
export function delJob(jobId) {
  return request.delete('/monitor/job/' + jobId)
}

// 任务状态修改
export function changeJobStatus(job) {
  return request.put('/monitor/job/changeStatus', job)
}

// 定时任务立即执行一次
export function runJob(jobId) {
  return request.put('/monitor/job/run', {jobId})
} 