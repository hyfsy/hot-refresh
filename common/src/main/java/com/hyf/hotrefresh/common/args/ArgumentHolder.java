package com.hyf.hotrefresh.common.args;

import com.hyf.hotrefresh.common.Services;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author baB_hyf
 * @date 2022/06/18
 * @see ArgumentParser
 * @see AnnotatedArgumentParser
 * @see Argument
 */
public class ArgumentHolder {

    private static final Map<String, Object> ARGS = new ConcurrentHashMap<>();

    private static final Map<String, AnnotatedArgumentParser> parsers;

    static {
        Map<String, AnnotatedArgumentParser> parserMap = new HashMap<>();
        Map<String, Object> initArgs = new HashMap<>();
        for (ArgumentParser argumentParser : Services.gets(ArgumentParser.class)) {
            AnnotatedArgumentParser parser = new AnnotatedArgumentParserAdapter(argumentParser);
            parser.init(initArgs);
            for (String supportArg : parser.value()) {
                AnnotatedArgumentParser annotatedArgumentParser = parserMap.get(supportArg);
                if (annotatedArgumentParser != null) {
                    throw new IllegalStateException("Argument is repeated: " + supportArg);
                }
                parserMap.put(supportArg, parser);
            }
        }
        parsers = parserMap;
        ARGS.putAll(initArgs);
    }

    public static void parse(String[] args) {

        if (args == null || args.length == 0) {
            return;
        }

        Map<String, List<String>> segmentGroups = new HashMap<>();

        List<String> segments = new ArrayList<>();
        Iterator<String> it = Arrays.stream(args).iterator();
        while (it.hasNext()) {
            String segment = it.next();
            if (segment.startsWith("-") && !segments.isEmpty()) { // loop
                String name = segments.remove(0);// remove name: -xxx
                segmentGroups.put(name, segments);
                segments = new ArrayList<>();
            }
            segments.add(segment);
        }
        if (!segments.isEmpty()) { // end
            String name = segments.remove(0);// remove name: -xxx
            segmentGroups.put(name, segments);
        }

        Map<String, Object> parsedArgs = new HashMap<>();
        segmentGroups.forEach((sn, sl) -> {
            AnnotatedArgumentParser parser = parsers.get(sn);
            if (parser != null) {
                if (sl.size() < parser.argc()) {
                    throw new IllegalArgumentException("Un support value length " + sl.size() + " for argument " + sn);
                }
                parser.parse(parsedArgs, sl);
            }
        });
        ARGS.putAll(parsedArgs);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(String name) {
        return (T) ARGS.get(name);
    }

    public static <T> T getOrDefault(String name, T defaultValue) {
        T value = get(name);
        return value != null ? value : defaultValue;
    }

    public static void put(String name, Object value) {
        ARGS.put(name, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T remove(String name) {
        return (T) ARGS.remove(name);
    }

    public static Map<String, Object> getMap() {
        return Collections.unmodifiableMap(ARGS);
    }

}
