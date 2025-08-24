package co.com.bancolombia.config.user;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.com.bancolombia.model.user.gateways.ManagementUserPort;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.usecase.creacionuser.ManagementUserUseCase;

@Configuration
public class UserManagementUseCasesConfig {

  @Bean
  public ManagementUserPort managementUserPort(UserRepository repo) {
    return new ManagementUserUseCase(repo);
  }

  @Bean
  public ManagementUserUseCase managementUserUseCase(UserRepository repo) {
    return new ManagementUserUseCase(repo);
  }
}
