package com.example.springbootmongodb.common.data;

import com.example.springbootmongodb.common.validator.Required;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ShopResponseRequest {
    @Required(fieldName = "Comment")
    private String comment;
}
