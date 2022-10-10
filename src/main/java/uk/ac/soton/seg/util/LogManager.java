package uk.ac.soton.seg.util;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LogManager {
    private static enum Level {
        ERROR,
        INFO,
        NOTIFY
    }

    // private static Logger log = Log.getLog(.class.getName());
    private static class SubLog implements Logger {
        private String name;
        private LogManager parent;

        SubLog(LogManager parent, String name) {
            this.parent = parent;
            this.name = name;
        }


        @Override
        public void error(String msg) {
            parent.logError(name, msg);
        }

        @Override
        public void info(String msg) {
            parent.logNotif(name, msg);
        }
    }

    private static LogManager log;

    public ObservableList<String> errorMessageObservableList = FXCollections.observableList(new ArrayList<>());
    public ObservableList<String> notifMessagesObservableList = FXCollections.observableList(new ArrayList<>());

    public static final Pattern STACK_TRACE_LOG_PATTERN = Pattern.compile("\\[[^\\]]*\\]");

    private LogManager() {
    }

    private void logNotif(String name, String msg) {
        var stackTraceElem = Thread.currentThread().getStackTrace()[3];
        log(Level.INFO, name, stackTraceElem.getClassName() + " " + stackTraceElem.getMethodName(), msg);
    }

    private void logError(String name, String msg) {
        var stackTraceElem = Thread.currentThread().getStackTrace()[3];
        log(Level.ERROR, name, stackTraceElem.getClassName() + " " + stackTraceElem.getMethodName(), msg);
    }

    private void log(Level level, String name, String method, String msg) {
        var dateStr = ZonedDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME).substring(0, 8);
        name = name == null ? "" : "[" + method + "]";
        var fullMsg = dateStr + name + " : " + msg.replaceAll("\n", "\n\t");

        switch (level) {
            case ERROR:
                errorMessageObservableList.add(fullMsg);
            case INFO:
            default:
                notifMessagesObservableList.add(fullMsg);
        }
    }

    public ObservableList<String> getErrors() {
        return errorMessageObservableList;
    }

    public ObservableList<String> getNotifs() {
        return notifMessagesObservableList;
    }

    public static Logger getLog(String name) {
        return new SubLog(getLog(), name);
    }

    public synchronized static LogManager getLog() {
        if (log == null) {
            log = new LogManager();
        }
        return log;
    }
}