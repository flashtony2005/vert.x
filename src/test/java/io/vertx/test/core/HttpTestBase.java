/*
 * Copyright (c) 2011-2014 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 *     The Eclipse Public License is available at
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 *     The Apache License v2.0 is available at
 *     http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */

package io.vertx.test.core;

import io.netty.handler.codec.http2.Http2CodecUtil;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.http.Http2Settings;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;

import java.util.concurrent.CountDownLatch;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class HttpTestBase extends VertxTestBase {

  public static final String DEFAULT_HTTP_HOST = "localhost";
  public static final int DEFAULT_HTTP_PORT = 8080;
  public static final String DEFAULT_TEST_URI = "some-uri";

  protected HttpServer server;
  protected HttpClient client;

  public void setUp() throws Exception {
    super.setUp();
    server = vertx.createHttpServer(new HttpServerOptions().setPort(DEFAULT_HTTP_PORT).setHost(DEFAULT_HTTP_HOST));
  }

  protected void tearDown() throws Exception {
    if (client != null) {
      try {
        client.close();
      } catch (IllegalStateException ignore) {
        // Client was already closed by the test
      }
    }
    if (server != null) {
      CountDownLatch latch = new CountDownLatch(1);
      server.close((asyncResult) -> {
        assertTrue(asyncResult.succeeded());
        latch.countDown();
      });
      awaitLatch(latch);
    }
    super.tearDown();
  }

  @SuppressWarnings("unchecked")
  protected <E> Handler<E> noOpHandler() {
    return noOp;
  }

  private static final Handler noOp = e -> {
  };

  protected void startServer() throws Exception {
    startServer(vertx.getOrCreateContext());
  }

  protected void startServer(Context context) throws Exception {
    CountDownLatch latch = new CountDownLatch(1);
    context.runOnContext(v -> {
      server.listen(onSuccess(s -> latch.countDown()));
    });
    awaitLatch(latch);
  }
}
