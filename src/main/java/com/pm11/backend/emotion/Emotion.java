package com.pm11.backend.emotion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "emotion")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Emotion {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @Column(nullable = false, length = 20)
    private String name;
}
