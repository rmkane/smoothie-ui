package org.example.smoothies.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmoothiesWrapper {
    private List<Smoothie> smoothies;
}
