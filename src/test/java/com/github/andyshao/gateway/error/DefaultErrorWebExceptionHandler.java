package com.github.andyshao.gateway.error;

import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import com.github.andyshao.exception.Result;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 
 * 
 * Title:<br>
 * Descript:<br>
 * Copyright: Copryright(c) Apr 10, 2019<br>
 * Encoding: UNIX UTF-8
 * 
 * @author Andy.Shao
 *
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DefaultErrorWebExceptionHandler implements ErrorWebExceptionHandler{

	@Override
	public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
		ServerHttpResponse response = exchange.getResponse();
		DataBufferFactory bufferFactory = response.bufferFactory();
		if(ex instanceof ResponseStatusException) {
			ResponseStatusException e = (ResponseStatusException) ex;
			HttpStatus status = e.getStatus();
			response.setStatusCode(status);
			return resp(response, bufferFactory, status);
		} else if(ex instanceof NotFoundException) {
			HttpStatus defaultStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			response.setStatusCode(defaultStatus);
			return resp(response, bufferFactory, defaultStatus);
		} else if(ex instanceof UnknownHostException) {
			HttpStatus defaultStatus = HttpStatus.SERVICE_UNAVAILABLE;
			response.setStatusCode(defaultStatus);
			return resp(response, bufferFactory, defaultStatus);
		}
		return Mono.empty();
	}

	Mono<Void> resp(ServerHttpResponse response, DataBufferFactory bufferFactory, HttpStatus status) {
		return resp(response, bufferFactory, Result.<Void>error(String.valueOf(status.value()), status.getReasonPhrase()));
	}

	static final Mono<Void> resp(ServerHttpResponse response, DataBufferFactory bufferFactory, Result<Void> ret) {
		return response.writeWith(Flux.just(ret)
				.map(it -> bufferFactory.wrap(wrapResult(it).getBytes(StandardCharsets.UTF_8))));
	}

	static final <T> String wrapResult(Result<T> result) {
        String data = "{}";
        if(Objects.nonNull(result.getData())) {
//            data = JSON.toJSONString(result.getData());
            return String.format("{\"code\":\"%s\", \"msg\":\"%s\", \"data\":%s}",
                    result.getCode(),
                    result.getMessage(),
                    data);
        } else {
            return String.format("{\"code\":\"%s\", \"msg\":\"%s\"}",
                    result.getCode(),
                    result.getMessage());
        }
    }
}
