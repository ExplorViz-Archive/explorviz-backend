package net.explorviz.monitoring.live_trace_processing.probe;

import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.explorviz.common.live_trace_processing.reader.TimeProvider;
import net.explorviz.common.live_trace_processing.record.event.AbstractAfterFailedEventRecord;
import net.explorviz.common.live_trace_processing.record.event.jdbc.AfterFailedJDBCOperationEventRecord;
import net.explorviz.common.live_trace_processing.record.event.jdbc.AfterJDBCOperationEventRecord;
import net.explorviz.common.live_trace_processing.record.event.jdbc.BeforeJDBCOperationEventRecord;
import net.explorviz.monitoring.live_trace_processing.main.MonitoringController;
import net.explorviz.monitoring.live_trace_processing.main.MonitoringStringRegistry;
import net.explorviz.monitoring.live_trace_processing.probe.tracemanagement.ProbeTraceMetaData;
import net.explorviz.monitoring.live_trace_processing.probe.tracemanagement.TraceRegistry;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * Defintion of Aspects for JDBC Monitoring
 *
 * @author Christian Zirkelbach
 *
 */
@Aspect
public class JDBCAspect {

  /* Configuration */
  private static final String NOT_WITHIN =
      "!within(explorviz..*) && !within(com.lmax..*) && !within(org.hyperic.sigar..*)";

  /* Related JDBC calls */
  /* http://docs.oracle.com/javase/7/docs/api/java/sql/Statement.html */
  private static final String RELATED_CALLS =
      /** Connection **/
      /* createStatment */
      "(call(java.sql.Statement java.sql.Connection.createStatement()) "
          /* prepareStatement */
          + "|| call(java.sql.PreparedStatement java.sql.Connection.prepareStatement(String)) "
          + "|| call(java.sql.PreparedStatement java.sql.Connection.prepareStatement(String, String[])) "
          + "|| call(java.sql.PreparedStatement java.sql.Connection.prepareStatement(String, int)) "
          + "|| call(java.sql.PreparedStatement java.sql.Connection.prepareStatement(String, int[])) "
          + "|| call(java.sql.PreparedStatement java.sql.Connection.prepareStatement(String, int, int)) "
          + "|| call(java.sql.PreparedStatement java.sql.Connection.prepareStatement(String, int, int, int)) "
          /* PrepareCall */
          + "|| call(java.sql.CallableStatement java.sql.Connection.prepareCall(String)) "
          + "|| call(java.sql.CallableStatement java.sql.Connection.prepareCall(String, int, int)) "
          + "|| call(java.sql.CallableStatement java.sql.Connection.prepareCall(String, int, int, int)) "
          /** Batches **/
          + "|| call(void java.sql.Statement.addBatch(String)) "
          + "|| call(java.sql.Statement java.sql.Statement.addBatch(int, int)) "
          + "|| call(java.sql.Statement java.sql.Statement.addBatch(int, int, int)) "
          + "|| call(int[] java.sql.Statement.executeBatch())"
          /** Statement **/
          /* execute */
          + "|| call(boolean java.sql.Statement.execute(String)) "
          + "|| call(boolean java.sql.Statement.execute(String,int)) "
          + "|| call(boolean java.sql.Statement.execute(String,int[])) "
          + "|| call(boolean java.sql.Statement.execute(String,String[])) "
          /* executeQuery */
          + "|| call(java.sql.ResultSet java.sql.Statement.executeQuery(String)) "
          /* executeUpdate */
          + "|| call(int java.sql.Statement.executeUpdate(String)) "
          + "|| call(int java.sql.Statement.executeUpdate(String, int)) "
          + "|| call(int java.sql.Statement.executeUpdate(String, int[])) "
          + "|| call(int java.sql.Statement.executeUpdate(String, String[])) "
          /** PreparedStatement **/
          /* setter */
          // void java.sql.PreparedStatement.setArray(int, Array)
          + "|| call(void java.sql.PreparedStatement.setArray(..)) "
          // void java.sql.PreparedStatement.setAsciiStream(int, InputStream,
          // int)
          + "|| call(void java.sql.PreparedStatement.setAsciiStream(..)) "
          // void java.sql.PreparedStatement.setAsciiStream(int, InputStream,
          // long)
          + "|| call(void java.sql.PreparedStatement.setAsciiStream(..)) "
          // void java.sql.PreparedStatement.setBigDecimal(int, BigDecimal)
          + "|| call(void java.sql.PreparedStatement.setBigDecimal(..)) "
          // void java.sql.PreparedStatement.setBinaryStream(int,
          // InputStream))
          + "|| call(void java.sql.PreparedStatement.setBinaryStream(..)) "
          // void java.sql.PreparedStatement.setBinaryStream(int, InputStream,
          // int)
          // void java.sql.PreparedStatement.setBinaryStream(int, InputStream,
          // long)
          + "|| call(void java.sql.PreparedStatement.setBinaryStream(..)) "
          // void java.sql.PreparedStatement.setBlob(int, Blob)
          // void java.sql.PreparedStatement.setBlob(int, InputStream)
          // void java.sql.PreparedStatement.setBlob(int, InputStream, long)
          + "|| call(void java.sql.PreparedStatement.setBlob(..)) "
          + "|| call(void java.sql.PreparedStatement.setBoolean(int, boolean)) "
          + "|| call(void java.sql.PreparedStatement.setByte(int, byte)) "
          + "|| call(void java.sql.PreparedStatement.setBytes(int, byte[])) "
          // void java.sql.PreparedStatement.setCharacterStream(int, Reader)
          // void java.sql.PreparedStatement.setCharacterStream(int, Reader,
          // int)
          // void java.sql.PreparedStatement.setCharacterStream(int, Reader,
          // long)
          + "|| call(void java.sql.PreparedStatement.setCharacterStream(..)) "
          // void java.sql.PreparedStatement.setClob(int, Clob)
          // void java.sql.PreparedStatement.setClob(int, Reader)
          // void java.sql.PreparedStatement.setClob(int, Reader, long)
          + "|| call(void java.sql.PreparedStatement.setClob(..)) "
          // void java.sql.PreparedStatement.setDate(int, Date)
          // void java.sql.PreparedStatement.setDate(int, Date, Calendar)
          + "|| call(void java.sql.PreparedStatement.setDate(..)) "
          + "|| call(void java.sql.PreparedStatement.setDouble(int, double)) "
          + "|| call(void java.sql.PreparedStatement.setFloat(int, float)) "
          + "|| call(void java.sql.PreparedStatement.setInt(int, int)) "
          + "|| call(void java.sql.PreparedStatement.setLong(int, long)) "
          // void java.sql.PreparedStatement.setNCharacterStream(int, Reader)
          // void java.sql.PreparedStatement.setNCharacterStream(int, Reader,
          // long)
          + "|| call(void java.sql.PreparedStatement.setNCharacterStream(..)) "
          // void java.sql.PreparedStatement.setNClob(int, NClob)
          // void java.sql.PreparedStatement.setNClob(int, Reader)
          // void java.sql.PreparedStatement.setNClob(int, Reader, long)
          + "|| call(void java.sql.PreparedStatement.setNClob(..)) "
          + "|| call(void java.sql.PreparedStatement.setNString(int, String)) "
          + "|| call(void java.sql.PreparedStatement.setNull(int, int)) "
          + "|| call(void java.sql.PreparedStatement.setNull(int, int, String)) "
          + "|| call(void java.sql.PreparedStatement.setObject(int, Object)) "
          + "|| call(void java.sql.PreparedStatement.setObject(int, Object, int)) "
          + "|| call(void java.sql.PreparedStatement.setObject(int, Object, int, int)) "
          // void java.sql.PreparedStatement.setRef(int, Ref)
          + "|| call(void java.sql.PreparedStatement.setRef(..)) "
          // void java.sql.PreparedStatement.setRowId(int, RowId)
          + "|| call(void java.sql.PreparedStatement.setRowId(..)) "
          // void java.sql.PreparedStatement.setShort(int, short)
          + "|| call(void java.sql.PreparedStatement.setShort(..)) "
          // void java.sql.PreparedStatement.setSQLML(int, SQLML)
          + "|| call(void java.sql.PreparedStatement.setSQLML(..)) "
          + "|| call(void java.sql.PreparedStatement.setString(int, String)) "
          // void java.sql.PreparedStatement.setTime(int, Time)
          // void java.sql.PreparedStatement.setTime(int, Time, Calendar)
          + "|| call(void java.sql.PreparedStatement.setTime(..)) "
          // void java.sql.PreparedStatement.setTimestamp(int, Timestamp)
          // void java.sql.PreparedStatement.setTimestamp(int, Timestamp,
          // Calendar)
          + "|| call(void java.sql.PreparedStatement.setTimestamp(..)) "
          // void java.sql.PreparedStatement.setUnicodeStream(int,
          // InputStream, int)
          + "|| call(void java.sql.PreparedStatement.setUnicodeStream(..)) "
          // void java.sql.PreparedStatement.setURL(int, URL)
          + "|| call(void java.sql.PreparedStatement.setURL(..)) "
          /* execute */
          + "|| call(boolean java.sql.PreparedStatement.execute()) "
          /* executeQuery */
          + "|| call(java.sql.ResultSet java.sql.PreparedStatement.executeQuery()) "
          /* executeUpdate */
          + "|| call(int java.sql.PreparedStatement.executeUpdate()) " + ") && this(thisObject) && "
          + NOT_WITHIN;

  @Around(RELATED_CALLS)
  public final Object operation(final Object thisObject, final ProceedingJoinPoint thisJoinPoint)
      throws Throwable {

    final ByteBuffer buffer = AbstractAspect.bufferStore.get();

    final ProbeTraceMetaData trace = TraceRegistry.getTrace();
    trace.incrementStackDepth();

    if (BeforeJDBCOperationEventRecord.COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID > buffer.remaining()) {
      // updateLastSendingTimeToCurrent();
      MonitoringController.sendOutBuffer(buffer);
    }

    buffer.put(BeforeJDBCOperationEventRecord.CLAZZ_ID);
    final long timeStart = TimeProvider.getCurrentTimestamp();
    buffer.putLong(trace.getTraceId());
    buffer.putInt(trace.getNextOrderId());
    final int objectId = System.identityHashCode(thisObject);
    if (objectId != 0) {
      buffer.putInt(objectId);
    }
    buffer.putInt(MonitoringController.getIdForSignature(thisJoinPoint.getSignature()));

    buffer.putInt(MonitoringStringRegistry.getIdForString(thisObject.getClass().getName()));
    buffer.putInt(MonitoringStringRegistry.getIdForString(this.getInterface(thisJoinPoint)));

    this.processMonitoringBeforeEvent(thisJoinPoint, buffer);

    final Object retval;

    try {
      retval = thisJoinPoint.proceed();
    } catch (final Throwable th) {
      if (AbstractAfterFailedEventRecord.COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID > buffer
          .remaining()) {
        // updateLastSendingTimeToCurrent();
        MonitoringController.sendOutBuffer(buffer);
      }

      buffer.put(AfterFailedJDBCOperationEventRecord.CLAZZ_ID);
      buffer.putLong(TimeProvider.getCurrentTimestamp() - timeStart);
      buffer.putLong(trace.getTraceId());
      buffer.putInt(trace.getNextOrderId());

      // final StringWriter errors = new StringWriter();
      // th.printStackTrace(new PrintWriter(errors));

      String message = th.getMessage();
      if (message == null) {
        message = "<unknown>";
      }

      buffer.putInt(MonitoringStringRegistry.getIdForString(message));

      trace.decreaseStackDepthAndEndTraceIfNeccessary();

      throw th;
    }

    if (AfterJDBCOperationEventRecord.COMPRESSED_BYTE_LENGTH_WITH_CLAZZ_ID > buffer.remaining()) {
      // updateLastSendingTimeToCurrent();
      MonitoringController.sendOutBuffer(buffer);
    }

    buffer.put(AfterJDBCOperationEventRecord.CLAZZ_ID);
    buffer.putLong(TimeProvider.getCurrentTimestamp() - timeStart);
    buffer.putLong(trace.getTraceId());
    buffer.putInt(trace.getNextOrderId());

    this.processMonitoringAfterEvent(thisJoinPoint, retval, buffer);

    trace.decreaseStackDepthAndEndTraceIfNeccessary();

    return retval;
  }

  private void processMonitoringBeforeEvent(final ProceedingJoinPoint currentJoinPoint,
      final ByteBuffer buffer) {

    final Object[] classArgs = this.getJoinPointArguments(currentJoinPoint);
    String stringClassArgs = this.convertJoinpointArgsToString(classArgs);

    if (stringClassArgs == null) {
      stringClassArgs = "";
    }

    buffer.putInt(MonitoringStringRegistry.getIdForString(stringClassArgs));
  }

  private void processMonitoringAfterEvent(final ProceedingJoinPoint currentJoinPoint,
      final Object returnValue, final ByteBuffer buffer) {

    final String className = this.getJoinPointClassName(currentJoinPoint);
    // final Object[] classArgs = getJoinPointArguments(currentJoinPoint);
    // final String stringClassArgs =
    // convertJoinpointArgsToString(classArgs);

    String formattedReturnValue = this.extractJoinPointReturnValue(className, returnValue);

    if (formattedReturnValue == null) {
      formattedReturnValue = "";
    }

    buffer.putInt(MonitoringStringRegistry.getIdForString(formattedReturnValue));
  }

  private String getJoinPointClassName(final ProceedingJoinPoint currentJoinPoint) {
    String className = null;
    try {
      className = currentJoinPoint.getSignature().toString();
    } catch (final Exception e) {
      e.getStackTrace();
      System.out.println(e.getMessage());
      className = e.toString();
    }
    return className;
  }

  private String getJoinPointReturnType(final String className) {
    String returnType = null;

    final String[] splittedClassName = className.split(" ");
    returnType = splittedClassName[0];

    return returnType;
  }

  private Object[] getJoinPointArguments(final ProceedingJoinPoint currentJoinPoint) {
    Object[] joinArgs = null;

    if (currentJoinPoint.getArgs() != null) {
      final Object[] tmpJoinArgs = currentJoinPoint.getArgs();

      if (tmpJoinArgs.length != 0) {
        joinArgs = currentJoinPoint.getArgs();
      }
    }
    return joinArgs;
  }

  private String convertJoinpointArgsToString(final Object[] joinPointArgs) {
    String returnValue = new String();

    if (joinPointArgs != null) {
      for (final Object obj : joinPointArgs) {
        returnValue += obj + ";";
      }
      // remove duplicate white sapces
      returnValue = returnValue.replaceAll("\\s+", " ");
    }
    return returnValue;
  }

  private String extractJoinPointReturnValue(final String className, final Object returnValue) {
    String formattedReturnValue = null;

    if (className != null && returnValue != null) {
      final String returnType = this.getJoinPointReturnType(className).toUpperCase();
      final Object classFileObject = returnValue;
      int numberOfRows = 0;

      switch (returnType) {
        case "STRING":
        case "BOOLEAN":
          formattedReturnValue = classFileObject.toString();
          break;
        case "INT":
          numberOfRows = (int) classFileObject;
          formattedReturnValue = String.valueOf(numberOfRows);
          break;
        case "RESULTSET":
          try {
            numberOfRows = 0;
            final ResultSet rs = (ResultSet) classFileObject;
            while (rs.next()) {
              numberOfRows++;
            }
            rs.close();
          } catch (final SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
          }
          formattedReturnValue = String.valueOf(numberOfRows);
          break;
        case "STATEMENT":
          break;
        case "PREPAREDSTATEMENT":
          break;
        case "INT[]":
          break;
        case "CALLABLESTATEMENT":
          break;
        default:
          break;
      }
    }
    return formattedReturnValue;
  }

  @SuppressWarnings("rawtypes")
  private String getInterface(final ProceedingJoinPoint thisJoinPoint) {
    final Class[] interfaces = thisJoinPoint.getSignature().getDeclaringType().getInterfaces();
    if (interfaces.length == 1) {
      return interfaces[0].getName();
    }
    if (interfaces.length == 0) {
      final Class<?> superClass = thisJoinPoint.getSignature().getDeclaringType().getSuperclass();
      if (superClass != null) {
        final String superClassName = superClass.getName();
        if (!superClassName.equals("java.lang.Object")) {
          return superClassName;
        }
      }
    }

    return "";
  }
}
