package com.example.smarthome.iot.entry;

import java.io.Serializable;

public class HomeSmartDefenseBean implements Serializable {
    private boolean defense;
    private String familyId;

    public boolean isDefense() {
        return defense;
    }

    public void setDefense(boolean defense) {
        this.defense = defense;
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }
}
