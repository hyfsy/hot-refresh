package com.hyf.hotrefresh.common.args;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

/**
 * @author baB_hyf
 * @date 2022/06/18
 */
public class AnnotatedArgumentParserAdapter implements AnnotatedArgumentParser {

    private ArgumentParser parser;

    private Argument argument;

    public AnnotatedArgumentParserAdapter(ArgumentParser parser) {
        if (parser == null) {
            throw new IllegalArgumentException("Parser must not be null");
        }

        this.parser = parser;
        Argument argument = parser.getClass().getAnnotation(Argument.class);
        if (argument == null) {
            if (parser instanceof AnnotatedArgumentParser) {
                argument = createArgumentUseAnnotatedArgumentParser((AnnotatedArgumentParser) parser);
            }
            else {
                argument = createDefaultArgument();
            }
        }
        this.argument = argument;
    }

    @Override
    public void init(Map<String, Object> initArgs) {
        parser.init(initArgs);
    }

    @Override
    public void parse(Map<String, Object> parsedArgs, List<String> segments) {
        parser.parse(parsedArgs, segments);
    }

    private Argument createDefaultArgument() {
        return new Argument() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Argument.class;
            }

            @Override
            public String[] value() {
                return new String[0];
            }

            @Override
            public int argc() {
                return 0;
            }
        };
    }

    private Argument createArgumentUseAnnotatedArgumentParser(AnnotatedArgumentParser parser) {
        return new Argument() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Argument.class;
            }

            @Override
            public String[] value() {
                return parser.value();
            }

            @Override
            public int argc() {
                return parser.argc();
            }
        };
    }

    @Override
    public String[] value() {
        return argument.value();
    }

    @Override
    public int argc() {
        return argument.argc();
    }
}
