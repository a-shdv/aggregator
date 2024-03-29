package com.company.aggregator.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "favourites")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Favourite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String title;
    String date;
    String company;
    @Column(columnDefinition = "text")
    String schedule;
    @Column(columnDefinition = "text")
    String source;
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    User user;
    String logo;
}
