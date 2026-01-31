package com.mtripode.jobapp.facade.dto;

import java.io.Serializable;

public class PositionDto extends BaseDto implements Serializable {

    private String title;
    private String location;
    private String description;
    private String companyName; // simplificado para exponer solo el nombre de la empresa
    private static final long serialVersionUID = 1L;

    public PositionDto() {
    }

    public PositionDto(Long id, String title, String location, String description, String companyName) {
        this.setId(id); // heredado de BaseDto
        this.title = title;
        this.location = location;
        this.description = description;
        this.companyName = companyName;
    }

    // Getters y setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
