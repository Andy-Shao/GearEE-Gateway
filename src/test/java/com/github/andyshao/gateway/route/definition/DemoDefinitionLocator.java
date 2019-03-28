package com.github.andyshao.gateway.route.definition;

import java.net.URI;
import java.util.ArrayList;

import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;

import com.github.andyshao.gateway.filter.ReplacementForwardGatewayFilterFactory;

import reactor.core.publisher.Flux;

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
public class DemoDefinitionLocator implements RouteDefinitionLocator {

	@Override
	public Flux<RouteDefinition> getRouteDefinitions() {
		return Flux.create(fs -> {
			RouteDefinition routeDefinition = new RouteDefinition();
            routeDefinition.setId("self-forward");
            routeDefinition.setUri(URI.create("replace://service.com"));
            ArrayList<PredicateDefinition> predicates = new ArrayList<>();
            routeDefinition.setPredicates(predicates);
            ArrayList<FilterDefinition> filters = new ArrayList<>();
            routeDefinition.setFilters(filters);

            PredicateDefinition path = new PredicateDefinition();
            predicates.add(path);
            path.setName("Path");
            path.getArgs().put("_genkey_0", "/shouldRemove/**");

            FilterDefinition rewritePath = new FilterDefinition();
            filters.add(rewritePath);
            rewritePath.setName(ReplacementForwardGatewayFilterFactory.NAME);
            rewritePath.getArgs().put(ReplacementForwardGatewayFilterFactory.TARGET, "/shouldRemove");
            rewritePath.getArgs().put(ReplacementForwardGatewayFilterFactory.REPLACEMENT, "");

            fs.next(routeDefinition);
            fs.complete();
		});
	}

}
