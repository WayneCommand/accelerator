package ltd.inmind.accelerator.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ltd.inmind.accelerator.model.vo.DataResponse;
import ltd.inmind.accelerator.serializer.DataResponseSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class WebFluxConfig implements WebFluxConfigurer {

    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //TODO 在1.0的时候解决origin的问题
        corsConfiguration.addAllowedOrigin("*");
        //corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addExposedHeader("X-AUTH-TOKEN");

        return corsConfiguration;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig());
        return source;
    }

    @Bean
    public Gson gson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(DataResponse.class, new DataResponseSerializer());

        return builder.create();
    }

}
