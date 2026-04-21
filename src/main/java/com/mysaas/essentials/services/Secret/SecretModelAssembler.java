package com.mysaas.essentials.services.Secret;

import com.mysaas.essentials.controllers.Secrets.SecretController;
import com.mysaas.essentials.model.dto.secret.SecretResponse;
import com.mysaas.essentials.model.entities.Secret;
import com.mysaas.essentials.model.mappers.SecretMapper;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class SecretModelAssembler implements RepresentationModelAssembler<Secret, EntityModel<SecretResponse>> {

    private  final SecretMapper secretMapper;
    private final SecretHelper secretHelper;

    public SecretModelAssembler(SecretMapper secretMapper, SecretHelper secretHelper) {
        this.secretMapper = secretMapper;
        this.secretHelper = secretHelper;
    }

    @Override
    public EntityModel<SecretResponse> toModel(Secret entity) {
        SecretResponse dto = secretMapper.toResponse(entity);

        UUID currentUserId = secretHelper.getAuthenticatedUserEntity().getId();
        boolean isAdmin = secretHelper.isCurrentUserAdmin();
        boolean isOwner = entity.getCustomerId().equals(currentUserId);

        String selfHref = isOwner ?
                linkTo(methodOn(SecretController.class).getMySecretById(entity.getId())).toUri().toString() :
                linkTo(methodOn(SecretController.class).getSecretById(entity.getId())).toUri().toString();

        EntityModel<SecretResponse> model = EntityModel.of(dto, Link.of(selfHref).withSelfRel());

        if (isOwner) {
            model.add(linkTo(methodOn(SecretController.class).updateMySecretById(entity.getId(), null)).withRel("update"));

            if (entity.isActive()) {
                model.add(linkTo(methodOn(SecretController.class).deactivateMySecretById(entity.getId())).withRel("deactivate"));
            } else {
                model.add(linkTo(methodOn(SecretController.class).activateMySecretById(entity.getId())).withRel("activate"));
            }
        }
        if (isAdmin) {
            model.add(linkTo(methodOn(SecretController.class).deleteSecretById(entity.getId())).withRel("admin-delete"));

            if (!isOwner) {
                model.add(linkTo(methodOn(SecretController.class).updateSecretById(entity.getId(), null)).withRel("admin-update"));
            }
        }

        return model;
    }

    @Override
    public CollectionModel<EntityModel<SecretResponse>> toCollectionModel(Iterable<? extends Secret> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}
