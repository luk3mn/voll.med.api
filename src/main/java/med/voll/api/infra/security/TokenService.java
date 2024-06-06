package med.voll.api.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import med.voll.api.domain.usuario.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service // Anotando essa classe com "@Service" significa que ela faz parte do Spring Boot e agora pode ser injetada no controller, por exemplo
public class TokenService {

    @Value("${api.security.token.secret}") // Para ler a variável do SECRET KEY do application.properties
    private String secret;

    public String gerarToken(Usuario usuario) {
        try {
            var algoritmo = Algorithm.HMAC256(secret); // "HMAC256" mais básico de geração de token
            return JWT.create()
                    .withIssuer("API Voll.med")
                    .withSubject(usuario.getLogin()) // informa o dono do token, nesse caso, a pessoa responsavel por gerar esse token
                    .withExpiresAt(dataExpiracao()) // passa uma data de expiração do token
                    .sign(algoritmo);
        } catch (JWTCreationException exception){
            throw new RuntimeException("erro ao gerrar token jwt", exception);
        }
    }

    private Instant dataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00")); // retorna un instance com UTC -3 que tem validade de 2 horas para a validação do token de acesso
    }

}
