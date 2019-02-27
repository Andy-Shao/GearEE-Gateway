package com.github.andyshao.gateway.core.impl;

import java.security.Principal;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import com.github.andyshao.gateway.core.IGearServerWebExchange;
import com.github.andyshao.gateway.core.IGearServerWebExchange.IGearBuilder;

import reactor.core.publisher.Mono;

public class DefaultGearServerWebExchangeBuilder implements IGearBuilder {
	private final IGearServerWebExchange delegate;
    @Nullable
    private ServerHttpRequest request;
    @Nullable
    private ServerHttpResponse response;
    @Nullable
    private Mono<Principal> principalMono;
    @Nullable
    private volatile Function<Mono<byte[]>, Mono<byte[]>> requestBodyProcess;
    @Nullable
    private volatile Function<Mono<byte[]>, Mono<byte[]>> responseBodyProcess;
    @Nullable
    private volatile Function<HttpHeaders, HttpHeaders> requestHeadProcess;
    @Nullable
    private volatile Function<HttpHeaders, HttpHeaders> responseHeadProcess;


    public DefaultGearServerWebExchangeBuilder(IGearServerWebExchange delegate) {
        Assert.notNull(delegate, "Delegate is required");
        this.delegate = delegate;
    }


    @Override
    public IGearServerWebExchange.IGearBuilder request(Consumer<ServerHttpRequest.Builder> consumer) {
        ServerHttpRequest.Builder builder = this.delegate.getRequest().mutate();
        consumer.accept(builder);
        return request(builder.build());
    }

    @Override
    public IGearServerWebExchange.IGearBuilder request(ServerHttpRequest request) {
        this.request = request;
        return this;
    }

    @Override
    public IGearServerWebExchange.IGearBuilder response(ServerHttpResponse response) {
        this.response = response;
        return this;
    }

    @Override
    public IGearServerWebExchange.IGearBuilder principal(Mono<Principal> principalMono) {
        this.principalMono = principalMono;
        return this;
    }

    @Override
    public IGearServerWebExchange build() {
        return new MutativeDecorator(this.delegate, this.request, this.response, this.principalMono,
                this.requestBodyProcess, this.responseBodyProcess, this.requestHeadProcess, this.responseHeadProcess);
    }

    @Override
    public IGearServerWebExchange.IGearBuilder requestBodyProcess(Function<Mono<byte[]>, Mono<byte[]>> process) {
        this.requestBodyProcess = process;
        return this;
    }

    @Override
    public IGearServerWebExchange.IGearBuilder requestHeadProcess(Function<HttpHeaders, HttpHeaders> process) {
        this.requestHeadProcess = process;
        return this;
    }

    @Override
    public IGearServerWebExchange.IGearBuilder responseBodyProcess(Function<Mono<byte[]>, Mono<byte[]>> process) {
        this.responseBodyProcess = process;
        return this;
    }

    @Override
    public IGearServerWebExchange.IGearBuilder responseHeadProcess(Function<HttpHeaders, HttpHeaders> process) {
        this.responseHeadProcess = process;
        return this;
    }


    /**
     * An immutable wrapper of an exchange returning property overrides -- given
     * to the constructor -- or original values otherwise.
     */
    private static class MutativeDecorator extends GearServerWebExchangeDecorator {
        @Nullable
        private final ServerHttpRequest request;
        @Nullable
        private final ServerHttpResponse response;
        @Nullable
        private final Mono<Principal> principalMono;


        public MutativeDecorator(IGearServerWebExchange delegate, @Nullable ServerHttpRequest request,
                                 @Nullable ServerHttpResponse response, @Nullable Mono<Principal> principalMono,
                                 @Nullable Function<Mono<byte[]>, Mono<byte[]>> requestBodyProcess,
                                 @Nullable Function<Mono<byte[]>, Mono<byte[]>> responseBodyProcess,
                                 @Nullable Function<HttpHeaders, HttpHeaders> requestHeadProcess,
                                 @Nullable Function<HttpHeaders, HttpHeaders> responseHeadProcess) {

            super(delegate);
            this.request = request;
            this.response = response;
            this.principalMono = principalMono;
            if(Objects.nonNull(requestBodyProcess)) {
                delegate.requestBodyProcess(requestBodyProcess);
            }
            if(Objects.nonNull(responseBodyProcess)) {
                delegate.responseBodyProcess(responseBodyProcess);
            }
            if(Objects.nonNull(requestHeadProcess)) {
                delegate.requestHeadProcess(requestHeadProcess);
            }
            if(Objects.nonNull(responseHeadProcess)) {
                delegate.responseHeadProcess(responseHeadProcess);
            }
        }

        @Override
        public ServerHttpRequest getRequest() {
            return (this.request != null ? this.request : getDelegate().getRequest());
        }

        @Override
        public ServerHttpResponse getResponse() {
            return (this.response != null ? this.response : getDelegate().getResponse());
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T extends Principal> Mono<T> getPrincipal() {
            return (this.principalMono != null ? (Mono<T>) this.principalMono : getDelegate().getPrincipal());
        }
    }
}
