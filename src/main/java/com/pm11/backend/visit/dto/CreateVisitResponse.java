package com.pm11.backend.visit.dto;

import java.time.Instant;

public record CreateVisitResponse(String visit_id, Integer place_id, Instant visited_at, Integer rating) {}
