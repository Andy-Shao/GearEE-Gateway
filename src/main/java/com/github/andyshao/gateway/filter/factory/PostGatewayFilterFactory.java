package com.github.andyshao.gateway.filter.factory;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.cloud.gateway.support.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.DefaultServerRequest;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;

import com.github.andyshao.gateway.core.IGearServerWebExchange;

import reactor.core.publisher.Flux;
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
public class PostGatewayFilterFactory extends AbstractGatewayFilterFactory<AbstractGatewayFilterFactory.NameConfig>{

	public PostGatewayFilterFactory() {
        super(NameConfig.class);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public GatewayFilter apply(NameConfig config) {
        return (exchange, chain) -> {
            if(exchange instanceof IGearServerWebExchange) {
            	IGearServerWebExchange customExchange = (IGearServerWebExchange) exchange;
                DefaultServerRequest serverRequest = new DefaultServerRequest(customExchange);
                Mono<byte[]> modifiedBody = serverRequest.bodyToMono(byte[].class)
                        .flatMap(it -> customExchange.requestBodyProcess().apply(Mono.just(it)));
                BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, byte[].class);
                HttpHeaders headers = customExchange.requestHeadProcess().apply(customExchange.getRequest().getHeaders());
                CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(customExchange, headers);
                return bodyInserter.insert(outputMessage, new BodyInserterContext())
                        .then(Mono.defer(() -> {
                            ServerHttpRequestDecorator requestDecorator =
                                    new ServerHttpRequestDecorator(customExchange.getRequest()) {
                                        @Override
                                        public Flux<DataBuffer> getBody() {
                                            return outputMessage.getBody();
                                        }

                                        @Override
                                        public HttpHeaders getHeaders() {
                                            return headers;
                                        }
                                    };
                            return chain.filter(customExchange.mutate()
                                .request(requestDecorator)
                                .build());
                        }));
            }
            return chain.filter(exchange);
        };
    }

    @Override
    public String name() {
        return "Post";
    }
}
