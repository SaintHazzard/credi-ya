package co.com.bancolombia.r2dbc.implementaciones.users;

import java.util.List;
import java.util.function.Function;

import co.com.bancolombia.model.common.CrearStrategy;
import co.com.bancolombia.model.helpers.MultiDecoratorAbstractFactory;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.CrearStrategyEnum;
import co.com.bancolombia.model.user.gateways.StrategyUserFactory;

/**
 * Implementación específica para CrearUsuarioStrategy que implementa StrategyFactory
 * Utiliza la fábrica genérica MultiDecoratorAbstractFactory
 */
public class UserCreationStrategyFactory implements StrategyUserFactory {

    private final MultiDecoratorAbstractFactory<CrearStrategy<User>, CrearStrategyEnum> factory;
    
    public UserCreationStrategyFactory(
            List<CrearStrategy<User>> strategies,
            List<Function<CrearStrategy<User>, CrearStrategy<User>>> decorators) {
        
        this.factory = new MultiDecoratorAbstractFactory<>(
                strategies, 
                decorators,
                CrearStrategy::getType);
        
        this.factory.init();
    }
    
    @Override
    public CrearStrategy<User> getStrategy(CrearStrategyEnum type) {
        return factory.getStrategy(type);
    }
}
