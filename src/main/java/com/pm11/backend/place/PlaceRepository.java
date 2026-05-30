package com.pm11.backend.place;

import com.pm11.backend.place.dto.PlaceScanRow;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PlaceRepository extends JpaRepository<Place, Integer> {

    @Query(
            """
            select new com.pm11.backend.place.dto.PlaceScanRow(
                p.id, p.latitude, p.longitude, p.free, p.indoor)
            from Place p""")
    List<PlaceScanRow> findAllScanForRecommendation();
}
