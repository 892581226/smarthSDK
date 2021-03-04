package com.example.smarthome.iot.impl.device.infraredscreen.bean;

import java.io.Serializable;

public class FamilyProjectCodeDto implements Serializable {
    private String familyId;
    private String project;

    @Override
    public String toString() {
        return "FamilyProjectCodeDto{" +
                "familyId='" + familyId + '\'' +
                ", project='" + project + '\'' +
                '}';
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }
}
