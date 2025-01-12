package com.ch.df.dao;

import com.ch.df.entity.GameCharacter;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
//캐릭터 정보를 삽입합니다.
@Mapper
public interface CharacterDAO {
    @Insert("""
        INSERT INTO characters (
            server_id, character_id, character_name, level, job_name, job_grow_name, fame, adventure_name, guild_name, streamer_id
        ) VALUES (
            #{serverId}, #{characterId}, #{characterName}, #{level}, #{jobName}, #{jobGrowName}, #{fame}, #{adventureName}, #{guildName}, #{streamerId}
        )
        ON DUPLICATE KEY UPDATE
            level = VALUES(level), fame = VALUES(fame), guild_name = VALUES(guild_name)
    """)
    void insertOrUpdateCharacter(GameCharacter character);
}
