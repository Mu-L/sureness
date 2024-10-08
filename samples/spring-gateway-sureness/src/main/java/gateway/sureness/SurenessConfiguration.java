package gateway.sureness;


import org.dromara.sureness.SurenessDefaultConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * sureness config
 * @author tomsun28
 * @date 23:38 2019-09-29
 */
@Configuration
public class SurenessConfiguration {

    /**
     * new sureness default config bean
     * @return default config bean
     */
    @Bean
    public SurenessDefaultConfig surenessConfig() {
        return new SurenessDefaultConfig(SurenessDefaultConfig.SUPPORT_SPRING_REACTIVE);
    }

}
