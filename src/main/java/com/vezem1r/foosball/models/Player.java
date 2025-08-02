package com.vezem1r.foosball.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "players")
public class Player {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Boolean isCapitan;

  @Column(nullable = false)
  private Boolean isLooking;

  @Column(nullable = false)
  private Boolean isSkipping;

  @OneToOne
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;
}
