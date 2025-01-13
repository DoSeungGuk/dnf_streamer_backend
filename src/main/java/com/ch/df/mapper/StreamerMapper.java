package com.ch.df.mapper;

import com.ch.df.domain.Streamer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StreamerMapper {

    Streamer findByName(@Param("name") String name);
    void insertStreamer(Streamer streamer);

}
