package com.mysaas.essentials.services.Secret;

import com.mysaas.essentials.controllers.Secrets.SecretAdminController;
import com.mysaas.essentials.controllers.Secrets.SecretController;
import com.mysaas.essentials.controllers.Users.AdminController;
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
               linkTo(methodOn(SecretAdminController.class).getSecretById(entity.getId()))
                       .withSelfRel()
                       .withType("GET"),

               linkTo(methodOn(SecretAdminController.class).getAllSecrets(0,12,"asc",null,null))
                       .withRel("all-users")
                       .withType("GET"),

               linkTo(methodOn(SecretAdminController.class).updateSecretById(entity.getId(), null))
                       .withRel("update")
                       .withType("PUT"),

               linkTo(methodOn(SecretAdminController.class).deleteSecretById(entity.getId()))
                       .withRel("delete")
                       .withType("DELETE"));

    }

    @Override
    public CollectionModel<EntityModel<SecretResponse>> toCollectionModel(Iterable<? extends Secret> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}
