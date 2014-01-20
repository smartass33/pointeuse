package pointeuse

import org.apache.log4j.spi.LoggingEvent

class EventLogAppender extends org.apache.log4j.AppenderSkeleton
implements org.apache.log4j.Appender {
 
    static appInitialized = false
 
    String source
 
    @Override
    protected void append(LoggingEvent event) {
        if (appInitialized) {
            //copied from Log4J's JDBCAppender
            event.getNDC();
            event.getThreadName();
            // Get a copy of this thread's MDC.
            event.getMDCCopy();
            event.getLocationInformation();
            event.getRenderedMessage();
            event.getThrowableStrRep();
 
            def limit = { string, maxLength -> string.substring(0, Math.min(string.length(), maxLength))}
 
            String logStatement = getLayout().format(event);
            // use new transaction so that the log entry will be written even if the currently running transaction is rolled back
            EventLog.withNewTransaction {
                EventLog eventLog = new EventLog()
                eventLog.message = "Log4 Error Log"
                eventLog.details = limit((logStatement ?: "Not details available, something is wrong"), EventLog.DETAILS_MAXSIZE)
                eventLog.source = limit(source ?: "Source not set", EventLog.SOURCE_MAXSIZE)
                eventLog.save()
            }
        }
    }
 
    /**
     * Set the source value for the logger (e.g. which application the logger belongs to)
     * @param source
     */
    public void setSource(String source) {
        this.source = source
    }
 
    @Override
    void close() {
        //noop
    }
 
    @Override
    boolean requiresLayout() {
        return true
    }
}
