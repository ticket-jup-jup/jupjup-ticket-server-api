package org.example.jupjupticketserverapi.servercredential.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.jupjupticketserverapi.global.entity.BaseEntity;

@Entity
@Table(name = "server_credentials")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ServerCredential extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String serviceName;

    @Column(nullable = false, unique = true, length = 100)
    private String apiKey;

    @Column(nullable = false)
    private boolean isUse;

    public ServerCredential(String serviceName, String apiKey) {
        this.serviceName = serviceName;
        this.apiKey = apiKey;
        this.isUse = true;
    }

    public void deactivate() {
        this.isUse = false;
    }
}
