package co.com.bancolombia.config.user;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.com.bancolombia.model.common.ReactiveTx;
import co.com.bancolombia.model.user.gateways.StrategyUserFactory;
import co.com.bancolombia.model.user.gateways.UserCreationPort;
import co.com.bancolombia.r2dbc.implementaciones.users.DelegateCrearUserService;

@Configuration
public class UserUseCasesConfig {
  
  @Bean
  public UserCreationPort userCreationPort(StrategyUserFactory factory, ReactiveTx reactiveTx) {
    return new DelegateCrearUserService(factory, reactiveTx);
  }



  
  

}
