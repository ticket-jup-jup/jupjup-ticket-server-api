package org.example.jupjupticketserverapi.servercredential.repository;

import org.example.jupjupticketserverapi.servercredential.entity.ServerCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServerCredentialRepository extends JpaRepository<ServerCredential, Long> {
    Optional<ServerCredential> findByServiceName(String serviceName);
}
