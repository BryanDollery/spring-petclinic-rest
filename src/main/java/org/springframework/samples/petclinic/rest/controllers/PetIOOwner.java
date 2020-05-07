package org.springframework.samples.petclinic.rest.controllers;

public class PetIOOwner {
    private int id;

    public PetIOOwner() {
    }

    public PetIOOwner(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
