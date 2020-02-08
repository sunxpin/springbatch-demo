package com.sun.api.itemreaderfile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Date 2020/2/8 14:20
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    private long id;
    private String firstName;
    private String lastName;
    private String birthday;
}
