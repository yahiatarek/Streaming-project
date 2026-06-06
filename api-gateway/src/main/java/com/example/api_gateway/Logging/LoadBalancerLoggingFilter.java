package com.example.api_gateway.Logging;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.*;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class LoadBalancerLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(LoadBalancerLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Response<?> loadBalancerResponse = exchange.getAttribute(GATEWAY_LOADBALANCER_RESPONSE_ATTR);
        URI routedUri = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
        Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);

        if (loadBalancerResponse != null && loadBalancerResponse.hasServer()) {
            Object server = loadBalancerResponse.getServer();

            if (server instanceof ServiceInstance serviceInstance) {
                log.info(
                        "Gateway routed {} {} via route={} to service={} instance={}://{}:{} finalUri={}",
                        exchange.getRequest().getMethod(),
                        exchange.getRequest().getURI().getPath(),
                        route != null ? route.getId() : "unknown",
                        serviceInstance.getServiceId(),
                        serviceInstance.getScheme(),
                        serviceInstance.getHost(),
                        serviceInstance.getPort(),
                        routedUri);
            }
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return ReactiveLoadBalancerClientFilter.LOAD_BALANCER_CLIENT_FILTER_ORDER + 1;
    }
}