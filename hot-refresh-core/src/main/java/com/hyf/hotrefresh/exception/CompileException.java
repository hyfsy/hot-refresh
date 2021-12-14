package com.hyf.hotrefresh.exception;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.util.*;

/**
 * @author baB_hyf
 * @date 2021/12/12
 */
public class CompileException extends RefreshException {

    private List<Diagnostic<? extends JavaFileObject>> diagnostics;

    public CompileException(String message) {
        super(message);
    }

    public CompileException(String message, Throwable cause) {
        super(message, cause);
    }

    public CompileException(String message, List<Diagnostic<? extends JavaFileObject>> diagnostics) {
        super(message);
        this.diagnostics = diagnostics;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + "\n" + getErrors();
    }

    private String getErrors() {
        StringBuilder errors = new StringBuilder();

        for (Map<String, Object> message : getErrorList()) {
            for (Map.Entry<String, Object> entry : message.entrySet()) {
                Object value = entry.getValue();
                if (value != null && !value.toString().isEmpty()) {
                    errors.append(entry.getKey());
                    errors.append(": ");
                    errors.append(value);
                }
                errors.append(" , ");
            }

            errors.append("\n");
        }

        return errors.toString();
    }

    private List<Map<String, Object>> getErrorList() {
        List<Map<String, Object>> messages = new ArrayList<>();
        if (diagnostics != null) {
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics) {
                Map<String, Object> message = new HashMap<>();
                message.put("line", diagnostic.getLineNumber());
                message.put("message", diagnostic.getMessage(Locale.US));
                messages.add(message);
            }
        }
        return messages;
    }
}
