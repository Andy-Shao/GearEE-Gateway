package com.github.andyshao.gateway.core;

import java.util.function.Function;

import org.springframework.http.HttpHeaders;

import reactor.core.publisher.Mono;

/**
 * 
 * 
 * Title:<br>
 * Descript:<br>
 * Copyright: Copryright(c) Feb 27, 2019<br>
 * Encoding: UNIX UTF-8
 * 
 * @author Andy.Shao
 *
 */
public final class GearServerWebExchangeUtils {
    public static final String IS_ENABLE_MODIFY_HOST = "is_enable_modify_host";
    public static final String MODIFIED_HOST = "modified_host";
	private GearServerWebExchangeUtils() {}
	
	public static final void addRequestHeadOperation(
            IGearServerWebExchange exchange, Function<HttpHeaders, HttpHeaders> operation) {
        Function<HttpHeaders, HttpHeaders> requestHeadProcess = exchange.requestHeadProcess();
        exchange.requestHeadProcess(httpHeaders -> {
            HttpHeaders headers = requestHeadProcess.apply(httpHeaders);
            return operation.apply(headers);
        });
    }

    public static final void addResponseHeadOperation(
            IGearServerWebExchange exchange, Function<HttpHeaders, HttpHeaders> operation){
        Function<HttpHeaders, HttpHeaders> responseHeadProcess = exchange.responseHeadProcess();
        exchange.responseHeadProcess(httpHeaders -> {
            HttpHeaders headers = responseHeadProcess.apply(httpHeaders);
            return operation.apply(headers);
        });
    }

    public static final void addRequestBodyOperation(
            IGearServerWebExchange exchange, Function<Mono<byte[]>, Mono<byte[]>> operation) {
        Function<Mono<byte[]>, Mono<byte[]>> requestBodyProcess = exchange.requestBodyProcess();
        exchange.requestBodyProcess(body -> operation.apply(requestBodyProcess.apply(body)));
    }

    public static final void addResponseBodyOperation(
            IGearServerWebExchange exchange, Function<Mono<byte[]>, Mono<byte[]>> operation) {
        Function<Mono<byte[]>, Mono<byte[]>> responseBodyProcess = exchange.responseBodyProcess();
        exchange.responseBodyProcess(body -> operation.apply(responseBodyProcess.apply(body)));
    }
}
