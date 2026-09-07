package com.trinetra.ai.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TelemetryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTelemetry(entity: TelemetryEntity)

    @Query("SELECT * FROM telemetry_records WHERE isSynced = 0 ORDER BY timestampUtc ASC")
    suspend fun getUnsyncedRecords(): List<TelemetryEntity>

    @Query("UPDATE telemetry_records SET isSynced = 1 WHERE caseId = :caseId")
    suspend fun markSynced(caseId: String)

    @Query("SELECT * FROM telemetry_records WHERE caseId = :caseId LIMIT 1")
    suspend fun getByCaseId(caseId: String): TelemetryEntity?

    @Query("SELECT COUNT(*) FROM telemetry_records WHERE isSynced = 0")
    suspend fun getUnsyncedCount(): Int
}
