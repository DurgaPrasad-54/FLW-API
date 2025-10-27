package com.iemr.flw.dto.iemr;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class OrsDistributionResponseListDTO {


    @JsonProperty("num_under5_children")
    private String num_under5_children;

    @JsonProperty("num_ors_packets")
    private String num_ors_packets;

    @JsonProperty("ifa_provision_date")
    private String ifa_provision_date;

    @JsonProperty("mcp_card_upload")
    private String mcp_card_upload;


}
