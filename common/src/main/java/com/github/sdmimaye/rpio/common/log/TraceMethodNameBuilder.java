package com.github.sdmimaye.rpio.common.log;

import org.apache.commons.lang3.ArrayUtils;

class TraceMethodNameBuilder {
    public String getMethodNames(int skipFrames, int depth) {
        StackTraceElement[] stackTrace = new Throwable().fillInStackTrace().getStackTrace();
        return getMethodNamesByStackTrace(stackTrace, skipFrames, depth);
    }

    public String getMethodNamesByStackTrace(StackTraceElement[] stackTrace, int skipFrames, int depth) {
        if (ArrayUtils.isEmpty(stackTrace)) {
            return "Stack trace unavailable.";
        }

        StringBuilder methodNamesBuilder = new StringBuilder(256);

        int requestedTraceDepth = skipFrames + depth;
        int availableTraceDepth = Math.min(requestedTraceDepth, stackTrace.length - 1);
        for (int i = availableTraceDepth; i > skipFrames; i--) {
            StackTraceElement stackFrame = stackTrace[i];
            if (stackFrame == null) {
                break;
            } else {
                appendStackFrame(methodNamesBuilder, stackFrame);
                methodNamesBuilder.append(" |> ");
            }
        }

        appendStackFrame(methodNamesBuilder, stackTrace[skipFrames]);

        return methodNamesBuilder.toString();
    }

    private StringBuilder appendStackFrame(final StringBuilder methodNamesBuilder, final StackTraceElement stackFrame) {
        methodNamesBuilder.append(getFormattedMethodName(stackFrame));
        int lineNumber = stackFrame.getLineNumber();
        if (lineNumber > 0) {
            methodNamesBuilder.append(":").append(lineNumber);
        }

        return methodNamesBuilder;
    }

    private String getFormattedMethodName(StackTraceElement stackTraceElement) {
        String fullClassName = stackTraceElement.getClassName();
        String shortClassName = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
        String methodName = stackTraceElement.getMethodName();
        return shortClassName + "." + methodName + "()";
    }
}
