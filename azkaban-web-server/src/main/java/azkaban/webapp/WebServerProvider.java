/*
 * Copyright 2017 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package azkaban.webapp;



import azakban.Constants;
import azakban.utils.Props;
import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.List;

import static java.util.Objects.requireNonNull;


public class WebServerProvider implements Provider<Server> {

  private static final Logger logger = Logger.getLogger(WebServerProvider.class);
  private static final int MAX_HEADER_BUFFER_SIZE = 10 * 1024 * 1024;

  @Inject
  private Props props;

  @Override
  public Server get() {
      requireNonNull(this.props);
      final int maxThreads = this.props
            .getInt("jetty.maxThreads", Constants.DEFAULT_JETTY_MAX_THREAD_COUNT);
      final boolean useSsl = this.props.getBoolean("jetty.use.ssl", true);
      final int port;
      final QueuedThreadPool queuedThreadPool = createThreadPool();
      final Server server = new Server(queuedThreadPool);
      final HttpConfiguration httpConfiguration = new HttpConfiguration();
      httpConfiguration.setSecurePort(8443);
      httpConfiguration.setSecureScheme("https");
      if (useSsl) {
          final int sslPortNumber = this.props
              .getInt("jetty.ssl.port", Constants.DEFAULT_SSL_PORT_NUMBER);
          port = sslPortNumber;
          HttpConfiguration httpsConfiguration = new HttpConfiguration(httpConfiguration);
          SecureRequestCustomizer secureRequestCustomizer = new SecureRequestCustomizer();
          secureRequestCustomizer.setStsMaxAge(2000);
          secureRequestCustomizer.setStsIncludeSubDomains(true);
          httpsConfiguration.addCustomizer(secureRequestCustomizer);
          server.addConnector(getSslSocketConnector(server, sslPortNumber, httpsConfiguration));
      } else {
          port = this.props.getInt("jetty.port", Constants.DEFAULT_PORT_NUMBER);
          server.addConnector(getSocketConnector(server, port, httpConfiguration));
      }



        // setting stats configuration for connectors
        //setStatsOnConnectors(server);

      logger.info(String.format(
            "Starting %sserver on port: %d # Max threads: %d", useSsl ? "SSL " : "", port, maxThreads));
      return server;
  }

//  private void setStatsOnConnectors(final Server server) {
//    final boolean isStatsOn = this.props.getBoolean("jetty.connector.stats", true);
//    logger.info("Setting up connector with stats on: " + isStatsOn);
//    for (final Connector connector : server.getConnectors()) {
//      connector.setStatsOn(isStatsOn);
//    }
//  }

    private QueuedThreadPool createThreadPool() {
        final int maxThreads = this.props.getInt("jetty.maxThreads", Constants.DEFAULT_JETTY_MAX_THREAD_COUNT);
        return new QueuedThreadPool(maxThreads);
    }

    private ServerConnector getSocketConnector(final Server server, final int port, final HttpConfiguration httpConfiguration) {
        final ServerConnector connector = new ServerConnector(server,
                new HttpConnectionFactory(httpConfiguration));
        connector.setPort(port);
        connector.setIdleTimeout(30000);
        //connector.setHeaderBufferSize(MAX_HEADER_BUFFER_SIZE);
        return connector;
    }

    private ServerConnector getSslSocketConnector(final Server server, final int sslPortNumber, final HttpConfiguration httpsConfiguration) {
        final SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(this.props.getString("jetty.keystore"));
        sslContextFactory.setKeyStorePassword(this.props.getString("jetty.password"));
        sslContextFactory.setKeyManagerPassword(this.props.getString("jetty.keypassword"));
        sslContextFactory.setTrustStorePath(this.props.getString("jetty.truststore"));
        sslContextFactory.setTrustStorePassword(this.props.getString("jetty.trustpassword"));

        // set up vulnerable cipher suites to exclude
        final List<String> cipherSuitesToExclude = this.props
            .getStringList("jetty.excludeCipherSuites");
        logger.info("Excluded Cipher Suites: " + String.valueOf(cipherSuitesToExclude));
        if (cipherSuitesToExclude != null && !cipherSuitesToExclude.isEmpty()) {
            sslContextFactory.setExcludeCipherSuites(cipherSuitesToExclude.toArray(new String[0]));
        }

        final ServerConnector secureConnector = new ServerConnector(server,
                new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
                new HttpConnectionFactory(httpsConfiguration));
        secureConnector.setPort(sslPortNumber);
        //secureConnector.setHeaderBufferSize(MAX_HEADER_BUFFER_SIZE);
        secureConnector.setIdleTimeout(50000);
        return secureConnector;
     }
}
