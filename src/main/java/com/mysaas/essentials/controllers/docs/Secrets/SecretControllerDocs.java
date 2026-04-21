package com.mysaas.essentials.controllers.docs.Secrets;

import com.mysaas.essentials.model.dto.secret.CreateSecretRequest;
import com.mysaas.essentials.model.dto.secret.SecretResponse;
import com.mysaas.essentials.model.dto.secret.UpdateSecretRequest;
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

public interface SecretControllerDocs {

    @Operation(
            summary = "Criar secret",
            description = "Cria um novo segredo vinculado ao usuário autenticado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Secret criada com sucesso",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SecretResponse.class)
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
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content
            )
    })
    ResponseEntity<EntityModel<SecretResponse>> insertSecret(@Valid @RequestBody CreateSecretRequest request);

    @Operation(
            summary = "Listar minhas secrets",
            description = "Retorna as secrets do usuário autenticado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Secret encontrada com sucesso",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SecretResponse.class)
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
    ResponseEntity<PagedModel<EntityModel<SecretResponse>>> getMySecrets(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                          @RequestParam(value = "size", defaultValue = "12") Integer size,
                                                                          @RequestParam(value = "direction", defaultValue = "asc") String direction,
                                                                          @RequestParam(value = "status", required = false) Boolean status,
                                                                          PagedResourcesAssembler<Secret> pagedResourcesAssembler);

    @Operation(
            summary = "Buscar minha secret por ID",
            description = "Retorna os dados de uma secret do usuário autenticado com base no ID informado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de secrets retornada com sucesso",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SecretResponse.class)
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
    ResponseEntity<EntityModel<SecretResponse>> getMySecretById(@PathVariable UUID id);

    @Operation(
            summary = "Atualizar minha secret",
            description = "Atualiza os dados de uma secret do usuário autenticado com base no ID informado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Secret atualizada com sucesso",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SecretResponse.class)
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
    ResponseEntity<EntityModel<SecretResponse>> updateMySecretById(@PathVariable UUID id, @Valid @RequestBody UpdateSecretRequest request);



    @Operation(
            summary = "Inativa minha secret",
            description = "Inativa uma secret do usuário autenticado com base no ID informado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Secret inativada com sucesso",
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
                    description = "Secret não encontrada",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content
            )
    })
    public ResponseEntity<EntityModel<SecretResponse>> deactivateMySecretById(@PathVariable UUID id);


    @Operation(
            summary = "Inativa minha secret",
            description = "Inativa uma secret do usuário autenticado com base no ID informado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Secret inativada com sucesso",
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
                    description = "Secret não encontrada",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content
            )
    })
    public ResponseEntity<EntityModel<SecretResponse>> deactivateSecretById(@PathVariable UUID id);



    @Operation(
            summary = "Ativa minha secret",
            description = "Ativa uma secret do usuário autenticado com base no ID informado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Secret Ativa com sucesso",
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
                    description = "Secret não encontrada",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content
            )
    })
    public ResponseEntity<EntityModel<SecretResponse>> activateMySecretById(@PathVariable UUID id);


    @Operation(
            summary = "Ativa minha secret",
            description = "Ativa uma secret do usuário autenticado com base no ID informado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Secret Ativa com sucesso",
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
                    description = "Secret não encontrada",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content
            )
    })
    public ResponseEntity<EntityModel<SecretResponse>> activeSecretById(@PathVariable UUID id);

    @Operation(
            summary = "Listar todas as secrets",
            description = "Pesquisa todas as secrets do sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de secrets retornada com sucesso",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SecretResponse.class)
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
                                    schema = @Schema(implementation = SecretResponse.class)
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
                                    schema = @Schema(implementation = SecretResponse.class)
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
            summary = "Remover secret",
            description = "Remove uma secret com base no ID informado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Secret removida com sucesso",
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
                    description = "Secret não encontrada",
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
