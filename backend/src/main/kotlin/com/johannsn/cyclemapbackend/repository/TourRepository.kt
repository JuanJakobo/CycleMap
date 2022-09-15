package com.johannsn.cyclemapbackend.repository

import com.johannsn.cyclemapbackend.entity.Tour
import org.springframework.data.jpa.repository.JpaRepository

interface TourRepository : JpaRepository<Tour, Long>