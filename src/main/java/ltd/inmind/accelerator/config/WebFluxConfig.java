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

import static ltd.inmind.accelerator.constants.SecurityConst.AUTHENTICATION_HEADER;

@Configuration
public class WebFluxConfig implements WebFluxConfigurer {

    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("http://127.0.0.1:8080");
        corsConfiguration.addAllowedOrigin("http://localhost:8080");
        corsConfiguration.addAllowedOrigin("http://accelerator.inmind.ltd");
        corsConfiguration.addAllowedOrigin("https://accelerator.inmind.ltd");
        corsConfiguration.addAllowedHeader(AUTHENTICATION_HEADER);
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addExposedHeader(AUTHENTICATION_HEADER);

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
