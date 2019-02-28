package com.github.andyshao.gateway.filter.factory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;

import com.github.andyshao.gateway.core.GearServerWebExchangeUtils;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 
 * 
 * Title:<br>
 * Descript:<br>
 * Copyright: Copryright(c) Feb 28, 2019<br>
 * Encoding: UNIX UTF-8
 * 
 * @author Andy.Shao
 *
 */
@Slf4j
public class ModifyHostGatewayFilterFactory implements GlobalFilter, Ordered {
	public static final int LOAD_BALANCER_CLIENT_FILTER_ORDER = 10100;

    static final URI buildPath(String requestUrl) {
        try {
            return new URI(requestUrl);
        } catch (URISyntaxException e) {
            log.error(String.format("The url '%s' doesn't exists", requestUrl), e);
            throw new RuntimeException(e);
        }
    }

	@Override
	public int getOrder() {
		return LOAD_BALANCER_CLIENT_FILTER_ORDER;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		Boolean isEnable = exchange.getAttribute(GearServerWebExchangeUtils.IS_ENABLE_MODIFY_HOST);
        if(Objects.isNull(isEnable) || !isEnable) {
            return chain.filter(exchange);
        }

        URI url = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
        URI requestUrl = null;
        String host = exchange.getAttribute(GearServerWebExchangeUtils.MODIFIED_HOST);
        if(Objects.nonNull(url)) {
            requestUrl = buildPath(host + url.getPath());
        } else {
            requestUrl = buildPath(host + exchange.getRequest().getURI().getPath());
        }
        exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, requestUrl);
        log.debug("Request URL is - {}", requestUrl);
        return chain.filter(exchange);
	}

}
