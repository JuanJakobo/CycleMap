package com.johannsn.cyclemapbackend.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import javax.persistence.*

@Entity
@Table(name = "Tours")
@JsonPropertyOrder("id", "text", "trips")
data class Tour(
    @Column(name = "text", nullable = false)
    val title: String,

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "tour", cascade = [CascadeType.ALL])
    @JsonIgnore
    val trips: MutableList<Trip> = mutableListOf(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tour_id")
    val id: Long = 0,
)