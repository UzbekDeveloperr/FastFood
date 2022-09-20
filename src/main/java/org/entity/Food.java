package org.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Food {
    Long id;
    String name;
    String description;
    String img_link;
    Long price;
    Long category_id;
}
