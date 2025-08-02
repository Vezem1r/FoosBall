package com.vezem1r.foosball.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "matchs")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Season season;

    @ManyToOne
    private Team teamA;

    @ManyToOne
    private Team teamB;

    private int teamAScore;
    private int teamBScore;

    private boolean confirmedByTeamA;
    private boolean confirmedByTeamB;

    private OffsetDateTime playedAt;
}
