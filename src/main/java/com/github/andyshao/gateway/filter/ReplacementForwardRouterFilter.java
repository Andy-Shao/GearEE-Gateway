package com.github.andyshao.gateway.filter;

import java.net.URI;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.server.ServerWebExchange;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * 
 * 
 * Title:<br>
 * Descript:<br>
 * Copyright: Copryright(c) Mar 28, 2019<br>
 * Encoding: UNIX UTF-8
 * 
 * @author Andy.Shao
 *
 */
@RequiredArgsConstructor
public class ReplacementForwardRouterFilter implements GlobalFilter, Ordered {
	public static final String SCHEME_KEY = "replace";
	private final ObjectProvider<DispatcherHandler> dispatcherHandler;

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		URI routeUrl = exchange.getRequiredAttribute(ServerWebExchangeUtils.GATEWAY_ALREADY_ROUTED_ATTR);
		
		String scheme = routeUrl.getScheme();
		if(ServerWebExchangeUtils.isAlreadyRouted(exchange) || !SCHEME_KEY.equals(scheme)) {
			return chain.filter(exchange);
		}
		return this.dispatcherHandler.getIfAvailable().handle(exchange);
	}

}
