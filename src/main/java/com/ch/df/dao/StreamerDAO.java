package com.ch.df.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

// 스트리머 테이블을 검색하여 adventureName과 serverId로 고유성을 확인합니다.
@Mapper
public interface StreamerDAO {
    @Select("""
        SELECT id
        FROM streamer
        WHERE adventure_name = #{adventureName} AND server_id = #{serverId}
    """)
    Integer findStreamerIdByAdventureNameAndServerId(
            @Param("adventureName") String adventureName,
            @Param("serverId") String serverId
    );
}
