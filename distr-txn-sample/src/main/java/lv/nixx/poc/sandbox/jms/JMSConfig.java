package lv.nixx.poc.sandbox.jms;

import com.atomikos.icatch.jta.UserTransactionManager;
import jakarta.jms.ConnectionFactory;
import jakarta.transaction.TransactionManager;
import jakarta.transaction.UserTransaction;
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
public class JMSConfig {


// Пример ручного создания ActiveMQXAConnectionFactory, однако, транзакция работает и без этого

/*
    @Bean
    public ConnectionFactory connectionFactory(
            @Value("${spring.activemq.broker-url}") String brokerURL,
            @Value("${spring.activemq.user}") String user,
            @Value("${spring.activemq.password}") String password
    ) {
        ActiveMQXAConnectionFactory activeMQXAConnectionFactory = new ActiveMQXAConnectionFactory();
        activeMQXAConnectionFactory.setBrokerURL(brokerURL);
        activeMQXAConnectionFactory.setUserName(user);
        activeMQXAConnectionFactory.setPassword(password);

        AtomikosConnectionFactoryBean xaConnectionFactory = new AtomikosConnectionFactoryBean();
        xaConnectionFactory.setUniqueResourceName("xaJmsConnectionFactory");
        xaConnectionFactory.setXaConnectionFactory(activeMQXAConnectionFactory);
        xaConnectionFactory.setPoolSize(5);
        xaConnectionFactory.setLocalTransactionMode(true);

        return xaConnectionFactory;
    }
*/

    @Bean
    public JmsListenerContainerFactory<?> containerFactory(ConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer) {
        // Configuration specific to QUEUE
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);

        factory.setTransactionManager(null);
        return factory;
    }

    @Bean
    public JmsTemplate jmsQueueTemplate(ConnectionFactory queueListenerFactory) {
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(queueListenerFactory);
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