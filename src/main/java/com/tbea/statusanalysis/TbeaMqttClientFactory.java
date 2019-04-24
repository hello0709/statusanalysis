package com.tbea.statusanalysis;
/*
 * Copyright (c) 2019 TBEA Inc. -- All Rights Reserved.
 */



import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PasswordFinder;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.core.ConsumerStopAction;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;

public class TbeaMqttClientFactory implements MqttPahoClientFactory {
    private volatile Boolean cleanSession;

    private volatile Integer connectionTimeout;
    private volatile Integer keepAliveInterval;
    private volatile String password;
    private volatile SocketFactory socketFactory;
    private volatile Properties sslProperties;
    private volatile String userName;
    private volatile MqttClientPersistence persistence;
    private volatile Will will;
    private volatile String[] serverURIs;

    public TbeaMqttClientFactory(String caCrtFile, String crtFile, String keyFile, final String
            password, String userName, String userPassword, String[] serverURIs) {

        this.userName = userName;
        this.password = userPassword;
        this.serverURIs = serverURIs;
        this.keepAliveInterval = 5;
        Security.addProvider(new BouncyCastleProvider());

        // load CA certificate
        PEMReader reader = null;
        try {
            reader = new PEMReader(new InputStreamReader(
                    new ByteArrayInputStream(Files.readAllBytes(Paths.get(caCrtFile)))));
            X509Certificate caCert = (X509Certificate) reader.readObject();
            reader.close();
            // load client certificate
            reader = new PEMReader(new InputStreamReader(
                    new ByteArrayInputStream(Files.readAllBytes(Paths.get(crtFile)))));
            X509Certificate cert = (X509Certificate) reader.readObject();
            reader.close();

            // load client private key
            reader = new PEMReader(new InputStreamReader(
                    new ByteArrayInputStream(Files.readAllBytes(Paths.get(keyFile)))),
                    new PasswordFinder() {
                        @Override
                        public char[] getPassword() {
                            return password.toCharArray();
                        }
                    });
            KeyPair key = (KeyPair) reader.readObject();
            reader.close();

            // CA certificate is used to authenticate server
            KeyStore caKs = KeyStore.getInstance(KeyStore.getDefaultType());
            caKs.load(null, null);
            caKs.setCertificateEntry("ca-certificate", caCert);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(caKs);

            // client key and certificates are sent to server so it can authenticate
            // us
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(null, null);
            ks.setCertificateEntry("certificate", cert);
            ks.setKeyEntry("private-key", key.getPrivate(), password.toCharArray(),
                    new java.security.cert.Certificate[] { cert });
            KeyManagerFactory kmf = KeyManagerFactory.
                    getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, password.toCharArray());

            // finally, create SSL socket factory
            SSLContext context = SSLContext.getInstance("TLSv1");
            context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            this.socketFactory = context.getSocketFactory();
        } catch (IOException | CertificateException | NoSuchAlgorithmException |
                UnrecoverableKeyException | KeyManagementException | KeyStoreException e) {
            e.printStackTrace();
        }
    }

    public void setCleanSession(Boolean cleanSession) {
        this.cleanSession = cleanSession;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setKeepAliveInterval(Integer keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSocketFactory(SocketFactory socketFactory) {
        this.socketFactory = socketFactory;
    }

    public void setSslProperties(Properties sslProperties) {
        this.sslProperties = sslProperties;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setWill(Will will) {
        this.will = will;
    }

    public void setPersistence(MqttClientPersistence persistence) {
        this.persistence = persistence;
    }

    public void setServerURIs(String[] serverURIs) {
        this.serverURIs = serverURIs;
    }

    @Override
    public MqttClient getClientInstance(String uri, String clientId) throws MqttException {
        return new MqttClient(uri == null ? "tcp://NO_URL_PROVIDED" : uri, clientId, this.persistence);
    }

    @Override
    public MqttAsyncClient getAsyncClientInstance(String uri, String clientId) throws MqttException {
        return new MqttAsyncClient(uri == null ? "tcp://NO_URL_PROVIDED" : uri, clientId, this.persistence);
    }

    @Override
    public MqttConnectOptions getConnectionOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        if (this.cleanSession != null) {
            options.setCleanSession(this.cleanSession.booleanValue());
        }

        if (this.connectionTimeout != null) {
            options.setConnectionTimeout(this.connectionTimeout.intValue());
        }

        if (this.keepAliveInterval != null) {
            options.setKeepAliveInterval(this.keepAliveInterval.intValue());
        }

        if (this.password != null) {
            options.setPassword(this.password.toCharArray());
        }

        if (this.socketFactory != null) {
            options.setSocketFactory(this.socketFactory);
        }

        if (this.sslProperties != null) {
            options.setSSLProperties(this.sslProperties);
        }

        if (this.userName != null) {
            options.setUserName(this.userName);
        }

        if (this.will != null) {
            options.setWill(this.will.getTopic(), this.will.getPayload(), this.will.getQos(), this.will.isRetained());
        }

        if (this.serverURIs != null) {
            options.setServerURIs(this.serverURIs);
        }

        return options;
    }

    @Override
    public ConsumerStopAction getConsumerStopAction() {
        return null;
    }

    public static class Will {
        private final String topic;
        private final byte[] payload;
        private final int qos;
        private final boolean retained;

        public Will(String topic, byte[] payload, int qos, boolean retained) {
            this.topic = topic;
            this.payload = payload;
            this.qos = qos;
            this.retained = retained;
        }

        protected String getTopic() {
            return this.topic;
        }

        protected byte[] getPayload() {
            return this.payload;
        }

        protected int getQos() {
            return this.qos;
        }

        protected boolean isRetained() {
            return this.retained;
        }
    }
}
