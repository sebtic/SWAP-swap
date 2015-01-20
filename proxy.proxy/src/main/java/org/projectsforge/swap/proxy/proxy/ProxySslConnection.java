/**
 * Copyright 2012 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr>
 * 
 * This software is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software. If not, see <http://www.gnu.org/licenses/>.
 * 
 * $Id$
 */
package org.projectsforge.swap.proxy.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.EofException;
import org.eclipse.jetty.io.nio.IndirectNIOBuffer;
import org.eclipse.jetty.io.nio.NIOBuffer;
import org.eclipse.jetty.io.nio.SslConnection;
import org.projectsforge.swap.proxy.certificate.CertificateTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ProxySslConnection.
 * 
 * @author Sébastien Aupetit
 */
public class ProxySslConnection extends SslConnection {

  /**
   * The Class ProxySslEndPoint.
   * 
   * @author Sébastien Aupetit
   */
  class ProxySslEndPoint extends SslEndPoint {
    
    /** The upgraded. */
    private boolean upgraded = false;

    /**
     * Extract input buffer.
     * 
     * @param buffer the buffer
     * @return the byte buffer
     */
    private ByteBuffer extractInputBuffer(final Buffer buffer) {
      assert buffer instanceof NIOBuffer;
      final NIOBuffer nbuf = (NIOBuffer) buffer;
      final ByteBuffer bbuf = nbuf.getByteBuffer();
      bbuf.position(buffer.putIndex());
      return bbuf;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jetty.io.nio.SslConnection.SslEndPoint#fill(org.eclipse.jetty.io.Buffer)
     */
    @Override
    public int fill(final Buffer buffer) throws IOException {

      try {
        if (upgraded) {
          return super.fill(buffer);
        } else {
          // wait for CONNECT

          final int filled = endp.fill(buffer);
          if (filled <= 0) {
            return filled;
          }
          final ByteBuffer bbuf = extractInputBuffer(buffer);
          bbuf.position(0);
          bbuf.limit(buffer.length());
          final StringBuilder sb = new StringBuilder();

          while (bbuf.hasRemaining()) {
            sb.append((char) bbuf.get());
          }
          final String content = sb.toString();
          if (content.startsWith("CONNECT ")) {
            final String target = content.substring("CONNECT ".length());
            final int index = target.indexOf(":");

            final String hostname = target.substring(0, index);
            final int spaceIndex = target.indexOf(' ', index + 1);
            int port;
            if (spaceIndex == -1) {
              port = 443;
            } else {
              port = Integer.parseInt(target.substring(index + 1, spaceIndex));
            }

            final CertificateTarget certificateTarget = new CertificateTarget(hostname, port);
            try {
              sslEngine.setHost(certificateTarget);
            } catch (final Exception e) {
              return -1;
            }
            logger.debug("Proxy CONNECT to {}", certificateTarget);
          } else {
            logger.error("SSL connection needed but no CONNECT received");
            bbuf.position(bbuf.position() - content.length());
            return super.fill(buffer);
          }

          buffer.setPutIndex(0);
          bbuf.position(0);
          bbuf.limit(bbuf.capacity());

          final NIOBuffer outNIOBuffer = new IndirectNIOBuffer(sslEngine.getSession()
              .getPacketBufferSize());
          final ByteBuffer out_buffer = outNIOBuffer.getByteBuffer();

          outNIOBuffer.compact();
          final int put = outNIOBuffer.putIndex();
          out_buffer.position();

          // fill response
          final String reply = "HTTP/1.0 200 Connection established\r\nProxy-agent: " + agentName
              + "/1.1\r\n\r\n";
          final byte[] replyBytes = reply.getBytes(Charset.forName("ASCII"));
          outNIOBuffer.clear();
          out_buffer.position(0);
          out_buffer.limit(out_buffer.capacity());
          out_buffer.put(replyBytes);
          outNIOBuffer.setGetIndex(0);
          outNIOBuffer.setPutIndex(put + replyBytes.length);

          endp.flush(outNIOBuffer);

          out_buffer.position(0);
          out_buffer.limit(out_buffer.capacity());

          upgraded = true;
          return super.fill(buffer);
        }
      } catch (final EofException t) {
        // solve bug JETTY-1377 (looping read on closed connection)
        return -1;
      }
    }
  }

  /** The logger. */
  private final Logger logger = LoggerFactory.getLogger(ProxySslConnection.class);

  /** The endp. */
  private final EndPoint endp;

  /** The ssl engine. */
  private final ProxySSLEngine sslEngine;

  /** The agent name. */
  private final String agentName;

  /**
   * Instantiates a new proxy ssl connection.
   * 
   * @param sslEngine the ssl engine
   * @param endp the endp
   * @param agentName the agent name
   */
  public ProxySslConnection(final ProxySSLEngine sslEngine, final EndPoint endp,
      final String agentName) {
    super(sslEngine, endp);
    this.endp = endp;
    this.sslEngine = sslEngine;
    this.agentName = agentName;
  }

  /* (non-Javadoc)
   * @see org.eclipse.jetty.io.nio.SslConnection#newSslEndPoint()
   */
  @Override
  protected SslEndPoint newSslEndPoint() {
    return new ProxySslEndPoint();
  }

}
