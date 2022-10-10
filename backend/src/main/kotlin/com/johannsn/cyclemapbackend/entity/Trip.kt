package com.johannsn.cyclemapbackend.entity

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import javax.persistence.*

@Entity
@Table(name = "TRIPS")
@JsonPropertyOrder("id", "text", "distance", "ascent", "descent", "tour", "coordinates")
data class Trip(
    @Column(nullable = false)
    val title: String,

    @Column(nullable = false)
    val text: String,

    //TODO add images
    //@Column(nullable = true)
    //val imageUrls: List<String>? = null,

    @ManyToOne()
    @JoinColumn(name = "tour_id", nullable = false)
    var tour: Tour? = null,

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "trip", cascade = [CascadeType.ALL])
    @Column(nullable = false)
    val coordinates: MutableList<Coordinates> = mutableListOf(),

    @Column
    val distance: Int = 0,

    @Column
    val ascent: Int = 0,

    @Column
    val descent: Int = 0,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_id")
    var id: Long = 0,
)
