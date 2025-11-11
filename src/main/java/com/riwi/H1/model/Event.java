package com.riwi.H1.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    private Long id;
    private String name;
    private String date;
    private String venue;
}
