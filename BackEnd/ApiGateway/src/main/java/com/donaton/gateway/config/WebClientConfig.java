package com.donaton.gateway.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Configuración central de WebClient para la comunicación reactiva
 * entre el API Gateway y los microservicios internos.
 *
 * Se definen beans nombrados por microservicio para que cada servicio
 * pueda tener parámetros independientes (timeouts, base URL, etc.)
 * sin interferencia entre ellos.
 */
@Configuration
public class WebClientConfig {

    /**
     * HttpClient base de Reactor Netty con timeouts configurados.
     * Reutilizado por todos los beans de WebClient.
     */
    private HttpClient httpClient() {
        return HttpClient.create()
                // Timeout para establecer la conexión TCP
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5_000)
                // Tiempo máximo total de la respuesta
                .responseTimeout(Duration.ofSeconds(10))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(10, TimeUnit.SECONDS))
                            .addHandlerLast(new WriteTimeoutHandler(10, TimeUnit.SECONDS))
                );
    }

    /**
     * Estrategia de codecs: aumenta el límite del buffer de respuesta
     * a 10MB para manejar respuestas grandes (ej: historial global).
     */
    private ExchangeStrategies exchangeStrategies() {
        return ExchangeStrategies.builder()
                .codecs(ClientCodecConfigurer::defaultCodecs)
                .build();
    }

    /**
     * WebClient dedicado al Microservicio de Donaciones.
     * Base URL apunta al puerto 8086.
     */
    @Bean("donacionWebClient")
    public WebClient donacionWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8086")
                .clientConnector(new ReactorClientHttpConnector(httpClient()))
                .exchangeStrategies(exchangeStrategies())
                .defaultHeader("X-Gateway-Source", "donaton-gateway")
                .build();
    }

    /**
     * WebClient dedicado al Microservicio de Logística.
     * Base URL apunta al puerto 8087.
     */
    @Bean("logisticaWebClient")
    public WebClient logisticaWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8087")
                .clientConnector(new ReactorClientHttpConnector(httpClient()))
                .exchangeStrategies(exchangeStrategies())
                .defaultHeader("X-Gateway-Source", "donaton-gateway")
                .build();
    }

    /**
     * WebClient dedicado al Microservicio de Necesidades en Terreno.
     * Base URL apunta al puerto 8088.
     */
    @Bean("necesidadesWebClient")
    public WebClient necesidadesWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8088")
                .clientConnector(new ReactorClientHttpConnector(httpClient()))
                .exchangeStrategies(exchangeStrategies())
                .defaultHeader("X-Gateway-Source", "donaton-gateway")
                .build();
    }
}