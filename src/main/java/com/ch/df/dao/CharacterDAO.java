package com.ch.df.dao;

import com.ch.df.entity.GameCharacter;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CharacterDAO {
    @Insert("""
    INSERT INTO characters (
        server_id, character_id, character_name, level, job_name, job_grow_name,
        fame, adventure_name, guild_name, streamer_id, taecho_count, last_checked
    ) VALUES (
        #{serverId}, #{characterId}, #{characterName}, #{level}, #{jobName}, #{jobGrowName},
        #{fame}, #{adventureName}, #{guildName}, #{streamerId}, #{taechoCount}, #{lastChecked}
    )
    ON DUPLICATE KEY UPDATE
        character_name = VALUES(character_name),
        level = VALUES(level),
        job_name = VALUES(job_name),
        job_grow_name = VALUES(job_grow_name),
        fame = VALUES(fame),
        adventure_name = VALUES(adventure_name),
        guild_name = VALUES(guild_name),
        streamer_id = VALUES(streamer_id),
        taecho_count = IFNULL(VALUES(taecho_count), taecho_count),
        last_checked = VALUES(last_checked)
""")
    void insertOrUpdateCharacter(GameCharacter character);

    // 캐릭터 ID 및 서버 ID로 캐릭터 검색
    @Select("""
        SELECT * FROM characters
        WHERE character_id = #{characterId} AND server_id = #{serverId}
    """)
    GameCharacter findCharacterByIdAndServer(
            @Param("characterId") String characterId,
            @Param("serverId") String serverId
    );
}
