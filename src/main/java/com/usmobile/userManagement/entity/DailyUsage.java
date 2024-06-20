package com.usmobile.userManagement.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "Daily_Usage")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DailyUsage {

    @Id
    private String id;
    private String mdn;
    private String userId;
    private Long usageDate;
    private int usedInMb;

}
