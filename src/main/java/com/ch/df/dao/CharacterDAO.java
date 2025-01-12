package com.ch.df.dao;

import com.ch.df.entity.CharacterRankingResponse;
import com.ch.df.entity.GameCharacter;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

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
    @Select("""
    SELECT 
        c.character_name AS characterName,
        c.fame AS fame,
        c.job_grow_name AS jobGrowName,
        s.name AS streamerName,
        c.taecho_count
    FROM 
        characters c
    JOIN 
        streamer s ON s.id = c.streamer_id
    ORDER BY 
        c.fame DESC
""")
    List<CharacterRankingResponse> findCharacterRankings();

    @Select("""
    SELECT
        c.character_name AS characterName,
        c.fame,
        c.job_grow_name AS jobGrowName,
        s.name AS streamer_name,
        c.taecho_count
    FROM
        characters c
    JOIN
        streamer_db.streamer s ON s.id = c.streamer_id
    WHERE
        taecho_count IS NOT NULL
    ORDER BY
        taecho_count DESC;
""")
    List<CharacterRankingResponse> findTopTaechoCharacters();

    @Select("""
    SELECT 
        s.name AS streamer_name, 
        SUM(c.taecho_count) AS total_taecho_count 
    FROM 
        characters c 
    JOIN 
        streamer s 
    ON 
        c.streamer_id = s.id 
    GROUP BY 
        s.name 
    ORDER BY 
        total_taecho_count DESC 
""")
    List<Map<String, Object>> findStreamerTaechoRankings();

}
