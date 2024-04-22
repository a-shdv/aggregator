package com.company.aggregator.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "vacancies")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = "user")
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Vacancy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String title;

    String date;

    String salary;

    String company;

    @Column(columnDefinition = "text")
    String requirements;

    @Column(columnDefinition = "text")
    String description;

    @Column(columnDefinition = "text")
    String schedule;

    @Column(columnDefinition = "text")
    String source;

    @Column(columnDefinition = "text")
    String logo;

    @ManyToOne
    @JoinColumn
    User user;
}
