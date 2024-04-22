package com.company.aggregator.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"data", "user"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    String type;

    @Lob
    @Column
    byte[] data;

    @OneToOne
    @JoinColumn
    User user;
}
