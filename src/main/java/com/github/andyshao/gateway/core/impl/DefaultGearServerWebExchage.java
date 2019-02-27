package com.github.andyshao.gateway.core.impl;

import java.security.Principal;
import java.time.Instant;
import java.util.Map;
import java.util.function.Function;

import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.Part;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;

import com.github.andyshao.gateway.core.IGearServerWebExchange;

import lombok.Getter;
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
public class DefaultGearServerWebExchage implements IGearServerWebExchange {
	@Getter
	private final ServerWebExchange delegate;
    private volatile Function<Mono<byte[]>, Mono<byte[]>> requestBodyProcess = p -> p;
    private volatile Function<Mono<byte[]>, Mono<byte[]>> responseBodyProcess = p -> p;
    private volatile Function<HttpHeaders, HttpHeaders> requestHeadProcess = p -> {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.putAll(p);
        httpHeaders.remove(HttpHeaders.CONTENT_LENGTH);
        return httpHeaders;
    };
    private volatile Function<HttpHeaders, HttpHeaders> responseHeadProcess = p -> {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.putAll(p);
        httpHeaders.remove(HttpHeaders.CONTENT_LENGTH);
        return httpHeaders;
    };

    public DefaultGearServerWebExchage(ServerWebExchange delegate) {
        this.delegate = delegate;
    }

    @Override
    public Function<Mono<byte[]>, Mono<byte[]>> requestBodyProcess() {
        return this.requestBodyProcess;
    }

    @Override
    public void requestBodyProcess(Function<Mono<byte[]>, Mono<byte[]>> process) {
        this.requestBodyProcess = process;
    }

    @Override
    public Function<HttpHeaders, HttpHeaders> requestHeadProcess() {
        return this.requestHeadProcess;
    }

    @Override
    public void requestHeadProcess(Function<HttpHeaders, HttpHeaders> process) {
        this.requestHeadProcess = process;
    }

    @Override
    public Function<Mono<byte[]>, Mono<byte[]>> responseBodyProcess() {
        return this.responseBodyProcess;
    }

    @Override
    public void responseBodyProcess(Function<Mono<byte[]>, Mono<byte[]>> process) {
        this.responseBodyProcess = process;
    }

    @Override
    public Function<HttpHeaders, HttpHeaders> responseHeadProcess() {
        return this.responseHeadProcess;
    }

    @Override
    public void responseHeadProcess(Function<HttpHeaders, HttpHeaders> process) {
        this.responseHeadProcess = process;
    }

    @Override
    public ServerHttpRequest getRequest() {
        return getDelegate().getRequest();
    }

    @Override
    public ServerHttpResponse getResponse() {
        return getDelegate().getResponse();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return getDelegate().getAttributes();
    }

    @Override
    public Mono<WebSession> getSession() {
        return getDelegate().getSession();
    }

    @Override
    public <T extends Principal> Mono<T> getPrincipal() {
        return getDelegate().getPrincipal();
    }

    @Override
    public Mono<MultiValueMap<String, String>> getFormData() {
        return getDelegate().getFormData();
    }

    @Override
    public Mono<MultiValueMap<String, Part>> getMultipartData() {
        return getDelegate().getMultipartData();
    }

    @Override
    public LocaleContext getLocaleContext() {
        return getDelegate().getLocaleContext();
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return getDelegate().getApplicationContext();
    }

    @Override
    public boolean isNotModified() {
        return getDelegate().isNotModified();
    }

    @Override
    public boolean checkNotModified(Instant lastModified) {
        return getDelegate().checkNotModified(lastModified);
    }

    @Override
    public boolean checkNotModified(String etag) {
        return getDelegate().checkNotModified(etag);
    }

    @Override
    public boolean checkNotModified(String etag, Instant lastModified) {
        return getDelegate().checkNotModified(etag, lastModified);
    }

    @Override
    public String transformUrl(String url) {
        return getDelegate().transformUrl(url);
    }

    @Override
    public void addUrlTransformer(Function<String, String> transformer) {
        getDelegate().addUrlTransformer(transformer);
    }
}
