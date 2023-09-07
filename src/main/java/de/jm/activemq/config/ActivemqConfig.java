package de.jm.activemq.config;

import org.apache.activemq.ActiveMQXASslConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jta.atomikos.AtomikosConnectionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.jms.ConnectionFactory;

@Configuration
@EnableTransactionManagement
public class ActivemqConfig
{
    //  ERZEUGUNG KEYSTORE/TRUSTSTORE
    //    keytool -genkeypair -keyalg RSA -keysize 2048 -alias broker -dname "CN=activemq,OU=none,O=none,C=DE" -ext "SAN:c=DNS:localhost,IP:127.0.0.1" -validity 3650 -storepass changeit -keypass changeit -keystore broker.ks -deststoretype pkcs12
    //    keytool -export -alias broker -keystore broker.ks -file broker_cert
    //    keytool -genkey -alias client -keyalg RSA -keystore client.jks
    //    keytool -import -alias broker -keystore client.jks -file broker_cert

    @Bean
    AtomikosConnectionFactoryBean connectionFactorySsl(
        @Value("${broker.https.url}") String url,
        @Value("${broker.username}") String username,
        @Value("${broker.password}") String password,
        @Value("${client.keystore.path}") String keystorePfad,
        @Value("${client.keystore.type}") String keystoreType,
        @Value("${client.keystore.password}") String keystorePassword) throws Exception
    {
        var factory = new ActiveMQXASslConnectionFactory(url);
        factory.setUserName(username);
        factory.setPassword(password);
        factory.setUseCompression(true);
        factory.setRmIdFromConnectionId(true);
        factory.setKeyStore(keystorePfad);
        factory.setKeyStorePassword(keystorePassword);
        factory.setTrustStore(keystorePfad);
        factory.setTrustStorePassword(keystorePassword);
        factory.setTrustStoreType(keystoreType);
        factory.setSendTimeout(10000);

        var atomikosFactory = new AtomikosConnectionFactoryBean();
        atomikosFactory.setXaConnectionFactory(factory);
        atomikosFactory.setUniqueResourceName("activemq");
        atomikosFactory.setMaxPoolSize(100);
        atomikosFactory.setMinPoolSize(30);

        return atomikosFactory;
    }

    @Bean
    JmsTemplate jmsTemplateSsl(
        ConnectionFactory connectionFactorySsl) {

        var template = new JmsTemplate();
        template.setConnectionFactory(connectionFactorySsl);
        template.setDeliveryPersistent(true);
        template.setSessionTransacted(true);

        return template;
    }


    @Bean
    ActiveMQQueue queueSsl() {
        return new ActiveMQQueue("msg_ssl");
    }

}
