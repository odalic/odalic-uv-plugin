package eu.odalic.uv.dpu.transformer.odalic.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

/**
 * I/O utilities.
 * 
 * @author Václav Brodec
 *
 */
public final class Io {

  private static final int BUFFER_SIZE = 16 * 1024;

  public static void fastCopy(final InputStream source, final OutputStream destination) throws IOException {
    final ReadableByteChannel inputChannel = Channels.newChannel(source);
    final WritableByteChannel outputChannel = Channels.newChannel(destination);
    
    fastCopy(inputChannel, outputChannel);
  }

  public static void fastCopy(final ReadableByteChannel source, final WritableByteChannel destination)
      throws IOException {
    final ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

    while (source.read(buffer) != -1) {
      buffer.flip();
      destination.write(buffer);
      buffer.compact();
    }

    buffer.flip();

    while (buffer.hasRemaining()) {
      destination.write(buffer);
    }
  }

  public static InputStream toStream(final String string, final Charset charset) {
    return new ByteArrayInputStream(string.getBytes(charset));
  }
}
