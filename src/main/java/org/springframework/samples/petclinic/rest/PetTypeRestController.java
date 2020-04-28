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
import org.springframework.samples.petclinic.model.PetType;
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
@RequestMapping("pettypes")
public class PetTypeRestController {

    @Autowired
    private ClinicService clinicService;

    @PreAuthorize("hasAnyRole(@roles.OWNER_ADMIN, @roles.VET_ADMIN)")
    @GetMapping
    public ResponseEntity<Collection<PetType>> getAllPetTypes() {
        Collection<PetType> petTypes = new ArrayList<>(this.clinicService.findAllPetTypes());
        if (petTypes.isEmpty()) {
            return new ResponseEntity<>(NOT_FOUND);
        }
        return new ResponseEntity<>(petTypes, OK);
    }

    @PreAuthorize("hasAnyRole(@roles.OWNER_ADMIN, @roles.VET_ADMIN)")
    @GetMapping(value = "/{petTypeId}")
    public ResponseEntity<PetType> getPetType(@PathVariable("petTypeId") int petTypeId) {
        PetType petType = this.clinicService.findPetTypeById(petTypeId);
        if (petType == null) {
            return new ResponseEntity<>(NOT_FOUND);
        }
        return new ResponseEntity<>(petType, OK);
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @PostMapping
    public ResponseEntity<PetType> addPetType(@RequestBody @Valid PetType petType, BindingResult bindingResult, UriComponentsBuilder ucBuilder) {
        BindingErrorsResponse errors = new BindingErrorsResponse();
        HttpHeaders headers = new HttpHeaders();
        if (bindingResult.hasErrors() || (petType == null)) {
            errors.addAllErrors(bindingResult);
            headers.add("errors", errors.toJSON());
            return new ResponseEntity<>(headers, BAD_REQUEST);
        }
        this.clinicService.savePetType(petType);
        headers.setLocation(ucBuilder.path("/api/pettypes/{id}").buildAndExpand(petType.getId()).toUri());
        return new ResponseEntity<>(petType, headers, CREATED);
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @PutMapping(value = "/{petTypeId}")
    public ResponseEntity<PetType> updatePetType(@PathVariable("petTypeId") int petTypeId, @RequestBody @Valid PetType petType, BindingResult bindingResult) {
        BindingErrorsResponse errors = new BindingErrorsResponse();
        HttpHeaders headers = new HttpHeaders();
        if (bindingResult.hasErrors() || (petType == null)) {
            errors.addAllErrors(bindingResult);
            headers.add("errors", errors.toJSON());
            return new ResponseEntity<>(headers, BAD_REQUEST);
        }
        PetType currentPetType = this.clinicService.findPetTypeById(petTypeId);
        if (currentPetType == null) {
            return new ResponseEntity<>(NOT_FOUND);
        }
        currentPetType.setName(petType.getName());
        this.clinicService.savePetType(currentPetType);
        return new ResponseEntity<>(currentPetType, NO_CONTENT);
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @DeleteMapping(value = "/{petTypeId}")
    @Transactional
    public ResponseEntity<Void> deletePetType(@PathVariable("petTypeId") int petTypeId) {
        PetType petType = this.clinicService.findPetTypeById(petTypeId);
        if (petType == null) {
            return new ResponseEntity<>(NOT_FOUND);
        }
        this.clinicService.deletePetType(petType);
        return new ResponseEntity<>(NO_CONTENT);
    }

}
