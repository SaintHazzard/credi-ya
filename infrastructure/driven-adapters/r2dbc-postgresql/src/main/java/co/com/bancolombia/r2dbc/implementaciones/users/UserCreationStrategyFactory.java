package co.com.bancolombia.r2dbc.implementaciones.users;

import java.util.List;
import java.util.function.Function;

import co.com.bancolombia.model.helpers.MultiDecoratorAbstractFactory;
import co.com.bancolombia.model.user.gateways.CrearUserStrategyEnum;
import co.com.bancolombia.model.user.gateways.CrearUsuarioStrategy;
import co.com.bancolombia.model.user.gateways.StrategyFactory;

/**
 * Implementación específica para CrearUsuarioStrategy que implementa StrategyFactory
 * Utiliza la fábrica genérica MultiDecoratorAbstractFactory
 */
public class UserCreationStrategyFactory implements StrategyFactory {

    private final MultiDecoratorAbstractFactory<CrearUsuarioStrategy, CrearUserStrategyEnum> factory;
    
    public UserCreationStrategyFactory(
            List<CrearUsuarioStrategy> strategies,
            List<Function<CrearUsuarioStrategy, CrearUsuarioStrategy>> decorators) {
        
        this.factory = new MultiDecoratorAbstractFactory<>(
                strategies, 
                decorators,
                CrearUsuarioStrategy::getType); // Extractor de tipo
        
        this.factory.init();
    }
    
    @Override
    public CrearUsuarioStrategy getStrategy(CrearUserStrategyEnum type) {
        return factory.getStrategy(type);
    }
}
