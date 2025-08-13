package com.thinkitive.thinkemr.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClinicAddress {

    @NotBlank(message = "Street address is required")
    @Size(max = 200, message = "Street address cannot exceed 200 characters")
    @Column(name = "street", nullable = false, length = 200)
    private String street;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City cannot exceed 100 characters")
    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 50, message = "State cannot exceed 50 characters")
    @Column(name = "state", nullable = false, length = 50)
    private String state;

    @NotBlank(message = "ZIP code is required")
    @Pattern(regexp = "^\\d{5}(-\\d{4})?$", message = "ZIP code must be in valid format (e.g., 12345 or 12345-6789)")
    @Column(name = "zip", nullable = false)
    private String zip;
} 