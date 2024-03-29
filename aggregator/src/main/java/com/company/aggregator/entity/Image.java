package com.company.aggregator.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "images",
        indexes = {
                @Index(name = "idx_images_filename", columnList = "filename")
        }
)
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
    String filename;
    String type;
    @Lob
    @Column
    byte[] data;

    @OneToOne(mappedBy = "avatar")
    User user;
}
