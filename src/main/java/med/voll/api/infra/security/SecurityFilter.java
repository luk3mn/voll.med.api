package med.voll.api.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import med.voll.api.domain.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // a anotação "@Componen" serve para o Spring carregar a classe automaticamente no projeto
public class SecurityFilter extends OncePerRequestFilter { // "OncePerRequestFilter" -> classe do Sprinf que utiliza o "Filter" do java para trabalhar com os filtros no contexto do Spring

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var tokenJWT = recuperarToken(request);

        if (tokenJWT != null) { // só busca do login pelo token se houver token recuperado
            var subject = tokenService.getSubject(tokenJWT); // envia o token para a classe responsável por validar o token e então pegar apenas o usuário (subject) desse token
            var usuario = repository.findByLogin(subject); // carrega as informações do banco de dados, referente ao usuário extraído do token

            // Força uma autenticação se o usuário ja fez login anteriormente.
            var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response); // indica que o filtro deve avançar passando o request e o response como parâmetro
    }

    private String recuperarToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization"); // procura se existe "Authorization" com o token no header da requisição

        if (authorizationHeader != null) { // retorna somente se encontrar um token no cabeçalho da requisição
            return authorizationHeader.replace("Bearer ", ""); // remove o "Bearer " antes do token para pegar apenas o token e retorná-lo
        }

        return null;
    }

}
