package com.mysaas.essentials.controllers.docs;

import com.mysaas.essentials.model.dto.UsersDTOS.Login.LoginRequest;
import com.mysaas.essentials.model.dto.UsersDTOS.Login.LoginResponse;
import com.mysaas.essentials.model.dto.UsersDTOS.Register.UserRegisterRequest;
import com.mysaas.essentials.model.dto.UsersDTOS.Register.UserRegisterResponse;
import com.mysaas.essentials.model.dto.UsersDTOS.Update.ChangePasswordRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthControllerDocs {

    @Operation(
            summary = "Autenticar usuário",
            description = "Realiza a autenticação do usuário e retorna um token de acesso."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login realizado com sucesso",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LoginResponse.class)
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
                    description = "Credenciais inválidas",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content
            )
    })
    ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request);



    @Operation(
            summary = "Cadastrar usuário",
            description = "Realiza o cadastro de um novo usuário no sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuário cadastrado com sucesso",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserRegisterResponse.class)
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
                    responseCode = "409",
                    description = "Usuário já existe",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content
            )
    })
    ResponseEntity<EntityModel<UserRegisterResponse>> insertUser(
            @Valid @RequestBody UserRegisterRequest userRegisterRequest
    );



    @Operation(
            summary = "Fazer Logout",
            description = "Realiza o logout de um usuário no sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Logout feito com sucesso",
                    content = @Content
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
                    responseCode = "409",
                    description = "Usuário já fez logout",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content
            )
    })
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response);


    @Operation(
            summary = "Atualizar Senha",
            description = "Realiza a atualização da senha do usuário."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Senha atualizada com sucesso",
                    content = @Content
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
                    responseCode = "409",
                    description = "Erro ao atualizar senha.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content
            )
    })
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest request);
}
