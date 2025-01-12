package com.ch.df.mapper;

import com.ch.df.domain.Streamer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StreamerMapper {

    void insertStreamer(Streamer streamer);

    Streamer findByName(@Param("name") String name);

}
