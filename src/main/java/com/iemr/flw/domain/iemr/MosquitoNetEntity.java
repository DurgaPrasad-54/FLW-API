package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "i_mobilization_mosquito_net",schema = "db_iemr")
public class MosquitoNetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long beneficiaryId;
    private Long houseHoldId;
    private LocalDate visitDate;
    private String userName;
    private String isNetDistributed;
}
