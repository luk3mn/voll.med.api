package med.voll.api.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import med.voll.api.medico.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("medicos")
public class MedicoController {

    @Autowired
    private MedicoRepository repository;

    @PostMapping
    @Transactional // para permitir a persistência de dados no banco de dados
    public void cadastrar(@RequestBody @Valid DadosCadastroMedico dados) { // indica que os dados vindo da requisição irão passar pela validação do bean validate
        repository.save(new Medico(dados)); // passa os dados recebidos no corpo da requisição para a entidade Medico
    }

    @GetMapping
    public Page<DadosListagemMedico> listar(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao) { // Para facilitar a paginação dos dados no GET endpoint e findAll deve receber esse parâmetro
        /*
        * PageableDefault -> é opcional, e serve apenas para definir a paginação padrão, caso nao seja informado nenhum parâmetro na requisição
        * A paginação serve para evitar que todos os registros do banco de dados sejam carregados de uma unica vez, assim, ao invés de retornar um List e ele retorna
        * um Page que controla a quantidade de dados que será exibida por vez.
        *
        * Example request => GET http://localhost:8080/medicos?size=1&page=2 -> controlado pelo front-end
        * Example Request => GEt http://localhost8080/medicos?sort=crm,desc&size=2&page=1
        * */
        return repository.findAllByAtivoTrue(paginacao).map(DadosListagemMedico::new); // Ao retornar Page<>, não precisa mais do stram, pois o Page ja tem o map integrado, e nem precisa da operaçãp final "toList"
//        return repository.findAll(paginacao).stream().map(DadosListagemMedico::new).toList();
    }

    @PutMapping
    @Transactional  // visto que o código está anotado como "@Transactional" o trecho de código abaixo ja faz salva as informações no banco de dados
    public void atualizar(@RequestBody @Valid DadosAtualizacaoMedico dados) {
        var medico = repository.getReferenceById(dados.id());
        medico.atualizarInformacoes(dados);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public void excluir(@PathVariable Long id) {
        var medico = repository.getReferenceById(id);
        medico.excluir();
    }

}
