package com.github.andyshao.gateway.filter;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;

import lombok.Getter;

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
public class ReplacementForwardGatewayFilterFactory extends AbstractGatewayFilterFactory<ReplacementForwardGatewayFilterFactory.ReplaceConfig>{
    public static final String TARGET = "target";
    public static final String REPLACEMENT = "replacement";
    public static final String NAME = "ReplacementForward";
    
    public ReplacementForwardGatewayFilterFactory() {
    	super(ReplaceConfig.class);
	}

	@Getter
    public static class ReplaceConfig {
        private String target;
        private String replacement;

        public ReplaceConfig setTarget(String target) {
            this.target = target;
            return this;
        }

        public ReplaceConfig setReplacement(String replacement) {
            this.replacement = replacement;
            return this;
        }
    }

	@Override
	public GatewayFilter apply(ReplaceConfig config) {
		return (exchange, chain) -> {
			Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
			URI routeUri = route.getUri();
			
			String scheme = routeUri.getScheme();
			if(ServerWebExchangeUtils.isAlreadyRouted(exchange) || !ReplacementForwardRouterFilter.SCHEME_KEY.equals(scheme)) {
				return chain.filter(exchange);
			}
			
			String url = exchange.getRequest()
					.getURI()
					.getPath()
					.replaceAll(config.getTarget(), config.getReplacement());
			return chain.filter(exchange.mutate()
					.request(exchange.getRequest().mutate().path(url).build())
					.build());
		};
	}

	@Override
	public String name() {
		return NAME;
	}

	@Override
	public List<String> shortcutFieldOrder() {
		return Arrays.asList(TARGET, REPLACEMENT);
	}
}
