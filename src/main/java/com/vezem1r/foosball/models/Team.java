package com.vezem1r.foosball.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String avatarUrl;

    @OneToOne
    @JoinColumn(nullable = false)
    private Player capitan;

    @OneToOne
    private Player teammate;

    @ManyToOne
    private  Season season;
}
