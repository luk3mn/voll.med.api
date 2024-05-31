package med.voll.api.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import med.voll.api.medico.DadosCadastroMedico;
import med.voll.api.medico.Medico;
import med.voll.api.medico.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
