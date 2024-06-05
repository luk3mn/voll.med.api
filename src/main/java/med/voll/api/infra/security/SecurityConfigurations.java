package med.voll.api.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // indica para o spring que iremos trabalhar com o modulo de securança
public class SecurityConfigurations {

    @Bean // Para expormos o retorno do metedo para o spring, de forma que ele conhea o método
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { // o retorno será "SecurityFilterChain"
        return http.csrf(csrf -> csrf.disable()) // "csrf().disable()" -> desabilitamos a proteção contra esse tipo de ataque porque iremos utilizar token que ja tem esse tipo de proteção
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Indica o sistema de autenticação é "STATELESS"
                .build();
    } // Em geral, esse método serve apenas para desabilitar a proteção padrão do Spring antes de aplicarmos uma proteção personalizada

    @Bean // serve para exportar uma classe para o Spring, fazendo com que ele consiga carregá-la e realizar a sua injeção de dependência em outras classes.
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    } // cria o objeto AuthenticationManager que será injetado no controller

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // utiliza a criptografia BCrypt
    }

}
