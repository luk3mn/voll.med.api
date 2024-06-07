package med.voll.api.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
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
//    private static final String ISSUER = "API Voll.med"; // -> Podemos passar essa constante no "withIssuer" no lugar de "API Voll.med" para padronizar nos dois metodos

    public String gerarToken(Usuario usuario) {
        try {
            var algoritmo = Algorithm.HMAC256(secret); // "HMAC256" mais básico de geração de token
            return JWT.create()
                    .withIssuer("API Voll.med")
                    .withSubject(usuario.getLogin()) // informa o dono do token, nesse caso, a pessoa responsavel por gerar esse token
                    .withExpiresAt(dataExpiracao()) // passa uma data de expiração do token
                    .sign(algoritmo);
        } catch (JWTCreationException exception){
            throw new RuntimeException("erro ao gerar token jwt", exception);
        }
    }

    public String getSubject(String tokenJWT) {
        try {
            var algoritmo = Algorithm.HMAC256(secret);
            return JWT.require(algoritmo)
                    .withIssuer("API Voll.med") // o issuer deve ser exatamente o mesmo do utilizado na geração do token
                    .build()
                    .verify(tokenJWT) // verifica se o token é valido
                    .getSubject(); // pega o assunto do token, nesse caso usuário.getLogin, que foi usado durante o login
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Token JWT inválido ou expirado!");
        }
    }


    private Instant dataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00")); // retorna un instance com UTC -3 que tem validade de 2 horas para a validação do token de acesso
    }

}
