package com.pm11.backend.auth.dto;

import java.time.Instant;

public record UserInfo(String user_id, String provider, Instant created_at) {}
