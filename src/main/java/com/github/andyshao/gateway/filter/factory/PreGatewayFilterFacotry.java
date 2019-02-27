package com.github.andyshao.gateway.filter.factory;

import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.cloud.gateway.support.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.DefaultClientResponse;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.ResponseCookie;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.server.ServerWebExchange;

import com.github.andyshao.gateway.core.IGearServerWebExchange;
import com.github.andyshao.gateway.core.impl.DefaultGearServerWebExchage;

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
public class PreGatewayFilterFacotry extends AbstractGatewayFilterFactory<AbstractGatewayFilterFactory.NameConfig>{
	public PreGatewayFilterFacotry() {
		super(NameConfig.class);
	}

	@Override
	public GatewayFilter apply(NameConfig config) {
		return new CustomGatewayFilter();
	}

	@Override
	public String name() {
		return "Prepare";
	}
	
	public class CustomGatewayFilter implements GatewayFilter, Ordered {
        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        	DefaultGearServerWebExchage customExchange = new DefaultGearServerWebExchage(exchange);
            ServerHttpResponseDecorator responseDecorator = buildResponseDecorator(customExchange);
            return chain.filter(customExchange.mutate()
                    .response(responseDecorator)
                    .build());
        }

        ServerHttpResponseDecorator buildResponseDecorator(IGearServerWebExchange customExchange) {
            return new ServerHttpResponseDecorator(customExchange.getResponse()) {
                        @Override
                        public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
                            return writeWith(Flux.from(body).flatMapSequential(p -> p));
                        }

                        @Override
                        public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
//                            String originalResponseContentType = customExchange.getAttribute(ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR);
//                            HttpHeaders httpHeaders = new HttpHeaders();
//                            httpHeaders.add(HttpHeaders.CONTENT_TYPE, originalResponseContentType);
                            HttpHeaders httpHeaders = customExchange.responseHeadProcess()
                                    .apply(customExchange.getResponse().getHeaders());
                            httpHeaders.remove(HttpHeaders.CONTENT_LENGTH);
                            ResponseAdapter responseAdapter = new ResponseAdapter(body, httpHeaders);
                            DefaultClientResponse clientResponse = new DefaultClientResponse(responseAdapter,
                                    ExchangeStrategies.withDefaults());
                            Mono<byte[]> modifiedBody = clientResponse.<byte[]>bodyToMono(byte[].class)
                                    .flatMap(it -> customExchange.responseBodyProcess().apply(Mono.just(it)));

                            BodyInserter<Mono<byte[]>, ReactiveHttpOutputMessage> bodyInserter =
                                    BodyInserters.fromPublisher(modifiedBody, byte[].class);
                            CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(
                                    customExchange, httpHeaders);
                            return bodyInserter.insert(outputMessage, new BodyInserterContext())
                                    .then(Mono.defer(() -> {
                                        Flux<DataBuffer> messageBody = outputMessage.getBody();
                                        HttpHeaders headers = getDelegate().getHeaders();
                                        if(!headers.containsKey(HttpHeaders.TRANSFER_ENCODING)) {
                                            messageBody = messageBody.doOnNext(data ->
                                                    headers.setContentLength(data.readableByteCount()));
                                        }

                                        return getDelegate().writeWith(messageBody);
                                    }));
                        }
                    };
        }

        @Override
        public int getOrder() {
            return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 2;
        }
    }

	public class ResponseAdapter implements ClientHttpResponse {

        private final Flux<DataBuffer> flux;
        private final HttpHeaders headers;

        @SuppressWarnings({ "unchecked", "rawtypes" })
		public ResponseAdapter(Publisher<? extends DataBuffer> body, HttpHeaders headers) {
            this.headers = headers;
            if (body instanceof Flux) {
                flux = (Flux) body;
            } else {
                flux = ((Mono)body).flux();
            }
        }

        @Override
        public Flux<DataBuffer> getBody() {
            return flux;
        }

        @Override
        public HttpHeaders getHeaders() {
            return headers;
        }

        @Override
        public HttpStatus getStatusCode() {
            return null;
        }

        @Override
        public int getRawStatusCode() {
            return 0;
        }

        @Override
        public MultiValueMap<String, ResponseCookie> getCookies() {
            return null;
        }
    }
}
