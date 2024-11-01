package org.devcalm.store.manager.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Locale;

@Configuration
public class LocaleSettingFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        var language = exchange.getRequest().getHeaders().getFirst("Accept-Language");
        var locale = language != null ? Locale.forLanguageTag(language) : Locale.ENGLISH;
        LocaleContextHolder.setLocale(locale);

        return chain.filter(exchange)
                .doFinally(signal -> LocaleContextHolder.resetLocaleContext());
    }
}
