package com.iemr.flw.repo.iemr;

import com.iemr.flw.dto.iemr.MosquitoNetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MosquitoNetRepository extends JpaRepository<MosquitoNetEntity, Long> {}
