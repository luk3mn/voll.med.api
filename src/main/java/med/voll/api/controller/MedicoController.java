package med.voll.api.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import med.voll.api.domain.medico.DadosListagemMedico;
import med.voll.api.domain.medico.Medico;
import med.voll.api.domain.medico.MedicoRepository;
import med.voll.api.domain.medico.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("medicos")
public class MedicoController {

    @Autowired
    private MedicoRepository repository;

    @PostMapping
    @Transactional // para permitir a persistência de dados no banco de dados
    public ResponseEntity cadastrar(@RequestBody @Valid DadosCadastroMedico dados, UriComponentsBuilder uriBuilder) { // indica que os dados vindo da requisição irão passar pela validação do bean validate

        var medico = new Medico(dados);
        repository.save(medico); // passa os dados recebidos no corpo da requisição para a entidade Medico

        // dados que serão retornados no header da resposta
        var uri = uriBuilder.path("/medicos/{id}").buildAndExpand(medico.getId()).toUri();

        return ResponseEntity.created(uri).body(new DadosDetalhamentoMedico(medico)); // no corpo da resposta será retornado o DTO com os dados
    }

    @GetMapping
    public ResponseEntity<Page<DadosListagemMedico>> listar(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao) { // Para facilitar a paginação dos dados no GET endpoint e findAll deve receber esse parâmetro
        /*
        * PageableDefault -> é opcional, e serve apenas para definir a paginação padrão, caso nao seja informado nenhum parâmetro na requisição
        * A paginação serve para evitar que todos os registros do banco de dados sejam carregados de uma unica vez, assim, ao invés de retornar um List e ele retorna
        * um Page que controla a quantidade de dados que será exibida por vez.
        *
        * Example request => GET http://localhost:8080/medicos?size=1&page=2 -> controlado pelo front-end
        * Example Request => GEt http://localhost8080/medicos?sort=crm,desc&size=2&page=1
        * */
        var page = repository.findAllByAtivoTrue(paginacao).map(DadosListagemMedico::new); // Ao retornar Page<>, não precisa mais do stram, pois o Page ja tem o map integrado, e nem precisa da operaçãp final "toList"
//        return repository.findAll(paginacao).stream().map(DadosListagemMedico::new).toList();

        return ResponseEntity.ok(page);
    }

    @PutMapping
    @Transactional  // visto que o código está anotado como "@Transactional" o trecho de código abaixo ja faz salva as informações no banco de dados
    public ResponseEntity atualizar(@RequestBody @Valid DadosAtualizacaoMedico dados) {
        var medico = repository.getReferenceById(dados.id());
        medico.atualizarInformacoes(dados);

        return ResponseEntity.ok(new DadosDetalhamentoMedico(medico));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity excluir(@PathVariable Long id) {
        var medico = repository.getReferenceById(id);
        medico.excluir();

        return ResponseEntity.noContent().build(); // retorna 204
    }

    @GetMapping("/{id}")
    public ResponseEntity detalhar(@PathVariable Long id) {
        var medico = repository.getReferenceById(id);
        return ResponseEntity.ok(new DadosDetalhamentoMedico(medico));
    }
}
