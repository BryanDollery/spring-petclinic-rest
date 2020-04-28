/*
 * Copyright 2016-2017 the original author or authors.
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

package org.springframework.samples.petclinic.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.model.Specialty;
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

/**
 * @author Vitaliy Fedoriv
 */

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("specialties")
public class SpecialtyRestController {

    @Autowired
    private ClinicService clinicService;

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @GetMapping
    public ResponseEntity<Collection<Specialty>> getAllSpecialtys() {
        Collection<Specialty> specialties = new ArrayList<>(this.clinicService.findAllSpecialties());
        if (specialties.isEmpty()) {
            return new ResponseEntity<>(NOT_FOUND);
        }
        return new ResponseEntity<>(specialties, OK);
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @GetMapping(value = "/{specialtyId}")
    public ResponseEntity<Specialty> getSpecialty(@PathVariable("specialtyId") int specialtyId) {
        Specialty specialty = this.clinicService.findSpecialtyById(specialtyId);
        if (specialty == null) {
            return new ResponseEntity<>(NOT_FOUND);
        }
        return new ResponseEntity<>(specialty, OK);
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @PostMapping
    public ResponseEntity<Specialty> addSpecialty(@RequestBody @Valid Specialty specialty, BindingResult bindingResult, UriComponentsBuilder ucBuilder) {
        BindingErrorsResponse errors = new BindingErrorsResponse();
        HttpHeaders headers = new HttpHeaders();
        if (bindingResult.hasErrors() || (specialty == null)) {
            errors.addAllErrors(bindingResult);
            headers.add("errors", errors.toJSON());
            return new ResponseEntity<>(headers, BAD_REQUEST);
        }
        this.clinicService.saveSpecialty(specialty);
        headers.setLocation(ucBuilder.path("/api/specialtys/{id}").buildAndExpand(specialty.getId()).toUri());
        return new ResponseEntity<>(specialty, headers, CREATED);
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @PutMapping(value = "/{specialtyId}")
    public ResponseEntity<Specialty> updateSpecialty(@PathVariable("specialtyId") int specialtyId, @RequestBody @Valid Specialty specialty, BindingResult bindingResult) {
        BindingErrorsResponse errors = new BindingErrorsResponse();
        HttpHeaders headers = new HttpHeaders();
        if (bindingResult.hasErrors() || (specialty == null)) {
            errors.addAllErrors(bindingResult);
            headers.add("errors", errors.toJSON());
            return new ResponseEntity<>(headers, BAD_REQUEST);
        }
        Specialty currentSpecialty = this.clinicService.findSpecialtyById(specialtyId);
        if (currentSpecialty == null) {
            return new ResponseEntity<>(NOT_FOUND);
        }
        currentSpecialty.setName(specialty.getName());
        this.clinicService.saveSpecialty(currentSpecialty);
        return new ResponseEntity<>(currentSpecialty, NO_CONTENT);
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @DeleteMapping(value = "/{specialtyId}")
    @Transactional
    public ResponseEntity<Void> deleteSpecialty(@PathVariable("specialtyId") int specialtyId) {
        Specialty specialty = this.clinicService.findSpecialtyById(specialtyId);
        if (specialty == null) {
            return new ResponseEntity<>(NOT_FOUND);
        }
        this.clinicService.deleteSpecialty(specialty);
        return new ResponseEntity<>(NO_CONTENT);
    }

}
