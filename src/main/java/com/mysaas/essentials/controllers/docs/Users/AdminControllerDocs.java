package com.mysaas.essentials.controllers.docs.Users;

import com.mysaas.essentials.model.dto.user.UpdateUserRequest;
import com.mysaas.essentials.model.dto.user.UpdateUserRoleRequest;
import com.mysaas.essentials.model.dto.user.UpdateUserStatusRequest;
import com.mysaas.essentials.model.dto.user.UserResponse;
import com.mysaas.essentials.model.entities.User;
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

public interface AdminControllerDocs {


    @Operation(
            summary = "Buscar usuário por ID",
            description = "Retorna os dados de um usuário com base no ID informado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário encontrado com sucesso",
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
                    description = "Usuário não encontrado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content
            )
    })
    ResponseEntity<EntityModel<UserResponse>> getUserById(@PathVariable UUID id);

    @Operation(
            summary = "Buscar todos usuários",
            description = "Pesquisa todos os usuários"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de usuários retornada com sucesso",
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
                    description = "Usuários não encontrado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content
            )
    })
    ResponseEntity<PagedModel<EntityModel<UserResponse>>> getAllUsers(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                              @RequestParam(value = "size", defaultValue = "12") Integer size,
                                                                              @RequestParam(value = "direction", defaultValue = "asc") String direction,
                                                                              @RequestParam(value = "status", required = false) Boolean status,
                                                                              PagedResourcesAssembler<User> pagedResourcesAssembler);

    @Operation(
            summary = "Buscar todos usuários por Nome",
            description = "Pesquisa todos os usuários por Nome"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de usuários por Nome retornada com sucesso",
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
                    description = "Usuários não encontrado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content
            )
    })
    ResponseEntity<PagedModel<EntityModel<UserResponse>>> findByName(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                              @RequestParam(value = "size", defaultValue = "12") Integer size,
                                                                              @RequestParam(value = "direction", defaultValue = "asc") String direction,
                                                                              @RequestParam(value = "status", required = false) Boolean status,
                                                                              @PathVariable("name") String name,
                                                                              PagedResourcesAssembler<User> pagedResourcesAssembler);


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
    ResponseEntity<Void> deleteUserById(@PathVariable UUID id);



    @Operation(
            summary = "Atualizar usuário",
            description = "Atualiza os dados de um usuário com base no ID informado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário atualizado com sucesso",
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
                    description = "Usuário não encontrado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content
            )
    })
    ResponseEntity<EntityModel<UserResponse>> updateUserById(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request
    );

    @Operation(
            summary = "Atualizar a role de um usuário",
            description = "Atualiza a role de um usuário com base no ID informado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário atualizado com sucesso",
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
                    description = "Usuário não encontrado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content
            )
    })
    public ResponseEntity<EntityModel<UserResponse>> updateRoleByUserID(@PathVariable UUID id, @Valid @RequestBody UpdateUserRoleRequest request);


    @Operation(
            summary = "Atualizar status de um usuário",
            description = "Atualiza o status de um usuário com base no ID informado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário atualizado com sucesso",
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
                    description = "Usuário não encontrado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content
            )
    })
    public  ResponseEntity<EntityModel<UserResponse>> updateStatusByUserId(@PathVariable UUID id, @Valid @RequestBody UpdateUserStatusRequest request);
}
