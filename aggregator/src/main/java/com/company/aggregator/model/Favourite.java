package com.company.aggregator.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "favourites")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Favourite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String date;
    private String company;
    @Column(columnDefinition = "text")
    private String schedule;
    @Column(columnDefinition = "text")
    private String source;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}
