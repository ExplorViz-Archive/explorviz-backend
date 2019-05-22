package net.explorviz.analysis.live_trace_processing.reader;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import net.explorviz.common.live_trace_processing.IdNotAvailableException;
import net.explorviz.common.live_trace_processing.StringRegistryReceiver;
import net.explorviz.common.live_trace_processing.filter.PipesMerger;
import net.explorviz.common.live_trace_processing.record.IRecord;
import net.explorviz.common.live_trace_processing.record.event.AbstractAfterEventRecord;
import net.explorviz.common.live_trace_processing.record.event.AbstractAfterFailedEventRecord;
import net.explorviz.common.live_trace_processing.record.event.AbstractBeforeOperationEventRecord;
import net.explorviz.common.live_trace_processing.record.event.constructor.AfterConstructorEventRecord;
import net.explorviz.common.live_trace_processing.record.event.constructor.AfterFailedConstructorEventRecord;
import net.explorviz.common.live_trace_processing.record.event.constructor.BeforeConstructorEventRecord;
import net.explorviz.common.live_trace_processing.record.event.jdbc.AfterFailedJDBCOperationEventRecord;
import net.explorviz.common.live_trace_processing.record.event.jdbc.AfterJDBCOperationEventRecord;
import net.explorviz.common.live_trace_processing.record.event.jdbc.BeforeJDBCOperationEventRecord;
import net.explorviz.common.live_trace_processing.record.event.normal.AfterFailedOperationEventRecord;
import net.explorviz.common.live_trace_processing.record.event.normal.AfterOperationEventRecord;
import net.explorviz.common.live_trace_processing.record.event.normal.BeforeOperationEventRecord;
import net.explorviz.common.live_trace_processing.record.event.remote.AfterReceivedRemoteCallRecord;
import net.explorviz.common.live_trace_processing.record.event.remote.AfterSentRemoteCallRecord;
import net.explorviz.common.live_trace_processing.record.event.remote.AfterUnknownReceivedRemoteCallRecord;
import net.explorviz.common.live_trace_processing.record.event.remote.BeforeReceivedRemoteCallRecord;
import net.explorviz.common.live_trace_processing.record.event.remote.BeforeSentRemoteCallRecord;
import net.explorviz.common.live_trace_processing.record.event.remote.BeforeUnknownReceivedRemoteCallRecord;
import net.explorviz.common.live_trace_processing.record.event.statics.AfterFailedStaticOperationEventRecord;
import net.explorviz.common.live_trace_processing.record.event.statics.AfterStaticOperationEventRecord;
import net.explorviz.common.live_trace_processing.record.event.statics.BeforeStaticOperationEventRecord;
import net.explorviz.common.live_trace_processing.record.misc.StringRegistryRecord;
import net.explorviz.common.live_trace_processing.record.misc.SystemMonitoringRecord;
import net.explorviz.common.live_trace_processing.record.trace.HostApplicationMetaDataRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TCPReaderOneClient implements Runnable {

  private static final Logger LOG = LoggerFactory.getLogger(TCPReaderOneClient.class);

  private HostApplicationMetaDataRecord hostApplicationMetadata;

  private final StringRegistryReceiver stringRegistry = new StringRegistryReceiver();
  private final List<byte[]> waitingForStringMessages = new ArrayList<>(32);

  private final SocketChannel socketChannel;

  private final Queue<IRecord> queue;
  private final PipesMerger<IRecord> merger;

  public TCPReaderOneClient(final SocketChannel socketChannel, final PipesMerger<IRecord> merger) {
    this.socketChannel = socketChannel;
    this.merger = merger;
    this.queue = merger.registerProducer();
  }

  @Override
  public void run() {
    final ByteBuffer buffer = ByteBuffer.allocate(256 * 1024);
    String remoteAddress = "";
    try {
      if (this.socketChannel.isConnected()) {
        remoteAddress = ((InetSocketAddress) this.socketChannel.getRemoteAddress()).getHostName();
        LOG.info("Client " + remoteAddress + " connected.");
      }

      // RemoteConfigurationServlet.getConnectedChildren().add(remoteAddress);

      // sendAdaptiveMonitoringList();

      while (this.socketChannel.read(buffer) != -1) {
        buffer.flip();
        this.messagesfromByteArray(buffer);
      }
    } catch (final IOException ex) {
      // RemoteConfigurationServlet.getConnectedChildren().remove(remoteAddress);
      LOG.info("Error in read() " + ex.getMessage());
    } finally {
      LOG.info("Client " + remoteAddress + " disconnected.");
      this.merger.deregisterProducer(this.queue);
    }
  }

  // private void sendAdaptiveMonitoringList() {
  // final ConcurrentHashMap<String, Set<String>> patternMap =
  // AdaptiveMonitoringPatternList
  // .getApplicationToPatternMap();
  // for (final Entry<String, Set<String>> entry : patternMap.entrySet()) {
  // for (final String pattern : entry.getValue()) {
  // RemoteConfigurator.addPattern(remoteAddress, pattern, entry.getKey());
  // }
  // }
  // }

  private final void messagesfromByteArray(final ByteBuffer buffer) {
    while (buffer.remaining() > 0) {
      final byte clazzId = buffer.get();
      switch (clazzId) {
        case HostApplicationMetaDataRecord.CLAZZ_ID: {
          if (buffer.remaining() >= HostApplicationMetaDataRecord.BYTE_LENGTH) {
            this.readInHostApplicationMetaData(buffer);
            break;
          }
          buffer.position(buffer.position() - 1);
          buffer.compact();
          return;
        }
        case BeforeOperationEventRecord.CLAZZ_ID: {
          if (buffer.remaining() >= AbstractBeforeOperationEventRecord.COMPRESSED_BYTE_LENGTH) {
            this.readInBeforeOperationEvent(buffer);
            break;
          }
          buffer.position(buffer.position() - 1);
          buffer.compact();
          return;
        }
        case AfterFailedOperationEventRecord.CLAZZ_ID: {
          if (buffer.remaining() >= AbstractAfterFailedEventRecord.COMPRESSED_BYTE_LENGTH) {
            this.readInAfterFailedOperationEvent(buffer);
            break;
          }
          buffer.position(buffer.position() - 1);
          buffer.compact();
          return;

        }
        case AfterOperationEventRecord.CLAZZ_ID: {
          if (buffer.remaining() >= AbstractAfterEventRecord.COMPRESSED_BYTE_LENGTH) {
            this.readInAfterOperationEvent(buffer);
            break;
          }
          buffer.position(buffer.position() - 1);
          buffer.compact();
          return;
        }
        case StringRegistryRecord.CLAZZ_ID: {
          int mapId = 0;
          int stringLength = 0;
          if (buffer.remaining() >= 8) {
            mapId = buffer.getInt();
            stringLength = buffer.getInt();
          } else {
            buffer.position(buffer.position() - 1);
            buffer.compact();
            return;
          }

          if (buffer.remaining() >= stringLength) {
            final byte[] stringByteArray = new byte[stringLength];

            buffer.get(stringByteArray);

            this.stringRegistry.putStringRecord(mapId, new String(stringByteArray));

            this.checkWaitingMessages();
          } else {
            buffer.position(buffer.position() - 9);
            buffer.compact();
            return;
          }
          break;
        }
        case SystemMonitoringRecord.CLAZZ_ID: {
          if (buffer.remaining() >= SystemMonitoringRecord.COMPRESSED_BYTE_LENGTH) {
            this.readInSystemMonitoringRecord(buffer);
            break;
          }
          buffer.position(buffer.position() - 1);
          buffer.compact();
          return;
        }
        case BeforeConstructorEventRecord.CLAZZ_ID: {
          if (buffer.remaining() >= AbstractBeforeOperationEventRecord.COMPRESSED_BYTE_LENGTH) {
            this.readInBeforeConstructorEvent(buffer);
            break;
          }
          buffer.position(buffer.position() - 1);
          buffer.compact();
          return;
        }
        case AfterFailedConstructorEventRecord.CLAZZ_ID: {
          if (buffer.remaining() >= AbstractAfterFailedEventRecord.COMPRESSED_BYTE_LENGTH) {
            this.readInAfterFailedConstructorEvent(buffer);
            break;
          }
          buffer.position(buffer.position() - 1);
          buffer.compact();
          return;
        }
        case AfterConstructorEventRecord.CLAZZ_ID: {
          if (buffer.remaining() >= AbstractAfterEventRecord.COMPRESSED_BYTE_LENGTH) {
            this.readInAfterConstructorEvent(buffer);
            break;
          }
          buffer.position(buffer.position() - 1);
          buffer.compact();
          return;
        }
        case BeforeReceivedRemoteCallRecord.CLAZZ_ID: {
          if (buffer.remaining() >= BeforeReceivedRemoteCallRecord.COMPRESSED_BYTE_LENGTH) {
            this.readInBeforeReceivedRemoteCallEvent(buffer);
            break;
          }
          buffer.position(buffer.position() - 1);
          buffer.compact();
          return;
        }
        case BeforeStaticOperationEventRecord.CLAZZ_ID: {
          if (buffer.remaining() >= BeforeStaticOperationEventRecord.COMPRESSED_BYTE_LENGTH) {
            this.readInBeforeStaticOperationEvent(buffer);
            break;
          }
          buffer.position(buffer.position() - 1);
          buffer.compact();
          return;
        }
        case AfterFailedStaticOperationEventRecord.CLAZZ_ID: {
          if (buffer.remaining() >= AbstractAfterFailedEventRecord.COMPRESSED_BYTE_LENGTH) {
            this.readInAfterFailedStaticOperationEvent(buffer);
            break;
          }
          buffer.position(buffer.position() - 1);
          buffer.compact();
          return;
        }
        case AfterStaticOperationEventRecord.CLAZZ_ID: {
          if (buffer.remaining() >= AbstractAfterEventRecord.COMPRESSED_BYTE_LENGTH) {
            this.readInAfterStaticOperationEvent(buffer);
            break;
          }
          buffer.position(buffer.position() - 1);
          buffer.compact();
          return;
        }
        case BeforeSentRemoteCallRecord.CLAZZ_ID: {
          if (buffer.remaining() >= BeforeSentRemoteCallRecord.COMPRESSED_BYTE_LENGTH) {
            this.readInBeforeSentRemoteCallEvent(buffer);
            break;
          }
          buffer.position(buffer.position() - 1);
          buffer.compact();
          return;
        }
        case AfterSentRemoteCallRecord.CLAZZ_ID: {
          if (buffer.remaining() >= AfterSentRemoteCallRecord.COMPRESSED_BYTE_LENGTH) {
            this.readInAfterSentRemoteCallEvent(buffer);
            break;
          }
          buffer.position(buffer.position() - 1);
          buffer.compact();
          return;
        }
        case BeforeUnknownReceivedRemoteCallRecord.CLAZZ_ID: {
          if (buffer.remaining() >= BeforeUnknownReceivedRemoteCallRecord.COMPRESSED_BYTE_LENGTH) {
            this.readInBeforeUnknownReceivedRemoteCallEvent(buffer);
            break;
          }
          buffer.position(buffer.position() - 1);
          buffer.compact();
          return;
        }
        case AfterUnknownReceivedRemoteCallRecord.CLAZZ_ID: {
          if (buffer.remaining() >= AfterUnknownReceivedRemoteCallRecord.COMPRESSED_BYTE_LENGTH) {
            this.readInAfterUnknownReceivedRemoteCallEvent(buffer);
            break;
          }
          buffer.position(buffer.position() - 1);
          buffer.compact();
          return;
        }
        case AfterReceivedRemoteCallRecord.CLAZZ_ID: {
          if (buffer.remaining() >= AfterReceivedRemoteCallRecord.COMPRESSED_BYTE_LENGTH) {
            this.readInAfterReceivedRemoteCallEvent(buffer);
            break;
          }
          buffer.position(buffer.position() - 1);
          buffer.compact();
          return;
        }
        case BeforeJDBCOperationEventRecord.CLAZZ_ID: {
          if (buffer.remaining() >= BeforeJDBCOperationEventRecord.COMPRESSED_BYTE_LENGTH) {
            this.readInBeforeJDBCOperationEvent(buffer);
            break;
          }
          buffer.position(buffer.position() - 1);
          buffer.compact();
          return;
        }
        case AfterFailedJDBCOperationEventRecord.CLAZZ_ID: {
          if (buffer.remaining() >= AbstractAfterFailedEventRecord.COMPRESSED_BYTE_LENGTH) {
            this.readInAfterFailedJDBCOperationEvent(buffer);
            break;
          }
          buffer.position(buffer.position() - 1);
          buffer.compact();
          return;

        }
        case AfterJDBCOperationEventRecord.CLAZZ_ID: {
          if (buffer.remaining() >= AfterJDBCOperationEventRecord.COMPRESSED_BYTE_LENGTH) {
            this.readInAfterJDBCOperationEvent(buffer);
            break;
          }
          buffer.position(buffer.position() - 1);
          buffer.compact();
          return;
        }
        case BeforeOperationEventRecord.CLAZZ_ID_FROM_WORKER: {
          if (buffer.remaining() >= 4) {
            final int recordSize = buffer.getInt();

            if (buffer.remaining() >= recordSize) {
              try {
                final BeforeOperationEventRecord beforeOperationEventRecord =
                    new BeforeOperationEventRecord(buffer, this.stringRegistry);
                this.putInQueue(beforeOperationEventRecord);
              } catch (final IdNotAvailableException e) {
                // should not happen
                e.printStackTrace();
                this.forcefullyDisconnect();
              }
              break;
            } else {
              buffer.position(buffer.position() - 1 - 4);
              buffer.compact();
              return;
            }
          }
          buffer.position(buffer.position() - 1);
          buffer.compact();
          return;
        }
        case AfterFailedOperationEventRecord.CLAZZ_ID_FROM_WORKER: {
          if (buffer.remaining() >= 4) {
            final int recordSize = buffer.getInt();

            if (buffer.remaining() >= recordSize) {
              try {
                this.putInQueue(new AfterFailedOperationEventRecord(buffer, this.stringRegistry));
              } catch (final IdNotAvailableException e) {
                // should not happen
                e.printStackTrace();
                this.forcefullyDisconnect();
              }
              break;
            } else {
              buffer.position(buffer.position() - 1 - 4);
              buffer.compact();
              return;
            }
          }
          buffer.position(buffer.position() - 1);
          buffer.compact();
          return;
        }
        case AfterOperationEventRecord.CLAZZ_ID_FROM_WORKER: {
          if (buffer.remaining() >= 4) {
            final int recordSize = buffer.getInt();

            if (buffer.remaining() >= recordSize) {
              try {
                this.putInQueue(new AfterOperationEventRecord(buffer, this.stringRegistry));
              } catch (final IdNotAvailableException e) {
                // should not happen
                e.printStackTrace();
                this.forcefullyDisconnect();
              }
              break;
            } else {
              buffer.position(buffer.position() - 1 - 4);
              buffer.compact();
              return;
            }
          }
          buffer.position(buffer.position() - 1);
          buffer.compact();
          return;
        }
        case SystemMonitoringRecord.CLAZZ_ID_FROM_WORKER: {
          if (buffer.remaining() >= SystemMonitoringRecord.BYTE_LENGTH) {
            try {
              this.putInQueue(
                  SystemMonitoringRecord.createFromByteBuffer(buffer, this.stringRegistry));
            } catch (final IdNotAvailableException e) {
              // should not happen
              e.printStackTrace();
              this.forcefullyDisconnect();
            }
            break;
          }
          buffer.position(buffer.position() - 1);
          buffer.compact();
          return;
        }
        case BeforeConstructorEventRecord.CLAZZ_ID_FROM_WORKER: {
          if (buffer.remaining() >= 4) {
            final int recordSize = buffer.getInt();

            if (buffer.remaining() >= recordSize) {
              try {
                this.putInQueue(new BeforeConstructorEventRecord(buffer, this.stringRegistry));
              } catch (final IdNotAvailableException e) {
                // should not happen
                e.printStackTrace();
                this.forcefullyDisconnect();
              }
              break;
            } else {
              buffer.position(buffer.position() - 1 - 4);
              buffer.compact();
              return;
            }
          }
          buffer.position(buffer.position() - 1);
          buffer.compact();
          return;
        }
        case AfterFailedConstructorEventRecord.CLAZZ_ID_FROM_WORKER: {
          if (buffer.remaining() >= 4) {
            final int recordSize = buffer.getInt();

            if (buffer.remaining() >= recordSize) {
              try {
                this.putInQueue(new AfterFailedConstructorEventRecord(buffer, this.stringRegistry));
              } catch (final IdNotAvailableException e) {
                // should not happen
                e.printStackTrace();
                this.forcefullyDisconnect();
              }
              break;
            } else {
              buffer.position(buffer.position() - 1 - 4);
              buffer.compact();
              return;
            }
          }
          buffer.position(buffer.position() - 1);
          buffer.compact();
          return;
        }
        case AfterConstructorEventRecord.CLAZZ_ID_FROM_WORKER: {
          if (buffer.remaining() >= 4) {
            final int recordSize = buffer.getInt();

            if (buffer.remaining() >= recordSize) {
              try {
                this.putInQueue(new AfterConstructorEventRecord(buffer, this.stringRegistry));
              } catch (final IdNotAvailableException e) {
                // should not happen
                e.printStackTrace();
                this.forcefullyDisconnect();
              }
              break;
            } else {
              buffer.position(buffer.position() - 1 - 4);
              buffer.compact();
              return;
            }
          }
          buffer.position(buffer.position() - 1);
          buffer.compact();
          return;
        }
        case BeforeReceivedRemoteCallRecord.CLAZZ_ID_FROM_WORKER: {
          if (buffer.remaining() >= 4) {
            final int recordSize = buffer.getInt();

            if (buffer.remaining() >= recordSize) {
              try {
                this.putInQueue(new BeforeReceivedRemoteCallRecord(buffer, this.stringRegistry));
              } catch (final IdNotAvailableException e) {
                // should not happen
                e.printStackTrace();
                this.forcefullyDisconnect();
              }
              break;
            } else {
              buffer.position(buffer.position() - 1 - 4);
              buffer.compact();
              return;
            }
          }
          buffer.position(buffer.position() - 1);
          buffer.compact();
          return;
        }
        case BeforeStaticOperationEventRecord.CLAZZ_ID_FROM_WORKER: {
          if (buffer.remaining() >= 4) {
            final int recordSize = buffer.getInt();

            if (buffer.remaining() >= recordSize) {
              try {
                this.putInQueue(new BeforeStaticOperationEventRecord(buffer, this.stringRegistry));
              } catch (final IdNotAvailableException e) {
                // should not happen
                e.printStackTrace();
                this.forcefullyDisconnect();
              }
              break;
            } else {
              buffer.position(buffer.position() - 1 - 4);
              buffer.compact();
              return;
            }
          }
          buffer.position(buffer.position() - 1);
          buffer.compact();
          return;
        }
        case AfterFailedStaticOperationEventRecord.CLAZZ_ID_FROM_WORKER: {
          if (buffer.remaining() >= 4) {
            final int recordSize = buffer.getInt();

            if (buffer.remaining() >= recordSize) {
              try {
                this.putInQueue(
                    new AfterFailedStaticOperationEventRecord(buffer, this.stringRegistry));
              } catch (final IdNotAvailableException e) {
                // should not happen
                e.printStackTrace();
                this.forcefullyDisconnect();
              }
              break;
            } else {
              buffer.position(buffer.position() - 1 - 4);
              buffer.compact();
              return;
            }
          }
          buffer.position(buffer.position() - 1);
          buffer.compact();
          return;
        }
        case AfterStaticOperationEventRecord.CLAZZ_ID_FROM_WORKER: {
          if (buffer.remaining() >= 4) {
            final int recordSize = buffer.getInt();

            if (buffer.remaining() >= recordSize) {
              try {
                this.putInQueue(new AfterStaticOperationEventRecord(buffer, this.stringRegistry));
              } catch (final IdNotAvailableException e) {
                // should not happen
                e.printStackTrace();
                this.forcefullyDisconnect();
              }
              break;
            } else {
              buffer.position(buffer.position() - 1 - 4);
              buffer.compact();
              return;
            }
          }
          buffer.position(buffer.position() - 1);
          buffer.compact();
          return;
        }
        case BeforeSentRemoteCallRecord.CLAZZ_ID_FROM_WORKER: {
          if (buffer.remaining() >= 4) {
            final int recordSize = buffer.getInt();

            if (buffer.remaining() >= recordSize) {
              try {
                this.putInQueue(new BeforeSentRemoteCallRecord(buffer, this.stringRegistry));
              } catch (final IdNotAvailableException e) {
                // should not happen
                e.printStackTrace();
                this.forcefullyDisconnect();
              }
              break;
            } else {
              buffer.position(buffer.position() - 1 - 4);
              buffer.compact();
              return;
            }
          }
          buffer.position(buffer.position() - 1);
          buffer.compact();
          return;
        }
        case AfterSentRemoteCallRecord.CLAZZ_ID_FROM_WORKER: {
          if (buffer.remaining() >= 4) {
            final int recordSize = buffer.getInt();

            if (buffer.remaining() >= recordSize) {
              try {
                this.putInQueue(new AfterSentRemoteCallRecord(buffer, this.stringRegistry));
              } catch (final IdNotAvailableException e) {
                // should not happen
                e.printStackTrace();
                this.forcefullyDisconnect();
              }
              break;
            } else {
              buffer.position(buffer.position() - 1 - 4);
              buffer.compact();
              return;
            }
          }
          buffer.position(buffer.position() - 1);
          buffer.compact();
          return;
        }
        case BeforeUnknownReceivedRemoteCallRecord.CLAZZ_ID_FROM_WORKER: {
          if (buffer.remaining() >= 4) {
            final int recordSize = buffer.getInt();

            if (buffer.remaining() >= recordSize) {
              try {
                this.putInQueue(
                    new BeforeUnknownReceivedRemoteCallRecord(buffer, this.stringRegistry));
              } catch (final IdNotAvailableException e) {
                // should not happen
                e.printStackTrace();
                this.forcefullyDisconnect();
              }
              break;
            } else {
              buffer.position(buffer.position() - 1 - 4);
              buffer.compact();
              return;
            }
          }
          buffer.position(buffer.position() - 1);
          buffer.compact();
          return;
        }
        case AfterUnknownReceivedRemoteCallRecord.CLAZZ_ID_FROM_WORKER: {
          if (buffer.remaining() >= 4) {
            final int recordSize = buffer.getInt();

            if (buffer.remaining() >= recordSize) {
              try {
                this.putInQueue(
                    new AfterUnknownReceivedRemoteCallRecord(buffer, this.stringRegistry));
              } catch (final IdNotAvailableException e) {
                // should not happen
                e.printStackTrace();
                this.forcefullyDisconnect();
              }
              break;
            } else {
              buffer.position(buffer.position() - 1 - 4);
              buffer.compact();
              return;
            }
          }
          buffer.position(buffer.position() - 1);
          buffer.compact();
          return;
        }
        case AfterReceivedRemoteCallRecord.CLAZZ_ID_FROM_WORKER: {
          if (buffer.remaining() >= 4) {
            final int recordSize = buffer.getInt();

            if (buffer.remaining() >= recordSize) {
              try {
                this.putInQueue(new AfterReceivedRemoteCallRecord(buffer, this.stringRegistry));
              } catch (final IdNotAvailableException e) {
                // should not happen
                e.printStackTrace();
                this.forcefullyDisconnect();
              }
              break;
            } else {
              buffer.position(buffer.position() - 1 - 4);
              buffer.compact();
              return;
            }
          }
          buffer.position(buffer.position() - 1);
          buffer.compact();
          return;
        }
        default: {
          System.out
              .println("unknown class id " + clazzId + " at offset " + (buffer.position() - 1));
          buffer.clear();
          return;
        }
      }
    }

    buffer.clear();
  }

  private final void readInHostApplicationMetaData(final ByteBuffer buffer) {
    final int systemnameId = buffer.getInt();
    final int ipaddressId = buffer.getInt();
    final int hostnameId = buffer.getInt();
    final int applicationId = buffer.getInt();
    final int languageId = buffer.getInt();

    try {
      final String systemname = this.stringRegistry.getStringFromId(systemnameId);
      final String ipaddress = this.stringRegistry.getStringFromId(ipaddressId);
      final String hostname = this.stringRegistry.getStringFromId(hostnameId);
      final String application = this.stringRegistry.getStringFromId(applicationId);
      final String language = this.stringRegistry.getStringFromId(languageId);

      this.hostApplicationMetadata =
          new HostApplicationMetaDataRecord(systemname, ipaddress, hostname, application, language);
    } catch (final IdNotAvailableException e) {
      this.putInWaitingMessages(buffer, HostApplicationMetaDataRecord.BYTE_LENGTH_WITH_CLAZZ_ID);
    }
  }

  private final void readInBeforeOperationEvent(final ByteBuffer buffer) {
    final long traceId = buffer.getLong();
    final int orderIndex = buffer.getInt();
    final int objectId = buffer.getInt();
    final int operationId = buffer.getInt();
    final int clazzId = buffer.getInt();
    final int interfaceId = buffer.getInt();

    try {
      final String operation = this.stringRegistry.getStringFromId(operationId);
      final String clazz = this.stringRegistry.getStringFromId(clazzId);
      final String implementedInterface = this.stringRegistry.getStringFromId(interfaceId);

      this.putInQueue(new BeforeOperationEventRecord(traceId, orderIndex, objectId, operation,
          clazz, implementedInterface, this.hostApplicationMetadata));
    } catch (final IdNotAvailableException e) {
      this.putInWaitingMessages(buffer,
          AbstractBeforeOperationEventRecord.COMPRESSED_BYTE_LENGTH + 1);
    }
  }

  private final void readInAfterFailedOperationEvent(final ByteBuffer buffer) {
    final long timestamp = buffer.getLong();
    final long traceId = buffer.getLong();
    final int orderIndex = buffer.getInt();
    final int causeId = buffer.getInt();

    try {
      final String cause = this.stringRegistry.getStringFromId(causeId);

      this.putInQueue(new AfterFailedOperationEventRecord(timestamp, traceId, orderIndex, cause,
          this.hostApplicationMetadata));
    } catch (final IdNotAvailableException e) {
      this.putInWaitingMessages(buffer,
          AbstractAfterFailedEventRecord.COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID);
    }
  }

  private final void readInAfterOperationEvent(final ByteBuffer buffer) {
    final long timestamp = buffer.getLong();
    final long traceId = buffer.getLong();
    final int orderIndex = buffer.getInt();
    this.putInQueue(new AfterOperationEventRecord(timestamp, traceId, orderIndex,
        this.hostApplicationMetadata));
  }

  private final void readInSystemMonitoringRecord(final ByteBuffer buffer) {
    final double cpuUtil = buffer.getDouble();
    final long usedRAM = buffer.getLong();
    final long absoluteRAM = buffer.getLong();

    if (this.hostApplicationMetadata != null) {
      this.putInQueue(
          new SystemMonitoringRecord(cpuUtil, usedRAM, absoluteRAM, this.hostApplicationMetadata));
    }
  }

  private final void readInBeforeConstructorEvent(final ByteBuffer buffer) {
    final long traceId = buffer.getLong();
    final int orderIndex = buffer.getInt();
    final int objectId = buffer.getInt();
    final int operationId = buffer.getInt();
    final int clazzId = buffer.getInt();
    final int interfaceId = buffer.getInt();

    try {
      final String operation = this.stringRegistry.getStringFromId(operationId);
      final String clazz = this.stringRegistry.getStringFromId(clazzId);
      final String implementedInterface = this.stringRegistry.getStringFromId(interfaceId);

      this.putInQueue(new BeforeConstructorEventRecord(traceId, orderIndex, objectId, operation,
          clazz, implementedInterface, this.hostApplicationMetadata));
    } catch (final IdNotAvailableException e) {
      this.putInWaitingMessages(buffer,
          AbstractBeforeOperationEventRecord.COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID);
    }
  }

  private final void readInAfterFailedConstructorEvent(final ByteBuffer buffer) {
    final long timestamp = buffer.getLong();
    final long traceId = buffer.getLong();
    final int orderIndex = buffer.getInt();
    final int causeId = buffer.getInt();

    try {
      this.putInQueue(new AfterFailedConstructorEventRecord(timestamp, traceId, orderIndex,
          this.stringRegistry.getStringFromId(causeId), this.hostApplicationMetadata));
    } catch (final IdNotAvailableException e) {
      this.putInWaitingMessages(buffer,
          AbstractAfterFailedEventRecord.COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID);
    }
  }

  private final void readInAfterConstructorEvent(final ByteBuffer buffer) {
    final long timestamp = buffer.getLong();
    final long traceId = buffer.getLong();
    final int orderIndex = buffer.getInt();

    this.putInQueue(new AfterConstructorEventRecord(timestamp, traceId, orderIndex,
        this.hostApplicationMetadata));
  }

  private final void readInBeforeReceivedRemoteCallEvent(final ByteBuffer buffer) {
    final long callerTraceId = buffer.getLong();
    final int callerOrderIndex = buffer.getInt();

    final long traceId = buffer.getLong();
    final int orderIndex = buffer.getInt();

    this.putInQueue(new BeforeReceivedRemoteCallRecord(callerTraceId, callerOrderIndex, traceId,
        orderIndex, this.hostApplicationMetadata));
  }

  private final void readInBeforeStaticOperationEvent(final ByteBuffer buffer) {
    final long traceId = buffer.getLong();
    final int orderIndex = buffer.getInt();
    final int operationId = buffer.getInt();
    final int clazzId = buffer.getInt();
    final int interfaceId = buffer.getInt();

    try {
      final String operation = this.stringRegistry.getStringFromId(operationId);
      final String clazz = this.stringRegistry.getStringFromId(clazzId);
      final String implementedInterface = this.stringRegistry.getStringFromId(interfaceId);

      this.putInQueue(new BeforeStaticOperationEventRecord(traceId, orderIndex, operation, clazz,
          implementedInterface, this.hostApplicationMetadata));
    } catch (final IdNotAvailableException e) {
      this.putInWaitingMessages(buffer,
          BeforeStaticOperationEventRecord.COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID);
    }
  }

  private final void readInAfterFailedStaticOperationEvent(final ByteBuffer buffer) {
    final long timestamp = buffer.getLong();
    final long traceId = buffer.getLong();
    final int orderIndex = buffer.getInt();
    final int causeId = buffer.getInt();

    try {
      final String cause = this.stringRegistry.getStringFromId(causeId);

      this.putInQueue(new AfterFailedStaticOperationEventRecord(timestamp, traceId, orderIndex,
          cause, this.hostApplicationMetadata));
    } catch (final IdNotAvailableException e) {
      this.putInWaitingMessages(buffer,
          AbstractAfterFailedEventRecord.COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID);
    }
  }

  private final void readInAfterStaticOperationEvent(final ByteBuffer buffer) {
    final long timestamp = buffer.getLong();
    final long traceId = buffer.getLong();
    final int orderIndex = buffer.getInt();

    this.putInQueue(new AfterStaticOperationEventRecord(timestamp, traceId, orderIndex,
        this.hostApplicationMetadata));
  }

  private final void readInBeforeSentRemoteCallEvent(final ByteBuffer buffer) {
    final long traceId = buffer.getLong();
    final int orderIndex = buffer.getInt();
    final int technologyId = buffer.getInt();

    try {
      this.putInQueue(
          new BeforeSentRemoteCallRecord(this.stringRegistry.getStringFromId(technologyId), traceId,
              orderIndex, this.hostApplicationMetadata));
    } catch (final IdNotAvailableException e) {
      this.putInWaitingMessages(buffer,
          BeforeSentRemoteCallRecord.COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID);
    }
  }

  private final void readInAfterSentRemoteCallEvent(final ByteBuffer buffer) {
    final long timestamp = buffer.getLong();
    final long traceId = buffer.getLong();
    final int orderIndex = buffer.getInt();

    this.putInQueue(new AfterSentRemoteCallRecord(timestamp, traceId, orderIndex,
        this.hostApplicationMetadata));

  }

  private final void readInBeforeUnknownReceivedRemoteCallEvent(final ByteBuffer buffer) {
    final long traceId = buffer.getLong();
    final int orderIndex = buffer.getInt();

    final int senderId = buffer.getInt();
    final int destionationId = buffer.getInt();

    try {
      this.putInQueue(
          new BeforeUnknownReceivedRemoteCallRecord(this.stringRegistry.getStringFromId(senderId),
              this.stringRegistry.getStringFromId(destionationId), traceId, orderIndex,
              this.hostApplicationMetadata));
    } catch (final IdNotAvailableException e) {
      this.putInWaitingMessages(buffer,
          BeforeUnknownReceivedRemoteCallRecord.COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID);
    }
  }

  private final void readInAfterUnknownReceivedRemoteCallEvent(final ByteBuffer buffer) {
    final long timestamp = buffer.getLong();
    final long traceId = buffer.getLong();
    final int orderIndex = buffer.getInt();

    this.putInQueue(new AfterUnknownReceivedRemoteCallRecord(timestamp, traceId, orderIndex,
        this.hostApplicationMetadata));

  }

  private final void readInAfterReceivedRemoteCallEvent(final ByteBuffer buffer) {
    final long timestamp = buffer.getLong();
    final long traceId = buffer.getLong();
    final int orderIndex = buffer.getInt();

    this.putInQueue(new AfterReceivedRemoteCallRecord(timestamp, traceId, orderIndex,
        this.hostApplicationMetadata));
  }

  private final void readInBeforeJDBCOperationEvent(final ByteBuffer buffer) {
    final long traceId = buffer.getLong();
    final int orderIndex = buffer.getInt();
    final int objectId = buffer.getInt();
    final int operationId = buffer.getInt();
    final int clazzId = buffer.getInt();
    final int interfaceId = buffer.getInt();

    final int sqlStatementId = buffer.getInt();

    try {
      final String operation = this.stringRegistry.getStringFromId(operationId);
      final String clazz = this.stringRegistry.getStringFromId(clazzId);
      final String implementedInterface = this.stringRegistry.getStringFromId(interfaceId);

      final String sqlStatement = this.stringRegistry.getStringFromId(sqlStatementId);

      this.putInQueue(new BeforeJDBCOperationEventRecord(traceId, orderIndex, objectId, operation,
          clazz, implementedInterface, sqlStatement, this.hostApplicationMetadata));
    } catch (final IdNotAvailableException e) {
      this.putInWaitingMessages(buffer, BeforeJDBCOperationEventRecord.COMPRESSED_BYTE_LENGTH + 1);
    }
  }

  private final void readInAfterFailedJDBCOperationEvent(final ByteBuffer buffer) {
    final long timestamp = buffer.getLong();
    final long traceId = buffer.getLong();
    final int orderIndex = buffer.getInt();
    final int causeId = buffer.getInt();

    try {
      final String cause = this.stringRegistry.getStringFromId(causeId);

      this.putInQueue(new AfterFailedJDBCOperationEventRecord(timestamp, traceId, orderIndex, cause,
          this.hostApplicationMetadata));
    } catch (final IdNotAvailableException e) {
      this.putInWaitingMessages(buffer,
          AbstractAfterFailedEventRecord.COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID);
    }
  }

  private final void readInAfterJDBCOperationEvent(final ByteBuffer buffer) {
    final long timestamp = buffer.getLong();
    final long traceId = buffer.getLong();
    final int orderIndex = buffer.getInt();

    final int formattedReturnValueId = buffer.getInt();

    try {
      final String formattedReturnValue =
          this.stringRegistry.getStringFromId(formattedReturnValueId);

      this.putInQueue(new AfterJDBCOperationEventRecord(timestamp, traceId, orderIndex,
          formattedReturnValue, this.hostApplicationMetadata));
    } catch (final IdNotAvailableException e) {
      this.putInWaitingMessages(buffer,
          AfterJDBCOperationEventRecord.COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID);
    }
  }

  private final void putInWaitingMessages(final ByteBuffer buffer, final int length) {
    final byte[] message = new byte[length];
    buffer.position(buffer.position() - length);
    buffer.get(message);
    this.waitingForStringMessages.add(message);
  }

  private final void checkWaitingMessages() {
    if (this.waitingForStringMessages.isEmpty()) {
      return;
    }

    final List<byte[]> localWaitingList = new ArrayList<>(this.waitingForStringMessages);
    this.waitingForStringMessages.clear();

    for (final byte[] waitingMessage : localWaitingList) {
      final ByteBuffer buffer = ByteBuffer.wrap(waitingMessage);
      final byte waitingMessageClazzId = buffer.get();
      switch (waitingMessageClazzId) {
        case HostApplicationMetaDataRecord.CLAZZ_ID:
          this.readInHostApplicationMetaData(buffer);
          break;
        case BeforeOperationEventRecord.CLAZZ_ID:
          this.readInBeforeOperationEvent(buffer);
          break;
        case AfterFailedOperationEventRecord.CLAZZ_ID:
          this.readInAfterFailedOperationEvent(buffer);
          break;
        case AfterOperationEventRecord.CLAZZ_ID:
          this.readInAfterOperationEvent(buffer);
          break;
        case BeforeConstructorEventRecord.CLAZZ_ID:
          this.readInBeforeConstructorEvent(buffer);
          break;
        case AfterFailedConstructorEventRecord.CLAZZ_ID:
          this.readInAfterFailedConstructorEvent(buffer);
          break;
        case AfterConstructorEventRecord.CLAZZ_ID:
          this.readInAfterConstructorEvent(buffer);
          break;
        case BeforeReceivedRemoteCallRecord.CLAZZ_ID:
          this.readInBeforeReceivedRemoteCallEvent(buffer);
          break;
        case BeforeStaticOperationEventRecord.CLAZZ_ID:
          this.readInBeforeStaticOperationEvent(buffer);
          break;
        case AfterFailedStaticOperationEventRecord.CLAZZ_ID:
          this.readInAfterFailedStaticOperationEvent(buffer);
          break;
        case AfterStaticOperationEventRecord.CLAZZ_ID:
          this.readInAfterStaticOperationEvent(buffer);
          break;
        case BeforeSentRemoteCallRecord.CLAZZ_ID:
          this.readInBeforeSentRemoteCallEvent(buffer);
          break;
        case AfterSentRemoteCallRecord.CLAZZ_ID:
          this.readInAfterSentRemoteCallEvent(buffer);
          break;
        case BeforeUnknownReceivedRemoteCallRecord.CLAZZ_ID:
          this.readInBeforeUnknownReceivedRemoteCallEvent(buffer);
          break;
        case AfterUnknownReceivedRemoteCallRecord.CLAZZ_ID:
          this.readInAfterUnknownReceivedRemoteCallEvent(buffer);
          break;
        case AfterReceivedRemoteCallRecord.CLAZZ_ID:
          this.readInAfterReceivedRemoteCallEvent(buffer);
          break;
        case BeforeJDBCOperationEventRecord.CLAZZ_ID:
          this.readInBeforeJDBCOperationEvent(buffer);
          break;
        case AfterFailedJDBCOperationEventRecord.CLAZZ_ID:
          this.readInAfterFailedJDBCOperationEvent(buffer);
          break;
        case AfterJDBCOperationEventRecord.CLAZZ_ID:
          this.readInAfterJDBCOperationEvent(buffer);
          break;
        default:
          break;
      }
    }
  }

  private final void putInQueue(final IRecord message) {
    while (!this.queue.offer(message)) {
      try {
        Thread.sleep(1);
      } catch (final InterruptedException e) {
      }
    }
  }

  private final void forcefullyDisconnect() {
    System.out.println("Forcefully disconnecting...");
    try {
      this.socketChannel.close();
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }
}
