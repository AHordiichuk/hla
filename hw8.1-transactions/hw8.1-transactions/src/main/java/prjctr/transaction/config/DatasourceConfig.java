package prjctr.transaction.config;

import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
class DatasourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "datasources.mysql")
    DataSource mysql() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "datasources.percona")
    DataSource percona() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "datasources.postgres")
    DataSource postgres() {
        return DataSourceBuilder.create().build();
    }

}
