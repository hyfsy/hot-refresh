package com.hyf.hotrefresh.hello.convert;

import com.hyf.hotrefresh.hello.entity.LombokDo;
import com.hyf.hotrefresh.hello.entity.LombokDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author baB_hyf
 * @date 2022/06/25
 */
@Mapper
public interface MixedConverter {

    MixedConverter INSTANCE = Mappers.getMapper(MixedConverter.class);

    // LombokDto convert(LombokDo personDo);



}