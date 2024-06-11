package ru.practicum.stat.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@Table(name = "record_statistic")
@NoArgsConstructor
@AllArgsConstructor
public class RecordStatistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    private String appName;

    private String uri;

    private String ip;

    private LocalDateTime timestamp;
}
