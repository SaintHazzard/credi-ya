package co.com.bancolombia.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class OpenApiResourceConfig implements WebFluxConfigurer {
  @Override
  public void addResourceHandlers( ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/openapi/**")
        .addResourceLocations("classpath:/openapi/");
  }
}
