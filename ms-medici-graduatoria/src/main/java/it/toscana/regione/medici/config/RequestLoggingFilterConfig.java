package it.toscana.regione.medici.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class RequestLoggingFilterConfig
{
    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(30000);
        filter.setIncludeHeaders(false);
        filter.setAfterMessagePrefix("REQUEST DATA (questo log potrebbe seguire la relativa response anzichè precederla): ");
        return filter;
    }
}
