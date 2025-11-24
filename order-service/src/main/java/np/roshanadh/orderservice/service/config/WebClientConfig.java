package np.roshanadh.orderservice.service.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

  // ✅ Plain WebClient.Builder (for direct localhost URLs)
  @Bean("plainWebClientBuilder")
  @Primary
  public WebClient.Builder plainWebClientBuilder() {
    return WebClient.builder();
  }

  // ❗ Only for future use (Eureka/Service Discovery). Not used in OrderService now.
  @Bean("lbWebClientBuilder")
  @LoadBalanced
  public WebClient.Builder loadBalancedWebClientBuilder() {
    return WebClient.builder();
  }
}

