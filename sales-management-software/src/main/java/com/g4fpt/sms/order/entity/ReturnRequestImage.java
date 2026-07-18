package com.g4fpt.sms.order.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "return_request_image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnRequestImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_request_id", nullable = false)
    private ReturnRequest returnRequest;

    @Column(name = "imageName", nullable = false, length = 500)
    private String imageUrl;
}
