package com.iemr.flw.dto.iemr;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class OrsDistributionListDTO {


    @SerializedName("num_under5_children")
    private Double num_under5_children;

    @SerializedName("num_ors_packets")
    private Double num_ors_packets;

    @SerializedName("ifa_provision_date")
    private String ifa_provision_date;

    @SerializedName("mcp_card_upload")
    private String mcp_card_upload;


}
