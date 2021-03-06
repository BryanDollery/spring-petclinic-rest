/*
 * Copyright 2016-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.rest.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.rest.errors.BindingErrorsResponse;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("vets")
public class VetRestController {

    private final ClinicService clinicService;

    public VetRestController(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @GetMapping
    public ResponseEntity<Collection<Vet>> getAllVets() {
        Collection<Vet> vets = new ArrayList<>(this.clinicService.findAllVets());
        if (vets.isEmpty()) {
            return new ResponseEntity<>(NOT_FOUND);
        }
        return new ResponseEntity<>(vets, OK);
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @GetMapping(value = "/{vetId}")
    public ResponseEntity<Vet> getVet(@PathVariable("vetId") int vetId) {
        Vet vet = this.clinicService.findVetById(vetId);
        if (vet == null) {
            return new ResponseEntity<>(NOT_FOUND);
        }
        return new ResponseEntity<>(vet, OK);
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @PostMapping
    public ResponseEntity<Vet> addVet(@RequestBody @Valid Vet vet, BindingResult bindingResult, UriComponentsBuilder ucBuilder) {
        BindingErrorsResponse errors = new BindingErrorsResponse();
        HttpHeaders headers = new HttpHeaders();
        if (bindingResult.hasErrors() || (vet == null)) {
            errors.addAllErrors(bindingResult);
            headers.add("errors", errors.toJSON());
            return new ResponseEntity<>(headers, BAD_REQUEST);
        }
        this.clinicService.saveVet(vet);
        headers.setLocation(ucBuilder.path("/api/vets/{id}").buildAndExpand(vet.getId()).toUri());
        return new ResponseEntity<>(vet, headers, CREATED);
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @PutMapping(value = "/{vetId}")
    public ResponseEntity<Vet> updateVet(@PathVariable("vetId") int vetId, @RequestBody @Valid Vet vet, BindingResult bindingResult) {
        BindingErrorsResponse errors = new BindingErrorsResponse();
        HttpHeaders headers = new HttpHeaders();
        if (bindingResult.hasErrors() || (vet == null)) {
            errors.addAllErrors(bindingResult);
            headers.add("errors", errors.toJSON());
            return new ResponseEntity<>(headers, BAD_REQUEST);
        }
        Vet currentVet = this.clinicService.findVetById(vetId);
        if (currentVet == null) {
            return new ResponseEntity<>(NOT_FOUND);
        }
        currentVet.setFirstName(vet.getFirstName());
        currentVet.setLastName(vet.getLastName());
        currentVet.clearSpecialties();
        for (Specialty spec : vet.getSpecialties()) {
            currentVet.addSpecialty(spec);
        }
        this.clinicService.saveVet(currentVet);
        return new ResponseEntity<>(currentVet, NO_CONTENT);
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @DeleteMapping(value = "/{vetId}")
    @Transactional
    public ResponseEntity<Void> deleteVet(@PathVariable("vetId") int vetId) {
        Vet vet = this.clinicService.findVetById(vetId);
        if (vet == null) {
            return new ResponseEntity<>(NOT_FOUND);
        }
        this.clinicService.deleteVet(vet);
        return new ResponseEntity<>(NO_CONTENT);
    }


}
