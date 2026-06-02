package org.example.smoothies.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Smoothie {
    private String name;
    private Ingredients ingredients;
}
