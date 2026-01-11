//package com.docops.document.config;
//
//import javax.sql.DataSource;
//
//import org.flywaydb.core.Flyway;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class ManualFlywayConfig {
//
//    @Bean
//    public Flyway flyway(DataSource dataSource) {
//
//        Flyway flyway = Flyway.configure()
//                .dataSource(dataSource)
//                .locations("classpath:db/migration")
//                .baselineOnMigrate(true)
//                .load();
//
//        flyway.migrate();
//        return flyway;
//    }
//}
