package com.pm11.backend.place.dto;

/**
 * 추천 등에서 거리·필터만 계산할 때 사용하는 경량 행. 큰 TEXT/JSON 컬럼은 조회하지 않는다.
 */
public record PlaceScanRow(
        Integer id, Double latitude, Double longitude, Boolean free, Boolean indoor) {}
