package com.mtripode.jobapp.facade.dto;

/**
 * DTO for creating, updating, and returning Company objects.
 */
public class CompanyDto extends BaseDto {

    private String name;
    private String website;
    private String description;

    // --- Constructors ---
    public CompanyDto() {
    }

    public CompanyDto(Long id, String name, String website, String description) {
        this.setId(id); // inherited from BaseDto
        this.name = name;
        this.website = website;
        this.description = description;
    }

    // --- Getters and Setters ---
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // --- toString ---
    @Override
    public String toString() {
        return "CompanyDto{"
                + "id=" + getId()
                + // from BaseDto
                ", name='" + name + '\''
                + ", website='" + website + '\''
                + ", description='" + description + '\''
                + ", createdAt=" + getCreatedAt()
                + // from BaseDto
                ", updatedAt=" + getUpdatedAt()
                + // from BaseDto
                '}';
    }
}
