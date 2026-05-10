package com.pm11.backend.user.dto;

import java.time.Instant;

public record DeleteMeResponse(String user_id, Instant deleted_at) {}
