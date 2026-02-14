package com.clm.gateway.handler;

//import com.clm.common.core.domain.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * Gateway 全局异常处理器
 *
 * @author 陈黎明
 */
@Slf4j
@Order(-1)
//@Component
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
//        ServerHttpResponse response = exchange.getResponse();
//
//        log.error("Gateway 异常: {}", ex.getMessage(), ex);
//
//        if (response.isCommitted()) {
//            return Mono.error(ex);
//        }
//
//        response.getHeaders().setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
//
//        Result<?> result;
//        HttpStatus status;
//
//        if (ex instanceof ResponseStatusException re) {
//            status = HttpStatus.valueOf(re.getStatusCode().value());
//            result = Result.error(status.value(), re.getReason());
//        } else {
//            status = HttpStatus.INTERNAL_SERVER_ERROR;
//            result = Result.error(500, "网关异常，请稍后重试");
//        }
//
//        response.setStatusCode(status);
//
//        // 手动序列化为 JSON
//        String json = toJson(result);
//        return response.writeWith(Mono.fromSupplier(() ->
//                response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8))));
        return Mono.error(ex);
    }
//    }

//    private String toJson(Result<?> result) {
//        // 简化版 JSON 序列化，避免引入 Jackson 依赖
//        if (result == null) {
//            return "{\"code\":500,\"msg\":\"未知异常\"}";
//        }
//        return String.format("{\"code\":%d,\"msg\":\"%s\"}",
//                result.getCode(),
//                result.getMsg() != null ? result.getMsg().replace("\"", "\\\"") : "");
//    }
}
