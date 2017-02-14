package com.github.sdmimaye.rpio.server.util.strings;

import freemarker.core.InvalidReferenceException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class PlaceholderUtil {
    private static final Logger logger = LoggerFactory.getLogger(PlaceholderUtil.class);

    public enum EscapeMode {
        NONE, HTML
    }

    private final Map<String, Object> dataMap;

    public static PlaceholderUtil forSelectedValues(Map<String, Object> dataMap) {
        return new PlaceholderUtil(dataMap);
    }

    public static PlaceholderUtil forItem(String name, Object item) {
        HashMap<String, Object> values = new HashMap<>();
        values.put(name, item);

        return new PlaceholderUtil(values);
    }

    public static PlaceholderUtil forItem(Object item) {
        Class<?> aClass = item.getClass();
        return forItem(aClass.getSimpleName(), item);
    }

    public static PlaceholderUtil forNoValues() {
        return new PlaceholderUtil(new HashMap<String, Object>());
    }

    private PlaceholderUtil(Map<String, Object> dataMap) {
        if (dataMap == null) {
            this.dataMap = new HashMap<>();
        } else {
            this.dataMap = new HashMap<>(dataMap);
        }
    }

    public String replace(String template) {
        return replace(template, EscapeMode.NONE);
    }

    public String replace(String template, EscapeMode escapeMode) {
        if (escapeMode == EscapeMode.HTML) {
            template = "<#escape x as x?html>" + template + "</#escape>";
        }

        try {
            Configuration config = new Configuration();
            config.setNumberFormat("0.######");
            config.setTemplateExceptionHandler((te, env, out) -> {
                logger.info("Template error: " + te.toString());
                try {
                    out.write("---");
                } catch (IOException e) {
                    throw new TemplateException(e, env);
                }
            });

            Template pathTemplate = new Template("name", new StringReader(template), config);
            StringWriter resultWriter = new StringWriter();

            pathTemplate.process(dataMap, resultWriter);
            return resultWriter.toString();
        } catch (InvalidReferenceException e) {
            logger.info("Freemarker failed to process '{}': {}", template, e.getMessage());
            return template;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse template: " + e.getMessage(), e);
        }
    }
}
