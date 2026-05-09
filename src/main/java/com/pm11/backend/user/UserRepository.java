package com.pm11.backend.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);
}
