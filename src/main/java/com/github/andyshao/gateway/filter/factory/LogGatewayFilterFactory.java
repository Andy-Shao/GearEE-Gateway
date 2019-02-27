package com.github.andyshao.gateway.filter.factory;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;

import com.github.andyshao.gateway.core.GearServerWebExchangeUtils;
import com.github.andyshao.gateway.core.IGearServerWebExchange;

import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class LogGatewayFilterFactory extends AbstractGatewayFilterFactory<AbstractGatewayFilterFactory.NameConfig> {
	public LogGatewayFilterFactory() {
        super(NameConfig.class);
    }

    @Override
    public GatewayFilter apply(NameConfig config) {
        return (exchange, chain) -> {
            if(exchange instanceof IGearServerWebExchange) {
            	IGearServerWebExchange customExchange = (IGearServerWebExchange) exchange;
                GearServerWebExchangeUtils.addRequestBodyOperation(customExchange, body ->
                        body.doOnNext(next -> log.info("Request body is - {}", new String(next))));
                GearServerWebExchangeUtils.addResponseBodyOperation(customExchange, body ->
                        body.doOnNext(next -> log.info("Response body is - {}", new String(next))));
            }
            return chain.filter(exchange);
        };
    }

    @Override
    public String name() {
        return "Logger";
    }
}
