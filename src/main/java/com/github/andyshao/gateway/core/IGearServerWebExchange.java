package com.github.andyshao.gateway.core;

import java.security.Principal;
import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

import com.github.andyshao.gateway.core.impl.DefaultGearServerWebExchangeBuilder;

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
public interface IGearServerWebExchange extends ServerWebExchange {
	Function<Mono<byte[]>, Mono<byte[]>> requestBodyProcess();
    Function<HttpHeaders, HttpHeaders> requestHeadProcess();
    Function<Mono<byte[]>, Mono<byte[]>> responseBodyProcess();
    Function<HttpHeaders, HttpHeaders> responseHeadProcess();

    @Override
    default IGearBuilder mutate() {
    	return new DefaultGearServerWebExchangeBuilder(this);
    }

    /**
     * Builder for mutating an existing {@link ICustomServerWebExchange}.
     * Removes the need
     */
    interface IGearBuilder extends ServerWebExchange.Builder {
        IGearBuilder requestBodyProcess(Function<Mono<byte[]>, Mono<byte[]>> process);
        IGearBuilder requestHeadProcess(Function<HttpHeaders, HttpHeaders> process);
        IGearBuilder responseBodyProcess(Function<Mono<byte[]>, Mono<byte[]>> process);
        IGearBuilder responseHeadProcess(Function<HttpHeaders, HttpHeaders> process);
        @Override
        IGearServerWebExchange build();
        @Override
        IGearBuilder request(ServerHttpRequest request);
        @Override
        IGearBuilder request(Consumer<ServerHttpRequest.Builder> requestBuilderConsumer);
        @Override
        IGearBuilder response(ServerHttpResponse response);
        @Override
        IGearBuilder principal(Mono<Principal> principalMono);
    }
}
