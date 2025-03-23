package lv.nixx.poc.sandbox.jms;

import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jms.AtomikosConnectionFactoryBean;
import jakarta.jms.ConnectionFactory;
import jakarta.transaction.TransactionManager;
import jakarta.transaction.UserTransaction;
import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

@Configuration
@PropertySource("classpath:application.properties")
@EnableJms
public class ApplicationConfig {

    /*
    @Bean
    public DataSource dataSource() {
        MysqlXADataSource mysqlXADataSource = new MysqlXADataSource();
        mysqlXADataSource.setUrl("jdbc:mysql://localhost:3306/db_txn_sandbox?useUnicode=yes&characterEncoding=UTF-8");
        mysqlXADataSource.setUser("user");
        mysqlXADataSource.setPassword("password");

//        HikariConfig hikariConfig = new HikariConfig();
//        hikariConfig.setDataSource(mysqlXADataSource);
//        hikariConfig.setAutoCommit(false);

        AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
        dataSource.setUniqueResourceName("xaDataSource");
        dataSource.setXaDataSource(mysqlXADataSource);

        return dataSource;
    }
     */

    @Bean
    public ConnectionFactory connectionFactory() {
        ActiveMQXAConnectionFactory activeMQXAConnectionFactory = new ActiveMQXAConnectionFactory();
        activeMQXAConnectionFactory.setBrokerURL("tcp://localhost:61616");
        activeMQXAConnectionFactory.setUserName("admin");
        activeMQXAConnectionFactory.setPassword("admin");

        AtomikosConnectionFactoryBean xaConnectionFactory = new AtomikosConnectionFactoryBean();
        xaConnectionFactory.setUniqueResourceName("xaJmsConnectionFactory");
        xaConnectionFactory.setXaConnectionFactory(activeMQXAConnectionFactory);
        xaConnectionFactory.setPoolSize(5);
        xaConnectionFactory.setLocalTransactionMode(true);


        return xaConnectionFactory;
    }

    @Bean
    public JmsListenerContainerFactory<?> containerFactory(ConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();

        configurer.configure(factory, connectionFactory);
        return factory;
    }

    @Bean
    public JmsListenerContainerFactory<?> topicListenerFactory(PlatformTransactionManager platformTransactionManager, ConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);

//        factory.setTransactionManager(platformTransactionManager);
//        factory.setSessionTransacted(true);
        factory.setPubSubDomain(true);
        return factory;
    }

    @Bean
    public JmsTemplate jmsQueueTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(connectionFactory);
        template.setSessionTransacted(true);

        return template;
    }

    @Bean(initMethod = "init", destroyMethod = "close")
    public UserTransactionManager atomikosTransactionManager() {
        UserTransactionManager tm = new UserTransactionManager();
        tm.setForceShutdown(false);
        return tm;
    }


    @Bean(name = "transactionManager")
    public PlatformTransactionManager jtaTransactionManager(UserTransaction userTransaction,
                                                            @Qualifier("atomikosTransactionManager") TransactionManager atomikosTransactionManager
    ) {
        return new JtaTransactionManager(userTransaction, atomikosTransactionManager);
    }
}