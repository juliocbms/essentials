package com.mysaas.essentials.services.Secret;

import com.mysaas.essentials.controllers.AdminController;
import com.mysaas.essentials.controllers.SecretController;
import com.mysaas.essentials.model.dto.secret.SecretResponse;
import com.mysaas.essentials.model.entities.Secret;
import com.mysaas.essentials.model.mappers.SecretMapper;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class SecretModelAssembler implements RepresentationModelAssembler<Secret, EntityModel<SecretResponse>> {

    private  final SecretMapper secretMapper;

    public SecretModelAssembler(SecretMapper secretMapper) {
        this.secretMapper = secretMapper;
    }

    @Override
    public EntityModel<SecretResponse> toModel(Secret entity) {
       SecretResponse dto = secretMapper.toResponse(entity);

       return EntityModel.of(dto,
               linkTo(methodOn(SecretController.class).getSecretById(entity.getId()))
                       .withSelfRel()
                       .withType("GET"));

    }

    @Override
    public CollectionModel<EntityModel<SecretResponse>> toCollectionModel(Iterable<? extends Secret> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}
