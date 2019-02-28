package com.github.andyshao.gateway.filter.factory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;

import com.github.andyshao.gateway.core.GearServerWebExchangeUtils;

import lombok.Getter;

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
public class EnableModifyHostGatewayFilterFactory extends AbstractGatewayFilterFactory<EnableModifyHostGatewayFilterFactory.Config> {
	private static final String ENABLE = "enable";
    private static final String URI = "uri";
	public EnableModifyHostGatewayFilterFactory() {
		super(Config.class);
	}
	
	@Getter
    public static class Config {
        private boolean enable = false;
        private String uri;

        public Config setEnable(boolean enable) {
            this.enable = enable;
            return this;
        }

        public Config setUri(String uri) {
            this.uri = uri;
            return this;
        }
    }

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			if(config.isEnable()) {
				Map<String, Object> attributes = exchange.getAttributes();
				attributes.put(GearServerWebExchangeUtils.IS_ENABLE_MODIFY_HOST, true);
				attributes.put(GearServerWebExchangeUtils.MODIFIED_HOST, config.getUri());
			}
			return chain.filter(exchange);
		};
	}

	@Override
	public List<String> shortcutFieldOrder() {
		return Arrays.asList(ENABLE, URI);
	}
}
