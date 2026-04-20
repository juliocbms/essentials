package com.mysaas.essentials.controllers.docs.Secrets;

import com.mysaas.essentials.model.dto.secret.SecretResponse;
import com.mysaas.essentials.model.dto.secret.UpdateSecretRequest;
import com.mysaas.essentials.model.dto.user.UserResponse;
import com.mysaas.essentials.model.entities.Secret;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

public interface SecretAdminControllerDocs {

    @Operation(
            summary = "Buscar secret por ID",
            description = "Retorna os dados de uma secret com base no ID informado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Secret encontrada com sucesso",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Requisição inválida",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Não autorizado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Secret não encontrada",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content
            )
    })
    ResponseEntity<EntityModel<SecretResponse>> getSecretById(@PathVariable UUID id);


    @Operation(
            summary = "Buscar todas as secrets",
            description = "Pesquisa todos as secrets"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de secrets retornada com sucesso",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Não autorizado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Secrets não encontradas",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content
            )
    })
    ResponseEntity<PagedModel<EntityModel<SecretResponse>>> getAllSecrets(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                          @RequestParam(value = "size", defaultValue = "12") Integer size,
                                                                          @RequestParam(value = "direction", defaultValue = "asc") String direction,
                                                                          @RequestParam(value = "status", required = false) Boolean status,
                                                                          PagedResourcesAssembler<Secret> pagedResourcesAssembler);


    @Operation(
            summary = "Atualizar secret",
            description = "Atualiza os dados de uma secret com base no ID informado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Secret atualizada com sucesso",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Não autorizado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Secret não encontrada",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content
            )
    })
    ResponseEntity<EntityModel<SecretResponse>> updateSecretById(@PathVariable UUID id, @Valid @RequestBody UpdateSecretRequest request);

    @Operation(
            summary = "Remover usuário",
            description = "Remove um usuário com base no ID informado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Usuário removido com sucesso",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Requisição inválida",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Não autorizado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content
            )
    })
    ResponseEntity<Void> deleteSecretById(@PathVariable UUID id);
}
