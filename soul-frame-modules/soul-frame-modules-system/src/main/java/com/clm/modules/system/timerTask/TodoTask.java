package com.clm.modules.system.timerTask;

import cn.hutool.core.date.DateUtil;
// import com.clm.modules.notice.domain.entity.Todo;
// import com.clm.modules.system.enums.TodoPriorityEnum;
// import com.clm.modules.system.enums.TodoStatusEnum;
// import com.clm.modules.notice.service.TodoService;
// import com.clm.modules.notice.enums.TodoStatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author 陈黎明
 * @since 2025-03-08
 */

@Component("todoTask")
@RequiredArgsConstructor
public class TodoTask {
    // private final TodoService todoService;

    // public void createTodo() {
    //     Todo todo = new Todo();
    //     todo.setTitle(parseTitleTemplate("力扣刷题-" + DateUtil.format(new Date(), "yyyy-MM-dd")));
    //     todo.setContent(parseContentTemplate("1、力扣中等题1道或简单2道"));
    //     todo.setCreateTime(LocalDateTime.now());
    //     todo.setDeadline(LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0));
    //     todo.setStatus(TodoStatusEnum.PENDING.getCode());
    //     todo.setPriority(TodoPriorityEnum.LOW.getCode());
    //     todo.setUserId(1L);
    //     todoService.save(todo);
    // }

    public String parseTitleTemplate(String title) {
        return title.replace("{date}", LocalDateTime.now().toString());
    }

    public String parseContentTemplate(String content) {
        return content.replace("{date}", LocalDateTime.now().toString());
    }
}
