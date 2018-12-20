package net.explorviz.kiekeradapter.filter.teetime;

/***************************************************************************
 * Copyright (C) 2016 iObserve Project (https://www.iobserve-devops.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 ***************************************************************************/

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import kieker.common.record.io.BinaryValueDeserializer;
import kieker.common.record.io.IValueDeserializer;
import kieker.common.registry.reader.ReaderRegistry;

/**
 * Represents one connection to read records from.
 *
 * @author Reiner Jung
 *
 */
public class Connection {

  /** The connection channel for in and output. */
  private final SocketChannel channel;
  /** String registy wrapper. */
  private final ReaderRegistry<String> registry = new ReaderRegistry<>();
  /** Byte buffer for reading the channel. */
  private final ByteBuffer buffer;
  /** Deserializer setup. */
  private final IValueDeserializer deserializer;

  /**
   * Create a connection for the given channel and utilizing the specified buffer size.
   *
   * @param channel connection channel used to read data
   * @param bufferSize buffer size for reading information
   */
  public Connection(final SocketChannel channel, final int bufferSize) {
    this.channel = channel;
    this.buffer = ByteBuffer.allocateDirect(bufferSize);
    this.deserializer = BinaryValueDeserializer.create(this.buffer, this.registry);
  }

  public ReaderRegistry<String> getRegistry() {
    return this.registry;
  }

  public SocketChannel getChannel() {
    return this.channel;
  }

  public ByteBuffer getBuffer() {
    return this.buffer;
  }

  public IValueDeserializer getValueDeserializer() {
    return this.deserializer;
  }

}
