package com.hyf.hotrefresh.hello.convert;

import com.hyf.hotrefresh.hello.entity.PersonDo;
import com.hyf.hotrefresh.hello.entity.PersonDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
@Mapper
public interface HelloConverter {

    HelloConverter INSTANCE = Mappers.getMapper(HelloConverter.class);

    // PersonDto convert(PersonDo personDo);
}
