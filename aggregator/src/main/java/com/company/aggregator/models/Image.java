package com.company.aggregator.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"data", "user"})
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String type;
    @Lob @Column private byte[] data;

    @OneToOne (mappedBy = "avatar")
    User user;
}
