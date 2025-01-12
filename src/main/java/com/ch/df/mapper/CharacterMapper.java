package com.ch.df.mapper;

import com.ch.df.domain.Character;
import com.ch.df.dto.CharacterSearchResponse;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CharacterMapper {
    @Select("SELECT * FROM characters WHERE streamer_id = #{streamerId}")
    List<Character> findByStreamerId(@Param("streamerId") int streamerId);

    @Insert("INSERT INTO characters (streamer_id, character_id, server_id, fame) VALUES (#{streamerId}, #{characterId}, #{serverId}, #{fame})")
    void insertCharacter(CharacterSearchResponse.Character character);
}
